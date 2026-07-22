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
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial state is Waiting`() = runTest {
        viewModel = TapToPayViewModel(mockRepository)

        val state = viewModel.nfcState.value
        assertThat(state).isEqualTo(NfcState.Waiting)
    }

    @Test
    fun `startNewTransaction sets state to Waiting and stores amount`() = runTest {
        viewModel = TapToPayViewModel(mockRepository)
        val amount = "25.50"

        viewModel.startNewTransaction(amount)

        assertThat(viewModel.nfcState.value).isEqualTo(NfcState.Waiting)
        assertThat(viewModel.lastAmount.value).isEqualTo(amount)
    }

    @Test
    fun `clearStateOnly resets state to Waiting`() = runTest {
        viewModel = TapToPayViewModel(mockRepository)

        viewModel.clearStateOnly()

        assertThat(viewModel.nfcState.value).isEqualTo(NfcState.Waiting)
    }

    @Test
    fun `multiple consecutive transactions maintain amount correctly`() = runTest {
        viewModel = TapToPayViewModel(mockRepository)

        viewModel.startNewTransaction("10.00")
        assertThat(viewModel.nfcState.value).isEqualTo(NfcState.Waiting)
        assertThat(viewModel.lastAmount.value).isEqualTo("10.00")

        viewModel.startNewTransaction("20.00")
        assertThat(viewModel.nfcState.value).isEqualTo(NfcState.Waiting)
        assertThat(viewModel.lastAmount.value).isEqualTo("20.00")
    }

}