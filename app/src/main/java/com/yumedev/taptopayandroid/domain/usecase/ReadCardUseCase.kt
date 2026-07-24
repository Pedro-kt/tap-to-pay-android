package com.yumedev.taptopayandroid.domain.usecase

import android.nfc.Tag
import com.yumedev.taptopayandroid.domain.model.EmvCardData
import com.yumedev.taptopayandroid.domain.repository.NfcRepository
import javax.inject.Inject

class ReadCardUseCase @Inject constructor(
    private val nfcRepository: NfcRepository
) {
    suspend operator fun invoke(tag: Tag, amountCents: Long? = null): Result<EmvCardData> {
        return nfcRepository.readCard(tag, amountCents)
    }
}
