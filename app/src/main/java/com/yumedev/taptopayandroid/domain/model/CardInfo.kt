package com.yumedev.taptopayandroid.domain.model

data class CardInfo(
    val cardNumber: String,
    val expirationDate: String,
    val cardholderName: String? = null,
    val cardType: CardType = CardType.UNKNOWN,
    val rawData: String? = null
)

enum class CardType {
    VISA,
    MASTERCARD,
    AMEX,
    DISCOVER,
    MAESTRO,
    UNKNOWN
}
