package com.yumedev.taptopayandroid.presentation.ui.components

import androidx.lifecycle.ViewModel
import com.yumedev.taptopayandroid.domain.repository.EmvTagInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TagInfoViewModel @Inject constructor(
    val tagInfoRepository: EmvTagInfoRepository
) : ViewModel()
