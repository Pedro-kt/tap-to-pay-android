package com.yumedev.taptopayandroid.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.yumedev.taptopayandroid.domain.model.ValidationType
import org.junit.Before
import org.junit.Test

class ValidatePanUseCaseTest {

    private lateinit var useCase: ValidatePanUseCase

    @Before
    fun setup() {
        useCase = ValidatePanUseCase()
    }

    @Test
    fun `invoke returns LUHN_VALID for valid Visa PAN`() {
        val validVisa = "4532015112830366"

        val result = useCase(validVisa)

        assertThat(result.isValid).isTrue()
        assertThat(result.validationType).isEqualTo(ValidationType.LUHN_VALID)
        assertThat(result.pan).isEqualTo(validVisa)
    }

    @Test
    fun `invoke returns LUHN_VALID for valid Mastercard PAN`() {
        val validMastercard = "5425233430109903"

        val result = useCase(validMastercard)

        assertThat(result.isValid).isTrue()
        assertThat(result.validationType).isEqualTo(ValidationType.LUHN_VALID)
        assertThat(result.pan).isEqualTo(validMastercard)
    }

    @Test
    fun `invoke returns LUHN_VALID for valid Amex PAN`() {
        val validAmex = "374245455400126"

        val result = useCase(validAmex)

        assertThat(result.isValid).isTrue()
        assertThat(result.validationType).isEqualTo(ValidationType.LUHN_VALID)
        assertThat(result.pan).isEqualTo(validAmex)
    }

    @Test
    fun `invoke returns LUHN_INVALID for invalid checksum`() {
        val invalidPan = "4532015112830367"

        val result = useCase(invalidPan)

        assertThat(result.isValid).isFalse()
        assertThat(result.validationType).isEqualTo(ValidationType.LUHN_INVALID)
        assertThat(result.pan).isEqualTo(invalidPan)
    }

    @Test
    fun `invoke returns INSUFFICIENT_DIGITS for PAN with 12 digits`() {
        val shortPan = "453201511283"

        val result = useCase(shortPan)

        assertThat(result.isValid).isFalse()
        assertThat(result.validationType).isEqualTo(ValidationType.INSUFFICIENT_DIGITS)
        assertThat(result.pan).isEqualTo(shortPan)
    }

    @Test
    fun `invoke returns INSUFFICIENT_DIGITS for PAN with 20 digits`() {
        val longPan = "45320151128303661234"

        val result = useCase(longPan)

        assertThat(result.isValid).isFalse()
        assertThat(result.validationType).isEqualTo(ValidationType.INSUFFICIENT_DIGITS)
        assertThat(result.pan).isEqualTo(longPan)
    }

    @Test
    fun `invoke returns INVALID_FORMAT for empty string`() {
        val emptyPan = ""

        val result = useCase(emptyPan)

        assertThat(result.isValid).isFalse()
        assertThat(result.validationType).isEqualTo(ValidationType.INVALID_FORMAT)
        assertThat(result.pan).isEqualTo(emptyPan)
    }

    @Test
    fun `invoke returns LUHN_VALID for PAN with spaces`() {
        val panWithSpaces = "4532 0151 1283 0366"

        val result = useCase(panWithSpaces)

        assertThat(result.isValid).isTrue()
        assertThat(result.validationType).isEqualTo(ValidationType.LUHN_VALID)
        assertThat(result.pan).isEqualTo(panWithSpaces)
    }

    @Test
    fun `invoke returns LUHN_VALID for PAN with dashes`() {
        val panWithDashes = "4532-0151-1283-0366"

        val result = useCase(panWithDashes)

        assertThat(result.isValid).isTrue()
        assertThat(result.validationType).isEqualTo(ValidationType.LUHN_VALID)
        assertThat(result.pan).isEqualTo(panWithDashes)
    }

    @Test
    fun `invoke returns LUHN_VALID for 13 digit valid PAN`() {
        val validPan13 = "4024007135532"

        val result = useCase(validPan13)

        assertThat(result.isValid).isTrue()
        assertThat(result.validationType).isEqualTo(ValidationType.LUHN_VALID)
    }

    @Test
    fun `invoke returns INSUFFICIENT_DIGITS for PAN with only letters`() {
        val onlyLetters = "ABCDEFGHIJKLMNOP"

        val result = useCase(onlyLetters)

        assertThat(result.isValid).isFalse()
        assertThat(result.validationType).isEqualTo(ValidationType.INVALID_FORMAT)
    }

    @Test
    fun `invoke returns LUHN_VALID for 16 digit PAN with all zeros - mathematically valid`() {
        val allZeros = "0000000000000000"

        val result = useCase(allZeros)

        assertThat(result.isValid).isTrue()
        assertThat(result.validationType).isEqualTo(ValidationType.LUHN_VALID)
        assertThat(result.pan).isEqualTo(allZeros)
    }

    @Test
    fun `invoke returns LUHN_INVALID for PAN with all nines`() {
        val allNines = "9999999999999999"

        val result = useCase(allNines)

        assertThat(result.isValid).isFalse()
        assertThat(result.validationType).isEqualTo(ValidationType.LUHN_INVALID)
    }

    @Test
    fun `invoke filters non-digit characters before validation`() {
        val panMixed = "4532-ABC-0151-XYZ-1283-0366"

        val result = useCase(panMixed)

        assertThat(result.isValid).isTrue()
        assertThat(result.validationType).isEqualTo(ValidationType.LUHN_VALID)
        assertThat(result.pan).isEqualTo(panMixed)
    }

    @Test
    fun `invoke returns INSUFFICIENT_DIGITS for PAN with 5 digits`() {
        val veryShortPan = "12345"

        val result = useCase(veryShortPan)

        assertThat(result.isValid).isFalse()
        assertThat(result.validationType).isEqualTo(ValidationType.INSUFFICIENT_DIGITS)
    }

    @Test
    fun `invoke returns INSUFFICIENT_DIGITS for single digit`() {
        val singleDigit = "5"

        val result = useCase(singleDigit)

        assertThat(result.isValid).isFalse()
        assertThat(result.validationType).isEqualTo(ValidationType.INSUFFICIENT_DIGITS)
    }

    @Test
    fun `invoke preserves original PAN in result`() {
        val originalPan = "4532 0151 1283 0366"

        val result = useCase(originalPan)

        assertThat(result.pan).isEqualTo(originalPan)
    }

    @Test
    fun `invoke returns INVALID_FORMAT for whitespace only string`() {
        val whitespaceOnly = "    "

        val result = useCase(whitespaceOnly)

        assertThat(result.isValid).isFalse()
        assertThat(result.validationType).isEqualTo(ValidationType.INVALID_FORMAT)
    }

    @Test
    fun `invoke returns INVALID_FORMAT for special characters only`() {
        val specialCharsOnly = "****----####"

        val result = useCase(specialCharsOnly)

        assertThat(result.isValid).isFalse()
        assertThat(result.validationType).isEqualTo(ValidationType.INVALID_FORMAT)
    }
}
