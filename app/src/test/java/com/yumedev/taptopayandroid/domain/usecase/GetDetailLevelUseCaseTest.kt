package com.yumedev.taptopayandroid.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.yumedev.taptopayandroid.domain.model.DetailLevel
import com.yumedev.taptopayandroid.domain.repository.PreferencesRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class GetDetailLevelUseCaseTest {

    private lateinit var preferencesRepository: PreferencesRepository
    private lateinit var useCase: GetDetailLevelUseCase

    @Before
    fun setup() {
        preferencesRepository = mockk()
        useCase = GetDetailLevelUseCase(preferencesRepository)
    }

    @Test
    fun `invoke returns SIMPLE detail level from repository`() {
        every { preferencesRepository.getDetailLevel() } returns DetailLevel.SIMPLE

        val result = useCase()

        assertThat(result).isEqualTo(DetailLevel.SIMPLE)
        verify(exactly = 1) { preferencesRepository.getDetailLevel() }
    }

    @Test
    fun `invoke returns DETAILED detail level from repository`() {
        every { preferencesRepository.getDetailLevel() } returns DetailLevel.DETAILED

        val result = useCase()

        assertThat(result).isEqualTo(DetailLevel.DETAILED)
    }
}
