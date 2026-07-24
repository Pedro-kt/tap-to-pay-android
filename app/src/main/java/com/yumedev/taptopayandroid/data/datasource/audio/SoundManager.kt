package com.yumedev.taptopayandroid.data.datasource.audio

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.yumedev.taptopayandroid.R
import com.yumedev.taptopayandroid.data.preferences.PreferencesManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesManager: PreferencesManager
) {

    private var mediaPlayer: MediaPlayer? = null

    fun playSuccess() {
        if (preferencesManager.isSoundEnabled) {
            playSound(R.raw.success, "success")
        }
    }

    fun playFailed() {
        if (preferencesManager.isSoundEnabled) {
            playSound(R.raw.failed, "failed")
        }
    }

    private fun playSound(resourceId: Int, soundName: String) {
        try {
            // Release previous instance if exists
            mediaPlayer?.release()

            // Create new MediaPlayer instance
            mediaPlayer = MediaPlayer.create(context, resourceId)

            mediaPlayer?.setOnCompletionListener {
                it.release()
                mediaPlayer = null
            }

            mediaPlayer?.setOnErrorListener { mp, what, extra ->
                mp.release()
                mediaPlayer = null
                true
            }

            mediaPlayer?.start()
        } catch (e: Exception) {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }
}
