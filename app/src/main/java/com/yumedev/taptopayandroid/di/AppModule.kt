package com.yumedev.taptopayandroid.di

import com.yumedev.taptopayandroid.data.datasource.nfc.NfcCardReader
import com.yumedev.taptopayandroid.data.repository.NfcRepositoryImpl
import com.yumedev.taptopayandroid.domain.repository.NfcRepository
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
    fun provideNfcCardReader(): NfcCardReader {
        return NfcCardReader()
    }

    @Provides
    @Singleton
    fun provideNfcRepository(
        nfcCardReader: NfcCardReader
    ): NfcRepository {
        return NfcRepositoryImpl(nfcCardReader)
    }
}
