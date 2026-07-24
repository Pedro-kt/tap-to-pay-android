package com.yumedev.taptopayandroid.domain.usecase

import com.yumedev.taptopayandroid.domain.repository.AudioRepository
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class PlayFailedSoundUseCaseTest {

    private lateinit var audioRepository: AudioRepository
    private lateinit var useCase: PlayFailedSoundUseCase

    @Before
    fun setup() {
        audioRepository = mockk(relaxed = true)
        useCase = PlayFailedSoundUseCase(audioRepository)
    }

    @Test
    fun `invoke plays error sound from repository`() {
        useCase()

        verify(exactly = 1) { audioRepository.playError() }
    }

    @Test
    fun `multiple invocations call repository multiple times`() {
        useCase()
        useCase()

        verify(exactly = 2) { audioRepository.playError() }
    }
}
