package com.yumedev.taptopayandroid.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.yumedev.taptopayandroid.domain.model.DetailLevel
import com.yumedev.taptopayandroid.domain.usecase.GetDetailLevelUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CardDetailViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getDetailLevelUseCase: GetDetailLevelUseCase
    private lateinit var viewModel: CardDetailViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getDetailLevelUseCase = mockk()
        every { getDetailLevelUseCase() } returns DetailLevel.DETAILED
        viewModel = CardDetailViewModel(getDetailLevelUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial detail level is DETAILED from use case`() {
        every { getDetailLevelUseCase() } returns DetailLevel.DETAILED

        val viewModel = CardDetailViewModel(getDetailLevelUseCase)

        assertThat(viewModel.detailLevel.value).isEqualTo(DetailLevel.DETAILED)
        verify(atLeast = 1) { getDetailLevelUseCase() }
    }

    @Test
    fun `initial detail level is SIMPLE from use case`() {
        every { getDetailLevelUseCase() } returns DetailLevel.SIMPLE

        val viewModel = CardDetailViewModel(getDetailLevelUseCase)

        assertThat(viewModel.detailLevel.value).isEqualTo(DetailLevel.SIMPLE)
        verify(atLeast = 1) { getDetailLevelUseCase() }
    }

    @Test
    fun `detailLevel StateFlow exposes value correctly`() {
        every { getDetailLevelUseCase() } returns DetailLevel.SIMPLE

        val viewModel = CardDetailViewModel(getDetailLevelUseCase)
        val detailLevel = viewModel.detailLevel.value

        assertThat(detailLevel).isEqualTo(DetailLevel.SIMPLE)
    }
}
