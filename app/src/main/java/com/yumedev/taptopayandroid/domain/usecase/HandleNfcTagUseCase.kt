package com.yumedev.taptopayandroid.domain.usecase

import android.nfc.Tag
import android.util.Log
import com.yumedev.taptopayandroid.domain.repository.NfcEventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    private var scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var lastProcessedTagId: String? = null
    private var clearJob: Job? = null

    // For testing purposes - allows injecting a test scope
    internal fun setTestScope(testScope: CoroutineScope) {
        scope = testScope
    }

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

    private fun scheduleTagIdClear() {
        clearJob?.cancel()
        clearJob = scope.launch {
            delay(TAG_DEDUPLICATION_DELAY_MS)
            lastProcessedTagId = null
        }
    }

    fun reset() {
        clearJob?.cancel()
        lastProcessedTagId = null
    }
}