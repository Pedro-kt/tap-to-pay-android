package com.yumedev.taptopayandroid.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yumedev.taptopayandroid.R
import com.yumedev.taptopayandroid.domain.model.EmvTagInfo
import com.yumedev.taptopayandroid.domain.model.TagCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagInfoBottomSheet(
    tagInfo: EmvTagInfo,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = tagInfo.tag,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = getCategoryString(tagInfo.category),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = stringResource(tagInfo.nameResId),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            HorizontalDivider()

            InfoSection(
                title = stringResource(tagInfo.nameResId),
                content = stringResource(tagInfo.descriptionResId)
            )

            InfoSection(
                title = stringResource(R.string.tag_info_purpose_label),
                content = stringResource(tagInfo.purposeResId)
            )

            InfoSection(
                title = stringResource(R.string.tag_info_format_label),
                content = stringResource(tagInfo.formatResId)
            )

            InfoSection(
                title = stringResource(R.string.tag_info_source_label),
                content = stringResource(tagInfo.sourceResId)
            )
        }
    }
}

@Composable
private fun InfoSection(
    title: String,
    content: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(12.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun getCategoryString(category: TagCategory): String {
    return when (category) {
        TagCategory.APPLICATION -> stringResource(R.string.tag_category_application)
        TagCategory.TRANSACTION -> stringResource(R.string.tag_category_transaction)
        TagCategory.CARDHOLDER -> stringResource(R.string.tag_category_cardholder)
        TagCategory.SECURITY -> stringResource(R.string.tag_category_security)
        TagCategory.PROCESSING -> stringResource(R.string.tag_category_processing)
        TagCategory.TERMINAL -> stringResource(R.string.tag_category_terminal)
        TagCategory.OTHER -> stringResource(R.string.tag_category_other)
    }
}
