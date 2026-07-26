package com.yumedev.taptopayandroid.di

import android.content.Context
import com.yumedev.taptopayandroid.data.datasource.audio.SoundManager
import com.yumedev.taptopayandroid.data.datasource.nfc.NfcManager
import com.yumedev.taptopayandroid.data.parser.EmvTagParser
import com.yumedev.taptopayandroid.data.preferences.PreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun providePreferencesManager(
        @ApplicationContext context: Context
    ): PreferencesManager {
        return PreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideSoundManager(
        @ApplicationContext context: Context,
        preferencesManager: PreferencesManager
    ): SoundManager {
        return SoundManager(context, preferencesManager)
    }

    @Provides
    @Singleton
    fun provideNfcManager(): NfcManager {
        return NfcManager()
    }
}
