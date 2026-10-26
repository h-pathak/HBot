package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActivityStatus
import com.example.data.model.ActivityTask
import com.example.data.model.Routine
import com.example.ui.components.BotMascot
import com.example.ui.components.MascotState
import com.example.ui.theme.*

@Composable
fun ActivityRoutinesScreen(
    activities: List<ActivityTask>,
    routines: List<Routine>,
    heartbeatStatus: String?,
    onRunHeartbeat: () -> Unit,
    onToggleRoutine: (String) -> Unit,
    onToggleTaskPause: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalCozyTheme.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.canvasBg)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("activity_routines_screen")
    ) {
        // Header Row & Proactive Workspace Sweep Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Always-On Engine 🌱",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = theme.textPrimary,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = "ChatGPT Dots Activity View & Scheduler",
                    style = MaterialTheme.typography.labelSmall.copy(color = theme.accentPrimary)
                )
            }

            // Prominent "Run Proactive Workspace Sweep 🌿" Button
            Button(
                onClick = onRunHeartbeat,
                colors = ButtonDefaults.buttonColors(containerColor = ReactorPrimary),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier.testTag("run_heartbeat_button")
            ) {
                Text(
                    text = "Workspace Sweep 🌿",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        // Heartbeat Progress Alert
        if (heartbeatStatus != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                color = theme.cardSurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ReactorPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = ReactorPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = heartbeatStatus,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = theme.textPrimary,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // SECTION 1: Ongoing Responsibilities (Dots Activity View)
            item {
                Text(
                    text = "ONGOING RESPONSIBILITIES (ACTIVITY VIEW)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = theme.textSecondary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }

            items(activities, key = { it.id }) { task ->
                CozyActivityTaskCard(
                    task = task,
                    onTogglePause = { onToggleTaskPause(task.id) }
                )
            }

            // SECTION 2: Standing Routines (Scheduler)
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "STANDING ROUTINES (CRON SCHEDULER)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = theme.textSecondary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }

            items(routines, key = { it.id }) { routine ->
                CozyRoutineCard(
                    routine = routine,
                    onToggle = { onToggleRoutine(routine.id) }
                )
            }
        }
    }
}

@Composable
fun CozyActivityTaskCard(
    task: ActivityTask,
    onTogglePause: () -> Unit
) {
    val theme = LocalCozyTheme.current

    Surface(
        color = theme.cardSurface,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, theme.cardBorder),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Agent & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BotMascot(
                        botId = task.botId,
                        state = if (task.status == ActivityStatus.RUNNING) MascotState.WORKING else MascotState.IDLE,
                        size = 32.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "@${task.botId}",
                            color = theme.accentPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "Step ${task.stepIndex} of ${task.totalSteps}",
                            style = MaterialTheme.typography.labelSmall.copy(color = theme.textSecondary, fontSize = 10.sp)
                        )
                    }
                }

                // Pause / Resume control
                IconButton(
                    onClick = onTogglePause,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(theme.cardElevated)
                ) {
                    Icon(
                        imageVector = if (task.status == ActivityStatus.RUNNING) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Toggle Pause",
                        tint = if (task.status == ActivityStatus.RUNNING) AtlasPrimary else ReactorPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = task.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = theme.textPrimary,
                    fontSize = 14.sp
                )
            )

            Text(
                text = task.currentStep,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = ReactorPrimary,
                    fontSize = 12.sp
                ),
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { task.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = theme.accentPrimary,
                trackColor = theme.cardElevated,
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Step Logs box
            Surface(
                color = theme.cardElevated,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(0.6.dp, theme.cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Latest steps taken:",
                        style = MaterialTheme.typography.labelSmall.copy(color = theme.textSecondary, fontSize = 10.sp)
                    )
                    task.logs.takeLast(2).forEach { log ->
                        Text(
                            text = "• $log",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = theme.textPrimary,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CozyRoutineCard(
    routine: Routine,
    onToggle: () -> Unit
) {
    val theme = LocalCozyTheme.current

    Surface(
        color = theme.cardSurface,
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, theme.cardBorder),
        shadowElevation = 1.dp,
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
                    BotMascot(
                        botId = routine.botId,
                        state = MascotState.IDLE,
                        size = 24.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "@${routine.botId} · ${routine.scheduleText}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = theme.textSecondary,
                            fontSize = 10.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = routine.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = theme.textPrimary,
                        fontSize = 13.sp
                    )
                )

                Text(
                    text = routine.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = theme.textSecondary,
                        fontSize = 11.sp
                    ),
                    maxLines = 2,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Text(
                    text = "Last executed: ${routine.lastRunText}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ReactorPrimary,
                        fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Switch(
                checked = routine.isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = ReactorPrimary,
                    uncheckedThumbColor = theme.textSecondary,
                    uncheckedTrackColor = theme.cardElevated
                )
            )
        }
    }
}
