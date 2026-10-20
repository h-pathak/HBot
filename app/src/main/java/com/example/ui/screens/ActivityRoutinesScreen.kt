package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate900)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("activity_routines_screen")
    ) {
        // Header Row & Proactive Heartbeat Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Always-On Engine",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate100,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = "Dots Activity View & Standing Routines",
                    style = MaterialTheme.typography.labelSmall.copy(color = Cyan400)
                )
            }

            // "Run Proactive Heartbeat Now" Button
            Button(
                onClick = onRunHeartbeat,
                colors = ButtonDefaults.buttonColors(containerColor = Cyan400),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("run_heartbeat_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = Slate950,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Run Heartbeat",
                    color = Slate950,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        // Heartbeat Progress Alert
        if (heartbeatStatus != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                color = Slate850,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Cyan400),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Cyan400,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = heartbeatStatus,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Slate100,
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
                        color = Slate400,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }

            items(activities, key = { it.id }) { task ->
                ActivityTaskCard(
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
                        color = Slate400,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }

            items(routines, key = { it.id }) { routine ->
                RoutineCard(
                    routine = routine,
                    onToggle = { onToggleRoutine(routine.id) }
                )
            }
        }
    }
}

@Composable
fun ActivityTaskCard(
    task: ActivityTask,
    onTogglePause: () -> Unit
) {
    Surface(
        color = Slate850,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
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
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Cyan400.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "@${task.botId}",
                            color = Cyan400,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Step ${task.stepIndex} of ${task.totalSteps}",
                        style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 11.sp)
                    )
                }

                // Pause / Resume control
                IconButton(
                    onClick = onTogglePause,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Slate800)
                ) {
                    Icon(
                        imageVector = if (task.status == ActivityStatus.RUNNING) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Toggle Pause",
                        tint = if (task.status == ActivityStatus.RUNNING) Amber400 else Emerald400,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = task.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Slate100,
                    fontSize = 14.sp
                )
            )

            Text(
                text = task.currentStep,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Emerald400,
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
                color = Cyan400,
                trackColor = Slate800,
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Step Logs accordion
            Surface(
                color = Slate900,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(0.6.dp, Slate700),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Latest execution log:",
                        style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 10.sp)
                    )
                    task.logs.takeLast(2).forEach { log ->
                        Text(
                            text = "• $log",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Slate300,
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
fun RoutineCard(
    routine: Routine,
    onToggle: () -> Unit
) {
    Surface(
        color = Slate850,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, Slate700),
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
                    Text(
                        text = "@${routine.botId}",
                        color = Cyan400,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = routine.scheduleText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate400,
                            fontSize = 10.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = routine.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate100,
                        fontSize = 13.sp
                    )
                )

                Text(
                    text = routine.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Slate300,
                        fontSize = 11.sp
                    ),
                    maxLines = 2,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Text(
                    text = "Last executed: ${routine.lastRunText}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Emerald400,
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
                    checkedThumbColor = Slate950,
                    checkedTrackColor = Cyan400,
                    uncheckedThumbColor = Slate400,
                    uncheckedTrackColor = Slate800
                )
            )
        }
    }
}
