package com.yumedev.taptopayandroid.util

import android.util.Log

object SecureLogger {

    private val SENSITIVE_TAGS = setOf(
        "5A",    // Primary Account Number (PAN)
        "57",    // Track 2 Equivalent Data
        "5F20",  // Cardholder Name
        "5F24",  // Application Expiration Date
        "9F26",  // Application Cryptogram
        "9F27",  // Cryptogram Information Data
        "5F34",  // PAN Sequence Number
    )

    fun d(tag: String, message: () -> String) {
        Log.d(tag, message())
    }

    fun dSecure(tag: String, message: String) {
        Log.d(tag, filterSensitiveData(message))
    }

    fun i(tag: String, message: String) {
        Log.i(tag, filterSensitiveData(message))
    }

    fun w(tag: String, message: String, throwable: Throwable? = null) {
        val filtered = filterSensitiveData(message)
        if (throwable != null) {
            Log.w(tag, filtered, throwable)
        } else {
            Log.w(tag, filtered)
        }
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        val filtered = filterSensitiveData(message)
        if (throwable != null) {
            Log.e(tag, filtered, throwable)
        } else {
            Log.e(tag, filtered)
        }
    }

    private fun filterSensitiveData(message: String): String {
        var filtered = message

        // Pattern to match EMV TLV structure: TAG LENGTH VALUE
        // Example: "5A 08 1234567890123456" -> "5A 08 [REDACTED]"
        SENSITIVE_TAGS.forEach { tag ->
            // Match patterns like "5A 08 1234567890123456" or "5A08..."
            val patterns = listOf(
                Regex("$tag\\s+[0-9A-Fa-f]{2}\\s+[0-9A-Fa-f\\s]+"),
                Regex("$tag[0-9A-Fa-f]{2}[0-9A-Fa-f]+")
            )

            patterns.forEach { pattern ->
                filtered = pattern.replace(filtered) { matchResult ->
                    val matched = matchResult.value
                    // Keep tag and length, redact value
                    val parts = matched.split(Regex("\\s+"), limit = 3)
                    if (parts.size >= 3) {
                        "${parts[0]} ${parts[1]} [REDACTED]"
                    } else {
                        "$tag [REDACTED]"
                    }
                }
            }
        }

        // Also filter potential full PAN sequences (13-19 consecutive digits)
        filtered = Regex("\\b\\d{13,19}\\b").replace(filtered) {
            val matched = it.value
            "****${matched.takeLast(4)}"
        }

        return filtered
    }

    fun maskPan(pan: String): String {
        return if (pan.length >= 4) {
            "*".repeat(pan.length - 4) + pan.takeLast(4)
        } else {
            "****"
        }
    }

    fun logByteArraySize(tag: String, name: String, data: ByteArray) {
        Log.d(tag, "$name: ${data.size} bytes")
    }
}