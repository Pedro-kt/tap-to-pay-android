package com.yumedev.taptopayandroid.data.parser

import com.yumedev.taptopayandroid.domain.model.*

object EmvTagParser {

    fun parseTag(tag: String, value: ByteArray): EmvTag {
        val tagInfo = EMV_TAG_DEFINITIONS[tag] ?: TagDefinition(tag, "Unknown Tag", "")

        val decoded = when (tag) {
            "5A", "57" -> decodeBcdPan(value)
            "5F24" -> decodeExpirationDate(value)
            "9F02" -> decodeAmount(value)
            "5F2A" -> decodeCurrencyCode(value)
            "9A" -> decodeTransactionDate(value)
            "9C" -> decodeTransactionType(value)
            "9F36" -> decodeAtc(value)
            "50", "5F20", "9F0B" -> decodeAscii(value)
            else -> null
        }

        return EmvTag(
            tag = tag,
            tagName = tagInfo.name,
            length = value.size,
            value = value.toHexString(),
            valueDecoded = decoded,
            description = tagInfo.description
        )
    }

    fun parseApplicationInfo(
        aidBytes: ByteArray,
        selectAidResponse: ByteArray
    ): ApplicationInfo {
        val aid = aidBytes.toHexString()
        val applicationLabel = findTag(selectAidResponse, "50")?.let { decodeAscii(it) }
        val priorityIndicator = findTag(selectAidResponse, "87")?.firstOrNull()?.toInt()
        val pdolBytes = findTag(selectAidResponse, "9F38")
        val pdol = pdolBytes?.toHexString()
        val pdolDescription = pdolBytes?.let { parsePdolDescription(it) }

        val cardType = determineCardType(aid, applicationLabel)

        return ApplicationInfo(
            aid = aid,
            aidBytes = aidBytes,
            applicationLabel = applicationLabel,
            priorityIndicator = priorityIndicator,
            pdol = pdol,
            pdolDescription = pdolDescription,
            cardType = cardType
        )
    }

    fun parseTransactionData(
        records: List<ByteArray>,
        amountCents: Long? = null
    ): TransactionData {
        val allData = records.flatMap { it.toList() }.toByteArray()

        val amountBytes = findTag(allData, "9F02")
        val amount = amountBytes?.let { decodeBcdAmount(it) } ?: amountCents
        val amountDisplay = amount?.let { formatAmount(it) }

        val currencyBytes = findTag(allData, "5F2A")
        val currencyCode = currencyBytes?.toHexString()
        val currencyName = currencyCode?.let { mapCurrencyCode(it) }

        val transactionDateBytes = findTag(allData, "9A")
        val transactionDate = transactionDateBytes?.toHexString()
        val transactionDateDisplay = transactionDate?.let { formatTransactionDate(it) }

        val transactionTypeBytes = findTag(allData, "9C")
        val transactionType = transactionTypeBytes?.firstOrNull()?.toInt()
        val transactionTypeDesc = transactionType?.let { mapTransactionType(it) }

        val atcBytes = findTag(allData, "9F36")
        val atc = atcBytes?.let { (it[0].toInt() and 0xFF shl 8) or (it[1].toInt() and 0xFF) }

        val unpredictableNumberBytes = findTag(allData, "9F37")
        val unpredictableNumber = unpredictableNumberBytes?.toHexString()

        return TransactionData(
            amountAuthorised = amount,
            amountAuthorisedDisplay = amountDisplay,
            currencyCode = currencyCode,
            currencyName = currencyName,
            transactionDate = transactionDate,
            transactionDateDisplay = transactionDateDisplay,
            transactionType = transactionType,
            transactionTypeDescription = transactionTypeDesc,
            atc = atc,
            unpredictableNumber = unpredictableNumber
        )
    }

    fun parseCardholderData(records: List<ByteArray>): CardholderData {
        val allData = records.flatMap { it.toList() }.toByteArray()

        val panBytes = findTag(allData, "5A")
        val pan = panBytes?.let { decodeBcdPan(it) } ?: "0000000000000000"
        val panLastFour = pan.takeLast(4).filter { it.isDigit() }

        val expirationBytes = findTag(allData, "5F24") ?: byteArrayOf()
        val expirationDate = expirationBytes.toHexString()
        val expirationDisplay = if (expirationBytes.size >= 3) {
            formatExpirationDate(expirationBytes)
        } else "??/??"

        val cardholderName = findTag(allData, "5F20")?.let { decodeAscii(it) }
        val cardholderNameExtended = findTag(allData, "9F0B")?.let { decodeAscii(it) }
        val track2 = findTag(allData, "57")?.toHexString()
        val panSeqNum = findTag(allData, "5F34")?.firstOrNull()?.toInt()

        return CardholderData(
            pan = pan,
            panLastFour = panLastFour,
            expirationDate = expirationDate,
            expirationDateDisplay = expirationDisplay,
            cardholderName = cardholderName,
            cardholderNameExtended = cardholderNameExtended,
            track2Equivalent = track2,
            panSequenceNumber = panSeqNum
        )
    }

