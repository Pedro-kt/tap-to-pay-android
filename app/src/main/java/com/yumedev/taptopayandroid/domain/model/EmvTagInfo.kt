package com.yumedev.taptopayandroid.domain.model

data class EmvTagInfo(
    val tag: String,
    val nameResId: Int,
    val descriptionResId: Int,
    val purposeResId: Int,
    val formatResId: Int,
    val sourceResId: Int,
    val category: TagCategory
)

enum class TagCategory {
    APPLICATION,
    TRANSACTION,
    CARDHOLDER,
    SECURITY,
    PROCESSING,
    TERMINAL,
    OTHER
}
