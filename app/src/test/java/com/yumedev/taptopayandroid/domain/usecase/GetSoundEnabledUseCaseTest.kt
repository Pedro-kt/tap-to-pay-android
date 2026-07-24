package com.yumedev.taptopayandroid.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.yumedev.taptopayandroid.domain.repository.PreferencesRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class GetSoundEnabledUseCaseTest {

    private lateinit var preferencesRepository: PreferencesRepository
    private lateinit var useCase: GetSoundEnabledUseCase

    @Before
    fun setup() {
        preferencesRepository = mockk()
        useCase = GetSoundEnabledUseCase(preferencesRepository)
    }

    @Test
    fun `invoke returns true when sound is enabled`() {
        every { preferencesRepository.isSoundEnabled() } returns true

        val result = useCase()

        assertThat(result).isTrue()
        verify(exactly = 1) { preferencesRepository.isSoundEnabled() }
    }

    @Test
    fun `invoke returns false when sound is disabled`() {
        every { preferencesRepository.isSoundEnabled() } returns false

        val result = useCase()

        assertThat(result).isFalse()
    }
}
