package com.yumedev.taptopayandroid.domain.model

data class CardholderData(
    val pan: String,
    val panLastFour: String,
    val expirationDate: String,
    val expirationDateDisplay: String,
    val cardholderName: String? = null,
    val cardholderNameExtended: String? = null,
    val track2Equivalent: String? = null,
    val panSequenceNumber: Int? = null,
    val panValidation: PanValidation? = null
)