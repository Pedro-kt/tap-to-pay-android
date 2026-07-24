package com.yumedev.taptopayandroid.domain.repository

import android.nfc.Tag
import kotlinx.coroutines.flow.SharedFlow

interface NfcEventRepository {

    val nfcTagFlow: SharedFlow<Tag>

    suspend fun emitTag(tag: Tag)
}
