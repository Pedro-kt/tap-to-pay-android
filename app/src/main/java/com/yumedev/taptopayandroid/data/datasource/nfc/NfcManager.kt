package com.yumedev.taptopayandroid.data.datasource.nfc

import android.nfc.Tag
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object NfcManager {
    private val _nfcTagFlow = MutableSharedFlow<Tag>(replay = 0)
    val nfcTagFlow: SharedFlow<Tag> = _nfcTagFlow.asSharedFlow()

    suspend fun emitTag(tag: Tag) {
        _nfcTagFlow.emit(tag)
    }
}