    fun findTag(data: ByteArray, targetTag: String): ByteArray? {
        val tagBytes = targetTag.hexToByteArray()
        var i = 0

        while (i < data.size) {
            val tagSize = if ((data[i].toInt() and 0x1F) == 0x1F) 2 else 1
            if (i + tagSize > data.size) break

            val currentTag = data.sliceArray(i until i + tagSize)
            i += tagSize

            if (i >= data.size) break
            val lengthByte = data[i].toInt() and 0xFF
            i++

            val length = if (lengthByte and 0x80 != 0) {
                val numLengthBytes = lengthByte and 0x7F
                if (i + numLengthBytes > data.size) break

                var actualLength = 0
                for (j in 0 until numLengthBytes) {
                    actualLength = (actualLength shl 8) or (data[i++].toInt() and 0xFF)
                }
                actualLength
            } else {
                lengthByte
            }

            if (currentTag.contentEquals(tagBytes)) {
                if (i + length > data.size) return null
                return data.sliceArray(i until i + length)
            }

            i += length
        }

        return null
    }

    private fun decodeBcdPan(value: ByteArray): String {
        return value.joinToString("") { byte ->
            val high = (byte.toInt() shr 4) and 0x0F
            val low = byte.toInt() and 0x0F
            "${if (high <= 9) high else ""}${if (low <= 9) low else ""}"
        }.filter { it.isDigit() }
    }

    private fun decodeExpirationDate(value: ByteArray): String {
        if (value.size < 3) return "??/??"
        return formatExpirationDate(value)
    }

    private fun formatExpirationDate(value: ByteArray): String {
        val year = String.format("%02d", value[0].toInt() and 0xFF)
        val month = String.format("%02d", value[1].toInt() and 0xFF)
        return "$month/$year"
    }

    private fun decodeAmount(value: ByteArray): String {
        val amount = decodeBcdAmount(value)
        return formatAmount(amount)
    }

    private fun decodeBcdAmount(value: ByteArray): Long {
        var amount = 0L
        for (byte in value) {
            val high = (byte.toInt() shr 4) and 0x0F
            val low = byte.toInt() and 0x0F
            amount = amount * 100 + high * 10 + low
        }
        return amount
    }

    private fun formatAmount(cents: Long): String {
        val dollars = cents / 100
        val centsRemainder = cents % 100
        return "$${dollars}.%02d".format(centsRemainder)
    }

    private fun decodeCurrencyCode(value: ByteArray): String {
        if (value.size < 2) return "Unknown"
        val code = ((value[0].toInt() and 0xFF) shl 8) or (value[1].toInt() and 0xFF)
        return "%04X".format(code)
    }

    private fun mapCurrencyCode(hexCode: String): String {
        val code = hexCode.toIntOrNull(16) ?: return "Unknown"
        return CURRENCY_CODES[code] ?: "Unknown ($hexCode)"
    }

    private fun decodeTransactionDate(value: ByteArray): String {
        if (value.size < 3) return "Unknown"
        val year = String.format("%02d", value[0].toInt() and 0xFF)
        val month = String.format("%02d", value[1].toInt() and 0xFF)
        val day = String.format("%02d", value[2].toInt() and 0xFF)
        return "20$year-$month-$day"
    }

    private fun formatTransactionDate(yymmdd: String): String {
        if (yymmdd.length != 6) return yymmdd
        val year = yymmdd.substring(0, 2)
        val month = yymmdd.substring(2, 4)
        val day = yymmdd.substring(4, 6)
        return "20$year-$month-$day"
    }

    private fun decodeTransactionType(value: ByteArray): String {
        val type = value.firstOrNull()?.toInt() ?: return "Unknown"
        return mapTransactionType(type)
    }

    private fun mapTransactionType(type: Int): String {
        return TRANSACTION_TYPES[type] ?: "Unknown (0x%02X)".format(type)
    }

    private fun decodeAtc(value: ByteArray): String {
        if (value.size < 2) return "0"
        val atc = ((value[0].toInt() and 0xFF) shl 8) or (value[1].toInt() and 0xFF)
        return "ATC: $atc"
    }

