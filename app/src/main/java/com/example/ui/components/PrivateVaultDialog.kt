package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.vault.PrivateVaultProfile

@Composable
fun PrivateVaultDialog(
    currentProfile: PrivateVaultProfile,
    onDismiss: () -> Unit,
    onSaveProfile: (PrivateVaultProfile) -> Unit,
    onImportJson: (String) -> Unit,
    onClearAll: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    var displayName by remember { mutableStateOf(currentProfile.displayName) }
    var roleAndDomain by remember { mutableStateOf(currentProfile.roleAndDomain) }
    var organizationOrLab by remember { mutableStateOf(currentProfile.organizationOrLab) }
    var dailyScheduleSheetId by remember { mutableStateOf(currentProfile.dailyScheduleSheetId) }
    var contentScheduleSheetId by remember { mutableStateOf(currentProfile.contentScheduleSheetId) }
    var customContextNotes by remember { mutableStateOf(currentProfile.customContextNotes) }

    var showImportDialog by remember { mutableStateOf(false) }
    var importJsonText by remember { mutableStateOf("") }
    var showConfirmWipe by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Private Vault",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Private On-Device Vault",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Zero PII in repo · Stored locally on this phone",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Form
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Explanatory Card
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "💡 Enter your personal name, lab affiliations, and private Google Sheet IDs here. Your agents will dynamically ground all reasoning in this data at runtime without committing anything to GitHub.",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(14.dp),
                            lineHeight = 18.sp
                        )
                    }

                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = { Text("Display Name / Greeting") },
                        placeholder = { Text("e.g. Alex") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = roleAndDomain,
                        onValueChange = { roleAndDomain = it },
                        label = { Text("Your Role & Technical Domain") },
                        placeholder = { Text("e.g. Biotech Founder & Microbiology Researcher") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = organizationOrLab,
                        onValueChange = { organizationOrLab = it },
                        label = { Text("Organization, Venture, or Lab") },
                        placeholder = { Text("e.g. BioTech Lab / University Department") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = dailyScheduleSheetId,
                        onValueChange = { dailyScheduleSheetId = it },
                        label = { Text("Daily Schedule Google Sheet ID") },
                        placeholder = { Text("1abc...xyz") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = contentScheduleSheetId,
                        onValueChange = { contentScheduleSheetId = it },
                        label = { Text("Content Schedule Google Sheet ID") },
                        placeholder = { Text("1def...uvw") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    OutlinedTextField(
                        value = customContextNotes,
                        onValueChange = { customContextNotes = it },
                        label = { Text("Custom Context, Grants & Projects (Private)") },
                        placeholder = { Text("Add grant references, vendor contacts, or personal preferences...") },
                        minLines = 4,
                        maxLines = 8,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    // JSON Import / Export Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val current = PrivateVaultProfile(
                                    displayName = displayName,
                                    roleAndDomain = roleAndDomain,
                                    organizationOrLab = organizationOrLab,
                                    dailyScheduleSheetId = dailyScheduleSheetId,
                                    contentScheduleSheetId = contentScheduleSheetId,
                                    customContextNotes = customContextNotes
                                )
                                clipboardManager.setText(AnnotatedString(current.toJsonString()))
                                onShowMessage("📋 Private Vault JSON copied to clipboard!")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Export JSON", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { showImportDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Import JSON", fontSize = 12.sp)
                        }
                    }

                    // Wipe Data Button
                    OutlinedButton(
                        onClick = { showConfirmWipe = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("🧹 Wipe All Local Vault Data", fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Save Button
                Button(
                    onClick = {
                        val updated = PrivateVaultProfile(
                            displayName = displayName.trim(),
                            roleAndDomain = roleAndDomain.trim(),
                            organizationOrLab = organizationOrLab.trim(),
                            dailyScheduleSheetId = dailyScheduleSheetId.trim(),
                            contentScheduleSheetId = contentScheduleSheetId.trim(),
                            customContextNotes = customContextNotes.trim()
                        )
                        onSaveProfile(updated)
                        onShowMessage("🔒 Private Vault saved & synchronized!")
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Save & Apply to Agents ✨", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    // Import Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Import Private Profile JSON") },
            text = {
                Column {
                    Text(
                        "Paste a valid PrivateVaultProfile JSON string below:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importJsonText,
                        onValueChange = { importJsonText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        placeholder = { Text("{\n  \"displayName\": \"...\",\n  ...\n}") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onImportJson(importJsonText)
                        showImportDialog = false
                        onDismiss()
                    }
                ) {
                    Text("Import")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Confirm Wipe Dialog
    if (showConfirmWipe) {
        AlertDialog(
            onDismissRequest = { showConfirmWipe = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Clear All Local Personal Data?") },
            text = {
                Text("This will immediately wipe all names, sheet IDs, and private context from local SharedPreferences on this device.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAll()
                        showConfirmWipe = false
                        onShowMessage("🧹 Local private vault wiped.")
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Wipe Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmWipe = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
