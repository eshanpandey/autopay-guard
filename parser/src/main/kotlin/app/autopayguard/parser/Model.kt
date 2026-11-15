package app.autopayguard.parser

import java.time.LocalDate

enum class MessageKind {
    /** RBI-mandated notice sent at least 24h before a recurring debit. */
    PRE_DEBIT_ALERT,
    /** A recurring debit has happened. */
    DEBIT,
    /** A new AutoPay / e-mandate was registered. */
    MANDATE_CREATED,
    /** A mandate was revoked, paused or cancelled. */
    MANDATE_CANCELLED,
    UNKNOWN,
}

data class ParsedMessage(
    val kind: MessageKind,
    val merchant: String?,
    val amountRupees: Double?,
    val date: LocalDate?,
    /** Mandate reference / UMN, when present. */
    val mandateRef: String?,
    /** True for tiny (Rs 1-5) debits typical of trial sign-ups. */
    val looksLikeTrialDebit: Boolean,
)
