package app.autopayguard.parser

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Parses bank / UPI SMS or notification text, entirely on-device.
 * Returns null when the text is not about a recurring payment or debit.
 */
object MessageParser {

    private val amountRe = Regex(
        """(?:rs\.?|inr|₹)\s*([0-9][0-9,]*(?:\.[0-9]{1,2})?)|([0-9][0-9,]*(?:\.[0-9]{1,2})?)\s*(?:rs\.?|inr|₹)""",
        RegexOption.IGNORE_CASE,
    )

    // Merchant after "towards"/"for"/"to"/"at"/"merchant:" up to a delimiter.
    private val merchantRes = listOf(
        Regex("""(?:towards|merchant\s*[:\-]?|payee\s*[:\-]?|mandate\s+(?:created\s+|registered\s+|set\s*up\s+)?(?:for|with|to))\s+([A-Za-z0-9&.' _\-]{2,40}?)(?=\s+(?:for|on|from|of|via|ref|umn|rs|inr|a/c|ac|is|has|will|\()|[.,;|]|\s*$)""", RegexOption.IGNORE_CASE),
        Regex("""(?:debited\s+(?:by|for|with)?[^.]*?\bto|paid\s+to|sent\s+to)\s+([A-Za-z0-9&.' _\-]{2,40}?)(?=\s+(?:for|on|from|of|via|ref|umn|upi|rs|inr|a/c|ac)|[.,;|]|\s*$)""", RegexOption.IGNORE_CASE),
        Regex("""\bat\s+([A-Z][A-Za-z0-9&.' _\-]{1,38}?)(?=\s+(?:for|on|from|via|ref|umn|rs|inr)|[.,;|]|\s*$)"""),
    )

    private val refRe = Regex(
        """(?:umn|mandate\s*(?:ref(?:erence)?|id|no\.?)|ref(?:erence)?\s*(?:no\.?|id)?|rrn)\s*[:\-]?\s*([A-Za-z0-9@._\-]{6,60})""",
        RegexOption.IGNORE_CASE,
    )

    private val dateFormats = listOf(
        "dd-MM-yyyy", "dd/MM/yyyy", "dd-MM-yy", "dd/MM/yy", "dd-MMM-yyyy", "dd MMM yyyy", "dd-MMM-yy", "dd MMM yy", "yyyy-MM-dd",
    ).map { DateTimeFormatter.ofPattern(it, Locale.ENGLISH) }

    private val dateRe = Regex(
        """\b(\d{1,2}[-/ ](?:\d{1,2}|[A-Za-z]{3})[-/ ]\d{2,4}|\d{4}-\d{2}-\d{2})\b""",
    )

    private val preDebitWords = listOf("will be debited", "scheduled", "upcoming", "pre-debit", "pre debit", "will be auto", "due on", "will be charged", "ऑटो डेबिट होगा", "डेबिट किया जाएगा")
    private val createdWords = listOf("mandate created", "mandate registered", "autopay set", "autopay registered", "e-mandate", "mandate has been created", "mandate successfully", "approved the mandate", "mandate setup", "mandate set up", "मैंडेट")
    private val cancelWords = listOf("mandate revoked", "revoked", "mandate cancelled", "mandate canceled", "mandate paused", "autopay cancelled", "autopay canceled", "mandate deleted", "रद्द")
    private val debitWords = listOf("debited", "deducted", "auto-debit", "autopay", "auto pay", "charged", "paid", "कट गए", "डेबिट")
    private val recurringHints = listOf("autopay", "auto pay", "auto-debit", "auto debit", "mandate", "umn", "standing instruction", "nach", "e-mandate", "recurring", "subscription", "ऑटो")

    fun parse(text: String, today: LocalDate = LocalDate.now()): ParsedMessage? {
        val t = text.replace('\n', ' ').replace(Regex("""\s+"""), " ").trim()
        if (t.isEmpty()) return null
        val lower = t.lowercase(Locale.ENGLISH)

        val kind = when {
            cancelWords.any { it in lower } -> MessageKind.MANDATE_CANCELLED
            preDebitWords.any { it in lower } && recurringHints.any { it in lower } -> MessageKind.PRE_DEBIT_ALERT
            createdWords.any { it in lower } -> MessageKind.MANDATE_CREATED
            debitWords.any { it in lower } && recurringHints.any { it in lower } -> MessageKind.DEBIT
            else -> return null
        }

        val amount = amountRe.find(t)?.let {
            (it.groupValues[1].ifEmpty { it.groupValues[2] }).replace(",", "").toDoubleOrNull()
        }
        val merchant = extractMerchant(t)
        val ref = refRe.find(t)?.groupValues?.get(1)?.trimEnd('.', ',')
        val date = extractDate(t) ?: if (kind == MessageKind.DEBIT || kind == MessageKind.MANDATE_CREATED) today else null

        return ParsedMessage(
            kind = kind,
            merchant = merchant,
            amountRupees = amount,
            date = date,
            mandateRef = ref,
            looksLikeTrialDebit = kind == MessageKind.DEBIT && amount != null && amount in 1.0..5.0,
        )
    }

    private fun extractMerchant(t: String): String? {
        for (re in merchantRes) {
            val m = re.find(t)?.groupValues?.get(1)?.trim()?.trimEnd('.', '-', ' ')
            if (!m.isNullOrBlank() && m.lowercase() !in setOf("autopay", "upi", "your a/c", "the", "a/c")) return m
        }
        return null
    }

    private fun extractDate(t: String): LocalDate? {
        for (m in dateRe.findAll(t)) {
            val s = m.groupValues[1]
            for (f in dateFormats) {
                try { return LocalDate.parse(s, f) } catch (_: Exception) {}
                try { return LocalDate.parse(s.replace(Regex("(?i)sept"), "Sep"), f) } catch (_: Exception) {}
            }
        }
        return null
    }
}
