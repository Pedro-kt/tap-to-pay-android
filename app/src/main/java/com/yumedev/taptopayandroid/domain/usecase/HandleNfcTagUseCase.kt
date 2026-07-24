package com.yumedev.taptopayandroid.domain.usecase

import android.nfc.Tag
import android.util.Log
import com.yumedev.taptopayandroid.domain.repository.NfcEventRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

// Use case for handling NFC tag discovery events.
@Singleton
class HandleNfcTagUseCase @Inject constructor(
    private val nfcEventRepository: NfcEventRepository
) {
    companion object {
        private const val TAG_DEDUPLICATION_DELAY_MS = 2000L
    }

    private var lastProcessedTagId: String? = null

    // Process an NFC tag discovery event
    suspend operator fun invoke(tag: Tag): Boolean {
        val tagId = tag.id.contentToString()

        // Prevent processing the same tag multiple times
        if (tagId == lastProcessedTagId) {
            return false
        }

        lastProcessedTagId = tagId

        // Emit the tag to the repository for processing
        nfcEventRepository.emitTag(tag)

        // Clear the processed tag after a delay to allow re-reading
        scheduleTagIdClear()

        return true
    }

    private suspend fun scheduleTagIdClear() {
        delay(TAG_DEDUPLICATION_DELAY_MS)
        lastProcessedTagId = null
    }

    fun reset() {
        lastProcessedTagId = null
    }
}