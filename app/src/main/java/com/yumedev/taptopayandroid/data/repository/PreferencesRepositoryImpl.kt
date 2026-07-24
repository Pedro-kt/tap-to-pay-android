package com.yumedev.taptopayandroid.data.repository

import com.yumedev.taptopayandroid.data.preferences.PreferencesManager
import com.yumedev.taptopayandroid.domain.model.DetailLevel
import com.yumedev.taptopayandroid.domain.repository.PreferencesRepository
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    private val preferencesManager: PreferencesManager
) : PreferencesRepository {

    override fun getThemeMode(): String {
        return preferencesManager.themeMode
    }

    override fun setThemeMode(mode: String) {
        preferencesManager.themeMode = mode
    }

    override fun isSoundEnabled(): Boolean {
        return preferencesManager.isSoundEnabled
    }

    override fun setSoundEnabled(enabled: Boolean) {
        preferencesManager.isSoundEnabled = enabled
    }

    override fun getDetailLevel(): DetailLevel {
        return when (preferencesManager.detailLevel) {
            PreferencesManager.DETAIL_LEVEL_SIMPLE -> DetailLevel.SIMPLE
            PreferencesManager.DETAIL_LEVEL_DETAILED -> DetailLevel.DETAILED
            else -> DetailLevel.DETAILED
        }
    }

    override fun setDetailLevel(level: DetailLevel) {
        preferencesManager.detailLevel = when (level) {
            DetailLevel.SIMPLE -> PreferencesManager.DETAIL_LEVEL_SIMPLE
            DetailLevel.DETAILED -> PreferencesManager.DETAIL_LEVEL_DETAILED
        }
    }
}
