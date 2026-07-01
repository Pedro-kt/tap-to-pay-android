package com.yumedev.taptopayandroid.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yumedev.taptopayandroid.R

@Composable
fun CustomTabSelector(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        ),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Tags & AID Tab
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onTabSelected(0) },
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    bottomStart = 20.dp,
                    topEnd = 0.dp,
                    bottomEnd = 0.dp
                ),
                color = if (selectedTab == 0) MaterialTheme.colorScheme.primary
                else Color.Transparent
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.tab_tags_aid),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (selectedTab == 0) FontWeight.Medium else FontWeight.Normal,
                        color = if (selectedTab == 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Divider
            if (selectedTab == 1) {
                VerticalDivider(
                    modifier = Modifier.fillMaxHeight(),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }

            // Comandos APDU Tab
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onTabSelected(1) },
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    bottomStart = 0.dp,
                    topEnd = 20.dp,
                    bottomEnd = 20.dp
                ),
                color = if (selectedTab == 1) MaterialTheme.colorScheme.primary
                else Color.Transparent
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.tab_apdu_commands),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (selectedTab == 1) FontWeight.Medium else FontWeight.Normal,
                        color = if (selectedTab == 1) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
