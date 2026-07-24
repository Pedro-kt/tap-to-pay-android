package com.yumedev.taptopayandroid.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.yumedev.taptopayandroid.domain.usecase.GetDetailLevelUseCase
import com.yumedev.taptopayandroid.domain.usecase.GetSoundEnabledUseCase
import com.yumedev.taptopayandroid.domain.usecase.GetThemeModeUseCase
import com.yumedev.taptopayandroid.domain.usecase.UpdateDetailLevelUseCase
import com.yumedev.taptopayandroid.domain.usecase.UpdateSoundEnabledUseCase
import com.yumedev.taptopayandroid.domain.usecase.UpdateThemeModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getThemeModeUseCase: GetThemeModeUseCase,
    private val updateThemeModeUseCase: UpdateThemeModeUseCase,
    private val getSoundEnabledUseCase: GetSoundEnabledUseCase,
    private val updateSoundEnabledUseCase: UpdateSoundEnabledUseCase,
    private val getDetailLevelUseCase: GetDetailLevelUseCase,
    private val updateDetailLevelUseCase: UpdateDetailLevelUseCase
) : ViewModel() {

    private val _themeMode = MutableStateFlow(getThemeModeUseCase())
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _soundEnabled = MutableStateFlow(getSoundEnabledUseCase())
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _detailLevel = MutableStateFlow(getDetailLevelUseCase())
    val detailLevel: StateFlow<String> = _detailLevel.asStateFlow()

    fun updateThemeMode(mode: String) {
        updateThemeModeUseCase(mode)
        _themeMode.value = mode
    }

    fun updateSoundEnabled(enabled: Boolean) {
        updateSoundEnabledUseCase(enabled)
        _soundEnabled.value = enabled
    }

    fun updateDetailLevel(level: String) {
        updateDetailLevelUseCase(level)
        _detailLevel.value = level
    }
}
