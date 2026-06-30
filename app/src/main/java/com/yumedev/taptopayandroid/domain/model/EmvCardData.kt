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
        return mapOf(
            "Información de la Aplicación" to getApplicationTags(),
            "Datos de la Transacción" to getTransactionTags(),
            "Datos del Tarjetahabiente" to getCardholderTags()
        )
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
                    description = "Identificador de aplicación de pago"
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
                        description = "Nombre legible de la aplicación"
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
                        valueDecoded = "Prioridad $priority",
                        description = "Prioridad de selección de aplicación"
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
                    description = "Monto autorizado de la transacción"
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
                    description = "Código de moneda ISO 4217"
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
                    description = "Fecha de la transacción (YYMMDD)"
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
                    description = "Tipo de transacción"
                ))
            }

            // Tag 9F36 ATC
            transactionData.atc?.let { atc ->
                add(EmvTag(
                    tag = "9F36",
                    tagName = "Application Transaction Counter",
                    length = 2,
                    value = "%04X".format(atc),
                    valueDecoded = "ATC: $atc transacciones",
                    description = "Contador de transacciones de la aplicación"
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
                    description = "Nonce anti-replay generado por el terminal"
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
                description = "Número de cuenta principal"
            ))

            // Tag 5F24 Expiration Date
            add(EmvTag(
                tag = "5F24",
                tagName = "Application Expiration Date",
                length = 3,
                value = cardholderData.expirationDate,
                valueDecoded = cardholderData.expirationDateDisplay,
                description = "Fecha de vencimiento de la tarjeta"
            ))

            // Tag 5F20 Cardholder Name
            cardholderData.cardholderName?.let { name ->
                add(EmvTag(
                    tag = "5F20",
                    tagName = "Cardholder Name",
                    length = name.length,
                    value = name.toByteArray().joinToString("") { "%02X".format(it) },
                    valueDecoded = name,
                    description = "Nombre del tarjetahabiente"
                ))
            }

            // Tag 57 Track 2
            cardholderData.track2Equivalent?.let { track2 ->
                add(EmvTag(
                    tag = "57",
                    tagName = "Track 2 Equivalent Data",
                    length = track2.length / 2,
                    value = track2,
                    valueDecoded = "Datos de banda magnética",
                    description = "Datos equivalentes a Track 2"
                ))
            }

            // Tag 5F34 PAN Sequence Number
            cardholderData.panSequenceNumber?.let { seq ->
                add(EmvTag(
                    tag = "5F34",
                    tagName = "PAN Sequence Number",
                    length = 1,
                    value = "%02X".format(seq),
                    valueDecoded = "Secuencia: $seq",
                    description = "Número de secuencia del PAN"
                ))
            }
        }
    }
}
