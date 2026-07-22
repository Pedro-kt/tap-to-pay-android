package com.yumedev.taptopayandroid.data.datasource.nfc

import org.junit.Test
import com.google.common.truth.Truth.assertThat

class NfcCardReaderTest {

    private val reader = NfcCardReader()

    @Test
    fun `isSuccessResponse returns true for valid 90 00 status`() {
        val response = byteArrayOf(0x6F.toByte(), 0x10.toByte(), 0x90.toByte(), 0x00.toByte())

        val isSuccess = reader.javaClass.getDeclaredMethod("isSuccessResponse", ByteArray::class.java).apply {
            isAccessible = true
        }.invoke(reader, response) as Boolean

        assertThat(isSuccess).isTrue()
    }

    @Test
    fun `isSuccessResponse returns false for error status 6A 82`() {
        val response = byteArrayOf(0x6A.toByte(), 0x82.toByte())

        val isSuccess = reader.javaClass.getDeclaredMethod("isSuccessResponse", ByteArray::class.java).apply {
            isAccessible = true
        }.invoke(reader, response) as Boolean

        assertThat(isSuccess).isFalse()
    }

    @Test
    fun `getStatusDescription returns OK for 90 00`() {
        val response = byteArrayOf(0x90.toByte(), 0x00.toByte())

        val description = reader.javaClass.getDeclaredMethod("getStatusDescription", ByteArray::class.java).apply {
            isAccessible = true
        }.invoke(reader, response) as String

        assertThat(description).isEqualTo("OK")
    }

    @Test
    fun `getStatusDescription returns File not found for 6A 82`() {
        val response = byteArrayOf(0x6A.toByte(), 0x82.toByte())

        val description = reader.javaClass.getDeclaredMethod("getStatusDescription", ByteArray::class.java).apply {
            isAccessible = true
        }.invoke(reader, response) as String

        assertThat(description).isEqualTo("File not found")
    }

    @Test
    fun `extractAID finds valid AID tag 4F in response`() {
        val aidBytes = byteArrayOf(0xA0.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x03.toByte())
        val response = byteArrayOf(
            0x6F.toByte(), 0x10.toByte(),
            0x4F.toByte(), 0x05.toByte()
        ) + aidBytes + byteArrayOf(0x90.toByte(), 0x00.toByte())

        val extractedAid = reader.javaClass.getDeclaredMethod("extractAID", ByteArray::class.java).apply {
            isAccessible = true
        }.invoke(reader, response) as ByteArray?

        assertThat(extractedAid).isNotNull()
        assertThat(extractedAid).isEqualTo(aidBytes)
    }

    @Test
    fun `extractAID returns null when no AID tag present`() {
        val response = byteArrayOf(0x6F.toByte(), 0x05.toByte(), 0x50.toByte(), 0x03.toByte(), 0x56.toByte(), 0x49.toByte(), 0x53.toByte())

        val extractedAid = reader.javaClass.getDeclaredMethod("extractAID", ByteArray::class.java).apply {
            isAccessible = true
        }.invoke(reader, response) as ByteArray?

        assertThat(extractedAid).isNull()
    }

    @Test
    fun `removeStatusWord removes last 2 bytes correctly`() {
        val response = byteArrayOf(0x6F.toByte(), 0x10.toByte(), 0x84.toByte(), 0x05.toByte(), 0x90.toByte(), 0x00.toByte())
        val expected = byteArrayOf(0x6F.toByte(), 0x10.toByte(), 0x84.toByte(), 0x05.toByte())

        val cleaned = reader.javaClass.getDeclaredMethod("removeStatusWord", ByteArray::class.java).apply {
            isAccessible = true
        }.invoke(reader, response) as ByteArray

        assertThat(cleaned).isEqualTo(expected)
    }

    @Test
    fun `buildSelectCommand creates valid APDU for AID`() {
        val aid = byteArrayOf(0xA0.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x03.toByte())

        val command = reader.javaClass.getDeclaredMethod("buildSelectCommand", ByteArray::class.java).apply {
            isAccessible = true
        }.invoke(reader, aid) as ByteArray

        assertThat(command[0]).isEqualTo(0x00.toByte())
        assertThat(command[1]).isEqualTo(0xA4.toByte())
        assertThat(command[4]).isEqualTo(aid.size.toByte())
    }
}
