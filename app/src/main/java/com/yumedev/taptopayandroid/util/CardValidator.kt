package com.yumedev.taptopayandroid.util

object CardValidator {

    private const val MIN_PAN_LENGTH = 13
    private const val MAX_PAN_LENGTH = 19

    fun validateLuhn(pan: String): Boolean {
        val digits = pan.filter { it.isDigit() }

        if (digits.length < MIN_PAN_LENGTH || digits.length > MAX_PAN_LENGTH) {
            return false
        }

        var sum = 0
        var alternate = false

        for (i in digits.length - 1 downTo 0) {
            var digit = digits[i].toString().toInt()

            if (alternate) {
                digit *= 2
                if (digit > 9) {
                    digit -= 9
                }
            }

            sum += digit
            alternate = !alternate
        }

        return sum % 10 == 0
    }

    fun isValidPanFormat(pan: String): Boolean {
        val digits = pan.filter { it.isDigit() }
        return digits.length in MIN_PAN_LENGTH..MAX_PAN_LENGTH
    }
}
