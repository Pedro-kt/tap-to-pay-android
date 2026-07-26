package com.yumedev.taptopayandroid.domain.repository

import com.yumedev.taptopayandroid.R
import com.yumedev.taptopayandroid.domain.model.EmvTagInfo
import com.yumedev.taptopayandroid.domain.model.TagCategory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmvTagInfoRepository @Inject constructor() {

    private val tagInfoMap: Map<String, EmvTagInfo> = mapOf(
        "4F" to EmvTagInfo(
            tag = "4F",
            nameResId = R.string.tag_4F_name,
            descriptionResId = R.string.tag_4F_description,
            purposeResId = R.string.tag_4F_purpose,
            formatResId = R.string.tag_4F_format,
            sourceResId = R.string.tag_4F_source,
            category = TagCategory.APPLICATION
        ),
        "50" to EmvTagInfo(
            tag = "50",
            nameResId = R.string.tag_50_name,
            descriptionResId = R.string.tag_50_description,
            purposeResId = R.string.tag_50_purpose,
            formatResId = R.string.tag_50_format,
            sourceResId = R.string.tag_50_source,
            category = TagCategory.APPLICATION
        ),
        "57" to EmvTagInfo(
            tag = "57",
            nameResId = R.string.tag_57_name,
            descriptionResId = R.string.tag_57_description,
            purposeResId = R.string.tag_57_purpose,
            formatResId = R.string.tag_57_format,
            sourceResId = R.string.tag_57_source,
            category = TagCategory.CARDHOLDER
        ),
        "5A" to EmvTagInfo(
            tag = "5A",
            nameResId = R.string.tag_5A_name,
            descriptionResId = R.string.tag_5A_description,
            purposeResId = R.string.tag_5A_purpose,
            formatResId = R.string.tag_5A_format,
            sourceResId = R.string.tag_5A_source,
            category = TagCategory.CARDHOLDER
        ),
        "5F20" to EmvTagInfo(
            tag = "5F20",
            nameResId = R.string.tag_5F20_name,
            descriptionResId = R.string.tag_5F20_description,
            purposeResId = R.string.tag_5F20_purpose,
            formatResId = R.string.tag_5F20_format,
            sourceResId = R.string.tag_5F20_source,
            category = TagCategory.CARDHOLDER
        ),
        "5F24" to EmvTagInfo(
            tag = "5F24",
            nameResId = R.string.tag_5F24_name,
            descriptionResId = R.string.tag_5F24_description,
            purposeResId = R.string.tag_5F24_purpose,
            formatResId = R.string.tag_5F24_format,
            sourceResId = R.string.tag_5F24_source,
            category = TagCategory.CARDHOLDER
        ),
        "5F25" to EmvTagInfo(
            tag = "5F25",
            nameResId = R.string.tag_5F25_name,
            descriptionResId = R.string.tag_5F25_description,
            purposeResId = R.string.tag_5F25_purpose,
            formatResId = R.string.tag_5F25_format,
            sourceResId = R.string.tag_5F25_source,
            category = TagCategory.APPLICATION
        ),
        "5F28" to EmvTagInfo(
            tag = "5F28",
            nameResId = R.string.tag_5F28_name,
            descriptionResId = R.string.tag_5F28_description,
            purposeResId = R.string.tag_5F28_purpose,
            formatResId = R.string.tag_5F28_format,
            sourceResId = R.string.tag_5F28_source,
            category = TagCategory.APPLICATION
        ),
        "5F2A" to EmvTagInfo(
            tag = "5F2A",
            nameResId = R.string.tag_5F2A_name,
            descriptionResId = R.string.tag_5F2A_description,
            purposeResId = R.string.tag_5F2A_purpose,
            formatResId = R.string.tag_5F2A_format,
            sourceResId = R.string.tag_5F2A_source,
            category = TagCategory.TRANSACTION
        ),
        "5F2D" to EmvTagInfo(
            tag = "5F2D",
            nameResId = R.string.tag_5F2D_name,
            descriptionResId = R.string.tag_5F2D_description,
            purposeResId = R.string.tag_5F2D_purpose,
            formatResId = R.string.tag_5F2D_format,
            sourceResId = R.string.tag_5F2D_source,
            category = TagCategory.CARDHOLDER
        ),
        "5F30" to EmvTagInfo(
            tag = "5F30",
            nameResId = R.string.tag_5F30_name,
            descriptionResId = R.string.tag_5F30_description,
            purposeResId = R.string.tag_5F30_purpose,
            formatResId = R.string.tag_5F30_format,
            sourceResId = R.string.tag_5F30_source,
            category = TagCategory.CARDHOLDER
        ),
        "5F34" to EmvTagInfo(
            tag = "5F34",
            nameResId = R.string.tag_5F34_name,
            descriptionResId = R.string.tag_5F34_description,
            purposeResId = R.string.tag_5F34_purpose,
            formatResId = R.string.tag_5F34_format,
            sourceResId = R.string.tag_5F34_source,
            category = TagCategory.CARDHOLDER
        ),
        "82" to EmvTagInfo(
            tag = "82",
            nameResId = R.string.tag_82_name,
            descriptionResId = R.string.tag_82_description,
            purposeResId = R.string.tag_82_purpose,
            formatResId = R.string.tag_82_format,
            sourceResId = R.string.tag_82_source,
            category = TagCategory.APPLICATION
        ),
        "84" to EmvTagInfo(
            tag = "84",
            nameResId = R.string.tag_84_name,
            descriptionResId = R.string.tag_84_description,
            purposeResId = R.string.tag_84_purpose,
            formatResId = R.string.tag_84_format,
            sourceResId = R.string.tag_84_source,
            category = TagCategory.APPLICATION
        ),
        "87" to EmvTagInfo(
            tag = "87",
            nameResId = R.string.tag_87_name,
            descriptionResId = R.string.tag_87_description,
            purposeResId = R.string.tag_87_purpose,
            formatResId = R.string.tag_87_format,
            sourceResId = R.string.tag_87_source,
            category = TagCategory.APPLICATION
        ),
        "8C" to EmvTagInfo(
            tag = "8C",
            nameResId = R.string.tag_8C_name,
            descriptionResId = R.string.tag_8C_description,
            purposeResId = R.string.tag_8C_purpose,
            formatResId = R.string.tag_8C_format,
            sourceResId = R.string.tag_8C_source,
            category = TagCategory.PROCESSING
        ),
        "8D" to EmvTagInfo(
            tag = "8D",
            nameResId = R.string.tag_8D_name,
            descriptionResId = R.string.tag_8D_description,
            purposeResId = R.string.tag_8D_purpose,
            formatResId = R.string.tag_8D_format,
            sourceResId = R.string.tag_8D_source,
            category = TagCategory.PROCESSING
        ),
        "8E" to EmvTagInfo(
            tag = "8E",
            nameResId = R.string.tag_8E_name,
            descriptionResId = R.string.tag_8E_description,
            purposeResId = R.string.tag_8E_purpose,
            formatResId = R.string.tag_8E_format,
            sourceResId = R.string.tag_8E_source,
            category = TagCategory.SECURITY
        ),
        "8F" to EmvTagInfo(
            tag = "8F",
            nameResId = R.string.tag_8F_name,
            descriptionResId = R.string.tag_8F_description,
            purposeResId = R.string.tag_8F_purpose,
            formatResId = R.string.tag_8F_format,
            sourceResId = R.string.tag_8F_source,
            category = TagCategory.SECURITY
        ),
        "90" to EmvTagInfo(
            tag = "90",
            nameResId = R.string.tag_90_name,
            descriptionResId = R.string.tag_90_description,
            purposeResId = R.string.tag_90_purpose,
            formatResId = R.string.tag_90_format,
            sourceResId = R.string.tag_90_source,
            category = TagCategory.SECURITY
        ),
        "92" to EmvTagInfo(
            tag = "92",
            nameResId = R.string.tag_92_name,
            descriptionResId = R.string.tag_92_description,
            purposeResId = R.string.tag_92_purpose,
            formatResId = R.string.tag_92_format,
            sourceResId = R.string.tag_92_source,
            category = TagCategory.SECURITY
        ),
        "94" to EmvTagInfo(
            tag = "94",
            nameResId = R.string.tag_94_name,
            descriptionResId = R.string.tag_94_description,
            purposeResId = R.string.tag_94_purpose,
            formatResId = R.string.tag_94_format,
            sourceResId = R.string.tag_94_source,
            category = TagCategory.PROCESSING
        ),
        "9A" to EmvTagInfo(
            tag = "9A",
            nameResId = R.string.tag_9A_name,
            descriptionResId = R.string.tag_9A_description,
            purposeResId = R.string.tag_9A_purpose,
            formatResId = R.string.tag_9A_format,
            sourceResId = R.string.tag_9A_source,
            category = TagCategory.TRANSACTION
        ),
        "9C" to EmvTagInfo(
            tag = "9C",
            nameResId = R.string.tag_9C_name,
            descriptionResId = R.string.tag_9C_description,
            purposeResId = R.string.tag_9C_purpose,
            formatResId = R.string.tag_9C_format,
            sourceResId = R.string.tag_9C_source,
            category = TagCategory.TRANSACTION
        ),
        "9F02" to EmvTagInfo(
            tag = "9F02",
            nameResId = R.string.tag_9F02_name,
            descriptionResId = R.string.tag_9F02_description,
            purposeResId = R.string.tag_9F02_purpose,
            formatResId = R.string.tag_9F02_format,
            sourceResId = R.string.tag_9F02_source,
            category = TagCategory.TRANSACTION
        ),
        "9F03" to EmvTagInfo(
            tag = "9F03",
            nameResId = R.string.tag_9F03_name,
            descriptionResId = R.string.tag_9F03_description,
            purposeResId = R.string.tag_9F03_purpose,
            formatResId = R.string.tag_9F03_format,
            sourceResId = R.string.tag_9F03_source,
            category = TagCategory.TRANSACTION
        ),
        "9F07" to EmvTagInfo(
            tag = "9F07",
            nameResId = R.string.tag_9F07_name,
            descriptionResId = R.string.tag_9F07_description,
            purposeResId = R.string.tag_9F07_purpose,
            formatResId = R.string.tag_9F07_format,
            sourceResId = R.string.tag_9F07_source,
            category = TagCategory.APPLICATION
        ),
        "9F08" to EmvTagInfo(
            tag = "9F08",
            nameResId = R.string.tag_9F08_name,
            descriptionResId = R.string.tag_9F08_description,
            purposeResId = R.string.tag_9F08_purpose,
            formatResId = R.string.tag_9F08_format,
            sourceResId = R.string.tag_9F08_source,
            category = TagCategory.APPLICATION
        ),
        "9F0B" to EmvTagInfo(
            tag = "9F0B",
            nameResId = R.string.tag_9F0B_name,
            descriptionResId = R.string.tag_9F0B_description,
            purposeResId = R.string.tag_9F0B_purpose,
            formatResId = R.string.tag_9F0B_format,
            sourceResId = R.string.tag_9F0B_source,
            category = TagCategory.CARDHOLDER
        ),
        "9F0D" to EmvTagInfo(
            tag = "9F0D",
            nameResId = R.string.tag_9F0D_name,
            descriptionResId = R.string.tag_9F0D_description,
            purposeResId = R.string.tag_9F0D_purpose,
            formatResId = R.string.tag_9F0D_format,
            sourceResId = R.string.tag_9F0D_source,
            category = TagCategory.SECURITY
        ),
        "9F0E" to EmvTagInfo(
            tag = "9F0E",
            nameResId = R.string.tag_9F0E_name,
            descriptionResId = R.string.tag_9F0E_description,
            purposeResId = R.string.tag_9F0E_purpose,
            formatResId = R.string.tag_9F0E_format,
            sourceResId = R.string.tag_9F0E_source,
            category = TagCategory.SECURITY
        ),
        "9F0F" to EmvTagInfo(
            tag = "9F0F",
            nameResId = R.string.tag_9F0F_name,
            descriptionResId = R.string.tag_9F0F_description,
            purposeResId = R.string.tag_9F0F_purpose,
            formatResId = R.string.tag_9F0F_format,
            sourceResId = R.string.tag_9F0F_source,
            category = TagCategory.SECURITY
        ),
        "9F10" to EmvTagInfo(
            tag = "9F10",
            nameResId = R.string.tag_9F10_name,
            descriptionResId = R.string.tag_9F10_description,
            purposeResId = R.string.tag_9F10_purpose,
            formatResId = R.string.tag_9F10_format,
            sourceResId = R.string.tag_9F10_source,
            category = TagCategory.APPLICATION
        ),
        "9F12" to EmvTagInfo(
            tag = "9F12",
            nameResId = R.string.tag_9F12_name,
            descriptionResId = R.string.tag_9F12_description,
            purposeResId = R.string.tag_9F12_purpose,
            formatResId = R.string.tag_9F12_format,
            sourceResId = R.string.tag_9F12_source,
            category = TagCategory.APPLICATION
        ),
        "9F1A" to EmvTagInfo(
            tag = "9F1A",
            nameResId = R.string.tag_9F1A_name,
            descriptionResId = R.string.tag_9F1A_description,
            purposeResId = R.string.tag_9F1A_purpose,
            formatResId = R.string.tag_9F1A_format,
            sourceResId = R.string.tag_9F1A_source,
            category = TagCategory.TERMINAL
        ),
        "9F21" to EmvTagInfo(
            tag = "9F21",
            nameResId = R.string.tag_9F21_name,
            descriptionResId = R.string.tag_9F21_description,
            purposeResId = R.string.tag_9F21_purpose,
            formatResId = R.string.tag_9F21_format,
            sourceResId = R.string.tag_9F21_source,
            category = TagCategory.TRANSACTION
        ),
        "9F26" to EmvTagInfo(
            tag = "9F26",
            nameResId = R.string.tag_9F26_name,
            descriptionResId = R.string.tag_9F26_description,
            purposeResId = R.string.tag_9F26_purpose,
            formatResId = R.string.tag_9F26_format,
            sourceResId = R.string.tag_9F26_source,
            category = TagCategory.SECURITY
        ),
        "9F27" to EmvTagInfo(
            tag = "9F27",
            nameResId = R.string.tag_9F27_name,
            descriptionResId = R.string.tag_9F27_description,
            purposeResId = R.string.tag_9F27_purpose,
            formatResId = R.string.tag_9F27_format,
            sourceResId = R.string.tag_9F27_source,
            category = TagCategory.SECURITY
        ),
        "9F32" to EmvTagInfo(
            tag = "9F32",
            nameResId = R.string.tag_9F32_name,
            descriptionResId = R.string.tag_9F32_description,
            purposeResId = R.string.tag_9F32_purpose,
            formatResId = R.string.tag_9F32_format,
            sourceResId = R.string.tag_9F32_source,
            category = TagCategory.SECURITY
        ),
        "9F33" to EmvTagInfo(
            tag = "9F33",
            nameResId = R.string.tag_9F33_name,
            descriptionResId = R.string.tag_9F33_description,
            purposeResId = R.string.tag_9F33_purpose,
            formatResId = R.string.tag_9F33_format,
            sourceResId = R.string.tag_9F33_source,
            category = TagCategory.TERMINAL
        ),
        "9F34" to EmvTagInfo(
            tag = "9F34",
            nameResId = R.string.tag_9F34_name,
            descriptionResId = R.string.tag_9F34_description,
            purposeResId = R.string.tag_9F34_purpose,
            formatResId = R.string.tag_9F34_format,
            sourceResId = R.string.tag_9F34_source,
            category = TagCategory.SECURITY
        ),
        "9F35" to EmvTagInfo(
            tag = "9F35",
            nameResId = R.string.tag_9F35_name,
            descriptionResId = R.string.tag_9F35_description,
            purposeResId = R.string.tag_9F35_purpose,
            formatResId = R.string.tag_9F35_format,
            sourceResId = R.string.tag_9F35_source,
            category = TagCategory.TERMINAL
        ),
        "9F36" to EmvTagInfo(
            tag = "9F36",
            nameResId = R.string.tag_9F36_name,
            descriptionResId = R.string.tag_9F36_description,
            purposeResId = R.string.tag_9F36_purpose,
            formatResId = R.string.tag_9F36_format,
            sourceResId = R.string.tag_9F36_source,
            category = TagCategory.TRANSACTION
        ),
        "9F37" to EmvTagInfo(
            tag = "9F37",
            nameResId = R.string.tag_9F37_name,
            descriptionResId = R.string.tag_9F37_description,
            purposeResId = R.string.tag_9F37_purpose,
            formatResId = R.string.tag_9F37_format,
            sourceResId = R.string.tag_9F37_source,
            category = TagCategory.SECURITY
        ),
        "9F38" to EmvTagInfo(
            tag = "9F38",
            nameResId = R.string.tag_9F38_name,
            descriptionResId = R.string.tag_9F38_description,
            purposeResId = R.string.tag_9F38_purpose,
            formatResId = R.string.tag_9F38_format,
            sourceResId = R.string.tag_9F38_source,
            category = TagCategory.PROCESSING
        ),
        "9F42" to EmvTagInfo(
            tag = "9F42",
            nameResId = R.string.tag_9F42_name,
            descriptionResId = R.string.tag_9F42_description,
            purposeResId = R.string.tag_9F42_purpose,
            formatResId = R.string.tag_9F42_format,
            sourceResId = R.string.tag_9F42_source,
            category = TagCategory.APPLICATION
        ),
        "9F44" to EmvTagInfo(
            tag = "9F44",
            nameResId = R.string.tag_9F44_name,
            descriptionResId = R.string.tag_9F44_description,
            purposeResId = R.string.tag_9F44_purpose,
            formatResId = R.string.tag_9F44_format,
            sourceResId = R.string.tag_9F44_source,
            category = TagCategory.APPLICATION
        ),
        "9F45" to EmvTagInfo(
            tag = "9F45",
            nameResId = R.string.tag_9F45_name,
            descriptionResId = R.string.tag_9F45_description,
            purposeResId = R.string.tag_9F45_purpose,
            formatResId = R.string.tag_9F45_format,
            sourceResId = R.string.tag_9F45_source,
            category = TagCategory.SECURITY
        ),
        "9F46" to EmvTagInfo(
            tag = "9F46",
            nameResId = R.string.tag_9F46_name,
            descriptionResId = R.string.tag_9F46_description,
            purposeResId = R.string.tag_9F46_purpose,
            formatResId = R.string.tag_9F46_format,
            sourceResId = R.string.tag_9F46_source,
            category = TagCategory.SECURITY
        ),
        "9F47" to EmvTagInfo(
            tag = "9F47",
            nameResId = R.string.tag_9F47_name,
            descriptionResId = R.string.tag_9F47_description,
            purposeResId = R.string.tag_9F47_purpose,
            formatResId = R.string.tag_9F47_format,
            sourceResId = R.string.tag_9F47_source,
            category = TagCategory.SECURITY
        ),
        "9F48" to EmvTagInfo(
            tag = "9F48",
            nameResId = R.string.tag_9F48_name,
            descriptionResId = R.string.tag_9F48_description,
            purposeResId = R.string.tag_9F48_purpose,
            formatResId = R.string.tag_9F48_format,
            sourceResId = R.string.tag_9F48_source,
            category = TagCategory.SECURITY
        ),
        "9F4A" to EmvTagInfo(
            tag = "9F4A",
            nameResId = R.string.tag_9F4A_name,
            descriptionResId = R.string.tag_9F4A_description,
            purposeResId = R.string.tag_9F4A_purpose,
            formatResId = R.string.tag_9F4A_format,
            sourceResId = R.string.tag_9F4A_source,
            category = TagCategory.SECURITY
        ),
        "9F4C" to EmvTagInfo(
            tag = "9F4C",
            nameResId = R.string.tag_9F4C_name,
            descriptionResId = R.string.tag_9F4C_description,
            purposeResId = R.string.tag_9F4C_purpose,
            formatResId = R.string.tag_9F4C_format,
            sourceResId = R.string.tag_9F4C_source,
            category = TagCategory.SECURITY
        ),
        "9F4D" to EmvTagInfo(
            tag = "9F4D",
            nameResId = R.string.tag_9F4D_name,
            descriptionResId = R.string.tag_9F4D_description,
            purposeResId = R.string.tag_9F4D_purpose,
            formatResId = R.string.tag_9F4D_format,
            sourceResId = R.string.tag_9F4D_source,
            category = TagCategory.TRANSACTION
        ),
        "9F6E" to EmvTagInfo(
            tag = "9F6E",
            nameResId = R.string.tag_9F6E_name,
            descriptionResId = R.string.tag_9F6E_description,
            purposeResId = R.string.tag_9F6E_purpose,
            formatResId = R.string.tag_9F6E_format,
            sourceResId = R.string.tag_9F6E_source,
            category = TagCategory.APPLICATION
        ),
        "9F7C" to EmvTagInfo(
            tag = "9F7C",
            nameResId = R.string.tag_9F7C_name,
            descriptionResId = R.string.tag_9F7C_description,
            purposeResId = R.string.tag_9F7C_purpose,
            formatResId = R.string.tag_9F7C_format,
            sourceResId = R.string.tag_9F7C_source,
            category = TagCategory.OTHER
        )
    )

    fun getTagInfo(tag: String): EmvTagInfo? {
        return tagInfoMap[tag.uppercase()]
    }

    fun hasDetailedInfo(tag: String): Boolean {
        return tagInfoMap.containsKey(tag.uppercase())
    }
}
