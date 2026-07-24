package com.yumedev.taptopayandroid.domain.usecase

import com.yumedev.taptopayandroid.domain.model.DetailLevel
import com.yumedev.taptopayandroid.domain.repository.PreferencesRepository
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class UpdateDetailLevelUseCaseTest {

    private lateinit var preferencesRepository: PreferencesRepository
    private lateinit var useCase: UpdateDetailLevelUseCase

    @Before
    fun setup() {
        preferencesRepository = mockk(relaxed = true)
        useCase = UpdateDetailLevelUseCase(preferencesRepository)
    }

    @Test
    fun `invoke sets SIMPLE detail level in repository`() {
        useCase(DetailLevel.SIMPLE)

        verify(exactly = 1) { preferencesRepository.setDetailLevel(DetailLevel.SIMPLE) }
    }

    @Test
    fun `invoke sets DETAILED detail level in repository`() {
        useCase(DetailLevel.DETAILED)

        verify(exactly = 1) { preferencesRepository.setDetailLevel(DetailLevel.DETAILED) }
    }
}
