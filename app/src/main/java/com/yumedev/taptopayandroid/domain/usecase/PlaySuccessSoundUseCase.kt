package com.yumedev.taptopayandroid.domain.usecase

import com.yumedev.taptopayandroid.data.datasource.audio.SoundManager
import javax.inject.Inject

class PlaySuccessSoundUseCase @Inject constructor(
    private val soundManager: SoundManager
) {
    operator fun invoke() {
        soundManager.playSuccess()
    }
}
