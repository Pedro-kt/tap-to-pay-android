package com.yumedev.taptopayandroid.data.repository

import com.yumedev.taptopayandroid.data.datasource.audio.SoundManager
import com.yumedev.taptopayandroid.domain.repository.AudioRepository
import javax.inject.Inject

class AudioRepositoryImpl @Inject constructor(
    private val soundManager: SoundManager
) : AudioRepository {

    override fun playSuccess() {
        soundManager.playSuccess()
    }

    override fun playError() {
        soundManager.playFailed()
    }
}
