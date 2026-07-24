package com.yumedev.taptopayandroid.domain.usecase

import com.yumedev.taptopayandroid.domain.repository.PreferencesRepository
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class UpdateThemeModeUseCaseTest {

    private lateinit var preferencesRepository: PreferencesRepository
    private lateinit var useCase: UpdateThemeModeUseCase

    @Before
    fun setup() {
        preferencesRepository = mockk(relaxed = true)
        useCase = UpdateThemeModeUseCase(preferencesRepository)
    }

    @Test
    fun `invoke sets light theme in repository`() {
        useCase(PreferencesRepository.THEME_LIGHT)

        verify(exactly = 1) { preferencesRepository.setThemeMode(PreferencesRepository.THEME_LIGHT) }
    }

    @Test
    fun `invoke sets dark theme in repository`() {
        useCase(PreferencesRepository.THEME_DARK)

        verify(exactly = 1) { preferencesRepository.setThemeMode(PreferencesRepository.THEME_DARK) }
    }

    @Test
    fun `invoke sets system theme in repository`() {
        useCase(PreferencesRepository.THEME_SYSTEM)

        verify(exactly = 1) { preferencesRepository.setThemeMode(PreferencesRepository.THEME_SYSTEM) }
    }
}
