package com.yumedev.taptopayandroid.data.repository

import android.nfc.Tag
import com.yumedev.taptopayandroid.data.datasource.nfc.NfcCardReader
import com.yumedev.taptopayandroid.domain.model.CardInfo
import com.yumedev.taptopayandroid.domain.repository.NfcRepository

class NfcRepositoryImpl(
    private val nfcCardReader: NfcCardReader = NfcCardReader()
) : NfcRepository {

    override suspend fun readCard(tag: Tag): Result<CardInfo> {
        return nfcCardReader.readCard(tag)
    }
}