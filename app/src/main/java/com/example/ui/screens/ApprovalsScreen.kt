package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ApprovalAction
import com.example.data.model.ApprovalStatus
import com.example.ui.components.InlineApprovalCard
import com.example.ui.theme.*

@Composable
fun ApprovalsScreen(
    approvals: List<ApprovalAction>,
    onApproveAction: (String) -> Unit,
    onRejectAction: (String) -> Unit,
    onEditAndApproveAction: (String, Map<String, String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("PENDING") } // "PENDING" or "ALL"

    val filteredList = when (selectedFilter) {
        "PENDING" -> approvals.filter { it.status == ApprovalStatus.PENDING }
        else -> approvals
    }

    val pendingCount = approvals.count { it.status == ApprovalStatus.PENDING }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate900)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("approvals_screen")
    ) {
        // Screen Header & Security Policy Callout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Approvals & Review Gate",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate100,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = "Strict Read vs. Write Security Boundary",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Amber400,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            // Pending Count Pill
            Surface(
                color = if (pendingCount > 0) Amber500 else Slate800,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "$pendingCount Pending",
                    color = if (pendingCount > 0) Slate950 else Slate300,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Security Boundary Explainer Card
        Surface(
            color = Slate850,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(0.8.dp, Slate700),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Security policy",
                    tint = Cyan400,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Read tools (Gmail search, Drive CAD, Calendar check, Sheets read) run autonomously. All write tools (Gmail send, Sheet modify, Calendar create) require one-tap human execution.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Slate300,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Filter Toggle (Pending vs History)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedFilter == "PENDING",
                onClick = { selectedFilter = "PENDING" },
                label = { Text("Pending Actions ($pendingCount)", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Amber500,
                    selectedLabelColor = Slate950,
                    containerColor = Slate800,
                    labelColor = Slate300
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("filter_pending")
            )

            FilterChip(
                selected = selectedFilter == "ALL",
                onClick = { selectedFilter = "ALL" },
                label = { Text("All / Executed History (${approvals.size})", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Cyan400,
                    selectedLabelColor = Slate950,
                    containerColor = Slate800,
                    labelColor = Slate300
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("filter_all")
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
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Empty approvals",
                        tint = Emerald400,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No pending approvals!",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Slate100,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "All requested agent actions have been resolved.",
                        style = MaterialTheme.typography.labelSmall.copy(color = Slate400)
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
