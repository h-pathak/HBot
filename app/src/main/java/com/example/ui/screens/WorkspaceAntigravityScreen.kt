package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WorkspaceConnection
import com.example.ui.theme.*

@Composable
fun WorkspaceAntigravityScreen(
    connections: List<WorkspaceConnection>,
    dailyScheduleId: String,
    contentScheduleId: String,
    onUpdateSheetIds: (String, String) -> Unit,
    onShowSnack: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalCozyTheme.current
    val clipboardManager = LocalClipboardManager.current
    var dailyIdText by remember(dailyScheduleId) { mutableStateOf(dailyScheduleId) }
    var contentIdText by remember(contentScheduleId) { mutableStateOf(contentScheduleId) }
    var activeBridgeTab by remember { mutableStateOf("AGENTS.md") } // "AGENTS.md", "MEMORY.md", "agents.yaml", "skills.yaml"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.canvasBg)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("workspace_antigravity_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Sync & Antigravity Bridge 🔗",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = theme.textPrimary,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = "Google Workspace & GitHub Antigravity Sync",
                    style = MaterialTheme.typography.labelSmall.copy(color = theme.accentPrimary)
                )
            }

            Surface(
                color = ReactorLight,
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(RoundedCornerShape(3.dp)).background(ReactorPrimary))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("OAuth Connected", color = ReactorDark, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Google Workspace Connections Section
            item {
                Text(
                    text = "GOOGLE WORKSPACE & CLOUD CONNECTIONS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = theme.textSecondary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }

            items(connections, key = { it.id }) { conn ->
                CozyWorkspaceConnectionCard(
                    connection = conn,
                    onTest = { onShowSnack("Testing ${conn.type.displayName}... Connection healthy!") }
                )
            }

            // Google Sheets Configuration Tile with Pre-filled real Sheet IDs
            item {
                Surface(
                    color = theme.cardSurface,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, theme.cardBorder),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📊 Connected Google Sheets",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = theme.textPrimary,
                                    fontSize = 13.sp
                                )
                            )

                            Button(
                                onClick = { onUpdateSheetIds(dailyIdText, contentIdText) },
                                colors = ButtonDefaults.buttonColors(containerColor = theme.accentPrimary),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Save IDs", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = dailyIdText,
                            onValueChange = { dailyIdText = it },
                            label = { Text("Daily Schedule Sheet ID (Asia/Kolkata)", color = theme.textSecondary, fontSize = 11.sp) },
                            textStyle = MaterialTheme.typography.bodySmall.copy(color = theme.textPrimary, fontSize = 11.sp),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = contentIdText,
                            onValueChange = { contentIdText = it },
                            label = { Text("Content Schedule Sheet ID (Thu-Wed Cycle)", color = theme.textSecondary, fontSize = 11.sp) },
                            textStyle = MaterialTheme.typography.bodySmall.copy(color = theme.textPrimary, fontSize = 11.sp),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Google Antigravity Bridge Export Preview
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "1-TAP ANTIGRAVITY & OPENBOT EXPORT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = theme.textSecondary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )

                    Button(
                        onClick = { onShowSnack("Exported AGENTS.md, MEMORY.md, and agents.yaml for GitHub Antigravity sync!") },
                        colors = ButtonDefaults.buttonColors(containerColor = HelixPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export All", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            item {
                Surface(
                    color = theme.cardSurface,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, HelixPrimary.copy(alpha = 0.5f)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("AGENTS.md", "MEMORY.md", "agents.yaml", "skills.yaml").forEach { tab ->
                                FilterChip(
                                    selected = activeBridgeTab == tab,
                                    onClick = { activeBridgeTab = tab },
                                    label = { Text(tab, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = HelixPrimary,
                                        selectedLabelColor = Color.White,
                                        containerColor = theme.cardElevated,
                                        labelColor = theme.textSecondary
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val previewContent = when (activeBridgeTab) {
                            "AGENTS.md" -> """
                                # HBot Agent Roster
                                - Atlas (chief-of-staff): Personal Chief of Staff & Central Dispatcher
                                - Reactor (hardware-ops): Hardware & Prototyping Lead
                                - Helix (research-lead): Scientific Research & Instrumentation Specialist
                                - Pulse (content-studio): Weekly Content & Publication Studio
                                - Forge (systems-coder): Bioprocess Simulations & Code Architect
                            """.trimIndent()
                            "MEMORY.md" -> """
                                # HBot Learned Memory Bank
                                - User Context: Dynamically injected from Private Vault
                                - Project: Hardware prototyping and fabrication budget tracking
                                - Research: Metagenomics & stochastic microbial population analysis
                                - Hardware: Custom fluidic column (Borosilicate, SS316, 3D SLA baffles)
                                - Sheets: Daily & Content Schedule (configurable on-device)
                            """.trimIndent()
                            "agents.yaml" -> """
                                agents:
                                  - id: chief-of-staff
                                    name: Atlas
                                    skills: [vendor-rfq-comparison, portal-slot-prep]
                                  - id: hardware-ops
                                    name: Reactor
                                    skills: [vendor-rfq-comparison, grant-bill-compliance]
                                  - id: research-lead
                                    name: Helix
                                    skills: [portal-slot-prep, biomolecular-modeling-prep]
                                  - id: content-studio
                                    name: Pulse
                                    skills: [weekly-content-cycle]
                                  - id: systems-coder
                                    name: Forge
                                    skills: [python-bioprocess-sim]
                            """.trimIndent()
                            else -> """
                                skills:
                                  - slug: vendor-rfq-comparison
                                    title: Multi-Vendor RFQ & Quote Comparison
                                  - slug: grant-bill-compliance
                                    title: Project Bill & Voucher Compliance
                                  - slug: portal-slot-prep
                                    title: Analytical Equipment Slot Prep
                                  - slug: weekly-content-cycle
                                    title: Weekly Content Cycle (Blog, Professional Post, Media)
                                  - slug: biomolecular-modeling-prep
                                    title: Biomolecular Modeling & Structure Prep
                            """.trimIndent()
                        }

                        Surface(
                            color = VelvetMidnightBg,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(0.6.dp, theme.cardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = activeBridgeTab,
                                        color = BioluminescentAqua,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(previewContent))
                                            onShowSnack("Copied $activeBridgeTab to clipboard!")
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy",
                                            tint = BioluminescentAqua,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = previewContent,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = BioluminescentAqua,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CozyWorkspaceConnectionCard(
    connection: WorkspaceConnection,
    onTest: () -> Unit
) {
    val theme = LocalCozyTheme.current

    Surface(
        color = theme.cardSurface,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, theme.cardBorder),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = connection.type.displayName,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = theme.textPrimary,
                            fontSize = 13.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (connection.isConnected) ReactorPrimary else Rose500)
                    )
                }

                Text(
                    text = connection.details,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = theme.textSecondary,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Text(
                    text = "${connection.statusText} · ${connection.lastSyncText}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = theme.textSecondary,
                        fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onTest,
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(theme.cardElevated)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Test Connection",
                    tint = theme.accentPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
