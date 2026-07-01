package com.yumedev.taptopayandroid.domain.model

// EMV Transaction Data from tags 9F02, 5F2A, 9A, 9C, 9F36, 9F37
data class TransactionData(
    // Tag 9F02 Amount, Authorised (6 bytes BCD)
    val amountAuthorised: Long? = null, // Amount in cents/minor units
    val amountAuthorisedDisplay: String? = null, // Formatted

    // Tag 5F2A Transaction Currency Code (2 bytes)
    val currencyCode: String? = null, // ISO 4217 code (e.g., "0840")
    val currencyName: String? = null, // Decoded name (e.g., "USD (ISO 4217)")

    // Tag 9A Transaction Date (3 bytes YYMMDD)
    val transactionDate: String? = null, // Raw date (YYMMDD)
    val transactionDateDisplay: String? = null, // Formatted

    // Tag 9C Transaction Type (1 byte)
    val transactionType: Int? = null, // Type code
    val transactionTypeDescription: String? = null, // Decoded

    // Tag 9F36 Application Transaction Counter (2 bytes)
    val atc: Int? = null, // ATC value

    // Tag 9F37 Unpredictable Number (4 bytes)
    val unpredictableNumber: String? = null, // Hex string (nonce anti-replay)

    // Metadata
    val timestamp: Long = System.currentTimeMillis()
)
