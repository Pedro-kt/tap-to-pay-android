package com.yumedev.taptopayandroid.domain.usecase

import com.yumedev.taptopayandroid.domain.repository.PreferencesRepository
import javax.inject.Inject

class GetSoundEnabledUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(): Boolean {
        return preferencesRepository.isSoundEnabled()
    }
}
