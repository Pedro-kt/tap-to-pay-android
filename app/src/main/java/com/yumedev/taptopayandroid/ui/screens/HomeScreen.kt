package com.yumedev.taptopayandroid.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yumedev.taptopayandroid.ui.components.Keypad
import com.yumedev.taptopayandroid.ui.components.PrimaryButton

private fun formatCurrency(digits: String): String {
    if (digits.isEmpty()) return "0.00"
    val padded = digits.padStart(3, '0')
    val intPart = padded.dropLast(2).trimStart('0').ifEmpty { "0" }
    val decPart = padded.takeLast(2)
    val formattedInt = intPart.reversed().chunked(3).joinToString(",").reversed()
    return "$formattedInt.$decPart"
}

@Composable
fun HomeScreen(
    onGeneratePayment: (String) -> Unit,
    innerPadding: PaddingValues = PaddingValues()
) {
    var rawDigits by remember { mutableStateOf("") }
    val displayAmount = formatCurrency(rawDigits)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        val amountFontSize = when {
            displayAmount.length <= 9 -> 80.sp
            else -> 64.sp
        }

        Text(
            text = "$displayAmount",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(24.dp),
            fontWeight = FontWeight.Bold,
            fontSize = amountFontSize,
            maxLines = 1
        )

        Spacer(modifier = Modifier.weight(1f))

        Keypad(
            onNumberClick = { number ->
                if (rawDigits.isEmpty() && number == 0) return@Keypad
                if (rawDigits.length < 9) rawDigits += number.toString()
            },
            onClear = {
                rawDigits = ""
            },
            onBackspace = {
                rawDigits = rawDigits.dropLast(1)
            }
        )

        Spacer(modifier = Modifier.weight(0.3f))

        PrimaryButton(
            text = "Start Payment",
            onClick = { onGeneratePayment(displayAmount) },
            modifier = Modifier.padding(16.dp),
            isEnable = displayAmount != "0.00",
            leadingIcon = Icons.Default.Nfc
        )
        Spacer(modifier = Modifier.weight(0.3f))
    }
}
