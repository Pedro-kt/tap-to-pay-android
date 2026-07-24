package com.yumedev.taptopayandroid.domain.usecase

import com.yumedev.taptopayandroid.domain.repository.AudioRepository
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class PlaySuccessSoundUseCaseTest {

    private lateinit var audioRepository: AudioRepository
    private lateinit var useCase: PlaySuccessSoundUseCase

    @Before
    fun setup() {
        audioRepository = mockk(relaxed = true)
        useCase = PlaySuccessSoundUseCase(audioRepository)
    }

    @Test
    fun `invoke plays success sound from repository`() {
        useCase()

        verify(exactly = 1) { audioRepository.playSuccess() }
    }

    @Test
    fun `multiple invocations call repository multiple times`() {
        useCase()
        useCase()
        useCase()

        verify(exactly = 3) { audioRepository.playSuccess() }
    }
}
