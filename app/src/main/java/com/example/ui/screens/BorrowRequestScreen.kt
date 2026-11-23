package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.SupplyItemEntity
import com.example.ui.components.GamiturnCard
import com.example.ui.components.ItemThumbnail
import com.example.ui.theme.GamiturnTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private data class DurationOption(
    val label: String,
    val hoursToAdd: Int
)

@Composable
fun BorrowRequestScreen(
    item: SupplyItemEntity?,
    onBack: () -> Unit,
    onSendRequest: (item: SupplyItemEntity, durationLabel: String, expectedReturnLabel: String, note: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = GamiturnTheme.palette
    val options = remember {
        listOf(
            DurationOption("3 hours", 3),
            DurationOption("6 hours", 6),
            DurationOption("1 day", 24),
            DurationOption("3 days", 72)
        )
    }
    var selectedDuration by rememberSaveable { mutableStateOf("3 days") }
    var note by rememberSaveable { mutableStateOf("") }

    val selectedOption = options.firstOrNull { it.label == selectedDuration } ?: options.last()
    val (expectedDateText, expectedSubText) = remember(selectedOption.hoursToAdd) {
        val cal = Calendar.getInstance().apply {
            add(Calendar.HOUR_OF_DAY, selectedOption.hoursToAdd)
        }
        val dateFmt = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val subFmt = SimpleDateFormat("EEEE · h:mm a", Locale.getDefault())
        dateFmt.format(cal.time) to subFmt.format(cal.time)
    }

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
                Text("Item unavailable", style = MaterialTheme.typography.titleMedium)
            }
            return@Box
        }

        Column(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.borrowing_request_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Item summary card matching PDF Screen 5
            GamiturnCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ItemThumbnail(
                        iconType = item.iconType,
                        photoUri = item.photoUri,
                        contentDescription = item.title,
                        size = 46.dp,
                        iconSize = 24.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "from ${item.ownerName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.textMuted
                        )
                    }
                }
            }

            Text(
                text = "BORROWING DURATION",
                style = MaterialTheme.typography.labelMedium,
                color = palette.textMuted
            )

            // Duration options matching PDF Screen 5
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                options.forEach { option ->
                    val isSelected = option.label == selectedDuration
                    Surface(
                        onClick = { selectedDuration = option.label },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) palette.indigoSoft else palette.cardFrost,
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) palette.indigoAction else palette.cardBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("duration_${option.label.replace(" ", "_")}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedDuration = option.label },
                                colors = RadioButtonDefaults.colors(selectedColor = palette.indigoAction)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = option.label,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = "Selected",
                                    tint = palette.indigoAction
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text = "EXPECTED RETURN",
                style = MaterialTheme.typography.labelMedium,
                color = palette.textMuted
            )

            GamiturnCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Schedule,
                        contentDescription = "Expected return date",
                        tint = palette.indigoAction
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = expectedDateText,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = expectedSubText,
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.textMuted
                        )
                    }
                }
            }

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note for owner (optional)") },
                placeholder = { Text("e.g. Need for Physics lab period 3") },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("borrow_note_input")
            )

            Text(
                text = "Owner usually replies within 2 hours.",
                style = MaterialTheme.typography.bodySmall,
                color = palette.textMuted
            )

            Button(
                onClick = {
                    onSendRequest(
                        item,
                        selectedOption.label,
                        "$expectedDateText · ${expectedSubText.substringAfter("· ").trim()}",
                        note
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .testTag("send_request_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = palette.indigoAction)
            ) {
                Text("Send request", style = MaterialTheme.typography.labelLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
