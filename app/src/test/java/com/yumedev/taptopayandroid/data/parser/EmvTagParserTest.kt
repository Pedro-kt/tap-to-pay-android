package com.yumedev.taptopayandroid.data.parser

import com.google.common.truth.Truth.assertThat
import com.yumedev.taptopayandroid.domain.model.CardType
import org.junit.Test

//Unit tests for EmvTagParser
class EmvTagParserTest {

    // Malformed TLV Data Tests

    @Test
    fun `findTag handles TLV with length exceeding available data - prevents buffer overflow`() {
        // Given - Tag claims 10 bytes but only 3 available (CRITICAL security issue)
        val malformedData = byteArrayOf(
            0x5A.toByte(), 0x0A, // Tag 5A, length claims 10 bytes
            0x41, 0x11, 0x11 // Only 3 bytes available
        )

        // When
        val result = EmvTagParser.findTag(malformedData, "5A")

        // Then - Should NOT crash or read beyond buffer
        // Should return null or handle gracefully
        assertThat(result).isNull()
    }

    @Test
    fun `findTag handles truncated two-byte tag - prevents out of bounds`() {
        // Given - Two-byte tag incomplete (only 1 byte)
        val truncatedData = byteArrayOf(
            0x9F.toByte() // Two-byte tag but missing second byte
        )

        // When
        val result = EmvTagParser.findTag(truncatedData, "9F02")

        // Then - Should not crash
        assertThat(result).isNull()
    }

