package com.yumedev.taptopayandroid.data.datasource.nfc

import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log
import com.yumedev.taptopayandroid.domain.model.CardInfo
import com.yumedev.taptopayandroid.domain.model.CardType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class NfcCardReader {

    companion object {
        private const val TAG = "NfcCardReader"
    }

    suspend fun readCard(tag: Tag): Result<CardInfo> = withContext(Dispatchers.IO) {
        var isoDep: IsoDep? = null
        try {
            isoDep = IsoDep.get(tag)
            if (isoDep == null) {
                return@withContext Result.failure(Exception("Card does not support ISO-DEP"))
            }

            isoDep.connect()
            isoDep.timeout = 5000 // 5 seconds timeout
            Log.d(TAG, "Connected to card")

            // Step 1: Select PPSE (Proximity Payment System Environment)
            val ppseCommand = byteArrayOf(
                0x00.toByte(), // CLA
                0xA4.toByte(), // INS (SELECT)
                0x04.toByte(), // P1
                0x00.toByte(), // P2
                0x0E.toByte(), // Lc (length of data)
                // PPSE AID
                0x32.toByte(), 0x50.toByte(), 0x41.toByte(), 0x59.toByte(),
                0x2E.toByte(), 0x53.toByte(), 0x59.toByte(), 0x53.toByte(),
                0x2E.toByte(), 0x44.toByte(), 0x44.toByte(), 0x46.toByte(),
                0x30.toByte(), 0x31.toByte(),
                0x00.toByte()  // Le (expected length)
            )

            val ppseResponse = isoDep.transceive(ppseCommand)
            Log.d(TAG, "PPSE Response: ${ppseResponse.toHexString()}")

            if (!isSuccessResponse(ppseResponse)) {
                return@withContext Result.failure(Exception("Failed to select PPSE"))
            }

            // Step 2: Extract AID from PPSE response
            val aid = extractAID(ppseResponse) ?: return@withContext Result.failure(Exception("No AID found in PPSE response"))
            Log.d(TAG, "Found AID: ${aid.toHexString()}")

            // Step 3: Select the payment application using AID
            val selectAidCommand = buildSelectCommand(aid)
            val aidResponse = isoDep.transceive(selectAidCommand)
            Log.d(TAG, "AID Response: ${aidResponse.toHexString()}")

            if (!isSuccessResponse(aidResponse)) {
                return@withContext Result.failure(Exception("Failed to select application"))
            }

            // Step 4: Get Processing Options (GPO)
            // Parse PDOL from AID response to build proper GPO
            val pdol = findTag(aidResponse, 0x9F.toByte(), 0x38.toByte())
            val gpoCommand = if (pdol != null && pdol.isNotEmpty()) {
                Log.d(TAG, "Found PDOL: ${pdol.toHexString()}")
                // Calculate total PDOL data length needed
                val pdolLength = parsePdolLength(pdol)
                Log.d(TAG, "PDOL requires $pdolLength bytes")

                // Build GPO with PDOL data (fill with zeros for simplicity)
                val pdolData = ByteArray(pdolLength)

                // Build command: 80 A8 00 00 Lc 83 Ld [Data] Le
                // Lc = 2 + pdolData.size (for tag 83 + length byte + data)
                val lc = 2 + pdolData.size

                Log.d(TAG, "Building GPO: Lc=$lc, PDOL Data Length=${pdolData.size}")

                byteArrayOf(
                    0x80.toByte(), // CLA
                    0xA8.toByte(), // INS
                    0x00.toByte(), // P1
                    0x00.toByte(), // P2
                    lc.toByte()    // Lc = length of command data
                ) + byteArrayOf(
                    0x83.toByte(),          // PDOL tag
                    pdolData.size.toByte()  // PDOL length
                ) + pdolData + byteArrayOf(0x00.toByte()) // Le
            } else {
                Log.d(TAG, "No PDOL found, using default GPO")
                // Default GPO
                byteArrayOf(
                    0x80.toByte(), 0xA8.toByte(), 0x00.toByte(), 0x00.toByte(),
                    0x02.toByte(), 0x83.toByte(), 0x00.toByte(), 0x00.toByte()
                )
            }

            Log.d(TAG, "GPO Command: ${gpoCommand.toHexString()}")

            val gpoResponse = isoDep.transceive(gpoCommand)
            Log.d(TAG, "GPO Response: ${gpoResponse.toHexString()}")

            var cardNumber = "**** **** **** ****"
            var expirationDate = "**/**"

            if (isSuccessResponse(gpoResponse)) {
                cardNumber = extractCardNumber(gpoResponse)
                expirationDate = extractExpirationDate(gpoResponse)

                // Parse AFL (Application File Locator) from GPO response
                val afl = findTag(gpoResponse, 0x94.toByte())
                if (afl != null && cardNumber == "**** **** **** ****") {
                    // Read records from AFL
                    var i = 0
                    while (i + 3 < afl.size) {
                        val sfi = (afl[i].toInt() shr 3) and 0x1F
                        val firstRecord = afl[i + 1].toInt() and 0xFF
                        val lastRecord = afl[i + 2].toInt() and 0xFF

                        for (record in firstRecord..lastRecord) {
                            try {
                                val readRecordCommand = byteArrayOf(
                                    0x00.toByte(), 0xB2.toByte(),
                                    record.toByte(),
                                    ((sfi shl 3) or 0x04).toByte(),
                                    0x00.toByte()
                                )
                                val recordResponse = isoDep.transceive(readRecordCommand)
                                Log.d(TAG, "Record SFI=$sfi Rec=$record: ${recordResponse.toHexString()}")

                                if (isSuccessResponse(recordResponse)) {
                                    val tempNumber = extractCardNumber(recordResponse)
                                    val tempDate = extractExpirationDate(recordResponse)

                                    if (tempNumber != "**** **** **** ****") {
                                        cardNumber = tempNumber
                                        expirationDate = tempDate
                                        break
                                    }
                                }
                            } catch (e: Exception) {
                                Log.d(TAG, "Failed to read SFI=$sfi Rec=$record: ${e.message}")
                            }
                        }
                        if (cardNumber != "**** **** **** ****") break
                        i += 4
                    }
                }
            } else {
                Log.w(TAG, "GPO failed with status: ${gpoResponse.toHexString()}")
            }

            // If still not found, try reading records manually
            // Try SFI 2 first (most common for Visa/Mastercard), then others
            if (cardNumber == "**** **** **** ****") {
                Log.d(TAG, "Attempting manual record read...")
                val sfiOrder = listOf(2, 1, 3, 4) // Try SFI 2 first

                for (sfi in sfiOrder) {
                    for (record in 1..3) { // Usually only records 1-3 have useful data
                        try {
                            val readRecordCommand = byteArrayOf(
                                0x00.toByte(), 0xB2.toByte(),
                                record.toByte(),
                                ((sfi shl 3) or 0x04).toByte(),
                                0x00.toByte()
                            )
                            val recordResponse = isoDep.transceive(readRecordCommand)

                            if (isSuccessResponse(recordResponse)) {
                                Log.d(TAG, "Record SFI=$sfi Rec=$record (${recordResponse.size} bytes)")
                                val tempNumber = extractCardNumber(recordResponse)
                                val tempDate = extractExpirationDate(recordResponse)

                                if (tempNumber != "**** **** **** ****") {
                                    Log.d(TAG, "Found PAN in SFI=$sfi Rec=$record")
                                    cardNumber = tempNumber
                                    expirationDate = tempDate
                                    break
                                }
                            } else {
                                // Don't log every failure to reduce noise
                                if (record == 1) { // Only log first record failure per SFI
                                    val sw = recordResponse.takeLast(2)
                                    Log.d(TAG, "SFI=$sfi not available: ${sw.toByteArray().toHexString()}")
                                }
                                break // If record 1 fails, skip rest of this SFI
                            }
                        } catch (e: Exception) {
                            if (record == 1) {
                                Log.d(TAG, "SFI=$sfi error: ${e.message}")
                            }
                            break // If record 1 errors, skip rest of this SFI
                        }
                    }
                    if (cardNumber != "**** **** **** ****") break
                }
            }

            // Determine card type from AID or card name in response
            var cardType = determineCardType(cardNumber)

            // If card number seems invalid (encrypted), try to determine type from AID response
            if (cardNumber.startsWith("0") || cardNumber.contains("****")) {
                // Look for card name in AID response (tag 0x50 - Application Label)
                val appLabel = findTag(aidResponse, 0x50.toByte())
                if (appLabel != null) {
                    val label = String(appLabel, Charsets.UTF_8)
                    Log.d(TAG, "Application Label: $label")
                    cardType = when {
                        label.contains("VISA", ignoreCase = true) -> CardType.VISA
                        label.contains("MASTERCARD", ignoreCase = true) -> CardType.MASTERCARD
                        label.contains("AMEX", ignoreCase = true) -> CardType.AMEX
                        else -> cardType
                    }
                }

                // For encrypted cards, show masked number with last 4 digits if available
                if (cardNumber != "**** **** **** ****" && !cardNumber.startsWith("****")) {
                    val digits = cardNumber.replace(" ", "")
                    if (digits.length >= 4) {
                        cardNumber = "**** **** **** ${digits.takeLast(4)}"
                    }
                }
            }

            val cardInfo = CardInfo(
                cardNumber = cardNumber,
                expirationDate = expirationDate,
                cardType = cardType,
                rawData = aidResponse.toHexString()
            )

            Log.d(TAG, "Card read successfully: $cardInfo")
            Result.success(cardInfo)

        } catch (e: IOException) {
            Log.e(TAG, "IO Error reading card", e)
            Result.failure(Exception("Communication error with card: ${e.message}"))
        } catch (e: Exception) {
            Log.e(TAG, "Error reading card", e)
            Result.failure(Exception("Error reading card: ${e.message}"))
        } finally {
            try {
                isoDep?.close()
                Log.d(TAG, "Connection closed")
            } catch (e: IOException) {
                Log.e(TAG, "Error closing connection", e)
            }
        }
    }

    private fun extractAID(response: ByteArray): ByteArray? {
        // Look for tag 0x4F (AID - Application Identifier)
        val aidTag = 0x4F.toByte()
        var i = 0
        while (i < response.size - 1) {
            if (response[i] == aidTag) {
                val length = response[i + 1].toInt() and 0xFF
                if (i + 2 + length <= response.size) {
                    return response.copyOfRange(i + 2, i + 2 + length)
                }
            }
            i++
        }
        return null
    }

    private fun buildSelectCommand(aid: ByteArray): ByteArray {
        return byteArrayOf(
            0x00.toByte(), // CLA
            0xA4.toByte(), // INS (SELECT)
            0x04.toByte(), // P1
            0x00.toByte(), // P2
            aid.size.toByte() // Lc
        ) + aid + byteArrayOf(0x00.toByte()) // Le
    }

    private fun parsePdolLength(pdol: ByteArray): Int {
        var totalLength = 0
        var i = 0

        while (i < pdol.size) {
            val currentByte = pdol[i].toInt() and 0xFF

            // Check if this is a 2-byte tag
            val is2ByteTag = (currentByte == 0x9F || currentByte == 0x5F ||
                             currentByte == 0xBF || currentByte == 0xDF)

            if (is2ByteTag) {
                // Skip 2 bytes for tag
                i += 2
            } else {
                // Skip 1 byte for tag
                i += 1
            }

            // Read length byte
            if (i < pdol.size) {
                val length = pdol[i].toInt() and 0xFF
                totalLength += length
                Log.d(TAG, "PDOL tag requires $length bytes")
                i++
            }
        }

        return totalLength
    }

    private fun isSuccessResponse(response: ByteArray): Boolean {
        if (response.size < 2) return false
        val sw1 = response[response.size - 2].toInt() and 0xFF
        val sw2 = response[response.size - 1].toInt() and 0xFF
        return sw1 == 0x90 && sw2 == 0x00
    }

    private fun extractCardNumber(response: ByteArray): String {
        // ATENTION: EMV data is complex and can vary between cards. This function attempts to extract the PAN (Primary Account Number) from common tags.
        // This is a simplified extraction and may not work for all cards. In production, consider using a proper EMV parsing library.

        // Try tag 0x5A (Application Primary Account Number) first
        var cardNumber = findTag(response, 0x5A.toByte())
        if (cardNumber != null) {
            Log.d(TAG, "Found tag 0x5A (PAN): ${cardNumber.toHexString()}")
            return formatCardNumber(cardNumber)
        }

        // Try tag 0x57 (Track 2 Equivalent Data)
        cardNumber = findTag(response, 0x57.toByte())
        if (cardNumber != null) {
            Log.d(TAG, "Found tag 0x57 (Track 2): ${cardNumber.toHexString()}")
            // Track 2 format: PAN + separator (D or =) + expiry + service code
            return formatCardNumberFromTrack2(cardNumber)
        }

        Log.d(TAG, "No PAN found in response")
        // Fallback: return masked number
        return "**** **** **** ****"
    }

    private fun findTag(data: ByteArray, tag: Byte): ByteArray? {
        var i = 0
        while (i < data.size - 1) {
            if (data[i] == tag) {
                val length = data[i + 1].toInt() and 0xFF
                if (i + 2 + length <= data.size) {
                    return data.copyOfRange(i + 2, i + 2 + length)
                }
            }
            i++
        }
        return null
    }

    private fun findTag(data: ByteArray, tag1: Byte, tag2: Byte): ByteArray? {
        var i = 0
        while (i < data.size - 2) {
            if (data[i] == tag1 && data[i + 1] == tag2) {
                val length = data[i + 2].toInt() and 0xFF
                if (i + 3 + length <= data.size) {
                    return data.copyOfRange(i + 3, i + 3 + length)
                }
            }
            i++
        }
        return null
    }

    private fun formatCardNumberFromTrack2(track2Data: ByteArray): String {
        val digits = StringBuilder()
        for (byte in track2Data) {
            val high = (byte.toInt() shr 4) and 0x0F
            val low = byte.toInt() and 0x0F

            if (high == 0xD || high == 0xF) break // Separator or padding
            if (high in 0..9) digits.append(high)

            if (low == 0xD || low == 0xF) break
            if (low in 0..9) digits.append(low)
        }

        val cardNumber = digits.toString()
        if (cardNumber.length >= 13) {
            return cardNumber.take(16).chunked(4).joinToString(" ")
        }
        return "**** **** **** ****"
    }

    private fun formatCardNumber(panBytes: ByteArray): String {
        val digits = StringBuilder()
        for (byte in panBytes) {
            val high = (byte.toInt() shr 4) and 0x0F
            val low = byte.toInt() and 0x0F
            if (high in 0..9) digits.append(high)
            if (low in 0..9) digits.append(low)
        }

        val cardNumber = digits.toString()
        if (cardNumber.length >= 13) {
            return cardNumber.chunked(4).joinToString(" ").take(19)
        }
        return "**** **** **** ${cardNumber.takeLast(4)}"
    }

    private fun extractExpirationDate(response: ByteArray): String {
        // Look for expiration date tag 0x5F24 (Application Expiration Date)
        // Format: YYMMDD
        val expiryTag1 = 0x5F.toByte()
        val expiryTag2 = 0x24.toByte()

        var i = 0
        while (i < response.size - 2) {
            if (response[i] == expiryTag1 && response[i + 1] == expiryTag2) {
                val length = response[i + 2].toInt() and 0xFF
                if (i + 3 + length <= response.size && length == 3) {
                    val dateBytes = response.copyOfRange(i + 3, i + 3 + length)
                    val year = String.format("%02X", dateBytes[0])
                    val month = String.format("%02X", dateBytes[1])
                    return "$month/$year"
                }
            }
            i++
        }

        // Try to extract from Track 2 data if available
        val track2Data = findTag(response, 0x57.toByte())
        if (track2Data != null) {
            return extractExpiryFromTrack2(track2Data)
        }

        return "**/**"
    }

    private fun extractExpiryFromTrack2(track2Data: ByteArray): String {
        val digits = StringBuilder()
        var foundSeparator = false

        for (byte in track2Data) {
            val high = (byte.toInt() shr 4) and 0x0F
            val low = byte.toInt() and 0x0F

            if (!foundSeparator) {
                if (high == 0xD) {
                    foundSeparator = true
                }
                continue
            }

            // After separator, next 4 digits are YYMM
            if (high in 0..9) digits.append(high)
            if (low in 0..9) digits.append(low)

            if (digits.length >= 4) {
                val year = digits.substring(0, 2)
                val month = digits.substring(2, 4)
                return "$month/$year"
            }
        }

        return "**/**"
    }

    private fun determineCardType(cardNumber: String): CardType {
        val digits = cardNumber.replace(" ", "").replace("*", "")
        if (digits.isEmpty()) return CardType.UNKNOWN

        return when {
            // This is a simplified determination based on common card number prefixes
            digits.startsWith("4") -> CardType.VISA
            digits.startsWith("5") -> CardType.MASTERCARD
            digits.startsWith("34") || digits.startsWith("37") -> CardType.AMEX
            digits.startsWith("6011") || digits.startsWith("65") -> CardType.DISCOVER
            else -> CardType.UNKNOWN
        }
    }

    private fun ByteArray.toHexString(): String {
        return joinToString(" ") { byte -> "%02X".format(byte) }
    }
}