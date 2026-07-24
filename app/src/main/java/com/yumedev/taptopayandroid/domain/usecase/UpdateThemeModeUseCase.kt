package com.yumedev.taptopayandroid.domain.usecase

import com.yumedev.taptopayandroid.data.preferences.PreferencesManager
import javax.inject.Inject

class UpdateThemeModeUseCase @Inject constructor(
    private val preferencesManager: PreferencesManager
) {
    operator fun invoke(themeMode: String) {
        preferencesManager.themeMode = themeMode
    }
}
