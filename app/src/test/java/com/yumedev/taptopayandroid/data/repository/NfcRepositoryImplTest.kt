package com.yumedev.taptopayandroid.data.repository

import android.nfc.Tag
import com.google.common.truth.Truth.assertThat
import com.yumedev.taptopayandroid.data.datasource.nfc.NfcCardReader
import com.yumedev.taptopayandroid.domain.model.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class NfcRepositoryImplTest {

    private lateinit var mockCardReader: NfcCardReader
    private lateinit var repository: NfcRepositoryImpl
    private lateinit var mockTag: Tag

    @Before
    fun setup() {
        mockCardReader = mockk()
        repository = NfcRepositoryImpl(mockCardReader)
        mockTag = mockk(relaxed = true)
    }

    @Test
    fun `readCard delegates to NfcCardReader and returns success`() = runTest {
        val mockCardData = createMockEmvCardData()
        coEvery { mockCardReader.readCard(mockTag, any()) } returns Result.success(mockCardData)

        val result = repository.readCard(mockTag, amountCents = 1000L)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(mockCardData)
        coVerify(exactly = 1) { mockCardReader.readCard(mockTag, 1000L) }
    }

    @Test
    fun `readCard delegates to NfcCardReader and returns failure`() = runTest {
        val exception = Exception("Card read failed")
        coEvery { mockCardReader.readCard(mockTag, any()) } returns Result.failure(exception)

        val result = repository.readCard(mockTag, amountCents = 2000L)

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("Card read failed")
        coVerify(exactly = 1) { mockCardReader.readCard(mockTag, 2000L) }
    }

    @Test
    fun `readCard passes null amount correctly`() = runTest {
        val mockCardData = createMockEmvCardData()
        coEvery { mockCardReader.readCard(mockTag, null) } returns Result.success(mockCardData)

        val result = repository.readCard(mockTag, amountCents = null)

        assertThat(result.isSuccess).isTrue()
        coVerify(exactly = 1) { mockCardReader.readCard(mockTag, null) }
    }

    @Test
    fun `readCard respects repository contract with Result wrapper`() = runTest {
        val mockCardData = createMockEmvCardData()
        coEvery { mockCardReader.readCard(any(), any()) } returns Result.success(mockCardData)

        val result = repository.readCard(mockTag)

        assertThat(result).isInstanceOf(Result::class.java)
        assertThat(result.isSuccess || result.isFailure).isTrue()
    }

    private fun createMockEmvCardData(): EmvCardData {
        val aidBytes = byteArrayOf(0xA0.toByte(), 0x00, 0x00, 0x00, 0x03, 0x10, 0x10)
        return EmvCardData(
            applicationInfo = ApplicationInfo(
                aid = aidBytes.joinToString("") { "%02X".format(it) },
                aidBytes = aidBytes,
                applicationLabel = "VISA",
                cardType = CardType.VISA,
                pdol = null
            ),
            transactionData = TransactionData(
                amountAuthorised = 1000L,
                currencyCode = "0840",
                transactionDate = "250122",
                atc = 1,
                unpredictableNumber = "12345678"
            ),
            cardholderData = CardholderData(
                pan = "1234567890123456",
                panLastFour = "3456",
                expirationDate = "2612",
                expirationDateDisplay = "12/26",
                cardholderName = "TEST/USER"
            ),
            apduCommands = emptyList(),
            additionalTags = emptyMap()
        )
    }
}
