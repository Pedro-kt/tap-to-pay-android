package com.yumedev.taptopayandroid.domain.usecase

import com.yumedev.taptopayandroid.domain.model.PanValidation
import com.yumedev.taptopayandroid.domain.model.ValidationType
import com.yumedev.taptopayandroid.util.CardValidator
import javax.inject.Inject

class ValidatePanUseCase @Inject constructor() {

    operator fun invoke(pan: String): PanValidation {
        val cleanPan = pan.filter { it.isDigit() }

        if (cleanPan.isEmpty()) {
            return PanValidation(
                isValid = false,
                pan = pan,
                validationType = ValidationType.INVALID_FORMAT
            )
        }

        if (!CardValidator.isValidPanFormat(cleanPan)) {
            return PanValidation(
                isValid = false,
                pan = pan,
                validationType = ValidationType.INSUFFICIENT_DIGITS
            )
        }

        val isLuhnValid = CardValidator.validateLuhn(cleanPan)

        return PanValidation(
            isValid = isLuhnValid,
            pan = pan,
            validationType = if (isLuhnValid) ValidationType.LUHN_VALID else ValidationType.LUHN_INVALID
        )
    }
}
