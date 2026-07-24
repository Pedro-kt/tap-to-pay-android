package com.yumedev.taptopayandroid.domain.usecase

import com.yumedev.taptopayandroid.domain.model.DetailLevel
import com.yumedev.taptopayandroid.domain.repository.PreferencesRepository
import javax.inject.Inject

class GetDetailLevelUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(): DetailLevel {
        return preferencesRepository.getDetailLevel()
    }
}
