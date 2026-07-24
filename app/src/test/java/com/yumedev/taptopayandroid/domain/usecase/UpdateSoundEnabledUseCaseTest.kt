package com.yumedev.taptopayandroid.domain.usecase

import com.yumedev.taptopayandroid.domain.repository.PreferencesRepository
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class UpdateSoundEnabledUseCaseTest {

    private lateinit var preferencesRepository: PreferencesRepository
    private lateinit var useCase: UpdateSoundEnabledUseCase

    @Before
    fun setup() {
        preferencesRepository = mockk(relaxed = true)
        useCase = UpdateSoundEnabledUseCase(preferencesRepository)
    }

    @Test
    fun `invoke enables sound in repository`() {
        useCase(true)

        verify(exactly = 1) { preferencesRepository.setSoundEnabled(true) }
    }

    @Test
    fun `invoke disables sound in repository`() {
        useCase(false)

        verify(exactly = 1) { preferencesRepository.setSoundEnabled(false) }
    }
}
