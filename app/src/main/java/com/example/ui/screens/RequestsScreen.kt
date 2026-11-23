package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AssignmentTurnedIn
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.BorrowRequestEntity
import com.example.ui.GamiturnUiState
import com.example.ui.components.GamiturnCard
import com.example.ui.components.ItemThumbnail
import com.example.ui.components.StatusPill
import com.example.ui.theme.GamiturnTheme

@Composable
fun RequestsScreen(
    uiState: GamiturnUiState,
    initialTab: Int,
    onApproveIncoming: (Int, String) -> Unit,
    onDeclineIncoming: (Int, String) -> Unit,
    onMarkReturned: (Int, Int, String) -> Unit,
    onOpenItem: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = GamiturnTheme.palette
    var selectedTab by rememberSaveable(initialTab) { mutableIntStateOf(initialTab) }
    var returningRequest by rememberSaveable { mutableStateOf<Int?>(null) }
    var showHistoryDialog by rememberSaveable { mutableStateOf(false) }

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
                    text = stringResource(R.string.requests_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Segmented Switcher matching PDF Screens 6 & 7
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val incomingSelected = selectedTab == 0
                        Surface(
                            onClick = { selectedTab = 0 },
                            shape = RoundedCornerShape(11.dp),
                            color = if (incomingSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                            shadowElevation = if (incomingSelected) 1.dp else 0.dp,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 44.dp)
                                .testTag("tab_incoming")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                val badgeText = if (uiState.pendingIncomingCount > 0) {
                                    "Incoming (${uiState.pendingIncomingCount})"
                                } else {
                                    "Incoming"
                                }
                                Text(
                                    text = badgeText,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (incomingSelected) MaterialTheme.colorScheme.onSurface else palette.textMuted
                                )
                            }
                        }

                        val mineSelected = selectedTab == 1
                        Surface(
                            onClick = { selectedTab = 1 },
                            shape = RoundedCornerShape(11.dp),
                            color = if (mineSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                            shadowElevation = if (mineSelected) 1.dp else 0.dp,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 44.dp)
                                .testTag("tab_my_requests")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "My requests",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (mineSelected) MaterialTheme.colorScheme.onSurface else palette.textMuted
                                )
                            }
                        }
                    }
                }
            }

            if (selectedTab == 0) {
                if (uiState.incomingRequests.isEmpty()) {
                    item {
                        GamiturnCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No incoming requests yet",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tap the + button below to share extra supplies with classmates.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = palette.textMuted
                                )
                            }
                        }
                    }
                } else {
                    items(uiState.incomingRequests, key = { it.id }) { req ->
                        IncomingRequestCard(
                            request = req,
                            onApprove = { onApproveIncoming(req.id, req.borrowerName) },
                            onDecline = { onDeclineIncoming(req.id, req.borrowerName) }
                        )
                    }
                }
            } else {
                // Summary line matching PDF Screen 7
                item {
                    Text(
                        text = "${uiState.activeMyRequestsCount} active · ${uiState.completedMyRequestsCount} completed",
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textMuted
                    )
                }

                if (uiState.myRequests.isEmpty()) {
                    item {
                        GamiturnCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "You haven't requested any items yet. Browse items to borrow what you need!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.textMuted,
                                modifier = Modifier.padding(20.dp)
                            )
                        }
                    }
                } else {
                    items(uiState.myRequests, key = { it.id }) { req ->
                        MyRequestCard(
                            request = req,
                            onOpenItem = { onOpenItem(req.itemId) },
                            onMarkReturnedClick = { returningRequest = req.id }
                        )
                    }
                }

                // Borrowing history footer card matching PDF Screen 7
                item {
                    val completedCount = (uiState.profile?.completedBorrowings ?: 8)
                    val avgRating = uiState.profile?.rating ?: 4.8f
                    GamiturnCard(
                        onClick = { showHistoryDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("borrowing_history_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Borrowing history",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "$completedCount completed borrowings · $avgRating average",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = palette.textMuted
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                contentDescription = "Open borrowing history",
                                tint = palette.textMuted
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }

    // Return & Review Dialog
    val activeReturnId = returningRequest
    if (activeReturnId != null) {
        var rating by rememberSaveable { mutableIntStateOf(5) }
        var comment by rememberSaveable { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { returningRequest = null },
            title = { Text("Return & Review") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Confirm you returned this item to its owner and leave an optional review:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (star in 1..5) {
                            IconButton(onClick = { rating = star }) {
                                Icon(
                                    imageVector = Icons.Rounded.Star,
                                    contentDescription = "$star stars",
                                    tint = if (star <= rating) palette.pendingText else palette.cardBorder
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("Short review (optional)") },
                        placeholder = { Text("Great condition, easy pickup!") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onMarkReturned(activeReturnId, rating, comment)
                        returningRequest = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = palette.indigoAction)
                ) {
                    Text("Confirm return")
                }
            },
            dismissButton = {
                TextButton(onClick = { returningRequest = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showHistoryDialog) {
        val returned = uiState.myRequests.filter { it.status == "Returned" }
        AlertDialog(
            onDismissRequest = { showHistoryDialog = false },
            title = { Text("Borrowing History") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (returned.isEmpty()) {
                        Text(
                            text = "Completed returns will appear here.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        returned.forEach { req ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(req.itemTitle, style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        "Owner: ${req.ownerName} · Returned ${req.expectedReturnLabel}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = palette.textMuted
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHistoryDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun IncomingRequestCard(
    request: BorrowRequestEntity,
    onApprove: () -> Unit,
    onDecline: () -> Unit
) {
    val palette = GamiturnTheme.palette
    GamiturnCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = palette.indigoSoft,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = request.borrowerName.firstOrNull()?.uppercaseChar()?.toString() ?: "S",
                            color = palette.indigoAction,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = request.borrowerName,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (request.borrowerVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Rounded.Verified,
                                contentDescription = "Verified student",
                                tint = palette.navyBrand,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                    Text(
                        text = "${request.itemTitle} · ${request.durationLabel}",
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textMuted
                    )
                }
                StatusPill(label = request.status)
            }

            // Personal info & return schedule verification block (from PDF Page 9 description)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Return: ${request.expectedReturnLabel}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${request.borrowerAddress} · ${request.borrowerMobile}",
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textMuted
                    )
                    if (request.note.isNotBlank()) {
                        Text(
                            text = "\"${request.note}\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (request.status == "Pending") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onApprove,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("approve_request_${request.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = palette.indigoAction)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Approve",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Approve")
                    }
                    OutlinedButton(
                        onClick = onDecline,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("decline_request_${request.id}"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, palette.cardBorder)
                    ) {
                        Text("Decline", color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            Text(
                text = request.timeAgoLabel,
                style = MaterialTheme.typography.labelSmall,
                color = palette.textMuted
            )
        }
    }
}

@Composable
private fun MyRequestCard(
    request: BorrowRequestEntity,
    onOpenItem: () -> Unit,
    onMarkReturnedClick: () -> Unit
) {
    val palette = GamiturnTheme.palette
    GamiturnCard(
        onClick = onOpenItem,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ItemThumbnail(
                    iconType = request.itemIconType,
                    photoUri = null,
                    contentDescription = request.itemTitle,
                    size = 46.dp,
                    iconSize = 24.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = request.itemTitle,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = request.ownerName,
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textMuted
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    StatusPill(label = request.status)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = request.expectedReturnLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textMuted
                    )
                }
            }

            if (request.status == "Approved" || request.status == "Borrowed") {
                OutlinedButton(
                    onClick = onMarkReturnedClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 44.dp)
                        .testTag("mark_returned_${request.id}"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AssignmentTurnedIn,
                        contentDescription = "Mark returned",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Mark returned & rate")
                }
            }
        }
    }
}
