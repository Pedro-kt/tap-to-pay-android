package com.yumedev.taptopayandroid.domain.repository

import android.nfc.Tag
import com.yumedev.taptopayandroid.domain.model.EmvCardData

interface NfcRepository {
    suspend fun readCard(tag: Tag, amountCents: Long? = null): Result<EmvCardData>
}
