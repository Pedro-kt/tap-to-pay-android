package com.yumedev.taptopayandroid.domain.model

/**
 * Represents an APDU command exchange during EMV transaction
 * @param sequence Order in the command sequence (1, 2, 3...)
 * @param name Command name (e.g., "SELECT PPSE", "GET PROCESSING OPTIONS")
 * @param description Detailed explanation of what this command does
 * @param commandApdu C-APDU (Command APDU) as hex string
 * @param responseApdu R-APDU (Response APDU) as hex string
 * @param statusWord Status word from response (e.g., "90 00" for success)
 * @param statusDescription Human-readable status (e.g., "OK", "Error")
 * @param timestampMs Timestamp when command was executed
 */
data class ApduCommand(
    val sequence: Int,
    val name: String,
    val description: String? = null,
    val commandApdu: String,
    val responseApdu: String,
    val statusWord: String,
    val statusDescription: String,
    val timestampMs: Long = System.currentTimeMillis()
)
