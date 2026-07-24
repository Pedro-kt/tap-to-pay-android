package com.yumedev.taptopayandroid.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.yumedev.taptopayandroid.domain.repository.PreferencesRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class GetThemeModeUseCaseTest {

    private lateinit var preferencesRepository: PreferencesRepository
    private lateinit var useCase: GetThemeModeUseCase

    @Before
    fun setup() {
        preferencesRepository = mockk()
        useCase = GetThemeModeUseCase(preferencesRepository)
    }

    @Test
    fun `invoke returns light theme from repository`() {
        every { preferencesRepository.getThemeMode() } returns PreferencesRepository.THEME_LIGHT

        val result = useCase()

        assertThat(result).isEqualTo(PreferencesRepository.THEME_LIGHT)
        verify(exactly = 1) { preferencesRepository.getThemeMode() }
    }

    @Test
    fun `invoke returns dark theme from repository`() {
        every { preferencesRepository.getThemeMode() } returns PreferencesRepository.THEME_DARK

        val result = useCase()

        assertThat(result).isEqualTo(PreferencesRepository.THEME_DARK)
    }

    @Test
    fun `invoke returns system theme from repository`() {
        every { preferencesRepository.getThemeMode() } returns PreferencesRepository.THEME_SYSTEM

        val result = useCase()

        assertThat(result).isEqualTo(PreferencesRepository.THEME_SYSTEM)
    }
}
