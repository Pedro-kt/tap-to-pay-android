package com.yumedev.taptopayandroid.data.repository

import android.nfc.Tag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.yumedev.taptopayandroid.data.datasource.nfc.NfcCardReader
import com.yumedev.taptopayandroid.domain.model.*
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Instrumentation tests for NfcRepositoryImpl.
 * These tests run on an Android device/emulator to have access to Android framework classes.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class NfcRepositoryImplTest {

    private lateinit var mockNfcCardReader: NfcCardReader
    private lateinit var repository: NfcRepositoryImpl
    private lateinit var mockTag: Tag

    @Before
    fun setup() {
        mockNfcCardReader = mockk()
        mockTag = mockk(relaxed = true)
        repository = NfcRepositoryImpl(mockNfcCardReader)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun readCard_returnsSuccess_whenNfcCardReaderSucceeds() = runTest {
        // Given
        val expectedEmvCardData = createSampleEmvCardData()
        coEvery { mockNfcCardReader.readCard(mockTag, null) } returns Result.success(expectedEmvCardData)

        // When
        val result = repository.readCard(mockTag)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(expectedEmvCardData)
        coVerify(exactly = 1) { mockNfcCardReader.readCard(mockTag, null) }
    }

    @Test
    fun readCard_returnsSuccessWithAmount_whenNfcCardReaderSucceeds() = runTest {
        // Given
        val amountCents = 2550L // $25.50
        val expectedEmvCardData = createSampleEmvCardData(amountCents)
        coEvery { mockNfcCardReader.readCard(mockTag, amountCents) } returns Result.success(expectedEmvCardData)

        // When
        val result = repository.readCard(mockTag, amountCents)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(expectedEmvCardData)
        assertThat(result.getOrNull()?.transactionData?.amountAuthorised).isEqualTo(amountCents)
        coVerify(exactly = 1) { mockNfcCardReader.readCard(mockTag, amountCents) }
    }

    @Test
    fun readCard_returnsFailure_whenNfcCardReaderFails() = runTest {
        // Given
        val expectedException = Exception("Card read failed")
        coEvery { mockNfcCardReader.readCard(mockTag, null) } returns Result.failure(expectedException)

        // When
        val result = repository.readCard(mockTag)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(expectedException)
        coVerify(exactly = 1) { mockNfcCardReader.readCard(mockTag, null) }
    }

    @Test
    fun readCard_handlesIOException_fromNfcCardReader() = runTest {
        // Given
        val ioException = IOException("Communication error")
        coEvery { mockNfcCardReader.readCard(mockTag, null) } returns Result.failure(ioException)

        // When
        val result = repository.readCard(mockTag)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(IOException::class.java)
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Communication error")
    }

    @Test
    fun readCard_handlesNullAmount_correctly() = runTest {
        // Given
        val expectedEmvCardData = createSampleEmvCardData()
        coEvery { mockNfcCardReader.readCard(mockTag, null) } returns Result.success(expectedEmvCardData)

        // When
        val result = repository.readCard(mockTag, null)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(expectedEmvCardData)
        coVerify(exactly = 1) { mockNfcCardReader.readCard(mockTag, null) }
    }

    @Test
    fun readCard_handlesZeroAmount_correctly() = runTest {
        // Given
        val zeroAmount = 0L
        val expectedEmvCardData = createSampleEmvCardData(zeroAmount)
        coEvery { mockNfcCardReader.readCard(mockTag, zeroAmount) } returns Result.success(expectedEmvCardData)

        // When
        val result = repository.readCard(mockTag, zeroAmount)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.transactionData?.amountAuthorised).isEqualTo(zeroAmount)
    }

    @Test
    fun readCard_handlesLargeAmount_correctly() = runTest {
        // Given
        val largeAmount = 999999999L // $9,999,999.99
        val expectedEmvCardData = createSampleEmvCardData(largeAmount)
        coEvery { mockNfcCardReader.readCard(mockTag, largeAmount) } returns Result.success(expectedEmvCardData)

        // When
        val result = repository.readCard(mockTag, largeAmount)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.transactionData?.amountAuthorised).isEqualTo(largeAmount)
    }

    @Test
    fun readCard_propagatesDifferentErrorMessages_correctly() = runTest {
        // Given
        val errorMessages = listOf(
            "Card does not support ISO-DEP",
            "Failed to select PPSE",
            "No AID found in PPSE response",
            "Failed to select application",
            "Communication timeout"
        )

        errorMessages.forEach { errorMessage ->
            // Given
            coEvery { mockNfcCardReader.readCard(mockTag, null) } returns Result.failure(Exception(errorMessage))

            // When
            val result = repository.readCard(mockTag)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message).isEqualTo(errorMessage)
        }
    }

    @Test
    fun readCard_returnsCorrectCardType_forDifferentCards() = runTest {
        // Test Visa
        val visaCard = createSampleEmvCardData(cardType = CardType.VISA)
        coEvery { mockNfcCardReader.readCard(mockTag, null) } returns Result.success(visaCard)
        var result = repository.readCard(mockTag)
        assertThat(result.getOrNull()?.cardType).isEqualTo(CardType.VISA)

        // Test Mastercard
        val mastercardCard = createSampleEmvCardData(cardType = CardType.MASTERCARD)
        coEvery { mockNfcCardReader.readCard(mockTag, null) } returns Result.success(mastercardCard)
        result = repository.readCard(mockTag)
        assertThat(result.getOrNull()?.cardType).isEqualTo(CardType.MASTERCARD)

        // Test Unknown
        val unknownCard = createSampleEmvCardData(cardType = CardType.UNKNOWN)
        coEvery { mockNfcCardReader.readCard(mockTag, null) } returns Result.success(unknownCard)
        result = repository.readCard(mockTag)
        assertThat(result.getOrNull()?.cardType).isEqualTo(CardType.UNKNOWN)
    }

    @Test
    fun readCard_handlesMultipleConsecutiveReads_correctly() = runTest {
        // Given
        val firstCard = createSampleEmvCardData(pan = "4111111111111111")
        val secondCard = createSampleEmvCardData(pan = "5500000000000004")

        coEvery { mockNfcCardReader.readCard(mockTag, null) } returnsMany listOf(
            Result.success(firstCard),
            Result.success(secondCard)
        )

        // When
        val firstResult = repository.readCard(mockTag)
        val secondResult = repository.readCard(mockTag)

        // Then
        assertThat(firstResult.getOrNull()?.cardholderData?.pan).isEqualTo("4111111111111111")
        assertThat(secondResult.getOrNull()?.cardholderData?.pan).isEqualTo("5500000000000004")
        coVerify(exactly = 2) { mockNfcCardReader.readCard(mockTag, null) }
    }

    @Test
    fun readCard_includesApduCommands_inResult() = runTest {
        // Given
        val apduCommands = listOf(
            ApduCommand(1, "SELECT PPSE", "Select PPSE", "00 A4 04 00", "90 00", "90 00", "OK"),
            ApduCommand(2, "SELECT AID", "Select AID", "00 A4 04 00", "90 00", "90 00", "OK")
        )
        val cardDataWithApdu = createSampleEmvCardData(apduCommands = apduCommands)
        coEvery { mockNfcCardReader.readCard(mockTag, null) } returns Result.success(cardDataWithApdu)

        // When
        val result = repository.readCard(mockTag)

        // Then
        assertThat(result.getOrNull()?.apduCommands).hasSize(2)
        assertThat(result.getOrNull()?.apduCommands?.first()?.name).isEqualTo("SELECT PPSE")
    }

    // Helper function to create sample EMV card data for testing
    private fun createSampleEmvCardData(
        amountCents: Long? = null,
        pan: String = "4111111111111111",
        cardType: CardType = CardType.VISA,
        apduCommands: List<ApduCommand> = emptyList()
    ): EmvCardData {
        val applicationInfo = ApplicationInfo(
            aid = "A0000000031010",
            aidBytes = byteArrayOf(0xA0.toByte(), 0x00, 0x00, 0x00, 0x03, 0x10, 0x10),
            applicationLabel = "VISA CREDIT",
            priorityIndicator = 1,
            pdol = "9F3704",
            pdolDescription = "PDOL: [9F37:04]",
            cardType = cardType
        )

        val cardholderData = CardholderData(
            pan = pan,
            panLastFour = pan.takeLast(4),
            expirationDate = "261231",
            expirationDateDisplay = "12/26",
            cardholderName = "JOHN DOE",
            cardholderNameExtended = null,
            track2Equivalent = null,
            panSequenceNumber = 1
        )

        val transactionData = TransactionData(
            amountAuthorised = amountCents,
            amountAuthorisedDisplay = amountCents?.let { String.format("$%.2f", it / 100.0) },
            currencyCode = "0840",
            currencyName = "USD",
            transactionDate = "260722",
            transactionDateDisplay = "22/07/2026",
            transactionType = 0x00,
            transactionTypeDescription = "Purchase",
            atc = 42,
            unpredictableNumber = "12345678",
            timestamp = System.currentTimeMillis()
        )

        return EmvCardData(
            applicationInfo = applicationInfo,
            transactionData = transactionData,
            cardholderData = cardholderData,
            apduCommands = apduCommands,
            additionalTags = emptyMap()
        )
    }
}
