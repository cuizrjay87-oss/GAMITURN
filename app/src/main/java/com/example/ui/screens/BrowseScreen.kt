package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.StudentProfileEntity
import com.example.data.local.SupplyItemEntity
import com.example.ui.GamiturnUiState
import com.example.ui.components.GamiturnCard
import com.example.ui.components.GamiturnLogoBadge
import com.example.ui.components.ItemThumbnail
import com.example.ui.components.StatusPill
import com.example.ui.theme.GamiturnTheme

@Composable
fun BrowseScreen(
    uiState: GamiturnUiState,
    onOpenSearch: () -> Unit,
    onSelectCategory: (String) -> Unit,
    onSelectItem: (Int) -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = GamiturnTheme.palette
    val profile = uiState.profile
    val initial = profile?.fullName?.firstOrNull()?.uppercaseChar()?.toString() ?: "S"

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header with Gamiturn badge, title, and student avatar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GamiturnLogoBadge(size = 38.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.browse_items_title),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = profile?.schoolName ?: "Student Borrowing Hub",
                                style = MaterialTheme.typography.bodySmall,
                                color = palette.textMuted
                            )
                        }
                    }

                    // Student Avatar Button
                    Surface(
                        onClick = onOpenProfile,
                        shape = CircleShape,
                        color = palette.indigoAction,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("profile_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = initial,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Search bar trigger matching PDF Screen 2
            item {
                Surface(
                    onClick = onOpenSearch,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("browse_search_bar"),
                    shape = RoundedCornerShape(16.dp),
                    color = palette.cardFrost,
                    border = androidx.compose.foundation.BorderStroke(1.dp, palette.cardBorder),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search items",
                            tint = palette.textMuted
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Search an item...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = palette.textMuted
                        )
                    }
                }
            }

            // CATEGORIES Section
            item {
                Text(
                    text = "CATEGORIES",
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.textMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    CategoryTile(
                        title = "School supplies",
                        itemCount = uiState.schoolSuppliesCount,
                        icon = Icons.Rounded.MenuBook,
                        onClick = { onSelectCategory("School supplies") },
                        testTag = "category_school_supplies"
                    )
                    CategoryTile(
                        title = "Gadgets",
                        itemCount = uiState.gadgetsCount,
                        icon = Icons.Rounded.Headphones,
                        onClick = { onSelectCategory("Gadgets") },
                        testTag = "category_gadgets"
                    )
                }
            }

            // Responsible Sharing Banner (subtle nod to SDG 12 on Page 4 of PDF)
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = palette.indigoSoft,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Eco,
                            contentDescription = "Responsible sharing",
                            tint = palette.availableText,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Share smarter, spend less",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Borrow school essentials from verified classmates instead of buying new.",
                                style = MaterialTheme.typography.bodySmall,
                                color = palette.textMuted
                            )
                        }
                    }
                }
            }

            // POPULAR NEAR YOU Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "POPULAR NEAR YOU",
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.textMuted
                    )
                    Text(
                        text = "${uiState.items.count { it.isAvailable }} available",
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.indigoAction
                    )
                }
            }

            items(uiState.items, key = { it.id }) { item ->
                SupplyItemRowCard(
                    item = item,
                    onClick = { onSelectItem(item.id) }
                )
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }
}

@Composable
private fun CategoryTile(
    title: String,
    itemCount: Int,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    val palette = GamiturnTheme.palette
    GamiturnCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = palette.indigoSoft,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = palette.indigoAction,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$itemCount items available to browse",
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.textMuted
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = "Open $title",
                tint = palette.textMuted
            )
        }
    }
}

@Composable
fun SupplyItemRowCard(
    item: SupplyItemEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = GamiturnTheme.palette
    GamiturnCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .testTag("supply_item_${item.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ItemThumbnail(
                iconType = item.iconType,
                photoUri = item.photoUri,
                contentDescription = item.title
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${item.category} · ${item.ownerName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Rounded.Verified,
                        contentDescription = "Verified student owner",
                        tint = palette.navyBrand,
                        modifier = Modifier.size(13.dp)
                    )
                }
                Text(
                    text = item.distanceLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.navyBrand
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            StatusPill(label = if (item.isAvailable) "Available" else "Borrowed")
        }
    }
}
