package com.yumedev.taptopayandroid.data.repository

import android.nfc.Tag
import com.yumedev.taptopayandroid.data.datasource.nfc.NfcManager
import com.yumedev.taptopayandroid.domain.repository.NfcEventRepository
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class NfcEventRepositoryImpl @Inject constructor(
    private val nfcManager: NfcManager
) : NfcEventRepository {

    override val nfcTagFlow: SharedFlow<Tag>
        get() = nfcManager.nfcTagFlow

    override suspend fun emitTag(tag: Tag) {
        nfcManager.emitTag(tag)
    }
}
