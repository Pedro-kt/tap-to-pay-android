package com.yumedev.taptopayandroid.presentation.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yumedev.taptopayandroid.R
import com.yumedev.taptopayandroid.data.preferences.PreferencesManager
import com.yumedev.taptopayandroid.domain.model.ApduCommand
import com.yumedev.taptopayandroid.domain.model.DetailLevel
import com.yumedev.taptopayandroid.domain.model.EmvCardData
import com.yumedev.taptopayandroid.presentation.ui.components.ApduCommandCard
import com.yumedev.taptopayandroid.presentation.ui.components.CustomTabSelector
import com.yumedev.taptopayandroid.presentation.ui.components.DetailSearchBar
import com.yumedev.taptopayandroid.presentation.ui.components.SimplifiedCardView
import com.yumedev.taptopayandroid.presentation.ui.components.TagCardContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    emvCardData: EmvCardData,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager.getInstance(context) }

    val detailLevel = when (preferencesManager.detailLevel) {
        PreferencesManager.DETAIL_LEVEL_SIMPLE -> DetailLevel.SIMPLE
        PreferencesManager.DETAIL_LEVEL_DETAILED -> DetailLevel.DETAILED
        else -> DetailLevel.DETAILED
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    BackHandler(onBack = onBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.transaction_details)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        if (detailLevel == DetailLevel.SIMPLE) {
            SimplifiedCardView(
                emvCardData = emvCardData,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                CustomTabSelector(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                )

                DetailSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = if (selectedTab == 0) stringResource(R.string.search_tag_aid_value) else stringResource(R.string.search_command_status),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp,end = 16.dp, top = 4.dp, bottom = 16.dp)
                )

                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        0 -> TagsAndAidTab(emvCardData, searchQuery)
                        1 -> ApduCommandsTab(emvCardData.apduCommands, searchQuery)
                    }
                }
            }
        }
    }
}

// Composable function to display the Tags & AID tab content
@Composable
fun TagsAndAidTab(
    emvCardData: EmvCardData,
    searchQuery: String
) {
    val tagsByCategory = emvCardData.getTagsByCategory()
    val filteredCategories = tagsByCategory.mapValues { (_, tags) ->
        tags.filter { tag ->
            if (searchQuery.isEmpty()) true
            else tag.tag.contains(searchQuery, ignoreCase = true) ||
                    tag.tagName.contains(searchQuery, ignoreCase = true) ||
                    tag.value.contains(searchQuery, ignoreCase = true) ||
                    tag.valueDecoded?.contains(searchQuery, ignoreCase = true) == true
        }
    }.filterValues { it.isNotEmpty() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        filteredCategories.entries.forEach { (category, tags) ->
            item(key = "category_$category") {
                Column {
                    // Category title
                    Text(
                        text = getCategoryName(category),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Grouped cards
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        tonalElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            tags.forEachIndexed { index, tag ->
                                TagCardContent(tag)
                                if (index < tags.size - 1) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ApduCommandsTab(
    commands: List<ApduCommand>,
    searchQuery: String
) {
    val filteredCommands = commands.filter { cmd ->
        if (searchQuery.isEmpty()) true
        else cmd.name.contains(searchQuery, ignoreCase = true) ||
                cmd.commandApdu.contains(searchQuery, ignoreCase = true) ||
                cmd.responseApdu.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = filteredCommands,
            key = { command -> command.sequence }
        ) { command ->
            ApduCommandCard(command)
        }
    }
}

@Composable
fun getCategoryName(category: String): String {
    return when (category) {
        "Application Information" -> stringResource(R.string.category_application_info)
        "Transaction Data" -> stringResource(R.string.category_transaction_data)
        "Cardholder Data" -> stringResource(R.string.category_cardholder_data)
        "Other Tags" -> stringResource(R.string.category_other_tags)
        else -> category.uppercase()
    }
}
