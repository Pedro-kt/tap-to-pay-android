package com.yumedev.taptopayandroid.util

import com.yumedev.taptopayandroid.domain.model.AipDecoded

object AipDecoder {

    fun decode(hexValue: String): AipDecoded? {
        val cleanHex = hexValue.replace(" ", "").trim()

        if (cleanHex.length != 4) {
            return null
        }

        val bytes = try {
            cleanHex.chunked(2).map { it.toInt(16).toByte() }
        } catch (e: Exception) {
            return null
        }

        if (bytes.size != 2) {
            return null
        }

        val byte1 = bytes[0].toInt() and 0xFF
        val byte2 = bytes[1].toInt() and 0xFF

        return AipDecoded(
            rawValue = hexValue,
            sdaSupported = (byte1 and 0x40) != 0,
            ddaSupported = (byte1 and 0x20) != 0,
            cardholderVerificationSupported = (byte1 and 0x10) != 0,
            terminalRiskManagementRequired = (byte1 and 0x08) != 0,
            issuerAuthenticationSupported = (byte1 and 0x04) != 0,
            onDeviceCardholderVerificationSupported = (byte1 and 0x02) != 0,
            cdaSupported = (byte1 and 0x01) != 0,
            crlSupported = (byte2 and 0x01) != 0
        )
    }
}
