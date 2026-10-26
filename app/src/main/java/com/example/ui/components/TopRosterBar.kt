package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewSidebar
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Bot
import com.example.ui.theme.*

@Composable
fun TopRosterBar(
    bots: List<Bot>,
    selectedChannelId: String,
    isNightMode: Boolean,
    isStudioMode: Boolean,
    userSubtitle: String = "Multi-Agent Workspace",
    onSelectChannel: (String) -> Unit,
    onToggleTheme: () -> Unit,
    onToggleStudioMode: () -> Unit,
    onOpenVoiceCall: () -> Unit,
    onOpenVault: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val theme = LocalCozyTheme.current

    Surface(
        color = theme.canvasBg,
        tonalElevation = 3.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(width = 0.5.dp, color = theme.cardBorder)
    ) {
        Column(modifier = Modifier.padding(top = 8.dp, bottom = 10.dp)) {
            // Header Row: App Identity, Theme Toggle, Studio Toggle, Voice Call
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Identity
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ReactorPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🌿", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "HBot",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = theme.textPrimary,
                                    fontSize = 17.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                color = ReactorLight,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "v2 Cozy",
                                    color = ReactorDark,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = userSubtitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = theme.textSecondary,
                                fontSize = 10.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Header Action Pill Cluster
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Private Vault Button
                    IconButton(
                        onClick = onOpenVault,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(theme.cardSurface)
                            .border(0.8.dp, theme.cardBorder, CircleShape)
                            .testTag("private_vault_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Private Vault",
                            tint = theme.accentPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Studio Mode Toggle
                    IconButton(
                        onClick = onToggleStudioMode,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(theme.cardSurface)
                            .border(0.8.dp, theme.cardBorder, CircleShape)
                            .testTag("studio_mode_toggle")
                    ) {
                        Icon(
                            imageVector = if (isStudioMode) Icons.AutoMirrored.Filled.ViewSidebar else Icons.Default.Smartphone,
                            contentDescription = "Toggle Studio Mode",
                            tint = if (isStudioMode) theme.accentPrimary else theme.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // 1-Tap Theme Toggle (Matcha & Oatmilk vs Bioluminescent Night)
                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(theme.cardSurface)
                            .border(0.8.dp, theme.cardBorder, CircleShape)
                            .testTag("theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isNightMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = if (isNightMode) BioluminescentAmber else WarmStoneText,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Live Voice Call button
                    FilledTonalButton(
                        onClick = onOpenVoiceCall,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = theme.cardSurface,
                            contentColor = theme.accentPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, theme.cardBorder),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("voice_call_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Live Voice Call",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Voice", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Horizontal Scrollable Living Bot Mascot Roster Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                bots.forEach { bot ->
                    val isSelected = bot.id == selectedChannelId
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onSelectChannel(bot.id) }
                            .background(if (isSelected) theme.cardSurface else Color.Transparent)
                            .border(
                                width = if (isSelected) 1.2.dp else 0.dp,
                                color = if (isSelected) theme.accentPrimary else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("bot_roster_${bot.id}")
                    ) {
                        BotMascot(
                            botId = bot.id,
                            state = bot.status.toMascotState(),
                            size = 46.dp
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = bot.name,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) theme.textPrimary else theme.textSecondary,
                                fontSize = 11.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
