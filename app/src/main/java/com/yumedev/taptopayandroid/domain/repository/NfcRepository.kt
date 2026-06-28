package com.yumedev.taptopayandroid.domain.repository

import android.nfc.Tag
import com.yumedev.taptopayandroid.domain.model.CardInfo

interface NfcRepository {
    suspend fun readCard(tag: Tag): Result<CardInfo>
}
