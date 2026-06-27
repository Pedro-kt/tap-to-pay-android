package com.yumedev.taptopayandroid.presentation.ui.components

import android.view.SoundEffectConstants
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yumedev.taptopayandroid.R

@Composable
fun Keypad(
    onNumberClick: (Int) -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit
) {
    val keys = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        "C", "0", "⌫"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
    ) {
        keys.chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth()) {
                row.forEach { key ->
                    SquareKey(
                        label = key,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            when (key) {
                                "C" -> onClear()
                                "⌫" -> onBackspace()
                                else -> onNumberClick(key.toInt())
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SquareKey(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .height(80.dp)
            .border(
                0.5.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
            .clickable {
                view.playSoundEffect(SoundEffectConstants.CLICK)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        if (label == "⌫") {
            Icon(
                painter = painterResource(R.drawable.delete),
                contentDescription = "Delete",
                modifier = Modifier.size(26.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        } else {
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
