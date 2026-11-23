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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.local.ChatMessageEntity
import com.example.ui.GamiturnUiState
import com.example.ui.theme.GamiturnTheme

@Composable
fun ChatScreen(
    uiState: GamiturnUiState,
    onSelectChannel: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = GamiturnTheme.palette
    var draft by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()

    val currentChannel = uiState.chatChannels.firstOrNull {
        it.channelId == uiState.activeChatChannelId
    }

    LaunchedEffect(uiState.messages.size, uiState.activeChatChannelId) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.lastIndex)
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = palette.indigoSoft,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Groups,
                            contentDescription = "Chat channel",
                            tint = palette.indigoAction
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentChannel?.title ?: "Org Group Chat",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = currentChannel?.memberLabel ?: "42 members · Campus Hub",
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.textMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Channel Switcher Row (Org Group Chat + any Owner DMs)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.chatChannels.forEach { channel ->
                    val selected = channel.channelId == uiState.activeChatChannelId
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectChannel(channel.channelId) },
                        label = { Text(channel.title) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = palette.indigoAction,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Messages container
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = palette.cardFrost,
                border = BorderStroke(1.dp, palette.cardBorder),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "TODAY",
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.textMuted
                            )
                        }
                    }

                    items(uiState.messages, key = { it.id }) { msg ->
                        ChatBubble(message = msg)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick "Ask for an item" template chip
            if (uiState.activeChatChannelId == "org_group") {
                AssistChip(
                    onClick = {
                        if (draft.isBlank()) {
                            draft = "Does anyone have a calculator I can borrow for tomorrow?"
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Campaign,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = { Text("Quick prompt: Ask if someone has an item") }
                )
            }

            // Message Composer matching PDF Screen 7 (right mockup)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    placeholder = { Text("Message") },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_message_input")
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    onClick = {
                        if (draft.isNotBlank()) {
                            onSendMessage(draft)
                            draft = ""
                        }
                    },
                    shape = CircleShape,
                    color = palette.indigoAction,
                    modifier = Modifier
                        .size(50.dp)
                        .testTag("send_chat_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Send,
                            contentDescription = "Send message",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessageEntity) {
    val palette = GamiturnTheme.palette
    val alignment = if (message.isFromMe) Alignment.End else Alignment.Start
    val bubbleColor = if (message.isFromMe) {
        palette.indigoAction
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = if (message.isFromMe) Color.White else MaterialTheme.colorScheme.onSurface
    val bubbleShape = if (message.isFromMe) {
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        if (!message.isFromMe) {
            Text(
                text = "${message.senderName} · ${message.timestampLabel}",
                style = MaterialTheme.typography.labelSmall,
                color = palette.textMuted,
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
            )
        }
        Surface(
            shape = bubbleShape,
            color = bubbleColor,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.message,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
        if (message.isFromMe) {
            Text(
                text = message.timestampLabel,
                style = MaterialTheme.typography.labelSmall,
                color = palette.textMuted,
                modifier = Modifier.padding(end = 4.dp, top = 2.dp)
            )
        }
    }
}
