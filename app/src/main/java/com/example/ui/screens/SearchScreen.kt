package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.GamiturnUiState
import com.example.ui.theme.GamiturnTheme

@Composable
fun SearchScreen(
    uiState: GamiturnUiState,
    onQueryChange: (String) -> Unit,
    onToggleCategory: (String) -> Unit,
    onToggleAvailableOnly: () -> Unit,
    onToggleNearMeOnly: () -> Unit,
    onClearFilters: () -> Unit,
    onSelectItem: (Int) -> Unit,
    onPostNeedToChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = GamiturnTheme.palette
    var showNeedDialog by rememberSaveable { mutableStateOf(false) }
    var neededItemDraft by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.search_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Live search input matching PDF Screen 3
            item {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onQueryChange,
                    placeholder = { Text("Search calculator, speaker, lab coat...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search",
                            tint = palette.indigoAction
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Clear search"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input")
                )
            }

            // Result count + clear filters
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val count = uiState.filteredItems.size
                    val suffix = if (uiState.searchQuery.isNotBlank()) {
                        " for \"${uiState.searchQuery.trim()}\""
                    } else ""
                    Text(
                        text = "$count ${if (count == 1) "result" else "results"}$suffix",
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.textMuted
                    )
                    if (uiState.selectedCategory != null || uiState.availableOnly || uiState.nearMeOnly || uiState.searchQuery.isNotBlank()) {
                        TextButton(onClick = onClearFilters) {
                            Text("Reset", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            // Filter Chips Row matching PDF Screen 3
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = uiState.availableOnly,
                        onClick = onToggleAvailableOnly,
                        label = { Text("Available only") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = palette.indigoAction,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_available")
                    )
                    FilterChip(
                        selected = uiState.selectedCategory == "School supplies",
                        onClick = { onToggleCategory("School supplies") },
                        label = { Text("School supplies") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = palette.indigoAction,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_supplies")
                    )
                    FilterChip(
                        selected = uiState.selectedCategory == "Gadgets",
                        onClick = { onToggleCategory("Gadgets") },
                        label = { Text("Gadgets") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = palette.indigoAction,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_gadgets")
                    )
                    FilterChip(
                        selected = uiState.nearMeOnly,
                        onClick = onToggleNearMeOnly,
                        label = { Text("Near me") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = palette.indigoAction,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_near_me")
                    )
                }
            }

            // Results list
            if (uiState.filteredItems.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = palette.cardFrost,
                        border = BorderStroke(1.dp, palette.cardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No matching items found",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Try adjusting your filters or ask classmates in Org Group Chat below.",
                                style = MaterialTheme.typography.bodySmall,
                                color = palette.textMuted
                            )
                        }
                    }
                }
            } else {
                items(uiState.filteredItems, key = { it.id }) { item ->
                    SupplyItemRowCard(
                        item = item,
                        onClick = { onSelectItem(item.id) }
                    )
                }
            }

            // "Can't find it?" card matching PDF Screen 3
            item {
                Surface(
                    onClick = {
                        neededItemDraft = uiState.searchQuery.ifBlank { "" }
                        showNeedDialog = true
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = palette.indigoSoft,
                    border = BorderStroke(1.dp, palette.cardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("post_need_card")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Campaign,
                            contentDescription = "Post what you need",
                            tint = palette.indigoAction
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Can't find it?",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Post what you need in Group Chat and let owners come to you.",
                                style = MaterialTheme.typography.bodySmall,
                                color = palette.textMuted
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }

    if (showNeedDialog) {
        AlertDialog(
            onDismissRequest = { showNeedDialog = false },
            title = { Text("Ask Org Group Chat") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "What school supply or gadget do you need to borrow?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = neededItemDraft,
                        onValueChange = { neededItemDraft = it },
                        label = { Text("Item needed") },
                        placeholder = { Text("e.g. Scientific calculator for tomorrow's exam") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val text = neededItemDraft.ifBlank { "school supply" }
                        showNeedDialog = false
                        onPostNeedToChat(text)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = palette.indigoAction)
                ) {
                    Text("Post to Chat")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNeedDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
