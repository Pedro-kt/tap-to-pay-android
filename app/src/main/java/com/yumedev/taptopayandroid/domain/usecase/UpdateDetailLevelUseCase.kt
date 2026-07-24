package com.yumedev.taptopayandroid.domain.usecase

import com.yumedev.taptopayandroid.domain.model.DetailLevel
import com.yumedev.taptopayandroid.domain.repository.PreferencesRepository
import javax.inject.Inject

class UpdateDetailLevelUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(detailLevel: DetailLevel) {
        preferencesRepository.setDetailLevel(detailLevel)
    }
}
