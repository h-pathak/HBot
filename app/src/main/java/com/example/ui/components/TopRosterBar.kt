package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Bot
import com.example.data.model.BotStatus
import com.example.ui.theme.*

@Composable
fun TopRosterBar(
    bots: List<Bot>,
    selectedChannelId: String,
    onSelectChannel: (String) -> Unit,
    onOpenVoiceCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Slate900,
        tonalElevation = 6.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(width = 0.5.dp, color = Slate800)
    ) {
        Column(modifier = Modifier.padding(top = 8.dp, bottom = 10.dp)) {
            // Header Row: App Identity & Voice Call Action
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Emerald400, Cyan400, Indigo500)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("H", color = Slate950, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "HariomBot",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Slate100,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "NocVita & MSU Baroda Workspace",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Slate400,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Live Voice Call button
                FilledTonalButton(
                    onClick = onOpenVoiceCall,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Slate800,
                        contentColor = Cyan400
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("voice_call_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Live Voice Call",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Voice Live", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Horizontal Scrollable Roster Bar (Presence as Interface)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                bots.forEach { bot ->
                    BotRosterItem(
                        bot = bot,
                        isSelected = bot.id == selectedChannelId,
                        onClick = { onSelectChannel(bot.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun BotRosterItem(
    bot: Bot,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Status animated border
    val infiniteTransition = rememberInfiniteTransition(label = "roster_orbit")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val borderModifier = when (bot.status) {
        BotStatus.THINKING -> Modifier
            .rotate(rotation)
            .border(
                width = 2.dp,
                brush = Brush.sweepGradient(listOf(Indigo500, Cyan400, Indigo500)),
                shape = CircleShape
            )
        BotStatus.WORKING -> Modifier
            .border(
                width = 2.dp,
                color = Emerald400,
                shape = CircleShape
            )
        BotStatus.WAITING_APPROVAL -> Modifier
            .border(
                width = 2.dp,
                color = Amber400,
                shape = CircleShape
            )
        else -> if (isSelected) {
            Modifier.border(2.dp, Cyan400, CircleShape)
        } else {
            Modifier.border(1.dp, Slate700, CircleShape)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(if (isSelected) Slate800 else Color.Transparent)
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag("bot_roster_${bot.id}")
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Outer Ring
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .then(borderModifier)
                    .clip(CircleShape)
                    .background(Slate850),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = bot.avatarEmoji,
                    fontSize = 20.sp
                )
            }

            // Status Indicator Dot or Badge
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(bot.status.color)
                    .border(1.5.dp, Slate900, CircleShape)
            )

            // Needs Approval Badge count
            if (bot.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(Amber500),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = bot.unreadCount.toString(),
                        color = Slate950,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = bot.name,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Slate100 else Slate400,
                fontSize = 11.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
