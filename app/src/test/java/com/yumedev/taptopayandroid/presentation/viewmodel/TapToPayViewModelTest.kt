package com.yumedev.taptopayandroid.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.yumedev.taptopayandroid.domain.model.*
import com.yumedev.taptopayandroid.domain.repository.NfcRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TapToPayViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockRepository: NfcRepository
    private lateinit var viewModel: TapToPayViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
        viewModel = TapToPayViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial state is Waiting`() = runTest {
        val state = viewModel.nfcState.value
        assertThat(state).isEqualTo(NfcState.Waiting)
    }

    @Test
    fun `initial lastEmvCardData is null`() = runTest {
        assertThat(viewModel.lastEmvCardData.value).isNull()
    }

    @Test
    fun `initial lastAmount is default value`() = runTest {
        assertThat(viewModel.lastAmount.value).isEqualTo("0.00")
    }

    @Test
    fun `startNewTransaction sets state to Waiting and stores amount`() = runTest {
        val amount = "25.50"

        viewModel.startNewTransaction(amount)

        assertThat(viewModel.nfcState.value).isEqualTo(NfcState.Waiting)
        assertThat(viewModel.lastAmount.value).isEqualTo(amount)
    }

    @Test
    fun `startNewTransaction with empty amount stores it correctly`() = runTest {
        val amount = ""

        viewModel.startNewTransaction(amount)

        assertThat(viewModel.nfcState.value).isEqualTo(NfcState.Waiting)
        assertThat(viewModel.lastAmount.value).isEqualTo("")
    }

    @Test
    fun `startNewTransaction with large amount stores it correctly`() = runTest {
        val amount = "9999999.99"

        viewModel.startNewTransaction(amount)

        assertThat(viewModel.lastAmount.value).isEqualTo(amount)
    }

    @Test
    fun `clearStateOnly resets state to Waiting`() = runTest {
        viewModel.clearStateOnly()

        assertThat(viewModel.nfcState.value).isEqualTo(NfcState.Waiting)
    }

    @Test
    fun `clearStateOnly does not clear lastAmount`() = runTest {
        val amount = "50.00"
        viewModel.startNewTransaction(amount)

        viewModel.clearStateOnly()

        assertThat(viewModel.lastAmount.value).isEqualTo(amount)
        assertThat(viewModel.nfcState.value).isEqualTo(NfcState.Waiting)
    }

    @Test
    fun `multiple consecutive transactions maintain amount correctly`() = runTest {
        viewModel.startNewTransaction("10.00")
        assertThat(viewModel.nfcState.value).isEqualTo(NfcState.Waiting)
        assertThat(viewModel.lastAmount.value).isEqualTo("10.00")

        viewModel.startNewTransaction("20.00")
        assertThat(viewModel.nfcState.value).isEqualTo(NfcState.Waiting)
        assertThat(viewModel.lastAmount.value).isEqualTo("20.00")
    }

    @Test
    fun `startNewTransaction overwrites previous amount`() = runTest {
        viewModel.startNewTransaction("100.00")
        assertThat(viewModel.lastAmount.value).isEqualTo("100.00")

        viewModel.startNewTransaction("200.00")
        assertThat(viewModel.lastAmount.value).isEqualTo("200.00")
    }

    @Test
    fun `clearStateOnly maintains amount after multiple transactions`() = runTest {
        viewModel.startNewTransaction("75.00")
        viewModel.clearStateOnly()

        assertThat(viewModel.lastAmount.value).isEqualTo("75.00")
        assertThat(viewModel.nfcState.value).isEqualTo(NfcState.Waiting)
    }

    @Test
    fun `state transitions correctly after multiple operations`() = runTest {
        // Initial state
        assertThat(viewModel.nfcState.value).isEqualTo(NfcState.Waiting)

        // Start transaction
        viewModel.startNewTransaction("50.00")
        assertThat(viewModel.nfcState.value).isEqualTo(NfcState.Waiting)

        // Clear state
        viewModel.clearStateOnly()
        assertThat(viewModel.nfcState.value).isEqualTo(NfcState.Waiting)
    }

    @Test
    fun `lastAmount persists across clearStateOnly calls`() = runTest {
        val amount = "123.45"
        viewModel.startNewTransaction(amount)

        // Call clearStateOnly multiple times
        viewModel.clearStateOnly()
        viewModel.clearStateOnly()
        viewModel.clearStateOnly()

        assertThat(viewModel.lastAmount.value).isEqualTo(amount)
    }

    @Test
    fun `startNewTransaction with zero amount`() = runTest {
        viewModel.startNewTransaction("0.00")

        assertThat(viewModel.lastAmount.value).isEqualTo("0.00")
        assertThat(viewModel.nfcState.value).isEqualTo(NfcState.Waiting)
    }

    @Test
    fun `startNewTransaction with decimal amounts`() = runTest {
        val amounts = listOf("0.01", "1.50", "99.99", "1000.00")

        amounts.forEach { amount ->
            viewModel.startNewTransaction(amount)
            assertThat(viewModel.lastAmount.value).isEqualTo(amount)
        }
    }

    @Test
    fun `clearStateOnly is idempotent`() = runTest {
        viewModel.startNewTransaction("100.00")

        viewModel.clearStateOnly()
        val stateAfterFirst = viewModel.nfcState.value

        viewModel.clearStateOnly()
        val stateAfterSecond = viewModel.nfcState.value

        assertThat(stateAfterFirst).isEqualTo(NfcState.Waiting)
        assertThat(stateAfterSecond).isEqualTo(NfcState.Waiting)
        assertThat(viewModel.lastAmount.value).isEqualTo("100.00")
    }

    @Test
    fun `lastEmvCardData starts as null`() = runTest {
        assertThat(viewModel.lastEmvCardData.value).isNull()
    }

    @Test
    fun `state flows are cold and maintain current value`() = runTest {
        // Verify nfcState maintains value
        viewModel.startNewTransaction("25.00")
        val state1 = viewModel.nfcState.value
        val state2 = viewModel.nfcState.value
        assertThat(state1).isEqualTo(state2)

        // Verify lastAmount maintains value
        val amount1 = viewModel.lastAmount.value
        val amount2 = viewModel.lastAmount.value
        assertThat(amount1).isEqualTo(amount2)
        assertThat(amount1).isEqualTo("25.00")
    }

    @Test
    fun `starting new transaction resets state to Waiting`() = runTest {
        // Start first transaction
        viewModel.startNewTransaction("10.00")
        assertThat(viewModel.nfcState.value).isEqualTo(NfcState.Waiting)

        // Start another transaction
        viewModel.startNewTransaction("20.00")
        assertThat(viewModel.nfcState.value).isEqualTo(NfcState.Waiting)
    }

    @Test
    fun `amount format is preserved as provided`() = runTest {
        // Test various formats
        viewModel.startNewTransaction("10")
        assertThat(viewModel.lastAmount.value).isEqualTo("10")

        viewModel.startNewTransaction("10.0")
        assertThat(viewModel.lastAmount.value).isEqualTo("10.0")

        viewModel.startNewTransaction("10.00")
        assertThat(viewModel.lastAmount.value).isEqualTo("10.00")
    }

    @Test
    fun `special characters in amount are preserved`() = runTest {
        viewModel.startNewTransaction("$100.00")
        assertThat(viewModel.lastAmount.value).isEqualTo("$100.00")
    }

    @Test
    fun `very large amounts are handled correctly`() = runTest {
        val largeAmount = "999999999.99"
        viewModel.startNewTransaction(largeAmount)

        assertThat(viewModel.lastAmount.value).isEqualTo(largeAmount)
        assertThat(viewModel.nfcState.value).isEqualTo(NfcState.Waiting)
    }

    @Test
    fun `ViewModel initialization completes successfully`() = runTest {
        // Verify that creating a new ViewModel doesn't throw
        val newViewModel = TapToPayViewModel(mockRepository)

        assertThat(newViewModel.nfcState.value).isEqualTo(NfcState.Waiting)
        assertThat(newViewModel.lastAmount.value).isEqualTo("0.00")
        assertThat(newViewModel.lastEmvCardData.value).isNull()
    }

    @Test
    fun `multiple ViewModels maintain independent state`() = runTest {
        val viewModel2 = TapToPayViewModel(mockRepository)

        viewModel.startNewTransaction("100.00")
        viewModel2.startNewTransaction("200.00")

        assertThat(viewModel.lastAmount.value).isEqualTo("100.00")
        assertThat(viewModel2.lastAmount.value).isEqualTo("200.00")
    }

    @Test
    fun `clearStateOnly does not affect lastEmvCardData when null`() = runTest {
        assertThat(viewModel.lastEmvCardData.value).isNull()

        viewModel.clearStateOnly()

        assertThat(viewModel.lastEmvCardData.value).isNull()
    }

    @Test
    fun `state transitions are synchronous for public methods`() = runTest {
        viewModel.startNewTransaction("50.00")
        // Should be immediately updated
        assertThat(viewModel.nfcState.value).isEqualTo(NfcState.Waiting)
        assertThat(viewModel.lastAmount.value).isEqualTo("50.00")

        viewModel.clearStateOnly()
        // Should be immediately updated
        assertThat(viewModel.nfcState.value).isEqualTo(NfcState.Waiting)
    }

    @Test
    fun `negative amounts are accepted if provided`() = runTest {
        viewModel.startNewTransaction("-10.00")
        assertThat(viewModel.lastAmount.value).isEqualTo("-10.00")
    }

    @Test
    fun `whitespace in amount is preserved`() = runTest {
        viewModel.startNewTransaction(" 100.00 ")
        assertThat(viewModel.lastAmount.value).isEqualTo(" 100.00 ")
    }

    // Helper function to create sample EMV card data for testing
    private fun createSampleEmvCardData(
        pan: String = "4111111111111111",
        cardType: CardType = CardType.VISA
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
            amountAuthorised = null,
            amountAuthorisedDisplay = null,
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
            apduCommands = emptyList(),
            additionalTags = emptyMap()
        )
    }
}
