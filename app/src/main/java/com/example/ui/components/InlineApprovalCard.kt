package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
    var isEditing by remember { mutableStateOf(false) }
    var editedFields by remember(action) { mutableStateOf(action.fields) }

    Surface(
        color = Slate850,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.2.dp,
            if (action.status == ApprovalStatus.PENDING) Amber400.copy(alpha = 0.6f) else Slate700
        ),
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
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(action.actionType.badgeColorHex))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = action.actionType.label,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "via @${action.requestingBotId}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Cyan400,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                // Security Shield Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Review Gate",
                        tint = if (action.status == ApprovalStatus.PENDING) Amber400 else Emerald400,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (action.status == ApprovalStatus.PENDING) "Review Required" else action.status.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (action.status == ApprovalStatus.PENDING) Amber400 else Emerald400,
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
                    color = Slate100,
                    fontSize = 14.sp
                )
            )

            Text(
                text = "Target: ${action.targetSummary}",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Slate400,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Fields Table / Preview
            Surface(
                color = Slate900,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(0.8.dp, Slate700),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    if (isEditing) {
                        editedFields.forEach { (key, value) ->
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(
                                    text = key,
                                    style = MaterialTheme.typography.labelSmall.copy(color = Cyan400, fontWeight = FontWeight.Bold)
                                )
                                OutlinedTextField(
                                    value = value,
                                    onValueChange = { newVal ->
                                        editedFields = editedFields.toMutableMap().apply { put(key, newVal) }
                                    },
                                    singleLine = key != "Body",
                                    maxLines = if (key == "Body") 6 else 1,
                                    textStyle = MaterialTheme.typography.bodySmall.copy(color = Slate100),
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
                                        color = Slate400,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier.weight(0.35f)
                                )
                                Text(
                                    text = value,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Slate100,
                                        fontSize = 12.sp
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

            // Action Buttons or Executed status
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
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save & Execute", color = Slate950, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        OutlinedButton(
                            onClick = { isEditing = false },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = Slate300, fontSize = 12.sp)
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onApprove(action.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("approve_btn_${action.id}")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Slate950, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Approve & Execute", color = Slate950, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { isEditing = true },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            modifier = Modifier.weight(0.9f)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = Cyan400, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit", color = Cyan400, fontSize = 12.sp)
                        }

                        IconButton(
                            onClick = { onReject(action.id) },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Slate800)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Reject", tint = Rose500, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            } else {
                // Completed status row
                Surface(
                    color = Slate900,
                    shape = RoundedCornerShape(8.dp),
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
                                .background(if (action.status == ApprovalStatus.REJECTED) Rose500 else Emerald400)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${action.executedAt ?: action.status.label} · ${action.executionResult ?: ""}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (action.status == ApprovalStatus.REJECTED) Rose500 else Emerald400,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
