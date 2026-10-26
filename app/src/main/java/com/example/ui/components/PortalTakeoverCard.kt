package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PortalTakeoverCard
import com.example.ui.theme.*

@Composable
fun PortalTakeoverCard(
    card: PortalTakeoverCard,
    onMarkCompleted: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val theme = LocalCozyTheme.current
    val clipboardManager = LocalClipboardManager.current
    var isDone by remember { mutableStateOf(card.isLoggedOn) }
    var copiedNotice by remember { mutableStateOf<String?>(null) }

    Surface(
        color = theme.cardSurface,
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, HelixPrimary.copy(alpha = 0.6f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(HelixPrimary.copy(alpha = 0.15f))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "🔑 Portal Takeover Checklist",
                            color = HelixPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    color = if (isDone) ReactorLight else theme.cardElevated,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (isDone) "✓ Completed" else "Action Needed",
                        color = if (isDone) ReactorDark else theme.textSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = card.portalName,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = theme.textPrimary,
                    fontSize = 14.sp
                )
            )

            Text(
                text = card.purpose,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = theme.textSecondary,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Step items
            card.checklist.forEach { item ->
                Surface(
                    color = theme.cardElevated,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(0.6.dp, theme.cardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.stepLabel,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = theme.textPrimary,
                                    fontSize = 11.sp
                                )
                            )
                            if (item.copyableValue != null) {
                                Text(
                                    text = "Value: ${item.copyableValue}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = HelixPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        if (item.copyableValue != null) {
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(item.copyableValue))
                                    copiedNotice = "Copied ${item.copyableValue}!"
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = theme.accentPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (copiedNotice != null) {
                Text(
                    text = copiedNotice ?: "",
                    color = ReactorPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mark completed button
            Button(
                onClick = {
                    isDone = true
                    onMarkCompleted()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDone) theme.cardElevated else HelixPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (isDone) Icons.Default.Check else Icons.Default.LockOpen,
                    contentDescription = null,
                    tint = if (isDone) theme.textSecondary else Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isDone) "Portal Verified & Logged In" else "Mark Logged In & Proceed ✨",
                    color = if (isDone) theme.textSecondary else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
