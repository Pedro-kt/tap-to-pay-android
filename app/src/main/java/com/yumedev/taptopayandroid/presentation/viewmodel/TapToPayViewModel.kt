package com.yumedev.taptopayandroid.presentation.viewmodel

import android.nfc.Tag
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yumedev.taptopayandroid.data.datasource.nfc.NfcManager
import com.yumedev.taptopayandroid.domain.model.EmvCardData
import com.yumedev.taptopayandroid.domain.model.NfcState
import com.yumedev.taptopayandroid.domain.usecase.PlayFailedSoundUseCase
import com.yumedev.taptopayandroid.domain.usecase.PlaySuccessSoundUseCase
import com.yumedev.taptopayandroid.domain.usecase.ReadCardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TapToPayViewModel @Inject constructor(
    private val readCardUseCase: ReadCardUseCase,
    private val playSuccessSoundUseCase: PlaySuccessSoundUseCase,
    private val playFailedSoundUseCase: PlayFailedSoundUseCase,
    private val nfcManager: NfcManager
) : ViewModel() {

    private val _nfcState = MutableStateFlow<NfcState>(NfcState.Waiting)
    val nfcState: StateFlow<NfcState> = _nfcState.asStateFlow()

    // Store last successful card data for detail screen
    private val _lastEmvCardData = MutableStateFlow<EmvCardData?>(null)
    val lastEmvCardData: StateFlow<EmvCardData?> = _lastEmvCardData.asStateFlow()

    // Store last transaction amount
    private val _lastAmount = MutableStateFlow("0.00")
    val lastAmount: StateFlow<String> = _lastAmount.asStateFlow()

    init {
        // Listen to NFC tags from MainActivity
        viewModelScope.launch {
            nfcManager.nfcTagFlow.collect { tag ->
                processNfcTag(tag)
            }
        }
    }

    private fun processNfcTag(tag: Tag) {
        viewModelScope.launch {
            val result = readCardUseCase(tag)

            _nfcState.value = result.fold(
                onSuccess = { emvCardData ->
                    _lastEmvCardData.value = emvCardData
                    playSuccessSoundUseCase()
                    NfcState.Success(emvCardData)
                },
                onFailure = { exception ->
                    playFailedSoundUseCase()
                    NfcState.Error(exception.message ?: "Unknown error reading card")
                }
            )
        }
    }

    fun clearStateOnly() {
        _nfcState.value = NfcState.Waiting
    }

    fun startNewTransaction(amount: String) {
        _nfcState.value = NfcState.Waiting
        _lastAmount.value = amount
    }
}
