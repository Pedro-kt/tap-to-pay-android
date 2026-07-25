package com.yumedev.taptopayandroid.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.yumedev.taptopayandroid.R
import com.yumedev.taptopayandroid.domain.model.ApduCommand

@Composable
fun ApduCommandCard(command: ApduCommand) {
    val clipboardManager = LocalClipboardManager.current
    var showStructure by remember(command.sequence) { mutableStateOf(false) }
    val isSuccess = command.statusWord.startsWith("90")
    val statusColor = if (isSuccess)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.errorContainer

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box {
                        Surface(
                            shape = CircleShape,
                            color = statusColor
                        ) {
                            Text(
                                text = command.sequence.toString(),
                                modifier = Modifier.padding(10.dp),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isSuccess)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onErrorContainer
                            )
                        }

                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 2.dp, y = 2.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Icon(
                                imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(2.dp),
                                tint = if (isSuccess)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = command.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        command.description?.let { desc ->
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusColor
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isSuccess) Icons.Default.Done else Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isSuccess)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = command.statusWord,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSuccess)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Text(
                        text = command.statusDescription,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // C-APDU
            ApduSection(
                label = stringResource(R.string.apdu_command_label),
                icon = Icons.Default.ArrowForward,
                data = command.commandApdu,
                isCommand = true,
                onCopy = { clipboardManager.setText(AnnotatedString(command.commandApdu)) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // R-APDU
            ApduSection(
                label = stringResource(R.string.apdu_response_label),
                icon = Icons.Default.ArrowBack,
                data = command.responseApdu,
                isCommand = false,
                onCopy = { clipboardManager.setText(AnnotatedString(command.responseApdu)) }
            )

            if (command.commandApdu.replace(" ", "").length >= 8) {
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    onClick = { showStructure = !showStructure },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = if (showStructure) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (showStructure) stringResource(R.string.hide_apdu_structure) else stringResource(R.string.show_apdu_structure),
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                if (showStructure) {
                    ApduStructureInfo(command.commandApdu)
                }
            }
        }
    }
}

@Composable
private fun ApduSection(
    label: String,
    icon: ImageVector,
    data: String,
    isCommand: Boolean,
    onCopy: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "${data.length / 2} ${stringResource(R.string.bytes_label)}",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                }

                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = stringResource(R.string.copy),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ) {
            Text(
                text = if (isCommand && data.length >= 8) {
                    formatApduCommandWithColors(data)
                } else {
                    buildAnnotatedString {
                        append(formatHexWithSpaces(data))
                    }
                },
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight.times(1.4f)
            )
        }
    }
}

// Format APDU command with color highlighting for different parts
@Composable
private fun formatApduCommandWithColors(hex: String): AnnotatedString {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val onSurface = MaterialTheme.colorScheme.onSurface

    return buildAnnotatedString {
        val formatted = formatHexWithSpaces(hex)
        val parts = formatted.split(" ")

        if (parts.size >= 4) {
            // CLA (Class)
            withStyle(style = SpanStyle(color = primary, fontWeight = FontWeight.Bold)) {
                append(parts[0])
            }
            append(" ")

            // INS (Instruction)
            withStyle(style = SpanStyle(color = secondary, fontWeight = FontWeight.Bold)) {
                append(parts[1])
            }
            append(" ")

            // P1 (Parameter 1)
            withStyle(style = SpanStyle(color = tertiary)) {
                append(parts[2])
            }
            append(" ")

            // P2 (Parameter 2)
            withStyle(style = SpanStyle(color = tertiary)) {
                append(parts[3])
            }

            // Resto (Lc, Data, Le)
            if (parts.size > 4) {
                append(" ")
                withStyle(style = SpanStyle(color = onSurface.copy(alpha = 0.7f))) {
                    append(parts.drop(4).joinToString(" "))
                }
            }
        } else {
            append(formatted)
        }
    }
}

@Composable
private fun ApduStructureInfo(commandApdu: String) {
    val cleanHex = commandApdu.replace(" ", "").trim()
    val bytes = cleanHex.chunked(2)

    if (bytes.size < 4) return

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = stringResource(R.string.apdu_structure_title),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // CLA
            StructureItem(stringResource(R.string.apdu_field_cla), bytes[0], stringResource(R.string.apdu_cla_description))

            // INS
            StructureItem(stringResource(R.string.apdu_field_ins), bytes[1], getInsDescription(bytes[1]))

            // P1
            StructureItem(stringResource(R.string.apdu_field_p1), bytes[2], stringResource(R.string.apdu_p1_description))

            // P2
            StructureItem(stringResource(R.string.apdu_field_p2), bytes[3], stringResource(R.string.apdu_p2_description))

            if (bytes.size > 4) {
                val lcValue = bytes[4].toIntOrNull(16) ?: 0

                when {
                    lcValue > 0 && bytes.size > 5 -> {
                        StructureItem(stringResource(R.string.apdu_field_lc), bytes[4], stringResource(R.string.apdu_lc_description, lcValue))

                        val dataEndIndex = minOf(5 + lcValue, bytes.size)
                        if (bytes.size >= dataEndIndex && dataEndIndex > 5) {
                            val dataBytes = bytes.subList(5, dataEndIndex).joinToString(" ")
                            val actualDataLength = dataEndIndex - 5
                            StructureItem(stringResource(R.string.apdu_field_data), dataBytes, stringResource(R.string.apdu_data_description, actualDataLength))

                            if (bytes.size > dataEndIndex) {
                                StructureItem(stringResource(R.string.apdu_field_le), bytes[dataEndIndex], stringResource(R.string.apdu_le_description))
                            }
                        }
                    }
                    lcValue == 0 -> {
                        StructureItem(stringResource(R.string.apdu_field_le), bytes[4], stringResource(R.string.apdu_le_256_description))
                    }
                    else -> {
                        val remaining = bytes.subList(4, bytes.size).joinToString(" ")
                        StructureItem(stringResource(R.string.apdu_field_data), remaining, stringResource(R.string.apdu_additional_data))
                    }
                }
            }
        }
    }
}

@Composable
private fun StructureItem(
    name: String,
    value: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            modifier = Modifier.width(40.dp)
        ) {
            Text(
                text = name,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = value.uppercase(),
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(60.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
    }
}

// Helper to get INS byte description
@Composable
private fun getInsDescription(ins: String): String {
    return when (ins.uppercase()) {
        "A4" -> stringResource(R.string.ins_select)
        "B0" -> stringResource(R.string.ins_read_binary)
        "B2" -> stringResource(R.string.ins_read_record)
        "CA" -> stringResource(R.string.ins_get_data)
        "20" -> stringResource(R.string.ins_verify)
        "88" -> stringResource(R.string.ins_internal_auth)
        "82" -> stringResource(R.string.ins_external_auth)
        "84" -> stringResource(R.string.ins_get_challenge)
        "C0" -> stringResource(R.string.ins_get_response)
        else -> stringResource(R.string.ins_instruction)
    }
}

private fun formatHexWithSpaces(hex: String): String {
    return hex.chunked(2).joinToString(" ").uppercase()
}
