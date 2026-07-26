package com.yumedev.taptopayandroid.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AipDecoderTest {

    @Test
    fun `decode returns null when hex value is too short`() {
        val result = AipDecoder.decode("00")
        assertThat(result).isNull()
    }

    @Test
    fun `decode returns null when hex value is too long`() {
        val result = AipDecoder.decode("0000FF")
        assertThat(result).isNull()
    }

    @Test
    fun `decode returns null when hex value is invalid`() {
        val result = AipDecoder.decode("ZZZZ")
        assertThat(result).isNull()
    }

    @Test
    fun `decode returns null when hex value is empty`() {
        val result = AipDecoder.decode("")
        assertThat(result).isNull()
    }

    @Test
    fun `decode handles hex value with spaces`() {
        val result = AipDecoder.decode("00 00")
        assertThat(result).isNotNull()
        assertThat(result?.rawValue).isEqualTo("00 00")
    }

    @Test
    fun `decode all zeros means no capabilities supported`() {
        val result = AipDecoder.decode("0000")
        assertThat(result).isNotNull()
        assertThat(result?.sdaSupported).isFalse()
        assertThat(result?.ddaSupported).isFalse()
        assertThat(result?.cdaSupported).isFalse()
        assertThat(result?.cardholderVerificationSupported).isFalse()
        assertThat(result?.terminalRiskManagementRequired).isFalse()
        assertThat(result?.issuerAuthenticationSupported).isFalse()
        assertThat(result?.onDeviceCardholderVerificationSupported).isFalse()
        assertThat(result?.crlSupported).isFalse()
    }

    @Test
    fun `decode SDA bit (byte 1, bit 7 - 0x40)`() {
        val result = AipDecoder.decode("4000")
        assertThat(result).isNotNull()
        assertThat(result?.sdaSupported).isTrue()
        assertThat(result?.ddaSupported).isFalse()
        assertThat(result?.cdaSupported).isFalse()
    }

    @Test
    fun `decode DDA bit (byte 1, bit 6 - 0x20)`() {
        val result = AipDecoder.decode("2000")
        assertThat(result).isNotNull()
        assertThat(result?.sdaSupported).isFalse()
        assertThat(result?.ddaSupported).isTrue()
        assertThat(result?.cdaSupported).isFalse()
    }

    @Test
    fun `decode cardholder verification bit (byte 1, bit 5 - 0x10)`() {
        val result = AipDecoder.decode("1000")
        assertThat(result).isNotNull()
        assertThat(result?.cardholderVerificationSupported).isTrue()
    }

    @Test
    fun `decode terminal risk management bit (byte 1, bit 4 - 0x08)`() {
        val result = AipDecoder.decode("0800")
        assertThat(result).isNotNull()
        assertThat(result?.terminalRiskManagementRequired).isTrue()
    }

    @Test
    fun `decode issuer authentication bit (byte 1, bit 3 - 0x04)`() {
        val result = AipDecoder.decode("0400")
        assertThat(result).isNotNull()
        assertThat(result?.issuerAuthenticationSupported).isTrue()
    }

    @Test
    fun `decode on-device verification bit (byte 1, bit 2 - 0x02)`() {
        val result = AipDecoder.decode("0200")
        assertThat(result).isNotNull()
        assertThat(result?.onDeviceCardholderVerificationSupported).isTrue()
    }

    @Test
    fun `decode CDA bit (byte 1, bit 1 - 0x01)`() {
        val result = AipDecoder.decode("0100")
        assertThat(result).isNotNull()
        assertThat(result?.sdaSupported).isFalse()
        assertThat(result?.ddaSupported).isFalse()
        assertThat(result?.cdaSupported).isTrue()
    }

    @Test
    fun `decode CRL bit (byte 2, bit 1 - 0x01)`() {
        val result = AipDecoder.decode("0001")
        assertThat(result).isNotNull()
        assertThat(result?.crlSupported).isTrue()
    }

    @Test
    fun `decode typical contactless AIP with DDA and CDA - 0x2100`() {
        val result = AipDecoder.decode("2100")
        assertThat(result).isNotNull()
        assertThat(result?.sdaSupported).isFalse()
        assertThat(result?.ddaSupported).isTrue()
        assertThat(result?.cdaSupported).isTrue()
        assertThat(result?.cardholderVerificationSupported).isFalse()
        assertThat(result?.terminalRiskManagementRequired).isFalse()
        assertThat(result?.issuerAuthenticationSupported).isFalse()
        assertThat(result?.onDeviceCardholderVerificationSupported).isFalse()
        assertThat(result?.crlSupported).isFalse()
    }

    @Test
    fun `decode typical contact AIP with SDA - 0x4000`() {
        val result = AipDecoder.decode("4000")
        assertThat(result).isNotNull()
        assertThat(result?.sdaSupported).isTrue()
        assertThat(result?.ddaSupported).isFalse()
        assertThat(result?.cdaSupported).isFalse()
    }

    @Test
    fun `decode full-featured AIP - 0x7F01`() {
        val result = AipDecoder.decode("7F01")
        assertThat(result).isNotNull()
        assertThat(result?.sdaSupported).isTrue()
        assertThat(result?.ddaSupported).isTrue()
        assertThat(result?.cdaSupported).isTrue()
        assertThat(result?.cardholderVerificationSupported).isTrue()
        assertThat(result?.terminalRiskManagementRequired).isTrue()
        assertThat(result?.issuerAuthenticationSupported).isTrue()
        assertThat(result?.onDeviceCardholderVerificationSupported).isTrue()
        assertThat(result?.crlSupported).isTrue()
    }

    @Test
    fun `decode AIP with multiple bits set - 0x6800`() {
        val result = AipDecoder.decode("6800")
        assertThat(result).isNotNull()
        assertThat(result?.sdaSupported).isTrue()
        assertThat(result?.ddaSupported).isTrue()
        assertThat(result?.cardholderVerificationSupported).isFalse()
        assertThat(result?.terminalRiskManagementRequired).isTrue()
        assertThat(result?.cdaSupported).isFalse()
    }

    @Test
    fun `decode preserves raw value with spaces`() {
        val input = "21 00"
        val result = AipDecoder.decode(input)
        assertThat(result).isNotNull()
        assertThat(result?.rawValue).isEqualTo(input)
    }

    @Test
    fun `decode preserves raw value without spaces`() {
        val input = "2100"
        val result = AipDecoder.decode(input)
        assertThat(result).isNotNull()
        assertThat(result?.rawValue).isEqualTo(input)
    }

    @Test
    fun `decode handles lowercase hex`() {
        val result = AipDecoder.decode("2f01")
        assertThat(result).isNotNull()
        assertThat(result?.ddaSupported).isTrue()
        assertThat(result?.terminalRiskManagementRequired).isTrue()
        assertThat(result?.issuerAuthenticationSupported).isTrue()
        assertThat(result?.onDeviceCardholderVerificationSupported).isTrue()
        assertThat(result?.cdaSupported).isTrue()
        assertThat(result?.crlSupported).isTrue()
    }

    @Test
    fun `decode handles uppercase hex`() {
        val result = AipDecoder.decode("2F01")
        assertThat(result).isNotNull()
        assertThat(result?.ddaSupported).isTrue()
        assertThat(result?.crlSupported).isTrue()
    }

    @Test
    fun `decode handles mixed case hex`() {
        val result = AipDecoder.decode("2f01")
        assertThat(result).isNotNull()
        assertThat(result?.ddaSupported).isTrue()
        assertThat(result?.crlSupported).isTrue()
    }

    @Test
    fun `decode typical Visa payWave AIP - 0x1C00`() {
        val result = AipDecoder.decode("1C00")
        assertThat(result).isNotNull()
        assertThat(result?.sdaSupported).isFalse()
        assertThat(result?.ddaSupported).isFalse()
        assertThat(result?.cdaSupported).isFalse()
        assertThat(result?.cardholderVerificationSupported).isTrue()
        assertThat(result?.terminalRiskManagementRequired).isTrue()
        assertThat(result?.issuerAuthenticationSupported).isTrue()
    }

    @Test
    fun `decode typical Mastercard PayPass AIP - 0x3C00`() {
        val result = AipDecoder.decode("3C00")
        assertThat(result).isNotNull()
        assertThat(result?.sdaSupported).isFalse()
        assertThat(result?.ddaSupported).isTrue()
        assertThat(result?.cdaSupported).isFalse()
        assertThat(result?.cardholderVerificationSupported).isTrue()
        assertThat(result?.terminalRiskManagementRequired).isTrue()
        assertThat(result?.issuerAuthenticationSupported).isTrue()
    }
}
