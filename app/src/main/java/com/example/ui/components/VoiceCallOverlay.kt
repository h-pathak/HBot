package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun VoiceCallOverlay(
    isSpeaking: Boolean,
    transcript: String,
    onVoiceInput: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalCozyTheme.current

    val infiniteTransition = rememberInfiniteTransition(label = "voice_wave")
    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse1"
    )

    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse2"
    )

    val quickVoicePrompts = listOf(
        "Atlas, run morning workspace briefing",
        "Reactor, check fabrication supplier quotes",
        "Helix, check analytical instrument booking slots",
        "Pulse, outline weekly publication draft",
        "Forge, calculate fluidic Reynolds number"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background((if (theme.isNight) VelvetMidnightBg else OatmilkBg).copy(alpha = 0.96f))
            .padding(24.dp)
            .testTag("voice_call_overlay"),
        contentAlignment = Alignment.Center
    ) {
        // Top row with close action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(ReactorPrimary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Live Agent Call · Asia/Kolkata",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = ReactorPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(theme.cardSurface)
                    .border(0.8.dp, theme.cardBorder, CircleShape)
                    .testTag("close_voice_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Call",
                    tint = theme.textSecondary
                )
            }
        }

        // Center: Living Animated Companions Orb
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                // Outer wave 2
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(pulse2)
                        .clip(CircleShape)
                        .background(theme.accentPrimary.copy(alpha = 0.12f))
                )

                // Outer wave 1
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(pulse1)
                        .clip(CircleShape)
                        .background(ReactorPrimary.copy(alpha = 0.20f))
                )

                // Mascot Living Character in Voice Orb
                BotMascot(
                    botId = "atlas",
                    state = if (isSpeaking) MascotState.WORKING else MascotState.LISTENING,
                    size = 80.dp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = if (isSpeaking) "HBot Speaking..." else "Listening...",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = theme.textPrimary
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Transcript bubble
            Surface(
                color = theme.cardSurface,
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, theme.cardBorder),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = if (transcript.isNotBlank()) transcript else "Ask anything regarding hardware prototyping, research, articles, or code...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (transcript.isNotBlank()) theme.textPrimary else theme.textSecondary,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Bottom prompt suggestions & End Call
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tap to speak directly:",
                style = MaterialTheme.typography.labelSmall.copy(color = theme.textSecondary),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(quickVoicePrompts) { prompt ->
                    Surface(
                        color = theme.cardSurface,
                        shape = RoundedCornerShape(18.dp),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, theme.cardBorder),
                        modifier = Modifier.clickable { onVoiceInput(prompt) }
                    ) {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = theme.textPrimary,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // End Call button
            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Rose500,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(48.dp)
                    .testTag("end_voice_call_button")
            ) {
                Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("End Voice Call", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
