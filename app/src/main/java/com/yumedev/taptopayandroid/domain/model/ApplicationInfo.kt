package com.yumedev.taptopayandroid.domain.model

//EMV Application Information from tags 4F, 50, 87, 9F38
data class ApplicationInfo(
    val aid: String, // Tag 4F - Application Identifier (hex)
    val aidBytes: ByteArray,
    val applicationLabel: String? = null, // Tag 50 - Application Label (ASCII)
    val priorityIndicator: Int? = null, // Tag 87 - Priority (1 = highest)
    val pdol: String? = null, // Tag 9F38 - Processing Data Object List (hex)
    val pdolDescription: String? = null,
    val cardType: CardType = CardType.UNKNOWN
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ApplicationInfo

        if (aid != other.aid) return false
        if (!aidBytes.contentEquals(other.aidBytes)) return false
        if (applicationLabel != other.applicationLabel) return false
        if (priorityIndicator != other.priorityIndicator) return false
        if (pdol != other.pdol) return false
        if (pdolDescription != other.pdolDescription) return false
        if (cardType != other.cardType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = aid.hashCode()
        result = 31 * result + aidBytes.contentHashCode()
        result = 31 * result + (applicationLabel?.hashCode() ?: 0)
        result = 31 * result + (priorityIndicator ?: 0)
        result = 31 * result + (pdol?.hashCode() ?: 0)
        result = 31 * result + (pdolDescription?.hashCode() ?: 0)
        result = 31 * result + cardType.hashCode()
        return result
    }
}
