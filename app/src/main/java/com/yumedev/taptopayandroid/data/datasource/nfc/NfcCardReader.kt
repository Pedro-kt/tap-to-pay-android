package com.yumedev.taptopayandroid.data.datasource.nfc

import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log
import com.yumedev.taptopayandroid.data.parser.EmvTagParser
import com.yumedev.taptopayandroid.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class NfcCardReader {

    companion object {
        private const val TAG = "NfcCardReader"
    }

    suspend fun readCard(tag: Tag, amountCents: Long? = null): Result<EmvCardData> = withContext(Dispatchers.IO) {
        var isoDep: IsoDep? = null
        val apduCommands = mutableListOf<ApduCommand>()
        val allRecords = mutableListOf<ByteArray>()
        var commandSequence = 1

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

            apduCommands.add(ApduCommand(
                sequence = commandSequence++,
                name = "SELECT PPSE",
                description = "Select Proximity Payment System Environment",
                commandApdu = ppseCommand.toHexString(),
                responseApdu = ppseResponse.toHexString(),
                statusWord = getStatusWord(ppseResponse),
                statusDescription = getStatusDescription(ppseResponse)
            ))

            Log.d(TAG, "PPSE Response: ${ppseResponse.toHexString()}")

            if (!isSuccessResponse(ppseResponse)) {
                return@withContext Result.failure(Exception("Failed to select PPSE"))
            }

            // Step 2: Extract AID from PPSE response
            val aidBytes = extractAID(ppseResponse) ?: return@withContext Result.failure(Exception("No AID found in PPSE response"))
            Log.d(TAG, "Found AID: ${aidBytes.toHexString()}")

            // Step 3: Select the payment application using AID
            val selectAidCommand = buildSelectCommand(aidBytes)
            val aidResponse = isoDep.transceive(selectAidCommand)

            apduCommands.add(ApduCommand(
                sequence = commandSequence++,
                name = "SELECT AID",
                description = "Select payment application",
                commandApdu = selectAidCommand.toHexString(),
                responseApdu = aidResponse.toHexString(),
                statusWord = getStatusWord(aidResponse),
                statusDescription = getStatusDescription(aidResponse)
            ))

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

            apduCommands.add(ApduCommand(
                sequence = commandSequence++,
                name = "GET PROCESSING OPTIONS",
                description = "Request card processing options",
                commandApdu = gpoCommand.toHexString(),
                responseApdu = gpoResponse.toHexString(),
                statusWord = getStatusWord(gpoResponse),
                statusDescription = getStatusDescription(gpoResponse)
            ))

            Log.d(TAG, "GPO Response: ${gpoResponse.toHexString()}")

            if (isSuccessResponse(gpoResponse)) {
                // Remove status word bytes (last 2 bytes) before adding to records
                allRecords.add(removeStatusWord(gpoResponse))

                // Parse AFL (Application File Locator) from GPO response
                val afl = findTag(gpoResponse, 0x94.toByte())
                if (afl != null) {
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

                                apduCommands.add(ApduCommand(
                                    sequence = commandSequence++,
                                    name = "READ RECORD - SFI $sfi #$record",
                                    description = "Read application data from SFI $sfi, record $record",
                                    commandApdu = readRecordCommand.toHexString(),
                                    responseApdu = recordResponse.toHexString(),
                                    statusWord = getStatusWord(recordResponse),
                                    statusDescription = getStatusDescription(recordResponse)
                                ))

                                Log.d(TAG, "Record SFI=$sfi Rec=$record: ${recordResponse.toHexString()}")

                                if (isSuccessResponse(recordResponse)) {
                                    // Remove status word bytes before adding
                                    allRecords.add(removeStatusWord(recordResponse))
                                }
                            } catch (e: Exception) {
                                Log.d(TAG, "Failed to read SFI=$sfi Rec=$record: ${e.message}")
                            }
                        }
                        i += 4
                    }
                }
            } else {
                Log.w(TAG, "GPO failed with status: ${gpoResponse.toHexString()}")
            }

            // Fallback: Manual record reading if needed
            if (allRecords.size <= 1) {
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
                                apduCommands.add(ApduCommand(
                                    sequence = commandSequence++,
                                    name = "READ RECORD - SFI $sfi #$record",
                                    description = "Manual read from SFI $sfi, record $record",
                                    commandApdu = readRecordCommand.toHexString(),
                                    responseApdu = recordResponse.toHexString(),
                                    statusWord = getStatusWord(recordResponse),
                                    statusDescription = getStatusDescription(recordResponse)
                                ))

                                // Remove status word bytes before adding
                                allRecords.add(removeStatusWord(recordResponse))
                                Log.d(TAG, "Record SFI=$sfi Rec=$record (${recordResponse.size} bytes)")
                            } else {
                                if (record == 1) {
                                    Log.d(TAG, "SFI=$sfi not available")
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
                }
            }

            // Parse all data using EmvTagParser
            val applicationInfo = EmvTagParser.parseApplicationInfo(aidBytes, aidResponse)
            val transactionData = EmvTagParser.parseTransactionData(allRecords, amountCents)
            val cardholderData = EmvTagParser.parseCardholderData(allRecords)

            // Extract all tags from all records
            val allData = allRecords.flatMap { it.toList() }.toByteArray()
            val additionalTags = EmvTagParser.extractAllTags(allData)

            val emvCardData = EmvCardData(
                applicationInfo = applicationInfo,
                transactionData = transactionData,
                cardholderData = cardholderData,
                apduCommands = apduCommands,
                additionalTags = additionalTags
            )

            Log.d(TAG, "Card read successfully")
            Result.success(emvCardData)

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

    private fun getStatusWord(response: ByteArray): String {
        if (response.size < 2) return "N/A"
        val sw1 = response[response.size - 2]
        val sw2 = response[response.size - 1]
        return String.format("%02X %02X", sw1, sw2)
    }

    private fun getStatusDescription(response: ByteArray): String {
        if (response.size < 2) return "Invalid response"
        val sw1 = response[response.size - 2].toInt() and 0xFF
        val sw2 = response[response.size - 1].toInt() and 0xFF

        return when {
            sw1 == 0x90 && sw2 == 0x00 -> "OK"
            sw1 == 0x6A && sw2 == 0x82 -> "File not found"
            sw1 == 0x6A && sw2 == 0x81 -> "Function not supported"
            sw1 == 0x69 && sw2 == 0x85 -> "Conditions not satisfied"
            sw1 == 0x6D && sw2 == 0x00 -> "Instruction not supported"
            sw1 == 0x6E && sw2 == 0x00 -> "Class not supported"
            else -> String.format("Error: %02X %02X", sw1, sw2)
        }
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

    private fun removeStatusWord(response: ByteArray): ByteArray {
        // Remove last 2 bytes (status word 90 00)
        return if (response.size >= 2) {
            response.copyOfRange(0, response.size - 2)
        } else {
            response
        }
    }

    private fun ByteArray.toHexString(): String {
        return joinToString(" ") { byte -> "%02X".format(byte) }
    }
}