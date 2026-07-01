package com.yumedev.taptopayandroid.data.repository

import android.nfc.Tag
import com.yumedev.taptopayandroid.data.datasource.nfc.NfcCardReader
import com.yumedev.taptopayandroid.domain.model.EmvCardData
import com.yumedev.taptopayandroid.domain.repository.NfcRepository

class NfcRepositoryImpl(
    private val nfcCardReader: NfcCardReader = NfcCardReader()
) : NfcRepository {

    override suspend fun readCard(tag: Tag, amountCents: Long?): Result<EmvCardData> {
        return nfcCardReader.readCard(tag, amountCents)
    }
}