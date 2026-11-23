package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.StudentProfileEntity
import com.example.ui.components.GamiturnCard
import com.example.ui.components.GamiturnLogoBadge
import com.example.ui.components.GamiturnWaveBackground
import com.example.ui.theme.GamiturnTheme

@Composable
fun AccountSetupScreen(
    existingProfile: StudentProfileEntity?,
    isEditingExisting: Boolean,
    onCreateAccount: (
        fullName: String,
        ageText: String,
        mobileNumber: String,
        address: String,
        schoolName: String,
        idPhotoUri: String?,
        isIdVerified: Boolean
    ) -> Unit,
    onQuickDemoLogin: () -> Unit,
    onCancelEdit: (() -> Unit)? = null
) {
    val palette = GamiturnTheme.palette
    var fullName by rememberSaveable { mutableStateOf(existingProfile?.fullName ?: "") }
    var ageText by rememberSaveable { mutableStateOf(existingProfile?.age?.toString() ?: "") }
    var mobileNumber by rememberSaveable { mutableStateOf(existingProfile?.mobileNumber ?: "") }
    var address by rememberSaveable { mutableStateOf(existingProfile?.address ?: "") }
    var schoolName by rememberSaveable {
        mutableStateOf(existingProfile?.schoolName ?: "Cordova National High School")
    }
    var pickedIdUri by rememberSaveable { mutableStateOf(existingProfile?.idPhotoUri) }
    var idVerified by rememberSaveable { mutableStateOf(existingProfile?.isIdVerified ?: false) }
    var validationError by rememberSaveable { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            pickedIdUri = uri.toString()
            idVerified = true
            validationError = null
        }
    }

    GamiturnWaveBackground(showWatermark = true) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Brand Header matching Slide 1
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GamiturnLogoBadge(size = 52.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "GAMITURN",
                            style = MaterialTheme.typography.headlineMedium,
                            color = palette.navyBrand,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.tagline),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                GamiturnCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isEditingExisting) "Update student profile"
                            else stringResource(R.string.create_account_title),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.verified_students_only),
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.textMuted
                        )

                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it; validationError = null },
                            label = { Text("Full name") },
                            placeholder = { Text("Maya Reyes") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_full_name")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = ageText,
                                onValueChange = { input ->
                                    ageText = input.filter { it.isDigit() }.take(2)
                                    validationError = null
                                },
                                label = { Text("Age") },
                                placeholder = { Text("17") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(0.35f)
                                    .testTag("input_age")
                            )
                            OutlinedTextField(
                                value = mobileNumber,
                                onValueChange = { mobileNumber = it; validationError = null },
                                label = { Text("Mobile number") },
                                placeholder = { Text("+63 917 440 1222") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(0.65f)
                                    .testTag("input_mobile")
                            )
                        }

                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it; validationError = null },
                            label = { Text("Address") },
                            placeholder = { Text("Blk 4 Lot 12, San Roque") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_address")
                        )

                        OutlinedTextField(
                            value = schoolName,
                            onValueChange = { schoolName = it },
                            label = { Text("School / Campus") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_school")
                        )

                        // Upload valid / student ID via Android Photo Picker
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                                .testTag("upload_student_id_card"),
                            shape = RoundedCornerShape(14.dp),
                            color = palette.indigoSoft,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (!pickedIdUri.isNullOrBlank()) {
                                    AsyncImage(
                                        model = Uri.parse(pickedIdUri),
                                        contentDescription = "Uploaded student ID preview",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                    )
                                } else {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Rounded.Badge,
                                                contentDescription = "Student ID icon",
                                                tint = palette.indigoAction
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (pickedIdUri != null) "Student ID attached"
                                        else "Upload valid / student ID",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "JPG or PNG, max 5 MB · Tap to choose photo",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = palette.textMuted
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Rounded.UploadFile,
                                    contentDescription = "Select ID image",
                                    tint = palette.indigoAction
                                )
                            }
                        }

                        // Quick verification indicator (or toggle for demo verification without gallery images)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { idVerified = !idVerified }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "Valid ID status",
                                tint = if (idVerified) palette.availableText else palette.textMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (idVerified) "Valid ID verified"
                                else "Tap to confirm student ID eligibility",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (idVerified) palette.availableText else palette.textMuted
                            )
                        }

                        AnimatedVisibility(visible = validationError != null) {
                            Text(
                                text = validationError.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Button(
                            onClick = {
                                when {
                                    fullName.isBlank() -> validationError = "Please enter your full name."
                                    mobileNumber.isBlank() -> validationError = "Please enter your mobile number."
                                    address.isBlank() -> validationError = "Please enter your address."
                                    else -> {
                                        onCreateAccount(
                                            fullName,
                                            ageText.ifBlank { "17" },
                                            mobileNumber,
                                            address,
                                            schoolName,
                                            pickedIdUri,
                                            true
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp)
                                .testTag("create_account_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = palette.indigoAction)
                        ) {
                            Text(
                                text = if (isEditingExisting) "Save changes" else "Create account",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        // Why we verify info box matching PDF Screen 1
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Info,
                                    contentDescription = "Verification info",
                                    tint = palette.navyBrand,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.why_we_verify_title),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = stringResource(R.string.why_we_verify_body),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = palette.textMuted
                                    )
                                }
                            }
                        }

                        if (onCancelEdit != null) {
                            TextButton(
                                onClick = onCancelEdit,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("Cancel")
                            }
                        } else {
                            TextButton(
                                onClick = onQuickDemoLogin,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .heightIn(min = 48.dp)
                                    .testTag("demo_login_button")
                            ) {
                                Text(
                                    text = "Already have an account? Log in as Maya Reyes",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = palette.navyBrand
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
