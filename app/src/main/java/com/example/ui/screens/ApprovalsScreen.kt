package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ApprovalAction
import com.example.data.model.ApprovalStatus
import com.example.ui.components.BotMascot
import com.example.ui.components.InlineApprovalCard
import com.example.ui.components.MascotState
import com.example.ui.theme.*

@Composable
fun ApprovalsScreen(
    approvals: List<ApprovalAction>,
    onApproveAction: (String) -> Unit,
    onRejectAction: (String) -> Unit,
    onEditAndApproveAction: (String, Map<String, String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalCozyTheme.current
    var selectedFilter by remember { mutableStateOf("PENDING") } // "PENDING", "APPROVED", "SKIPPED"

    val filteredList = when (selectedFilter) {
        "PENDING" -> approvals.filter { it.status == ApprovalStatus.PENDING }
        "APPROVED" -> approvals.filter { it.status == ApprovalStatus.APPROVED || it.status == ApprovalStatus.EDITED }
        "SKIPPED" -> approvals.filter { it.status == ApprovalStatus.REJECTED }
        else -> approvals
    }

    val pendingCount = approvals.count { it.status == ApprovalStatus.PENDING }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.canvasBg)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("approvals_screen")
    ) {
        // Screen Header & High-Fives mascot
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "High-Fives & Review Gate ✨",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = theme.textPrimary,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = "Zero-Trust Human Authorization Boundary",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AtlasPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            BotMascot(
                botId = "reactor",
                state = if (pendingCount > 0) MascotState.WAITING_APPROVAL else MascotState.CELEBRATING,
                size = 40.dp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Security Boundary Explainer Card
        Surface(
            color = theme.cardSurface,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(0.8.dp, theme.cardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = theme.accentPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Read tools run autonomously. Outbound emails, sheet modifications, and calendar bookings strictly require one-tap authorization.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = theme.textSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Chips (Pending, Approved, Skipped)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedFilter == "PENDING",
                onClick = { selectedFilter = "PENDING" },
                label = { Text("High-Fives Needed ($pendingCount)", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AtlasPrimary,
                    selectedLabelColor = DeepEspressoText,
                    containerColor = theme.cardSurface,
                    labelColor = theme.textSecondary
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("filter_pending")
            )

            FilterChip(
                selected = selectedFilter == "APPROVED",
                onClick = { selectedFilter = "APPROVED" },
                label = { Text("Approved (${approvals.count { it.status == ApprovalStatus.APPROVED || it.status == ApprovalStatus.EDITED }})", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ReactorPrimary,
                    selectedLabelColor = Color.White,
                    containerColor = theme.cardSurface,
                    labelColor = theme.textSecondary
                ),
                shape = RoundedCornerShape(16.dp)
            )

            FilterChip(
                selected = selectedFilter == "SKIPPED",
                onClick = { selectedFilter = "SKIPPED" },
                label = { Text("Skipped (${approvals.count { it.status == ApprovalStatus.REJECTED }})", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Rose500,
                    selectedLabelColor = Color.White,
                    containerColor = theme.cardSurface,
                    labelColor = theme.textSecondary
                ),
                shape = RoundedCornerShape(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Approvals List
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    BotMascot(
                        botId = "atlas",
                        state = MascotState.CELEBRATING,
                        size = 56.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "All clear! No pending high-fives.",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = theme.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Your companions are actively working in the background.",
                        style = MaterialTheme.typography.labelSmall.copy(color = theme.textSecondary)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredList, key = { it.id }) { action ->
                    InlineApprovalCard(
                        action = action,
                        onApprove = onApproveAction,
                        onReject = onRejectAction,
                        onEditAndApprove = onEditAndApproveAction
                    )
                }
            }
        }
    }
}
