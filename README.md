# AutoPay Guard

Android-first app that spots recurring debits (UPI AutoPay, cards, NACH), makes the 24-hour
pre-debit alert actionable, and helps users get refunds. See the requirements doc for scope.

- `parser/` - pure Kotlin message parser with tests. Run: `gradle :parser:test`
- `spike/` - Android notification-listener + share-to-app spike (needs Android Studio to build).
