package com.yumedev.taptopayandroid.domain.model

sealed class NfcState {
    data object Idle : NfcState()
    data object Waiting : NfcState()
    data class Success(val cardInfo: CardInfo) : NfcState()
    data class Error(val message: String) : NfcState()
}
