package com.yumedev.taptopayandroid.presentation.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.yumedev.taptopayandroid.R
import com.yumedev.taptopayandroid.domain.model.EmvCardData
import com.yumedev.taptopayandroid.domain.model.CardType
import com.yumedev.taptopayandroid.presentation.ui.theme.md_dark_success
import com.yumedev.taptopayandroid.presentation.ui.theme.md_light_success
import kotlinx.coroutines.delay

@Composable
fun SuccessScreen(
    amount: String,
    emvCardData: EmvCardData,
    innerPadding: PaddingValues,
    onNavigateToDetails: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    // Auto-navigate after 3 seconds
    LaunchedEffect(Unit) {
        delay(3000)
        onNavigateToDetails()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Surface(
            modifier = Modifier.clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                        append("${stringResource(id = R.string.total_label)} ")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("$$amount")
                    }
                },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Main content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            // Success check icon in circle
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp.dp
            val iconSize = (screenWidth * 0.35f).coerceAtMost(150.dp)
            val successColor = if (isDarkTheme) md_dark_success else md_light_success

            Box(
                modifier = Modifier
                    .size(iconSize)
                    .clip(CircleShape)
                    .background(successColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Success",
                    modifier = Modifier.size(iconSize * 0.5f),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // "Tarjeta leída" message
            Text(
                text = stringResource(id = R.string.card_read),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append(getCardTypeName(emvCardData.cardType))
                            append(" ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("•••• ${emvCardData.cardholderData.panLastFour}")
                            }
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "loading")
                val rotation by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "rotation"
                )

                CircularProgressIndicator(
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(rotation),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = stringResource(id = R.string.opening_details),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun getCardTypeName(cardType: CardType): String {
    return when (cardType) {
        CardType.VISA -> "Visa"
        CardType.MASTERCARD -> "Mastercard"
        CardType.AMEX -> "American Express"
        CardType.DISCOVER -> "Discover"
        CardType.MAESTRO -> "Maestro"
        CardType.UNKNOWN -> "Card"
    }
}
