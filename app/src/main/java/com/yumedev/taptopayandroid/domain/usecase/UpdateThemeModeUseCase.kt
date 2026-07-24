package com.yumedev.taptopayandroid.domain.usecase

import com.yumedev.taptopayandroid.domain.repository.PreferencesRepository
import javax.inject.Inject

class UpdateThemeModeUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(themeMode: String) {
        preferencesRepository.setThemeMode(themeMode)
    }
}