    @Test
    fun `findTag handles empty data array - edge case`() {
        // Given
        val emptyData = byteArrayOf()

        // When
        val result = EmvTagParser.findTag(emptyData, "5A")

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `extractAllTags handles deeply nested constructed tags - prevents stack overflow`() {
        // Given - Recursive TLV structure (could cause infinite loop)
        val nestedData = byteArrayOf(
            0x70, 0x06, // Constructed tag (template)
                0x70, 0x04, // Nested constructed
                    0x70, 0x02, // Double nested
                        0x5A.toByte(), 0x00 // Finally a primitive tag
        )

        // When - Should complete without stack overflow
        val tags = EmvTagParser.extractAllTags(nestedData)

        // Then - Should parse without hanging or crashing
        assertThat(tags).isNotNull()
    }

    // BCD Decoding Edge Cases

    @Test
    fun `decodeBcdPan handles empty PAN - prevents crash`() {
        // Given - Empty PAN bytes
        val emptyPan = byteArrayOf()

        // When
        val tag = EmvTagParser.parseTag("5A", emptyPan)

        // Then - Should not crash, return empty or safe default
        assertThat(tag.valueDecoded).isNotNull()
        assertThat(tag.valueDecoded).isEmpty()
    }

    @Test
    fun `decodeBcdPan filters all padding correctly - prevents invalid card numbers`() {
        // Given - PAN with all 'F' padding (invalid BCD)
        val paddedPan = byteArrayOf(
            0x41, 0x11, 0x11, 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte()
        )

        // When
        val tag = EmvTagParser.parseTag("5A", paddedPan)

        // Then - Should filter out all F's, only valid digits remain (41 + 11 + 11 = 411111)
        assertThat(tag.valueDecoded).isEqualTo("411111")
    }

    @Test
    fun `decodeExpirationDate handles invalid month - real world data corruption`() {
        // Given - Month 13 (impossible, corrupted data)
        val invalidMonth = byteArrayOf(0x26, 0x13, 0x31)

        // When
        val tag = EmvTagParser.parseTag("5F24", invalidMonth)

        // Then - Should return formatted but shows corrupted data (13/26)
        // This is valid behavior - parser shows what's on card
        assertThat(tag.valueDecoded).isNotNull()
        assertThat(tag.valueDecoded).contains("/")
    }

    @Test
    fun `decodeBcdAmount handles maximum value - overflow protection`() {
        // Given - Maximum BCD amount (all 9s)
        val maxAmount = byteArrayOf(
            0x99.toByte(), 0x99.toByte(), 0x99.toByte(),
            0x99.toByte(), 0x99.toByte(), 0x99.toByte()
        )

        // When
        val tag = EmvTagParser.parseTag("9F02", maxAmount)

        // Then - Should handle large numbers without overflow
        assertThat(tag.valueDecoded).isNotNull()
        assertThat(tag.valueDecoded).contains("$")
    }

    // Card Type Detection Edge Cases

    @Test
    fun `determineCardType handles unknown AID and no label - real world scenario`() {
        // Given - Completely unknown card (regional bank, prepaid, etc)
        val unknownAid = byteArrayOf(0xD2.toByte(), 0x76, 0x00, 0x00)
        val emptyResponse = byteArrayOf()

        // When
        val appInfo = EmvTagParser.parseApplicationInfo(unknownAid, emptyResponse)

        // Then - Should default to UNKNOWN, not crash
        assertThat(appInfo.cardType).isEqualTo(CardType.UNKNOWN)
    }

    @Test
    fun `determineCardType prioritizes AID over misleading label - prevents spoofing`() {
        // Given - Mastercard AID but VISA label (malicious/corrupted card)
        val mastercardAid = byteArrayOf(0xA0.toByte(), 0x00, 0x00, 0x00, 0x04, 0x10, 0x10)
        val visaLabel = "VISA CREDIT".toByteArray(Charsets.US_ASCII)
        val response = byteArrayOf(0x50.toByte(), visaLabel.size.toByte()) + visaLabel

        // When
        val appInfo = EmvTagParser.parseApplicationInfo(mastercardAid, response)

        // Then - AID should take precedence (security critical)
        assertThat(appInfo.cardType).isEqualTo(CardType.MASTERCARD)
    }

    // parseCardholderData Edge Cases

    @Test
    fun `parseCardholderData handles corrupted TLV structure - prevents crash`() {
        // Given - Malformed TLV (length byte missing)
        val corruptedData = byteArrayOf(
            0x5A.toByte() // Tag without length or value
        )

        // When - Should not crash on corrupted data
        val cardholderData = EmvTagParser.parseCardholderData(listOf(corruptedData))

        // Then - Should return safe defaults
        assertThat(cardholderData.pan).isNotEmpty()
        assertThat(cardholderData.expirationDateDisplay).isNotNull()
    }

    @Test
    fun `parseCardholderData extracts last 4 digits correctly even with padding`() {
        // Given - PAN with 'F' padding that should be filtered
        val data = byteArrayOf(
            0x5A.toByte(), 0x08,
            0x41, 0x11, 0x11, 0x11, 0x56, 0x78, 0x90.toByte(), 0x1F.toByte()
        )

        // When
        val cardholderData = EmvTagParser.parseCardholderData(listOf(data))

        // Then - Last 4 should only include valid digits
        assertThat(cardholderData.panLastFour).hasLength(4)
        assertThat(cardholderData.panLastFour.all { it.isDigit() }).isTrue()
    }

    @Test
    fun `parseCardholderData handles multiple records without duplicates`() {
        // Given - Same PAN in multiple records (real scenario with multiple SFI)
        val record1 = byteArrayOf(
            0x5A.toByte(), 0x08,
            0x41, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11
        )
        val record2 = byteArrayOf(
            0x5A.toByte(), 0x08,
            0x41, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11
        )

        // When
        val cardholderData = EmvTagParser.parseCardholderData(listOf(record1, record2))

        // Then - Should use first occurrence (EMV spec)
        assertThat(cardholderData.pan).isEqualTo("4111111111111111")
    }

    @Test
    fun `extractAllTags skips malformed tags without crashing entire parse`() {
        // Given - Mix of valid and malformed tags
        val mixedData = byteArrayOf(
            0x5A.toByte(), 0x08, 0x41, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, // Valid
            0x9F.toByte(), 0xFF.toByte(), 0x05, // Malformed (length too large)
            0x50.toByte(), 0x04, 0x56, 0x49, 0x53, 0x41 // Valid "VISA"
        )

        // When - Should parse valid tags, skip malformed
        val tags = EmvTagParser.extractAllTags(mixedData)

        // Then - Should have extracted at least the valid tags
        assertThat(tags).isNotEmpty()
        assertThat(tags.containsKey("5A") || tags.containsKey("50")).isTrue()
    }

    @Test
    fun `parseApplicationInfo extracts PDOL correctly from response`() {
        val aid = byteArrayOf(0xA0.toByte(), 0x00, 0x00, 0x00, 0x03, 0x10, 0x10)
        val responseWithPdol = byteArrayOf(
            0x9F.toByte(), 0x38.toByte(), 0x06.toByte(),
            0x9F.toByte(), 0x66.toByte(), 0x04.toByte(),
            0x5F.toByte(), 0x2A.toByte(), 0x02.toByte()
        )

        val appInfo = EmvTagParser.parseApplicationInfo(aid, responseWithPdol)

        assertThat(appInfo.pdol).isNotNull()
        assertThat(appInfo.pdol?.length).isAtLeast(1)
    }
}