package com.example.ui.screens

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.ItemReviewEntity
import com.example.data.local.SupplyItemEntity
import com.example.ui.components.GamiturnCard
import com.example.ui.components.StatusPill
import com.example.ui.components.itemIconForType
import com.example.ui.theme.GamiturnTheme

@Composable
fun ItemDetailScreen(
    item: SupplyItemEntity?,
    reviews: List<ItemReviewEntity>,
    onBack: () -> Unit,
    onRequestBorrow: (Int) -> Unit,
    onMessageOwner: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = GamiturnTheme.palette

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        if (item == null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Item not found", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(onClick = onBack) {
                    Text("Go back")
                }
            }
            return@Box
        }

        LazyColumn(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top bar with Back navigation
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("item_detail_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.item_details_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Large item preview card matching PDF Screen 4
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = palette.indigoSoft,
                    border = BorderStroke(1.dp, palette.cardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        StatusPill(
                            label = if (item.isAvailable) "Available" else "Borrowed",
                            modifier = Modifier.align(Alignment.TopStart)
                        )
                        if (!item.photoUri.isNullOrBlank()) {
                            AsyncImage(
                                model = Uri.parse(item.photoUri),
                                contentDescription = item.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(132.dp)
                                    .align(Alignment.Center)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                        } else {
                            Icon(
                                imageVector = itemIconForType(item.iconType),
                                contentDescription = item.title,
                                tint = palette.navyBrand,
                                modifier = Modifier
                                    .size(88.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }
            }

            // Title, spec, and owner block
            item {
                GamiturnCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${item.modelOrSpec} · ${item.category}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.textMuted
                            )
                        }

                        // Owner info row
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = palette.indigoAction,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = item.ownerName.firstOrNull()?.uppercaseChar()?.toString() ?: "S",
                                            color = androidx.compose.ui.graphics.Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = item.ownerName,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Rounded.Verified,
                                            contentDescription = "Verified student",
                                            tint = palette.navyBrand,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Rounded.Star,
                                            contentDescription = "Rating",
                                            tint = palette.pendingText,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${item.ownerRating} rating · ${item.ownerReviewCount} reviews",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = palette.textMuted
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.LocationOn,
                                contentDescription = "Pickup location",
                                tint = palette.indigoAction,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item.pickupLocation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Reviews Section
            item {
                Text(
                    text = "REVIEWS",
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.textMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (reviews.isEmpty()) {
                item {
                    GamiturnCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "No borrower reviews yet. Be the first student to borrow and rate this item!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.textMuted,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(reviews, key = { it.id }) { review ->
                    GamiturnCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "\"${review.comment}\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${review.reviewerName} · ${review.timeAgo}",
                                style = MaterialTheme.typography.bodySmall,
                                color = palette.textMuted
                            )
                        }
                    }
                }
            }

            // Primary Actions matching PDF Screen 4
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 16.dp)
                ) {
                    if (item.isOwnedByCurrentUser) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = palette.indigoSoft,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "You listed this item. Check the Requests tab to approve or decline student borrowers.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.navyBrand,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    } else {
                        Button(
                            onClick = { onRequestBorrow(item.id) },
                            enabled = item.isAvailable,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 52.dp)
                                .testTag("request_to_borrow_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = palette.indigoAction)
                        ) {
                            Text(
                                text = if (item.isAvailable) "Request to borrow" else "Currently borrowed",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        OutlinedButton(
                            onClick = { onMessageOwner(item.ownerName) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp)
                                .testTag("message_owner_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ChatBubbleOutline,
                                contentDescription = "Message owner",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Message the owner", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}
