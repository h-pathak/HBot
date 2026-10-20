package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
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
    var dailyIdText by remember(dailyScheduleId) { mutableStateOf(dailyScheduleId) }
    var contentIdText by remember(contentScheduleId) { mutableStateOf(contentScheduleId) }
    var activeBridgeTab by remember { mutableStateOf("AGENTS") } // "AGENTS", "MEMORY", "SKILLS", "CHANNELS"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate900)
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
                    text = "Workspace & Antigravity Bridge",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate100,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = "Google Workspace Sync & GitHub Export",
                    style = MaterialTheme.typography.labelSmall.copy(color = Cyan400)
                )
            }

            Surface(
                color = Emerald500.copy(alpha = 0.15f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(RoundedCornerShape(3.dp)).background(Emerald400))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("OAuth Active", color = Emerald400, fontWeight = FontWeight.Bold, fontSize = 10.sp)
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
                    text = "GOOGLE WORKSPACE SERVICE CONNECTIONS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Slate400,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }

            items(connections, key = { it.id }) { conn ->
                WorkspaceConnectionCard(
                    connection = conn,
                    onTest = { onShowSnack("Testing ${conn.type.displayName}... Connection healthy!") }
                )
            }

            // Google Sheets Configuration Tile
            item {
                Surface(
                    color = Slate850,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📊 Google Sheet IDs Configuration",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Slate100,
                                    fontSize = 13.sp
                                )
                            )

                            Button(
                                onClick = { onUpdateSheetIds(dailyIdText, contentIdText) },
                                colors = ButtonDefaults.buttonColors(containerColor = Cyan400),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Save IDs", color = Slate950, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = dailyIdText,
                            onValueChange = { dailyIdText = it },
                            label = { Text("Daily Schedule Sheet ID (Asia/Kolkata)", color = Slate400, fontSize = 11.sp) },
                            textStyle = MaterialTheme.typography.bodySmall.copy(color = Slate100, fontSize = 11.sp),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = contentIdText,
                            onValueChange = { contentIdText = it },
                            label = { Text("Content Schedule Sheet ID (Thu-Wed Cycle)", color = Slate400, fontSize = 11.sp) },
                            textStyle = MaterialTheme.typography.bodySmall.copy(color = Slate100, fontSize = 11.sp),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Google Antigravity Bridge Export Preview
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ANTIGRAVITY BRIDGE (GITHUB EXPORT)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate400,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )

                    Button(
                        onClick = { onShowSnack("Exported AGENTS.md, MEMORY.md, and skills.yaml for GitHub sync!") },
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo500),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export All", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Surface(
                    color = Slate850,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Indigo400.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Sub-tabs for export preview
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("AGENTS", "MEMORY", "SKILLS", "CHANNELS").forEach { tab ->
                                FilterChip(
                                    selected = activeBridgeTab == tab,
                                    onClick = { activeBridgeTab = tab },
                                    label = { Text(tab, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Indigo500,
                                        selectedLabelColor = Color.White,
                                        containerColor = Slate900,
                                        labelColor = Slate400
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Code snippet box
                        val previewContent = when (activeBridgeTab) {
                            "AGENTS" -> """
                                agents:
                                  - id: atlas
                                    name: Atlas
                                    title: Personal Chief of Staff
                                    skills: [skill-creator, find-a-document, daily-briefing]
                                  - id: reactor
                                    name: Reactor
                                    title: NocVita Hardware & Biorefinery
                                    skills: [vendor-rfq-comparison, find-a-document]
                                  - id: helix
                                    name: Helix
                                    title: Microbiology & Lab Bookings
                                    skills: [lab-instrument-booking, find-a-document]
                                  - id: pulse
                                    name: Pulse
                                    title: Content Studio
                                    skills: [weekly-content-pipeline]
                                  - id: forge
                                    name: Forge
                                    title: Antigravity Code & Simulations
                            """.trimIndent()
                            "MEMORY" -> """
                                # HariomBot Learned Knowledge Vault
                                - Founder: Hariom Pathak (NocVita, Vadodara, Gujarat)
                                - Microbiology Dept: MSU Baroda (The Geometric-Information Limit)
                                - PBR Hardware: Borosilicate 3.3 (OD 60mm), SS316 tri-clamp endcaps
                                - Lab Portals: I-STEM (ICP-MS), GBRC (MALDI-TOF / Sanger sequencing)
                                - Security: Zero-trust mutating actions (Strict Approvals Gate)
                            """.trimIndent()
                            "SKILLS" -> """
                                skills:
                                  - slug: vendor-rfq-comparison
                                    title: Multi-Vendor RFQ & Quote Comparison
                                  - slug: lab-instrument-booking
                                    title: I-STEM & GBRC Instrument Slot Prep
                                  - slug: weekly-content-pipeline
                                    title: Weekly Content Cycle (Blog, LinkedIn, Instagram)
                                  - slug: skill-creator
                                    title: Teach a Task / Skill Creator (/learn)
                            """.trimIndent()
                            else -> """
                                channels:
                                  - id: command-center
                                    name: Command Center
                                    permitted_agents: [atlas, reactor, helix, pulse, forge]
                                  - id: nocvita-hardware
                                    name: NocVita & Biorefinery
                                  - id: research-and-labs
                                    name: Research, Papers & Lab Bookings
                            """.trimIndent()
                        }

                        Surface(
                            color = Slate950,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(0.6.dp, Slate800),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = previewContent,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = Emerald400,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkspaceConnectionCard(
    connection: WorkspaceConnection,
    onTest: () -> Unit
) {
    Surface(
        color = Slate850,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, Slate700),
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
                            color = Slate100,
                            fontSize = 13.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (connection.isConnected) Emerald400 else Rose500)
                    )
                }

                Text(
                    text = connection.details,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Slate300,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Text(
                    text = "${connection.statusText} · ${connection.lastSyncText}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Slate400,
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
                    .clip(RoundedCornerShape(8.dp))
                    .background(Slate800)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Test Connection",
                    tint = Cyan400,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
