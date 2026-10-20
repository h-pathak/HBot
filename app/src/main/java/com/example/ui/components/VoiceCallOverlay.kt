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
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
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
        "Atlas, run 8:30 AM workspace briefing",
        "Reactor, compare Borosilicate column quotes",
        "Helix, check I-STEM ICP-MS booking slots",
        "Pulse, outline Friday bioprocess blog draft",
        "Forge, check oscillatory flow Reynolds number"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950.copy(alpha = 0.94f))
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
                        .background(Emerald400)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Live Agent Call · Asia/Kolkata",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Emerald400,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Slate800)
                    .testTag("close_voice_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Call",
                    tint = Slate300
                )
            }
        }

        // Center: Animated Glowing Orb
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
                        .background(Cyan500.copy(alpha = 0.12f))
                )

                // Outer wave 1
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(pulse1)
                        .clip(CircleShape)
                        .background(Emerald400.copy(alpha = 0.22f))
                )

                // Core Agent Sphere
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Cyan400, Indigo500, Slate900)
                            )
                        )
                        .border(2.dp, Cyan400, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.Mic,
                        contentDescription = "Microphone",
                        tint = Slate950,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (isSpeaking) "HariomBot Speaking..." else "Listening to Hariom...",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Transcript bubble
            Surface(
                color = Slate800,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = if (transcript.isNotBlank()) transcript else "Ask anything regarding NocVita, MSU Baroda labs, or your content calendar...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (transcript.isNotBlank()) Slate100 else Slate400,
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
                style = MaterialTheme.typography.labelSmall.copy(color = Slate400),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(quickVoicePrompts) { prompt ->
                    Surface(
                        color = Slate800,
                        shape = RoundedCornerShape(18.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                        modifier = Modifier.clickable { onVoiceInput(prompt) }
                    ) {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.labelSmall.copy(color = Cyan400),
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
                    contentColor = Slate950
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(48.dp)
                    .testTag("end_voice_call_button")
            ) {
                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("End Voice Call", fontWeight = FontWeight.Bold)
            }
        }
    }
}
