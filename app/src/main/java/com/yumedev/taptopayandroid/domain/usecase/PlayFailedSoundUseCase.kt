package com.yumedev.taptopayandroid.domain.usecase

import com.yumedev.taptopayandroid.domain.repository.AudioRepository
import javax.inject.Inject

class PlayFailedSoundUseCase @Inject constructor(
    private val audioRepository: AudioRepository
) {
    operator fun invoke() {
        audioRepository.playError()
    }
}
