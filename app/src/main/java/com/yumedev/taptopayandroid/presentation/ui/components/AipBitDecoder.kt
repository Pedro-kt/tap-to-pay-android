package com.yumedev.taptopayandroid.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yumedev.taptopayandroid.R
import com.yumedev.taptopayandroid.domain.model.AipDecoded

@Composable
fun AipBitDecoder(aip: AipDecoded) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.aip_bit_decoding_title),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            BitItem(
                label = stringResource(R.string.aip_sda_supported),
                description = stringResource(R.string.aip_sda_description),
                isSupported = aip.sdaSupported
            )

            BitItem(
                label = stringResource(R.string.aip_dda_supported),
                description = stringResource(R.string.aip_dda_description),
                isSupported = aip.ddaSupported
            )

            BitItem(
                label = stringResource(R.string.aip_cda_supported),
                description = stringResource(R.string.aip_cda_description),
                isSupported = aip.cdaSupported
            )

            BitItem(
                label = stringResource(R.string.aip_cardholder_verification),
                description = null,
                isSupported = aip.cardholderVerificationSupported
            )

            BitItem(
                label = stringResource(R.string.aip_terminal_risk_management),
                description = null,
                isSupported = aip.terminalRiskManagementRequired
            )

            BitItem(
                label = stringResource(R.string.aip_issuer_authentication),
                description = null,
                isSupported = aip.issuerAuthenticationSupported
            )

            BitItem(
                label = stringResource(R.string.aip_on_device_verification),
                description = null,
                isSupported = aip.onDeviceCardholderVerificationSupported
            )

            BitItem(
                label = stringResource(R.string.aip_crl_supported),
                description = null,
                isSupported = aip.crlSupported
            )
        }
    }
}

@Composable
private fun BitItem(
    label: String,
    description: String?,
    isSupported: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isSupported) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = if (isSupported)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSupported) FontWeight.Medium else FontWeight.Normal,
                color = if (isSupported)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )

            description?.let { desc ->
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}
