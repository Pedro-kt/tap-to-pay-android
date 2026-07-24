package com.yumedev.taptopayandroid.data.repository

import android.nfc.Tag
import com.google.common.truth.Truth.assertThat
import com.yumedev.taptopayandroid.data.datasource.nfc.NfcManager
import com.yumedev.taptopayandroid.domain.repository.NfcEventRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class NfcEventRepositoryImplTest {

    private lateinit var nfcManager: NfcManager
    private lateinit var repository: NfcEventRepository
    private lateinit var testFlow: MutableSharedFlow<Tag>

    @Before
    fun setup() {
        testFlow = MutableSharedFlow(replay = 0)
        nfcManager = mockk(relaxed = true)
        every { nfcManager.nfcTagFlow } returns testFlow
        repository = NfcEventRepositoryImpl(nfcManager)
    }

    @Test
    fun `nfcTagFlow exposes NfcManager flow correctly`() {
        val flow = repository.nfcTagFlow
        assertThat(flow).isEqualTo(testFlow)
    }

    @Test
    fun `emitTag delegates to NfcManager emitTag`() = runTest {
        val mockTag = mockk<Tag>(relaxed = true)
        coEvery { nfcManager.emitTag(any()) } returns Unit

        repository.emitTag(mockTag)

        coVerify(exactly = 1) { nfcManager.emitTag(mockTag) }
    }

    @Test
    fun `emitTag passes correct tag to NfcManager`() = runTest {
        val mockTag = mockk<Tag>(relaxed = true)
        val tagId = byteArrayOf(0x01, 0x02, 0x03, 0x04)
        every { mockTag.id } returns tagId
        coEvery { nfcManager.emitTag(any()) } returns Unit

        repository.emitTag(mockTag)

        coVerify(exactly = 1) { nfcManager.emitTag(mockTag) }
    }

    @Test
    fun `multiple emitTag calls are handled correctly`() = runTest {
        val mockTag1 = mockk<Tag>(relaxed = true)
        val mockTag2 = mockk<Tag>(relaxed = true)
        val mockTag3 = mockk<Tag>(relaxed = true)
        coEvery { nfcManager.emitTag(any()) } returns Unit

        repository.emitTag(mockTag1)
        repository.emitTag(mockTag2)
        repository.emitTag(mockTag3)

        coVerify(exactly = 1) { nfcManager.emitTag(mockTag1) }
        coVerify(exactly = 1) { nfcManager.emitTag(mockTag2) }
        coVerify(exactly = 1) { nfcManager.emitTag(mockTag3) }
    }

    @Test
    fun `emitted tags flow through nfcTagFlow`() = runTest {
        val mockTag = mockk<Tag>(relaxed = true)
        val realNfcManager = NfcManager()
        val realRepository = NfcEventRepositoryImpl(realNfcManager)

        val job = launch {
            val receivedTag = realRepository.nfcTagFlow.first()
            assertThat(receivedTag).isEqualTo(mockTag)
        }

        kotlinx.coroutines.delay(100)
        realRepository.emitTag(mockTag)
        job.join()
    }
}
