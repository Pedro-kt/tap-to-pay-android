package com.yumedev.taptopayandroid.domain.model

data class AipDecoded(
    val rawValue: String,
    val sdaSupported: Boolean,
    val ddaSupported: Boolean,
    val cdaSupported: Boolean,
    val cardholderVerificationSupported: Boolean,
    val terminalRiskManagementRequired: Boolean,
    val issuerAuthenticationSupported: Boolean,
    val onDeviceCardholderVerificationSupported: Boolean,
    val crlSupported: Boolean
)
