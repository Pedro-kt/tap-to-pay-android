package com.yumedev.taptopayandroid.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.yumedev.taptopayandroid.domain.usecase.GetDetailLevelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CardDetailViewModel @Inject constructor(
    private val getDetailLevelUseCase: GetDetailLevelUseCase
) : ViewModel() {

    private val _detailLevel = MutableStateFlow(getDetailLevelUseCase())
    val detailLevel: StateFlow<String> = _detailLevel.asStateFlow()
}
