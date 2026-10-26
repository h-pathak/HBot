package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Security
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
import com.example.data.model.ApprovalAction
import com.example.data.model.ApprovalStatus
import com.example.ui.theme.*

@Composable
fun InlineApprovalCard(
    action: ApprovalAction,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
    onEditAndApprove: (String, Map<String, String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalCozyTheme.current
    var isEditing by remember { mutableStateOf(false) }
    var editedFields by remember(action) { mutableStateOf(action.fields) }

    Surface(
        color = theme.cardSurface,
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.2.dp,
            if (action.status == ApprovalStatus.PENDING) AtlasPrimary.copy(alpha = 0.7f) else theme.cardBorder
        ),
        shadowElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("approval_card_${action.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Action Type Badge & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(action.actionType.badgeColorHex))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = action.actionType.label,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "via @${action.requestingBotId}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = theme.accentPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Security Shield Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Review Gate",
                        tint = if (action.status == ApprovalStatus.PENDING) AtlasPrimary else ReactorPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (action.status == ApprovalStatus.PENDING) "High-Five Needed!" else action.status.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (action.status == ApprovalStatus.PENDING) AtlasPrimary else ReactorPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Title & Target
            Text(
                text = action.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = theme.textPrimary,
                    fontSize = 14.sp
                )
            )

            Text(
                text = "Target: ${action.targetSummary}",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = theme.textSecondary,
                    fontSize = 11.sp
                )
            )

            // Budget Tag
            if (!action.budgetTag.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = theme.accentPrimary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "🏷️ ${action.budgetTag}",
                        color = theme.accentPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            // Diff preview box if available
            if (!action.diffPreview.isNullOrBlank() && !isEditing) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = theme.cardElevated,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(0.6.dp, theme.cardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = action.diffPreview,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = ReactorPrimary,
                        lineHeight = 14.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Fields Table / Preview
            Surface(
                color = theme.cardElevated,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(0.8.dp, theme.cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    if (isEditing) {
                        editedFields.forEach { (key, value) ->
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(
                                    text = key,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = theme.accentPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                OutlinedTextField(
                                    value = value,
                                    onValueChange = { newVal ->
                                        editedFields = editedFields.toMutableMap().apply { put(key, newVal) }
                                    },
                                    singleLine = key != "Body",
                                    maxLines = if (key == "Body") 6 else 1,
                                    textStyle = MaterialTheme.typography.bodySmall.copy(color = theme.textPrimary),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    } else {
                        action.fields.entries.take(4).forEach { (key, value) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = key,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = theme.textSecondary,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier.weight(0.35f)
                                )
                                Text(
                                    text = value,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = theme.textPrimary,
                                        fontSize = 11.sp
                                    ),
                                    maxLines = if (key == "Body") 3 else 1,
                                    modifier = Modifier.weight(0.65f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            if (action.status == ApprovalStatus.PENDING) {
                if (isEditing) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                isEditing = false
                                onEditAndApprove(action.id, editedFields)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ReactorPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save & Send ✨", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        OutlinedButton(
                            onClick = { isEditing = false },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = theme.textSecondary, fontSize = 12.sp)
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onApprove(action.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = ReactorPrimary),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                            modifier = Modifier
                                .weight(1.4f)
                                .testTag("approve_btn_${action.id}")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Approve & Send ✨", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { isEditing = true },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                            modifier = Modifier.weight(0.8f)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = theme.accentPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit ✏️", color = theme.accentPrimary, fontSize = 11.sp)
                        }

                        IconButton(
                            onClick = { onReject(action.id) },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(theme.cardElevated)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Skip", tint = Rose500, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            } else {
                Surface(
                    color = theme.cardElevated,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (action.status == ApprovalStatus.REJECTED) Rose500 else ReactorPrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${action.executedAt ?: action.status.label} · ${action.executionResult ?: ""}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (action.status == ApprovalStatus.REJECTED) Rose500 else ReactorPrimary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
