package com.yumedev.taptopayandroid.domain.model

// EMV Cardholder Data from tags 5A, 5F24, 5F20, 9F0B, 57, 5F34
data class CardholderData(
    // Tag 5A Primary Account Number (Variable length BCD)
    val pan: String, // Full PAN (masked if sensitive)
    val panLastFour: String, // Last 4 digits for display

    // Tag 5F24 Application Expiration Date (3 bytes YYMMDD)
    val expirationDate: String, // Raw date (YYMMDD)
    val expirationDateDisplay: String, // Formatted (MM/YY)

    // Tag 5F20 Cardholder Name (Variable ASCII)
    val cardholderName: String? = null, // Cardholder name

    // Tag 9F0B Cardholder Name Extended (Variable ASCII)
    val cardholderNameExtended: String? = null,

    // Tag 57 Track 2 Equivalent Data (Variable)
    val track2Equivalent: String? = null, // Hex string

    // Tag 5F34 PAN Sequence Number (1 byte)
    val panSequenceNumber: Int? = null // Sequence number for multiple cards with same PAN
)