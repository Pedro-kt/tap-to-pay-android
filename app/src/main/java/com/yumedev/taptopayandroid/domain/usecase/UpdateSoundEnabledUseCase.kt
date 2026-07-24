package com.yumedev.taptopayandroid.domain.usecase

import com.yumedev.taptopayandroid.data.preferences.PreferencesManager
import javax.inject.Inject

class UpdateSoundEnabledUseCase @Inject constructor(
    private val preferencesManager: PreferencesManager
) {
    operator fun invoke(enabled: Boolean) {
        preferencesManager.isSoundEnabled = enabled
    }
}
