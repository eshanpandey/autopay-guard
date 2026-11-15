package app.autopayguard.parser

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MessageParserTest {
    private val today = LocalDate.of(2026, 11, 15)
    private fun p(s: String) = MessageParser.parse(s, today)

    @Test fun preDebitAlert() {
        val r = assertNotNull(p("Dear Customer, Rs.599.00 will be debited on 20-11-2026 from your A/c towards Kuku FM via UPI AutoPay. UMN: abc123@okaxis. Pls maintain balance."))
        assertEquals(MessageKind.PRE_DEBIT_ALERT, r.kind)
        assertEquals(599.0, r.amountRupees)
        assertEquals(LocalDate.of(2026, 11, 20), r.date)
        assertEquals("Kuku FM", r.merchant)
        assertEquals("abc123@okaxis", r.mandateRef)
    }

    @Test fun trialDebitFlagged() {
        val r = assertNotNull(p("Rs 2.00 debited from A/c XX1234 on 15-11-2026 towards MasteredU AI for UPI Mandate. Ref No 4455667788"))
        assertEquals(MessageKind.DEBIT, r.kind)
        assertTrue(r.looksLikeTrialDebit)
        assertEquals(2.0, r.amountRupees)
        assertEquals("MasteredU AI", r.merchant)
    }

    @Test fun normalAutopayDebitNotTrial() {
        val r = assertNotNull(p("INR 1,299.00 debited via AutoPay towards Netflix on 02/11/2026. Mandate ID: MND998877"))
        assertEquals(1299.0, r.amountRupees)
        assertEquals(false, r.looksLikeTrialDebit)
        assertEquals("Netflix", r.merchant)
        assertEquals("MND998877", r.mandateRef)
    }

    @Test fun mandateCreated() {
        val r = assertNotNull(p("UPI mandate created for Spotify for Rs 119 monthly. UMN: spot55@ybl"))
        assertEquals(MessageKind.MANDATE_CREATED, r.kind)
        assertEquals(119.0, r.amountRupees)
        assertEquals("Spotify", r.merchant)
    }

    @Test fun mandateRevoked() {
        val r = assertNotNull(p("Your UPI mandate revoked successfully for Hotstar. UMN: hs@paytm"))
        assertEquals(MessageKind.MANDATE_CANCELLED, r.kind)
    }

    @Test fun rupeeSymbolAndNamedMonth() {
        val r = assertNotNull(p("₹199 will be auto debited on 05-Dec-2026 towards JioCinema through AutoPay"))
        assertEquals(MessageKind.PRE_DEBIT_ALERT, r.kind)
        assertEquals(LocalDate.of(2026, 12, 5), r.date)
        assertEquals(199.0, r.amountRupees)
    }

    @Test fun unrelatedMessagesIgnored() {
        assertNull(p("Your OTP is 123456. Do not share."))
        assertNull(p("Rs 500 debited from A/c XX1234 at ATM on 10-11-2026"))
        assertNull(p(""))
    }

    @Test fun hindiPreDebit() {
        val r = assertNotNull(p("आपके खाते से Rs 149 ऑटो डेबिट होगा 21-11-2026 को Zee5 के लिए AutoPay द्वारा"))
        assertEquals(MessageKind.PRE_DEBIT_ALERT, r.kind)
        assertEquals(149.0, r.amountRupees)
    }
}
