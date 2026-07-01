package com.yumedev.taptopayandroid.data.datasource.audio

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.yumedev.taptopayandroid.R
import com.yumedev.taptopayandroid.data.preferences.PreferencesManager

object SoundManager {

    private var mediaPlayer: MediaPlayer? = null

    fun playSuccess(context: Context) {
        val preferencesManager = PreferencesManager.getInstance(context)
        if (preferencesManager.isSoundEnabled) {
            playSound(context, R.raw.success, "success")
        }
    }

    fun playFailed(context: Context) {
        val preferencesManager = PreferencesManager.getInstance(context)
        if (preferencesManager.isSoundEnabled) {
            playSound(context, R.raw.failed, "failed")
        }
    }

    private fun playSound(context: Context, resourceId: Int, soundName: String) {
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
