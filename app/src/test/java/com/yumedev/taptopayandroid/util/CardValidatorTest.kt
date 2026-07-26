package com.yumedev.taptopayandroid.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CardValidatorTest {

    @Test
    fun `validateLuhn returns true for valid Visa card`() {
        val validVisa = "4532015112830366"

        val result = CardValidator.validateLuhn(validVisa)

        assertThat(result).isTrue()
    }

    @Test
    fun `validateLuhn returns true for valid Mastercard`() {
        val validMastercard = "5425233430109903"

        val result = CardValidator.validateLuhn(validMastercard)

        assertThat(result).isTrue()
    }

    @Test
    fun `validateLuhn returns true for valid Amex`() {
        val validAmex = "374245455400126"

        val result = CardValidator.validateLuhn(validAmex)

        assertThat(result).isTrue()
    }

    @Test
    fun `validateLuhn returns true for valid Discover`() {
        val validDiscover = "6011111111111117"

        val result = CardValidator.validateLuhn(validDiscover)

        assertThat(result).isTrue()
    }

    @Test
    fun `validateLuhn returns false for invalid checksum`() {
        val invalidPan = "4532015112830367"

        val result = CardValidator.validateLuhn(invalidPan)

        assertThat(result).isFalse()
    }

    @Test
    fun `validateLuhn returns false for PAN with less than 13 digits`() {
        val shortPan = "453201511283"

        val result = CardValidator.validateLuhn(shortPan)

        assertThat(result).isFalse()
    }

    @Test
    fun `validateLuhn returns false for PAN with more than 19 digits`() {
        val longPan = "45320151128303661234"

        val result = CardValidator.validateLuhn(longPan)

        assertThat(result).isFalse()
    }

    @Test
    fun `validateLuhn returns false for empty string`() {
        val emptyPan = ""

        val result = CardValidator.validateLuhn(emptyPan)

        assertThat(result).isFalse()
    }

    @Test
    fun `validateLuhn handles PAN with spaces correctly`() {
        val panWithSpaces = "4532 0151 1283 0366"

        val result = CardValidator.validateLuhn(panWithSpaces)

        assertThat(result).isTrue()
    }

    @Test
    fun `validateLuhn handles PAN with dashes correctly`() {
        val panWithDashes = "4532-0151-1283-0366"

        val result = CardValidator.validateLuhn(panWithDashes)

        assertThat(result).isTrue()
    }

    @Test
    fun `validateLuhn returns true for 13 digit valid PAN`() {
        val validPan13 = "4024007135532"

        val result = CardValidator.validateLuhn(validPan13)

        assertThat(result).isTrue()
    }

    @Test
    fun `validateLuhn returns false for PAN with letters`() {
        val panWithLetters = "4532ABC112830366"

        val result = CardValidator.validateLuhn(panWithLetters)

        assertThat(result).isFalse()
    }

    @Test
    fun `validateLuhn returns true for 16 digit all zeros - mathematically valid`() {
        val allZeros = "0000000000000000"

        val result = CardValidator.validateLuhn(allZeros)

        assertThat(result).isTrue()
    }

    @Test
    fun `validateLuhn returns false for all nines`() {
        val allNines = "9999999999999999"

        val result = CardValidator.validateLuhn(allNines)

        assertThat(result).isFalse()
    }

    @Test
    fun `isValidPanFormat returns true for 13 digit PAN`() {
        val pan13 = "4111111111111"

        val result = CardValidator.isValidPanFormat(pan13)

        assertThat(result).isTrue()
    }

    @Test
    fun `isValidPanFormat returns true for 16 digit PAN`() {
        val pan16 = "4532015112830366"

        val result = CardValidator.isValidPanFormat(pan16)

        assertThat(result).isTrue()
    }

    @Test
    fun `isValidPanFormat returns true for 19 digit PAN`() {
        val pan19 = "6011000990139424123"

        val result = CardValidator.isValidPanFormat(pan19)

        assertThat(result).isTrue()
    }

    @Test
    fun `isValidPanFormat returns false for 12 digit PAN`() {
        val pan12 = "411111111111"

        val result = CardValidator.isValidPanFormat(pan12)

        assertThat(result).isFalse()
    }

    @Test
    fun `isValidPanFormat returns false for 20 digit PAN`() {
        val pan20 = "60110009901394241234"

        val result = CardValidator.isValidPanFormat(pan20)

        assertThat(result).isFalse()
    }

    @Test
    fun `isValidPanFormat returns false for empty string`() {
        val emptyPan = ""

        val result = CardValidator.isValidPanFormat(emptyPan)

        assertThat(result).isFalse()
    }

    @Test
    fun `isValidPanFormat ignores non-digit characters`() {
        val panWithSpaces = "4532 0151 1283 0366"

        val result = CardValidator.isValidPanFormat(panWithSpaces)

        assertThat(result).isTrue()
    }

    @Test
    fun `isValidPanFormat returns true when only digits are in valid range`() {
        val panMixed = "4532-ABC-0151-XYZ-1283-0366"

        val result = CardValidator.isValidPanFormat(panMixed)

        assertThat(result).isTrue()
    }
}
