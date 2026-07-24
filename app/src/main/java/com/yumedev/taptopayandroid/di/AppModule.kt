package com.yumedev.taptopayandroid.di

import com.yumedev.taptopayandroid.data.datasource.audio.SoundManager
import com.yumedev.taptopayandroid.data.datasource.nfc.NfcCardReader
import com.yumedev.taptopayandroid.data.datasource.nfc.NfcManager
import com.yumedev.taptopayandroid.data.parser.EmvTagParser
import com.yumedev.taptopayandroid.data.preferences.PreferencesManager
import com.yumedev.taptopayandroid.data.repository.AudioRepositoryImpl
import com.yumedev.taptopayandroid.data.repository.NfcEventRepositoryImpl
import com.yumedev.taptopayandroid.data.repository.NfcRepositoryImpl
import com.yumedev.taptopayandroid.data.repository.PreferencesRepositoryImpl
import com.yumedev.taptopayandroid.domain.repository.AudioRepository
import com.yumedev.taptopayandroid.domain.repository.NfcEventRepository
import com.yumedev.taptopayandroid.domain.repository.NfcRepository
import com.yumedev.taptopayandroid.domain.repository.PreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNfcCardReader(
        emvTagParser: EmvTagParser
    ): NfcCardReader {
        return NfcCardReader(emvTagParser)
    }

    @Provides
    @Singleton
    fun provideNfcRepository(
        nfcCardReader: NfcCardReader
    ): NfcRepository {
        return NfcRepositoryImpl(nfcCardReader)
    }

    @Provides
    @Singleton
    fun providePreferencesRepository(
        preferencesManager: PreferencesManager
    ): PreferencesRepository {
        return PreferencesRepositoryImpl(preferencesManager)
    }

    @Provides
    @Singleton
    fun provideAudioRepository(
        soundManager: SoundManager
    ): AudioRepository {
        return AudioRepositoryImpl(soundManager)
    }

    @Provides
    @Singleton
    fun provideNfcEventRepository(
        nfcManager: NfcManager
    ): NfcEventRepository {
        return NfcEventRepositoryImpl(nfcManager)
    }
}