    private fun decodeAscii(value: ByteArray): String {
        return value.toString(Charsets.US_ASCII).trim()
    }

    private fun parsePdolDescription(pdolBytes: ByteArray): String {
        return "PDOL with ${pdolBytes.size} bytes"
    }

    private fun determineCardType(aid: String, label: String?): CardType {
        when {
            aid.startsWith("A0000000031010") -> return CardType.VISA
            aid.startsWith("A000000004") -> return CardType.MASTERCARD
            aid.startsWith("A000000025") -> return CardType.AMEX
            aid.startsWith("A0000001523010") -> return CardType.DISCOVER
            aid.startsWith("A0000000043060") -> return CardType.MAESTRO
        }

        label?.uppercase()?.let {
            when {
                it.contains("VISA") -> return CardType.VISA
                it.contains("MASTERCARD") || it.contains("MC") -> return CardType.MASTERCARD
                it.contains("AMEX") || it.contains("AMERICAN EXPRESS") -> return CardType.AMEX
                it.contains("DISCOVER") -> return CardType.DISCOVER
                it.contains("MAESTRO") -> return CardType.MAESTRO
            }
        }

        return CardType.UNKNOWN
    }

    private fun ByteArray.toHexString(): String {
        return joinToString("") { "%02X".format(it) }
    }

    private fun String.hexToByteArray(): ByteArray {
        val clean = this.replace(" ", "")
        return ByteArray(clean.length / 2) { i ->
            clean.substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
    }

    private data class TagDefinition(
        val tag: String,
        val name: String,
        val description: String
    )

    private val EMV_TAG_DEFINITIONS = mapOf(
        "4F" to TagDefinition("4F", "Application Identifier (AID)", "Identifies the payment application"),
        "50" to TagDefinition("50", "Application Label", "Human-readable application name"),
        "57" to TagDefinition("57", "Track 2 Equivalent Data", "Magnetic stripe data"),
        "5A" to TagDefinition("5A", "Primary Account Number (PAN)", "Card number"),
        "5F20" to TagDefinition("5F20", "Cardholder Name", "Name on card"),
        "5F24" to TagDefinition("5F24", "Application Expiration Date", "Card expiration (YYMMDD)"),
        "5F2A" to TagDefinition("5F2A", "Transaction Currency Code", "ISO 4217 currency code"),
        "5F34" to TagDefinition("5F34", "PAN Sequence Number", "Distinguishes cards with same PAN"),
        "82" to TagDefinition("82", "Application Interchange Profile", "Card capabilities"),
        "87" to TagDefinition("87", "Application Priority Indicator", "Application selection priority"),
        "8E" to TagDefinition("8E", "CVM List", "Cardholder Verification Method list"),
        "94" to TagDefinition("94", "Application File Locator", "Indicates data file locations"),
        "9A" to TagDefinition("9A", "Transaction Date", "Date of transaction (YYMMDD)"),
        "9C" to TagDefinition("9C", "Transaction Type", "Type of transaction"),
        "9F02" to TagDefinition("9F02", "Amount, Authorised", "Transaction amount"),
        "9F0B" to TagDefinition("9F0B", "Cardholder Name Extended", "Extended cardholder name"),
        "9F10" to TagDefinition("9F10", "Issuer Application Data", "Data from card issuer"),
        "9F26" to TagDefinition("9F26", "Application Cryptogram", "Transaction cryptogram"),
        "9F27" to TagDefinition("9F27", "Cryptogram Information Data", "Cryptogram type"),
        "9F33" to TagDefinition("9F33", "Terminal Capabilities", "Terminal feature support"),
        "9F34" to TagDefinition("9F34", "CVM Results", "Cardholder verification results"),
        "9F35" to TagDefinition("9F35", "Terminal Type", "Type of terminal"),
        "9F36" to TagDefinition("9F36", "Application Transaction Counter", "Number of transactions on card"),
        "9F37" to TagDefinition("9F37", "Unpredictable Number", "Random number for security"),
        "9F38" to TagDefinition("9F38", "PDOL", "Processing options data requirements")
    )

    private val CURRENCY_CODES = mapOf(
        840 to "USD (ISO 4217)",
        978 to "EUR (ISO 4217)",
        826 to "GBP (ISO 4217)",
        484 to "MXN (ISO 4217)",
        124 to "CAD (ISO 4217)",
        392 to "JPY (ISO 4217)"
    )

    private val TRANSACTION_TYPES = mapOf(
        0x00 to "Purchase of goods/services",
        0x01 to "Cash withdrawal",
        0x09 to "Purchase with cashback",
        0x20 to "Refund/Return"
    )
}
