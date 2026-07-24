package com.yumedev.taptopayandroid.data.repository

import com.google.common.truth.Truth.assertThat
import com.yumedev.taptopayandroid.data.preferences.PreferencesManager
import com.yumedev.taptopayandroid.domain.model.DetailLevel
import com.yumedev.taptopayandroid.domain.repository.PreferencesRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class PreferencesRepositoryImplTest {

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var repository: PreferencesRepository

    @Before
    fun setup() {
        preferencesManager = mockk(relaxed = true)
        repository = PreferencesRepositoryImpl(preferencesManager)
    }

    @Test
    fun `getThemeMode returns theme from PreferencesManager`() {
        every { preferencesManager.themeMode } returns PreferencesRepository.THEME_DARK

        val result = repository.getThemeMode()

        assertThat(result).isEqualTo(PreferencesRepository.THEME_DARK)
        verify(exactly = 1) { preferencesManager.themeMode }
    }

    @Test
    fun `getThemeMode returns light theme correctly`() {
        every { preferencesManager.themeMode } returns PreferencesRepository.THEME_LIGHT

        val result = repository.getThemeMode()

        assertThat(result).isEqualTo(PreferencesRepository.THEME_LIGHT)
    }

    @Test
    fun `getThemeMode returns system theme correctly`() {
        every { preferencesManager.themeMode } returns PreferencesRepository.THEME_SYSTEM

        val result = repository.getThemeMode()

        assertThat(result).isEqualTo(PreferencesRepository.THEME_SYSTEM)
    }

    @Test
    fun `setThemeMode updates PreferencesManager correctly`() {
        val newTheme = PreferencesRepository.THEME_DARK

        repository.setThemeMode(newTheme)

        verify(exactly = 1) { preferencesManager.themeMode = newTheme }
    }

    @Test
    fun `setThemeMode sets light theme correctly`() {
        repository.setThemeMode(PreferencesRepository.THEME_LIGHT)

        verify { preferencesManager.themeMode = PreferencesRepository.THEME_LIGHT }
    }

    @Test
    fun `setThemeMode sets system theme correctly`() {
        repository.setThemeMode(PreferencesRepository.THEME_SYSTEM)

        verify { preferencesManager.themeMode = PreferencesRepository.THEME_SYSTEM }
    }

    @Test
    fun `isSoundEnabled returns true when sound is enabled`() {
        every { preferencesManager.isSoundEnabled } returns true

        val result = repository.isSoundEnabled()

        assertThat(result).isTrue()
        verify(exactly = 1) { preferencesManager.isSoundEnabled }
    }

    @Test
    fun `isSoundEnabled returns false when sound is disabled`() {
        every { preferencesManager.isSoundEnabled } returns false

        val result = repository.isSoundEnabled()

        assertThat(result).isFalse()
    }

    @Test
    fun `setSoundEnabled enables sound correctly`() {
        repository.setSoundEnabled(true)

        verify(exactly = 1) { preferencesManager.isSoundEnabled = true }
    }

    @Test
    fun `setSoundEnabled disables sound correctly`() {
        repository.setSoundEnabled(false)

        verify(exactly = 1) { preferencesManager.isSoundEnabled = false }
    }

    @Test
    fun `getDetailLevel returns SIMPLE when PreferencesManager has DETAIL_LEVEL_SIMPLE`() {
        every { preferencesManager.detailLevel } returns PreferencesManager.DETAIL_LEVEL_SIMPLE

        val result = repository.getDetailLevel()

        assertThat(result).isEqualTo(DetailLevel.SIMPLE)
        verify(exactly = 1) { preferencesManager.detailLevel }
    }

    @Test
    fun `getDetailLevel returns DETAILED when PreferencesManager has DETAIL_LEVEL_DETAILED`() {
        every { preferencesManager.detailLevel } returns PreferencesManager.DETAIL_LEVEL_DETAILED

        val result = repository.getDetailLevel()

        assertThat(result).isEqualTo(DetailLevel.DETAILED)
    }

    @Test
    fun `getDetailLevel returns DETAILED as default for unknown values`() {
        every { preferencesManager.detailLevel } returns "unknown_value"

        val result = repository.getDetailLevel()

        assertThat(result).isEqualTo(DetailLevel.DETAILED)
    }

    @Test
    fun `setDetailLevel converts SIMPLE enum to string correctly`() {
        repository.setDetailLevel(DetailLevel.SIMPLE)

        verify(exactly = 1) {
            preferencesManager.detailLevel = PreferencesManager.DETAIL_LEVEL_SIMPLE
        }
    }

    @Test
    fun `setDetailLevel converts DETAILED enum to string correctly`() {
        repository.setDetailLevel(DetailLevel.DETAILED)

        verify(exactly = 1) {
            preferencesManager.detailLevel = PreferencesManager.DETAIL_LEVEL_DETAILED
        }
    }

    @Test
    fun `multiple theme mode changes are handled correctly`() {
        every { preferencesManager.themeMode } returns PreferencesRepository.THEME_LIGHT

        repository.setThemeMode(PreferencesRepository.THEME_DARK)
        every { preferencesManager.themeMode } returns PreferencesRepository.THEME_DARK
        val result1 = repository.getThemeMode()

        repository.setThemeMode(PreferencesRepository.THEME_LIGHT)
        every { preferencesManager.themeMode } returns PreferencesRepository.THEME_LIGHT
        val result2 = repository.getThemeMode()

        assertThat(result1).isEqualTo(PreferencesRepository.THEME_DARK)
        assertThat(result2).isEqualTo(PreferencesRepository.THEME_LIGHT)
        verify(exactly = 1) { preferencesManager.themeMode = PreferencesRepository.THEME_DARK }
        verify(exactly = 1) { preferencesManager.themeMode = PreferencesRepository.THEME_LIGHT }
    }

    @Test
    fun `all preferences can be read and written independently`() {
        every { preferencesManager.themeMode } returns PreferencesRepository.THEME_DARK
        every { preferencesManager.isSoundEnabled } returns true
        every { preferencesManager.detailLevel } returns PreferencesManager.DETAIL_LEVEL_SIMPLE

        val theme = repository.getThemeMode()
        val sound = repository.isSoundEnabled()
        val detail = repository.getDetailLevel()

        assertThat(theme).isEqualTo(PreferencesRepository.THEME_DARK)
        assertThat(sound).isTrue()
        assertThat(detail).isEqualTo(DetailLevel.SIMPLE)
    }
}
