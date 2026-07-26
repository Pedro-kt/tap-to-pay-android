package com.yumedev.taptopayandroid.domain.model

data class PanValidation(
    val isValid: Boolean,
    val pan: String,
    val validationType: ValidationType
)

enum class ValidationType {
    LUHN_VALID,
    LUHN_INVALID,
    INSUFFICIENT_DIGITS,
    INVALID_FORMAT
}
