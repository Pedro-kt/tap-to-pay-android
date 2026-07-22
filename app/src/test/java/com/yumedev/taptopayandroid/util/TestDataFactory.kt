package com.yumedev.taptopayandroid.util

import com.yumedev.taptopayandroid.domain.model.*

/**
 * Factory class for creating test data objects.
 * Provides builder methods with sensible defaults for testing.
 */
object TestDataFactory {

    /**
     * Creates a sample ApplicationInfo for testing
     */
    fun createApplicationInfo(
        aid: String = "A0000000031010",
        aidBytes: ByteArray = byteArrayOf(0xA0.toByte(), 0x00, 0x00, 0x00, 0x03, 0x10, 0x10),
        applicationLabel: String = "VISA CREDIT",
        priorityIndicator: Int = 1,
        pdol: String = "9F3704",
        pdolDescription: String = "PDOL: [9F37:04]",
        cardType: CardType = CardType.VISA
    ): ApplicationInfo {
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

    /**
     * Creates a sample CardholderData for testing
     */
    fun createCardholderData(
        pan: String = "4111111111111111",
        panLastFour: String = pan.takeLast(4),
        expirationDate: String = "261231",
        expirationDateDisplay: String = "12/26",
        cardholderName: String? = "JOHN DOE",
        cardholderNameExtended: String? = null,
        track2Equivalent: String? = null,
        panSequenceNumber: Int? = 1
    ): CardholderData {
        return CardholderData(
            pan = pan,
            panLastFour = panLastFour,
            expirationDate = expirationDate,
            expirationDateDisplay = expirationDateDisplay,
            cardholderName = cardholderName,
            cardholderNameExtended = cardholderNameExtended,
            track2Equivalent = track2Equivalent,
            panSequenceNumber = panSequenceNumber
        )
    }

    /**
     * Creates a sample TransactionData for testing
     */
    fun createTransactionData(
        amountAuthorised: Long? = null,
        amountAuthorisedDisplay: String? = amountAuthorised?.let { String.format("$%.2f", it / 100.0) },
        currencyCode: String = "0840",
        currencyName: String = "USD",
        transactionDate: String = "260722",
        transactionDateDisplay: String = "22/07/2026",
        transactionType: Int = 0x00,
        transactionTypeDescription: String = "Purchase",
        atc: Int = 42,
        unpredictableNumber: String = "12345678",
        timestamp: Long = System.currentTimeMillis()
    ): TransactionData {
        return TransactionData(
            amountAuthorised = amountAuthorised,
            amountAuthorisedDisplay = amountAuthorisedDisplay,
            currencyCode = currencyCode,
            currencyName = currencyName,
            transactionDate = transactionDate,
            transactionDateDisplay = transactionDateDisplay,
            transactionType = transactionType,
            transactionTypeDescription = transactionTypeDescription,
            atc = atc,
            unpredictableNumber = unpredictableNumber,
            timestamp = timestamp
        )
    }

    /**
     * Creates a sample ApduCommand for testing
     */
    fun createApduCommand(
        sequence: Int = 1,
        name: String = "SELECT PPSE",
        description: String = "Select Proximity Payment System Environment",
        commandApdu: String = "00 A4 04 00 0E 32 50 41 59 2E 53 59 53 2E 44 44 46 30 31 00",
        responseApdu: String = "6F 23 84 0E 32 50 41 59 2E 53 59 53 2E 44 44 46 30 31 90 00",
        statusWord: String = "90 00",
        statusDescription: String = "OK"
    ): ApduCommand {
        return ApduCommand(
            sequence = sequence,
            name = name,
            description = description,
            commandApdu = commandApdu,
            responseApdu = responseApdu,
            statusWord = statusWord,
            statusDescription = statusDescription
        )
    }

    /**
     * Creates a sample EmvTag for testing
     */
    fun createEmvTag(
        tag: String = "4F",
        tagName: String = "Application Identifier (AID)",
        length: Int = 7,
        value: String = "A0000000031010",
        valueDecoded: String = "A0000000031010",
        description: String = "Payment application identifier"
    ): EmvTag {
        return EmvTag(
            tag = tag,
            tagName = tagName,
            length = length,
            value = value,
            valueDecoded = valueDecoded,
            description = description
        )
    }

    /**
     * Creates a complete EmvCardData for testing
     */
    fun createEmvCardData(
        applicationInfo: ApplicationInfo = createApplicationInfo(),
        transactionData: TransactionData = createTransactionData(),
        cardholderData: CardholderData = createCardholderData(),
        apduCommands: List<ApduCommand> = emptyList(),
        additionalTags: Map<String, EmvTag> = emptyMap()
    ): EmvCardData {
        return EmvCardData(
            applicationInfo = applicationInfo,
            transactionData = transactionData,
            cardholderData = cardholderData,
            apduCommands = apduCommands,
            additionalTags = additionalTags
        )
    }

    // Convenience builders for specific card types

    fun createVisaCard(
        pan: String = "4111111111111111",
        amountCents: Long? = null
    ): EmvCardData {
        return createEmvCardData(
            applicationInfo = createApplicationInfo(
                aid = "A0000000031010",
                aidBytes = byteArrayOf(0xA0.toByte(), 0x00, 0x00, 0x00, 0x03, 0x10, 0x10),
                applicationLabel = "VISA CREDIT",
                cardType = CardType.VISA
            ),
            cardholderData = createCardholderData(pan = pan),
            transactionData = createTransactionData(amountAuthorised = amountCents)
        )
    }

    fun createMastercardCard(
        pan: String = "5500000000000004",
        amountCents: Long? = null
    ): EmvCardData {
        return createEmvCardData(
            applicationInfo = createApplicationInfo(
                aid = "A0000000041010",
                aidBytes = byteArrayOf(0xA0.toByte(), 0x00, 0x00, 0x00, 0x04, 0x10, 0x10),
                applicationLabel = "MASTERCARD",
                cardType = CardType.MASTERCARD
            ),
            cardholderData = createCardholderData(pan = pan),
            transactionData = createTransactionData(amountAuthorised = amountCents)
        )
    }

    fun createAmericanExpressCard(
        pan: String = "340000000000009",
        amountCents: Long? = null
    ): EmvCardData {
        return createEmvCardData(
            applicationInfo = createApplicationInfo(
                aid = "A000000025010801",
                aidBytes = byteArrayOf(
                    0xA0.toByte(), 0x00, 0x00, 0x00, 0x25,
                    0x01, 0x08, 0x01
                ),
                applicationLabel = "AMERICAN EXPRESS",
                cardType = CardType.AMEX
            ),
            cardholderData = createCardholderData(pan = pan),
            transactionData = createTransactionData(amountAuthorised = amountCents)
        )
    }

    fun createUnknownCard(
        pan: String = "6011000000000004",
        amountCents: Long? = null
    ): EmvCardData {
        return createEmvCardData(
            applicationInfo = createApplicationInfo(
                aid = "A0000000000000",
                aidBytes = byteArrayOf(0xA0.toByte(), 0x00, 0x00, 0x00, 0x00, 0x00, 0x00),
                applicationLabel = "UNKNOWN",
                cardType = CardType.UNKNOWN
            ),
            cardholderData = createCardholderData(pan = pan),
            transactionData = createTransactionData(amountAuthorised = amountCents)
        )
    }

    /**
     * Creates EMV card data with full APDU command history
     */
    fun createEmvCardDataWithApduCommands(): EmvCardData {
        val apduCommands = listOf(
            createApduCommand(
                sequence = 1,
                name = "SELECT PPSE",
                description = "Select Proximity Payment System Environment"
            ),
            createApduCommand(
                sequence = 2,
                name = "SELECT AID",
                description = "Select payment application",
                commandApdu = "00 A4 04 00 07 A0 00 00 00 03 10 10 00"
            ),
            createApduCommand(
                sequence = 3,
                name = "GET PROCESSING OPTIONS",
                description = "Request card processing options",
                commandApdu = "80 A8 00 00 02 83 00 00"
            ),
            createApduCommand(
                sequence = 4,
                name = "READ RECORD - SFI 2 #1",
                description = "Read application data from SFI 2, record 1",
                commandApdu = "00 B2 01 14 00"
            )
        )

        return createEmvCardData(apduCommands = apduCommands)
    }

    /**
     * Creates EMV card data with additional tags
     */
    fun createEmvCardDataWithAdditionalTags(): EmvCardData {
        val additionalTags = mapOf(
            "9F33" to createEmvTag(
                tag = "9F33",
                tagName = "Terminal Capabilities",
                length = 3,
                value = "E0F8C8",
                valueDecoded = "Terminal Capabilities: E0F8C8",
                description = "Terminal capabilities for EMV processing"
            ),
            "9F35" to createEmvTag(
                tag = "9F35",
                tagName = "Terminal Type",
                length = 1,
                value = "22",
                valueDecoded = "Attended terminal, online capable",
                description = "Terminal type designation"
            )
        )

        return createEmvCardData(additionalTags = additionalTags)
    }
}
