package com.yumedev.taptopayandroid.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yumedev.taptopayandroid.R
import com.yumedev.taptopayandroid.domain.model.ApduCommand

@Composable
fun ApduCommandCard(command: ApduCommand) {
    val clipboardManager = LocalClipboardManager.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp
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
                    // Sequence Number
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = command.sequence.toString(),
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Command Name
                    Column {
                        Text(
                            text = command.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
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

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (command.statusWord.startsWith("90")) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        text = "${command.statusWord} - ${command.statusDescription}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (command.statusWord.startsWith("90")) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // C-APDU
            ApduSection(
                label = stringResource(R.string.apdu_command_label),
                icon = Icons.Default.ArrowForward,
                data = command.commandApdu,
                onCopy = { clipboardManager.setText(AnnotatedString(command.commandApdu)) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // R-APDU
            ApduSection(
                label = stringResource(R.string.apdu_response_label),
                icon = Icons.Default.Check,
                data = command.responseApdu,
                onCopy = { clipboardManager.setText(AnnotatedString(command.responseApdu)) }
            )
        }
    }
}

@Composable
private fun ApduSection(
    label: String,
    icon: ImageVector,
    data: String,
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
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
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

        Spacer(modifier = Modifier.height(4.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = formatHexWithSpaces(data),
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

private fun formatHexWithSpaces(hex: String): String {
    return hex.chunked(2).joinToString(" ").uppercase()
}
