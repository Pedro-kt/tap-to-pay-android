package com.yumedev.taptopayandroid.presentation.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yumedev.taptopayandroid.R

@Composable
fun DetailSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.height(50.dp),
        placeholder = {
            Text(
                placeholder,
                style = MaterialTheme.typography.bodySmall
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = stringResource(R.string.search),
                modifier = Modifier.size(18.dp)
            )
        },
        trailingIcon = {
            Icon(
                Icons.Default.FilterList,
                contentDescription = stringResource(R.string.filter),
                modifier = Modifier.size(18.dp)
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        textStyle = MaterialTheme.typography.bodySmall,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}
