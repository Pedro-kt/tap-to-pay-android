package com.yumedev.taptopayandroid.domain.model

//Complete EMV card data including application info, transaction data, cardholder data, APDU commands, and raw tags
data class EmvCardData(
    val applicationInfo: ApplicationInfo,
    val transactionData: TransactionData,
    val cardholderData: CardholderData,
    val apduCommands: List<ApduCommand> = emptyList(),
    val additionalTags: Map<String, EmvTag> = emptyMap()
) {
    // Convenience property to get card type
    val cardType: CardType
        get() = applicationInfo.cardType

    // Get all tags grouped by category
    fun getTagsByCategory(): Map<String, List<EmvTag>> {
        val manualTags = mapOf(
            "Application Information" to getApplicationTags(),
            "Transaction Data" to getTransactionTags(),
            "Cardholder Data" to getCardholderTags()
        )

        // Merge with additional tags, avoiding duplicates
        val manualTagIds = manualTags.values.flatten().map { it.tag }.toSet()
        val extraTags = additionalTags.values.filter { it.tag !in manualTagIds }

        // Group extra tags by category
        val extraByCategory = extraTags.groupBy { tag ->
            when {
                tag.tag.startsWith("4F") || tag.tag.startsWith("50") ||
                tag.tag.startsWith("87") || tag.tag.startsWith("9F38") -> "Application Information"

                tag.tag.startsWith("9F02") || tag.tag.startsWith("5F2A") ||
                tag.tag.startsWith("9A") || tag.tag.startsWith("9C") ||
                tag.tag.startsWith("9F36") || tag.tag.startsWith("9F37") -> "Transaction Data"

                tag.tag.startsWith("5A") || tag.tag.startsWith("5F24") ||
                tag.tag.startsWith("5F20") || tag.tag.startsWith("57") ||
                tag.tag.startsWith("5F34") -> "Cardholder Data"

                else -> "Other Tags"
            }
        }

        // Merge categories
        return manualTags.mapValues { (category, tags) ->
            tags + (extraByCategory[category] ?: emptyList())
        } + extraByCategory.filterKeys { it !in manualTags.keys }
    }

    // Get application-related tags
    private fun getApplicationTags(): List<EmvTag> {
        return buildList {
            // Tag 4F AID
            add(
                EmvTag(
                    tag = "4F",
                    tagName = "Application Identifier (AID)",
                    length = applicationInfo.aidBytes.size,
                    value = applicationInfo.aid,
                    valueDecoded = applicationInfo.aid,
                    description = "Payment application identifier"
                )
            )

            // Tag 50 Application Label
            applicationInfo.applicationLabel?.let { label ->
                add(
                    EmvTag(
                        tag = "50",
                        tagName = "Application Label",
                        length = label.length,
                        value = label.toByteArray().joinToString("") { "%02X".format(it) },
                        valueDecoded = label,
                        description = "Human-readable application name"
                    )
                )
            }

            // Tag 87 Priority Indicator
            applicationInfo.priorityIndicator?.let { priority ->
                add(
                    EmvTag(
                        tag = "87",
                        tagName = "Application Priority Indicator",
                        length = 1,
                        value = "%02X".format(priority),
                        valueDecoded = "Priority $priority",
                        description = "Application selection priority"
                    )
                )
            }

            // Tag 9F38 PDOL
            applicationInfo.pdol?.let { pdol ->
                add(
                    EmvTag(
                        tag = "9F38",
                        tagName = "PDOL",
                        length = pdol.length / 2,
                        value = pdol,
                        valueDecoded = applicationInfo.pdolDescription,
                        description = "Processing Data Object List"
                    )
                )
            }
        }
    }

    // Get transaction-related tags
    private fun getTransactionTags(): List<EmvTag> {
        return buildList {
            // Tag 9F02 Amount
            transactionData.amountAuthorised?.let { amount ->
                add(EmvTag(
                    tag = "9F02",
                    tagName = "Amount, Authorised",
                    length = 6,
                    value = String.format("%012X", amount),
                    valueDecoded = transactionData.amountAuthorisedDisplay,
                    description = "Authorized transaction amount"
                ))
            }

            // Tag 5F2A Currency Code
            transactionData.currencyCode?.let { code ->
                add(EmvTag(
                    tag = "5F2A",
                    tagName = "Transaction Currency Code",
                    length = 2,
                    value = code,
                    valueDecoded = transactionData.currencyName,
                    description = "ISO 4217 currency code"
                ))
            }

            // Tag 9A Transaction Date
            transactionData.transactionDate?.let { date ->
                add(EmvTag(
                    tag = "9A",
                    tagName = "Transaction Date",
                    length = 3,
                    value = date,
                    valueDecoded = transactionData.transactionDateDisplay,
                    description = "Transaction date (YYMMDD)"
                ))
            }

            // Tag 9C Transaction Type
            transactionData.transactionType?.let { type ->
                add(EmvTag(
                    tag = "9C",
                    tagName = "Transaction Type",
                    length = 1,
                    value = "%02X".format(type),
                    valueDecoded = transactionData.transactionTypeDescription,
                    description = "Transaction type"
                ))
            }

            // Tag 9F36 ATC
            transactionData.atc?.let { atc ->
                add(EmvTag(
                    tag = "9F36",
                    tagName = "Application Transaction Counter",
                    length = 2,
                    value = "%04X".format(atc),
                    valueDecoded = "ATC: $atc transactions",
                    description = "Application transaction counter"
                ))
            }

            // Tag 9F37 Unpredictable Number
            transactionData.unpredictableNumber?.let { nonce ->
                add(EmvTag(
                    tag = "9F37",
                    tagName = "Unpredictable Number",
                    length = 4,
                    value = nonce,
                    valueDecoded = nonce,
                    description = "Anti-replay nonce generated by terminal"
                ))
            }
        }
    }

    // Get cardholder-related tags
    private fun getCardholderTags(): List<EmvTag> {
        return buildList {
            // Tag 5A PAN
            add(EmvTag(
                tag = "5A",
                tagName = "Primary Account Number (PAN)",
                length = cardholderData.pan.length / 2,
                value = cardholderData.pan,
                valueDecoded = "****${cardholderData.panLastFour}",
                description = "Primary account number"
            ))

            // Tag 5F24 Expiration Date
            add(EmvTag(
                tag = "5F24",
                tagName = "Application Expiration Date",
                length = 3,
                value = cardholderData.expirationDate,
                valueDecoded = cardholderData.expirationDateDisplay,
                description = "Card expiration date"
            ))

            // Tag 5F20 Cardholder Name
            cardholderData.cardholderName?.let { name ->
                add(EmvTag(
                    tag = "5F20",
                    tagName = "Cardholder Name",
                    length = name.length,
                    value = name.toByteArray().joinToString("") { "%02X".format(it) },
                    valueDecoded = name,
                    description = "Cardholder name"
                ))
            }

            // Tag 57 Track 2
            cardholderData.track2Equivalent?.let { track2 ->
                add(EmvTag(
                    tag = "57",
                    tagName = "Track 2 Equivalent Data",
                    length = track2.length / 2,
                    value = track2,
                    valueDecoded = "Magnetic stripe data",
                    description = "Track 2 equivalent data"
                ))
            }

            // Tag 5F34 PAN Sequence Number
            cardholderData.panSequenceNumber?.let { seq ->
                add(EmvTag(
                    tag = "5F34",
                    tagName = "PAN Sequence Number",
                    length = 1,
                    value = "%02X".format(seq),
                    valueDecoded = "Sequence: $seq",
                    description = "PAN sequence number"
                ))
            }
        }
    }
}
