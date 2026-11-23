package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ui.components.itemIconForType
import com.example.ui.theme.GamiturnTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LendItemSheet(
    onDismiss: () -> Unit,
    onSubmitItem: (
        title: String,
        modelOrSpec: String,
        category: String,
        description: String,
        pickupLocation: String,
        photoUri: String?,
        iconType: String
    ) -> Unit
) {
    val palette = GamiturnTheme.palette
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by rememberSaveable { mutableStateOf("") }
    var modelOrSpec by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("School supplies") }
    var description by rememberSaveable { mutableStateOf("") }
    var pickupLocation by rememberSaveable { mutableStateOf("Main Library Desk") }
    var photoUri by rememberSaveable { mutableStateOf<String?>(null) }
    var iconType by rememberSaveable { mutableStateOf("calculator") }
    var errorMsg by rememberSaveable { mutableStateOf<String?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) photoUri = uri.toString()
    }

    val iconChoices = listOf(
        "calculator" to "Calc",
        "compass" to "Drafting",
        "lab_coat" to "Lab",
        "speaker" to "Speaker",
        "power_bank" to "Power",
        "ring_light" to "Light",
        "headphones" to "Audio",
        "hub" to "Adapter"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Lend an item to classmates",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Share extra school supplies or gadgets you are not using today.",
                style = MaterialTheme.typography.bodySmall,
                color = palette.textMuted
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it; errorMsg = null },
                label = { Text("Item name") },
                placeholder = { Text("e.g. Casio FX-991EX Calculator") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("lend_title_input")
            )

            OutlinedTextField(
                value = modelOrSpec,
                onValueChange = { modelOrSpec = it },
                label = { Text("Model or specification") },
                placeholder = { Text("e.g. Solar powered, slide cover") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "CATEGORY",
                style = MaterialTheme.typography.labelMedium,
                color = palette.textMuted
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("School supplies", "Gadgets").forEach { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = { category = cat },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = palette.indigoAction,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it; errorMsg = null },
                label = { Text("Condition & notes") },
                placeholder = { Text("Clean, working condition, bring back charged...") },
                minLines = 2,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("lend_description_input")
            )

            OutlinedTextField(
                value = pickupLocation,
                onValueChange = { pickupLocation = it },
                label = { Text("Campus pickup location") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Icon selector
            Text(
                text = "ITEM ICON",
                style = MaterialTheme.typography.labelMedium,
                color = palette.textMuted
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                iconChoices.forEach { (typeKey, label) ->
                    val selected = iconType == typeKey
                    Surface(
                        onClick = { iconType = typeKey },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selected) palette.indigoSoft else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(
                            width = if (selected) 1.5.dp else 1.dp,
                            color = if (selected) palette.indigoAction else palette.cardBorder
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = itemIconForType(typeKey),
                                contentDescription = label,
                                tint = if (selected) palette.indigoAction else palette.textMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Optional photo picker
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                shape = RoundedCornerShape(14.dp),
                color = palette.indigoSoft,
                border = BorderStroke(1.dp, palette.cardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!photoUri.isNullOrBlank()) {
                        AsyncImage(
                            model = Uri.parse(photoUri),
                            contentDescription = "Item photo preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.AddPhotoAlternate,
                            contentDescription = "Attach item photo",
                            tint = palette.indigoAction
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (photoUri != null) "Photo attached · Tap to change"
                        else "Add item photo (optional)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (errorMsg != null) {
                Text(
                    text = errorMsg.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(0.35f)
                        .heightIn(min = 48.dp)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        if (title.isBlank()) {
                            errorMsg = "Please enter the item name."
                        } else {
                            onSubmitItem(
                                title,
                                modelOrSpec,
                                category,
                                description.ifBlank { "Good working condition, available for fellow students." },
                                pickupLocation,
                                photoUri,
                                iconType
                            )
                        }
                    },
                    modifier = Modifier
                        .weight(0.65f)
                        .heightIn(min = 48.dp)
                        .testTag("submit_lend_item_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = palette.indigoAction)
                ) {
                    Text("Publish item")
                }
            }
        }
    }
}
