package com.yumedev.taptopayandroid.presentation.viewmodel

import android.nfc.Tag
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yumedev.taptopayandroid.data.datasource.nfc.NfcManager
import com.yumedev.taptopayandroid.data.repository.NfcRepositoryImpl
import com.yumedev.taptopayandroid.domain.model.NfcState
import com.yumedev.taptopayandroid.domain.repository.NfcRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TapToPayViewModel(
    private val nfcRepository: NfcRepository = NfcRepositoryImpl()
) : ViewModel() {

    private val _nfcState = MutableStateFlow<NfcState>(NfcState.Waiting)
    val nfcState: StateFlow<NfcState> = _nfcState.asStateFlow()

    init {
        // Listen to NFC tags from MainActivity
        viewModelScope.launch {
            NfcManager.nfcTagFlow.collect { tag ->
                processNfcTag(tag)
            }
        }
    }

    fun startWaitingForCard() {
        _nfcState.value = NfcState.Waiting
    }

    private fun processNfcTag(tag: Tag) {
        viewModelScope.launch {
            // Read card immediately and transition directly to Success or Error
            val result = nfcRepository.readCard(tag)

            _nfcState.value = result.fold(
                onSuccess = { cardInfo ->
                    NfcState.Success(cardInfo)
                },
                onFailure = { exception ->
                    NfcState.Error(exception.message ?: "Unknown error reading card")
                }
            )
        }
    }

    fun resetState() {
        _nfcState.value = NfcState.Waiting
    }
}
