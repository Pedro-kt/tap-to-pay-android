package com.yumedev.taptopayandroid.domain.usecase

import com.yumedev.taptopayandroid.domain.repository.PreferencesRepository
import javax.inject.Inject

class UpdateSoundEnabledUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(enabled: Boolean) {
        preferencesRepository.setSoundEnabled(enabled)
    }
}
