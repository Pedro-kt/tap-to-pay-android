package com.yumedev.taptopayandroid.domain.usecase

import com.yumedev.taptopayandroid.data.preferences.PreferencesManager
import javax.inject.Inject

class GetDetailLevelUseCase @Inject constructor(
    private val preferencesManager: PreferencesManager
) {
    operator fun invoke(): String {
        return preferencesManager.detailLevel
    }
}
