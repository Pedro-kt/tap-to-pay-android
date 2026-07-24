package com.yumedev.taptopayandroid.domain.usecase

import android.nfc.Tag
import com.google.common.truth.Truth.assertThat
import com.yumedev.taptopayandroid.domain.repository.NfcEventRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class HandleNfcTagUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var nfcEventRepository: NfcEventRepository
    private lateinit var useCase: HandleNfcTagUseCase

    @Before
    fun setup() {
        nfcEventRepository = mockk(relaxed = true)
        useCase = HandleNfcTagUseCase(nfcEventRepository)
        coEvery { nfcEventRepository.emitTag(any()) } returns Unit
    }

    @Test
    fun `invoke processes new tag and returns true`() = runTest {
        val mockTag = mockk<Tag>(relaxed = true)
        every { mockTag.id } returns byteArrayOf(0x01, 0x02, 0x03, 0x04)

        val result = useCase(mockTag)

        assertThat(result).isTrue()
        coVerify(exactly = 1) { nfcEventRepository.emitTag(mockTag) }
    }

    @Test
    fun `invoke skips duplicate tag and returns false`() = runTest {
        useCase.setTestScope(this)
        val mockTag = mockk<Tag>(relaxed = true)
        val tagId = byteArrayOf(0x01, 0x02, 0x03, 0x04)
        every { mockTag.id } returns tagId

        val result1 = useCase(mockTag)
        val result2 = useCase(mockTag)

        assertThat(result1).isTrue()
        assertThat(result2).isFalse()
        coVerify(exactly = 1) { nfcEventRepository.emitTag(mockTag) }
    }

    @Test
    fun `invoke processes different tags independently`() = runTest {
        val mockTag1 = mockk<Tag>(relaxed = true)
        val mockTag2 = mockk<Tag>(relaxed = true)
        every { mockTag1.id } returns byteArrayOf(0x01, 0x02, 0x03, 0x04)
        every { mockTag2.id } returns byteArrayOf(0x05, 0x06, 0x07, 0x08)

        val result1 = useCase(mockTag1)
        val result2 = useCase(mockTag2)

        assertThat(result1).isTrue()
        assertThat(result2).isTrue()
        coVerify(exactly = 1) { nfcEventRepository.emitTag(mockTag1) }
        coVerify(exactly = 1) { nfcEventRepository.emitTag(mockTag2) }
    }

    @Test
    fun `invoke allows re-processing tag after deduplication delay`() = runTest {
        useCase.setTestScope(this)
        val mockTag = mockk<Tag>(relaxed = true)
        every { mockTag.id } returns byteArrayOf(0x01, 0x02, 0x03, 0x04)

        val result1 = useCase(mockTag)
        advanceTimeBy(2000) // TAG_DEDUPLICATION_DELAY_MS = 2000
        testScheduler.runCurrent() // Ensure background coroutine completes
        val result2 = useCase(mockTag)

        // Then
        assertThat(result1).isTrue()
        assertThat(result2).isTrue()
        coVerify(exactly = 2) { nfcEventRepository.emitTag(mockTag) }
    }

    @Test
    fun `invoke deduplicates during delay period`() = runTest {
        useCase.setTestScope(this)
        val mockTag = mockk<Tag>(relaxed = true)
        every { mockTag.id } returns byteArrayOf(0x01, 0x02, 0x03, 0x04)

        val result1 = useCase(mockTag)
        advanceTimeBy(1000)
        val result2 = useCase(mockTag)

        assertThat(result1).isTrue()
        assertThat(result2).isFalse()
        coVerify(exactly = 1) { nfcEventRepository.emitTag(mockTag) }
    }

    @Test
    fun `reset clears deduplication state`() = runTest {
        val mockTag = mockk<Tag>(relaxed = true)
        every { mockTag.id } returns byteArrayOf(0x01, 0x02, 0x03, 0x04)

        val result1 = useCase(mockTag)
        useCase.reset()
        val result2 = useCase(mockTag)

        assertThat(result1).isTrue()
        assertThat(result2).isTrue()
        coVerify(exactly = 2) { nfcEventRepository.emitTag(mockTag) }
    }

    @Test
    fun `multiple rapid tag reads are handled correctly`() = runTest {
        useCase.setTestScope(this)
        val mockTag = mockk<Tag>(relaxed = true)
        every { mockTag.id } returns byteArrayOf(0x01, 0x02, 0x03, 0x04)

        val result1 = useCase(mockTag)
        val result2 = useCase(mockTag)
        val result3 = useCase(mockTag)
        val result4 = useCase(mockTag)
        val result5 = useCase(mockTag)

        assertThat(result1).isTrue()
        assertThat(result2).isFalse()
        assertThat(result3).isFalse()
        assertThat(result4).isFalse()
        assertThat(result5).isFalse()
        coVerify(exactly = 1) { nfcEventRepository.emitTag(mockTag) }
    }

    @Test
    fun `tag with empty ID is handled correctly`() = runTest {
        val mockTag = mockk<Tag>(relaxed = true)
        every { mockTag.id } returns byteArrayOf()

        val result = useCase(mockTag)

        assertThat(result).isTrue()
        coVerify(exactly = 1) { nfcEventRepository.emitTag(mockTag) }
    }

    @Test
    fun `alternating tags are processed correctly`() = runTest {

        useCase.setTestScope(this)
        val mockTag1 = mockk<Tag>(relaxed = true)
        val mockTag2 = mockk<Tag>(relaxed = true)
        every { mockTag1.id } returns byteArrayOf(0x01, 0x02)
        every { mockTag2.id } returns byteArrayOf(0x03, 0x04)

        val result1 = useCase(mockTag1)
        val result2 = useCase(mockTag2)
        val result3 = useCase(mockTag1)
        val result4 = useCase(mockTag2)

        assertThat(result1).isTrue()
        assertThat(result2).isTrue()
        assertThat(result3).isTrue() // tag1 != tag2 (last processed), so processes
        assertThat(result4).isTrue() // tag2 != tag1 (last processed), so processes
        coVerify(exactly = 2) { nfcEventRepository.emitTag(mockTag1) }
        coVerify(exactly = 2) { nfcEventRepository.emitTag(mockTag2) }
    }
}
