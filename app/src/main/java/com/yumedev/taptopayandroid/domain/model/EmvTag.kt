package com.yumedev.taptopayandroid.domain.model

data class EmvTag(
    val tag: String,
    val tagName: String,
    val length: Int,
    val value: String,
    val valueDecoded: String? = null,
    val description: String? = null
)
