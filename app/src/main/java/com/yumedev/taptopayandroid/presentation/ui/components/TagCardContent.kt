package com.yumedev.taptopayandroid.presentation.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yumedev.taptopayandroid.R
import com.yumedev.taptopayandroid.domain.model.EmvTag
import com.yumedev.taptopayandroid.domain.repository.EmvTagInfoRepository

@Composable
fun TagCardContent(
    tag: EmvTag,
    tagInfoRepository: EmvTagInfoRepository = hiltViewModel<TagInfoViewModel>().tagInfoRepository
) {

    val clipboardManager = LocalClipboardManager.current
    var isExpanded by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }
    val isLongValue = tag.value.length > 32
    val shouldShowExpandButton = isLongValue
    val tagIcon = getTagIcon(tag.tag)
    val tagImportance = getTagImportance(tag.tag)
    val badgeColor = getBadgeColor(tagImportance)
    val hasDetailedInfo = tagInfoRepository.hasDetailedInfo(tag.tag)
    val tagInfo = tagInfoRepository.getTagInfo(tag.tag)

    if (showInfoDialog && tagInfo != null) {
        TagInfoBottomSheet(
            tagInfo = tagInfo,
            onDismiss = { showInfoDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = tagIcon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = badgeColor.first
                )

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor.first
                ) {
                    Text(
                        text = tag.tag,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = badgeColor.second,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tag.tagName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (tagImportance == TagImportance.CRITICAL) {
                        Text(
                            text = stringResource(R.string.critical_tag),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (hasDetailedInfo) {
                    Surface(
                        onClick = { showInfoDialog = true },
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant
                        ),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = stringResource(R.string.tag_info_title),
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Surface(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(tag.value))
                    },
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant
                    ),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = stringResource(R.string.copy),
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${stringResource(R.string.hex_value_label)} (${tag.length} ${stringResource(R.string.bytes_suffix)})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )

                    if (shouldShowExpandButton) {
                        TextButton(
                            onClick = { isExpanded = !isExpanded },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = if (isExpanded) stringResource(R.string.show_less) else stringResource(R.string.show_all),
                                style = MaterialTheme.typography.labelSmall
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                val displayValue = if (isExpanded || !isLongValue) {
                    formatHexWithSpaces(tag.value)
                } else {
                    formatHexWithSpaces(tag.value.take(32)) + "..."
                }

                Text(
                    text = displayValue,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        tag.valueDecoded?.let { decoded ->
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResource(R.string.decoded_label),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = decoded,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        tag.description?.let { desc ->
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

private fun getTagIcon(tagId: String): ImageVector {
    return when (tagId) {
        "5A" -> Icons.Default.CreditCard // PAN
        "5F20" -> Icons.Default.Person // Cardholder Name
        "5F24" -> Icons.Default.CalendarToday // Expiration Date
        "5F30" -> Icons.Default.Tag // Service Code
        "9F12" -> Icons.AutoMirrored.Filled.Label // Application Preferred Name
        "4F" -> Icons.Default.Fingerprint // AID
        "50" -> Icons.Default.AppRegistration // Application Label
        "57" -> Icons.Default.Storage // Track 2 Equivalent
        "9F26" -> Icons.Default.Lock // Application Cryptogram
        "9F27" -> Icons.Default.VpnKey // Cryptogram Info
        "9F36" -> Icons.Default.Numbers // ATC
        "9F37" -> Icons.Default.Shuffle // Unpredictable Number
        "8C", "8D" -> Icons.AutoMirrored.Filled.List // CDOL
        "9F38" -> Icons.AutoMirrored.Filled.ListAlt // PDOL
        "94" -> Icons.Default.Folder // AFL
        else -> Icons.Default.Tag
    }
}

enum class TagImportance {
    CRITICAL,
    HIGH,
    NORMAL
}

private fun getTagImportance(tagId: String): TagImportance {
    return when (tagId) {
        "5A", "5F20", "5F24", "57" -> TagImportance.CRITICAL
        "4F", "50", "9F26", "9F27", "9F36" -> TagImportance.HIGH
        else -> TagImportance.NORMAL
    }
}

@Composable
private fun getBadgeColor(importance: TagImportance): Pair<androidx.compose.ui.graphics.Color, androidx.compose.ui.graphics.Color> {
    return when (importance) {
        TagImportance.CRITICAL -> Pair(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer
        )
        TagImportance.HIGH -> Pair(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        TagImportance.NORMAL -> Pair(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

private fun formatHexWithSpaces(hex: String): String {
    return hex.chunked(2).joinToString(" ").uppercase()
}