package com.yumedev.taptopayandroid.domain.usecase

import com.yumedev.taptopayandroid.domain.repository.PreferencesRepository
import javax.inject.Inject

/**
 * Use case for retrieving the current theme mode preference.
 *
 * This follows Clean Architecture by depending on the repository interface
 * rather than the concrete implementation.
 */
class GetThemeModeUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(): String {
        return preferencesRepository.getThemeMode()
    }
}
