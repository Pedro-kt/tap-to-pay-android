package com.yumedev.taptopayandroid.presentation.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.BrightnessAuto
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Splitscreen
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yumedev.taptopayandroid.R
import com.yumedev.taptopayandroid.data.preferences.PreferencesManager
import java.util.Calendar
import androidx.core.net.toUri

enum class ThemeOption {
    LIGHT, DARK, SYSTEM
}

@Composable
fun SettingsScreen(
    innerPadding: PaddingValues = PaddingValues(),
    onThemeChanged: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val packageInfo = remember {
        context.packageManager.getPackageInfo(context.packageName, 0)
    }
    val versionName = packageInfo.versionName
    val versionText = "$versionName"

    val preferencesManager = remember { PreferencesManager.getInstance(context) }

    val initialTheme = when (preferencesManager.themeMode) {
        PreferencesManager.THEME_LIGHT -> ThemeOption.LIGHT
        PreferencesManager.THEME_DARK -> ThemeOption.DARK
        PreferencesManager.THEME_SYSTEM -> ThemeOption.SYSTEM
        else -> ThemeOption.SYSTEM
    }

    var selectedTheme by remember { mutableStateOf(initialTheme) }
    var soundEnabled by remember { mutableStateOf(preferencesManager.isSoundEnabled) }
    var rawLogsEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            SettingsSection(
                title = stringResource(R.string.appearance_section)
            ) {
                SettingsItem(
                    icon = Icons.Outlined.BrightnessAuto,
                    title = stringResource(R.string.theme_title),
                    subtitle = stringResource(R.string.theme_subtitle)
                ) {
                    ThemeSelector(
                        selectedTheme = selectedTheme,
                        onThemeSelected = { newTheme ->
                            selectedTheme = newTheme
                            val themeMode = when (newTheme) {
                                ThemeOption.LIGHT -> PreferencesManager.THEME_LIGHT
                                ThemeOption.DARK -> PreferencesManager.THEME_DARK
                                ThemeOption.SYSTEM -> PreferencesManager.THEME_SYSTEM
                            }
                            onThemeChanged(themeMode)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    )
                }
            }
        }

        item {
            SettingsSection(
                title = stringResource(R.string.sound_vibration_section)
            ) {
                SettingsItemSwitch(
                    icon = Icons.Outlined.VolumeUp,
                    title = stringResource(R.string.sound_on_detect_title),
                    subtitle = stringResource(R.string.sound_on_detect_subtitle),
                    checked = soundEnabled,
                    onCheckedChange = {
                        soundEnabled = it
                        preferencesManager.isSoundEnabled = it
                    }
                )
            }
        }

        item {
            SettingsSection(
                title = stringResource(R.string.developer_mode_section)
            ) {
                SettingsItemSwitch(
                    icon = Icons.Outlined.Code,
                    title = stringResource(R.string.raw_nfc_logs_title),
                    subtitle = stringResource(R.string.raw_nfc_logs_subtitle),
                    checked = rawLogsEnabled,
                    onCheckedChange = { rawLogsEnabled = it }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = 56.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                SettingsItemNavigable(
                    icon = Icons.Outlined.Splitscreen,
                    title = stringResource(R.string.detail_level_title),
                    subtitle = stringResource(R.string.detail_level_subtitle),
                    endText = stringResource(R.string.detail_level_detailed),
                    onClick = { }
                )
            }
        }

        item {
            SettingsSection(
                title = stringResource(R.string.about_section)
            ) {
                SettingsItemInfo(
                    icon = Icons.Outlined.Info,
                    title = stringResource(R.string.version_title),
                    subtitle = versionText
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = 56.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                SettingsItemNavigable(
                    icon = Icons.Outlined.Code,
                    title = stringResource(R.string.developer_title),
                    subtitle = stringResource(R.string.developer_subtitle),
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW,
                            "https://${context.getString(R.string.developer_github)}".toUri())
                        context.startActivity(intent)
                    }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = 56.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                SettingsItemNavigable(
                    icon = Icons.Outlined.OpenInNew,
                    title = stringResource(R.string.repository_title),
                    subtitle = stringResource(R.string.repository_url),
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW,
                            "https://${context.getString(R.string.repository_url)}".toUri())
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 1.dp
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
        }
        content()
    }
}

@Composable
private fun SettingsItemSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
private fun SettingsItemNavigable(
    icon: ImageVector,
    title: String,
    subtitle: String,
    endText: String? = null,
    showChevron: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }
        if (endText != null) {
            Text(
                text = endText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
            if (showChevron) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
        if (showChevron) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsItemInfo(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ThemeSelector(
    selectedTheme: ThemeOption,
    onThemeSelected: (ThemeOption) -> Unit,
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
            ThemeOption(
                text = stringResource(R.string.theme_light),
                icon = Icons.Outlined.BrightnessAuto,
                isSelected = selectedTheme == ThemeOption.LIGHT,
                onClick = { onThemeSelected(ThemeOption.LIGHT) },
                modifier = Modifier.weight(1f),
                cornerRadius = RoundedCornerShape(
                    topStart = 20.dp,
                    bottomStart = 20.dp,
                    topEnd = 0.dp,
                    bottomEnd = 0.dp
                )
            )

            if (selectedTheme != ThemeOption.LIGHT) {
                androidx.compose.material3.VerticalDivider(
                    modifier = Modifier.fillMaxHeight(),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }

            ThemeOption(
                text = stringResource(R.string.theme_dark),
                icon = Icons.Outlined.DarkMode,
                isSelected = selectedTheme == ThemeOption.DARK,
                onClick = { onThemeSelected(ThemeOption.DARK) },
                modifier = Modifier.weight(1f),
                cornerRadius = RoundedCornerShape(0.dp)
            )

            if (selectedTheme != ThemeOption.DARK) {
                androidx.compose.material3.VerticalDivider(
                    modifier = Modifier.fillMaxHeight(),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }

            ThemeOption(
                text = stringResource(R.string.theme_system),
                icon = Icons.Outlined.PhoneAndroid,
                isSelected = selectedTheme == ThemeOption.SYSTEM,
                onClick = { onThemeSelected(ThemeOption.SYSTEM) },
                modifier = Modifier.weight(1f),
                cornerRadius = RoundedCornerShape(
                    topStart = 0.dp,
                    bottomStart = 0.dp,
                    topEnd = 20.dp,
                    bottomEnd = 20.dp
                )
            )
        }
    }
}

@Composable
private fun ThemeOption(
    text: String,
    icon: ImageVector?,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: RoundedCornerShape
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick),
        shape = cornerRadius,
        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null && isSelected) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
