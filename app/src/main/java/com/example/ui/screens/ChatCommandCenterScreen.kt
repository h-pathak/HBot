package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ChatCommandCenterScreen(
    channelId: String,
    messages: List<ChatMessage>,
    activeBot: Bot?,
    inputText: String,
    isGenerating: Boolean,
    onInputTextChanged: (String) -> Unit,
    onSendMessage: (String?) -> Unit,
    onApproveAction: (String) -> Unit,
    onRejectAction: (String) -> Unit,
    onEditAndApproveAction: (String, Map<String, String>) -> Unit,
    onOpenVoiceCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var showSlashCommands by remember { mutableStateOf(false) }

    val slashCommands = listOf(
        Pair("/briefing", "Run full 08:30 AM workspace & schedule briefing"),
        Pair("/rfq", "Generate technical vendor RFQ email for PBR components"),
        Pair("/content", "Trigger Thursday-to-Wednesday content pipeline sweep"),
        Pair("/lab-check", "Check I-STEM & GBRC analytical slots availability"),
        Pair("/learn", "Extract discussed workflow and save as new SKILL.md"),
        Pair("/interview", "Start 1-question-at-a-time Business Context Interview")
    )

    // Auto scroll to bottom when messages update
    LaunchedEffect(messages.size, isGenerating) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate900)
    ) {
        // Active Channel Header Sub-bar
        Surface(
            color = Slate850,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (channelId == "command-center") "⚡ Command Center" else "${activeBot?.avatarEmoji ?: "🤖"} @${activeBot?.name ?: channelId}",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Cyan400,
                            fontSize = 13.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(activeBot?.status?.color ?: Emerald400)
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = activeBot?.status?.label ?: "Active",
                            color = Slate950,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = activeBot?.currentAction ?: "All agents coordinated",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Slate400,
                        fontSize = 10.sp
                    ),
                    maxLines = 1
                )
            }
        }

        // Timeline Messages
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 12.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                when {
                    msg.handoff != null -> {
                        MultiBotHandoffBanner(handoff = msg.handoff)
                    }
                    msg.senderType == SenderType.USER -> {
                        UserMessageBubble(text = msg.text, timestamp = msg.timestamp)
                    }
                    else -> {
                        BotMessageBubble(
                            message = msg,
                            onApproveAction = onApproveAction,
                            onRejectAction = onRejectAction,
                            onEditAndApproveAction = onEditAndApproveAction
                        )
                    }
                }
            }

            // Typing / Thinking Indicator
            if (isGenerating) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = Cyan400,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${activeBot?.name ?: "Agent"} is analyzing workspace & drafting reply...",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Cyan400,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // Slash command autocomplete popup
        AnimatedVisibility(
            visible = showSlashCommands || inputText.startsWith("/"),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Surface(
                color = Slate850,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Quick Slash Commands",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate400,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    slashCommands.forEach { (cmd, desc) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    showSlashCommands = false
                                    onSendMessage(cmd)
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = cmd,
                                color = Cyan400,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.width(90.dp)
                            )
                            Text(
                                text = desc,
                                color = Slate300,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Fast Quick-Action Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(slashCommands) { (cmd, _) ->
                Surface(
                    color = Slate800,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(0.6.dp, Slate700),
                    modifier = Modifier.clickable { onSendMessage(cmd) }
                ) {
                    Text(
                        text = cmd,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Cyan400,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Bottom Composer Bar
        Surface(
            color = Slate950,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 0.5.dp, color = Slate800)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Slash command toggle button
                IconButton(
                    onClick = { showSlashCommands = !showSlashCommands },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (showSlashCommands) Cyan400 else Slate850)
                ) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = "Commands",
                        tint = if (showSlashCommands) Slate950 else Slate300,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Text Input
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputTextChanged,
                    placeholder = {
                        Text(
                            text = "Message @${activeBot?.name ?: "agents"} or type / ...",
                            color = Slate400,
                            fontSize = 13.sp
                        )
                    },
                    singleLine = false,
                    maxLines = 4,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Slate100, fontSize = 13.sp),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Cyan400,
                        unfocusedBorderColor = Slate700,
                        focusedContainerColor = Slate850,
                        unfocusedContainerColor = Slate850
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSendMessage(null) }),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field")
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Mic / Voice Button
                IconButton(
                    onClick = onOpenVoiceCall,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Slate850)
                        .testTag("mic_dictation_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Dictate / Call",
                        tint = Cyan400,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Send Button
                FilledIconButton(
                    onClick = { onSendMessage(null) },
                    enabled = inputText.isNotBlank() && !isGenerating,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Cyan400,
                        disabledContainerColor = Slate800
                    ),
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank()) Slate950 else Slate600,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun UserMessageBubble(text: String, timestamp: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Surface(
            color = Cyan500,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Slate950,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                ),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
        Text(
            text = timestamp,
            style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 9.sp),
            modifier = Modifier.padding(top = 2.dp, end = 4.dp)
        )
    }
}

@Composable
fun BotMessageBubble(
    message: ChatMessage,
    onApproveAction: (String) -> Unit,
    onRejectAction: (String) -> Unit,
    onEditAndApproveAction: (String, Map<String, String>) -> Unit
) {
    val bot = Bot.ROSTER.find { it.id == message.botId } ?: Bot.ROSTER[0]

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        // Bot name header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        ) {
            Text(
                text = bot.avatarEmoji,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = bot.name,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = bot.themeColor,
                    fontSize = 11.sp
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message.timestamp,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Slate400,
                    fontSize = 9.sp
                )
            )
        }

        // Main prose bubble
        if (message.text.isNotBlank()) {
            Surface(
                color = Slate800,
                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                border = androidx.compose.foundation.BorderStroke(0.6.dp, Slate700),
                modifier = Modifier.widthIn(max = 340.dp)
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Slate100,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    ),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }
        }

        // Embedded Vendor Scorecard
        if (message.scorecard != null) {
            Spacer(modifier = Modifier.height(8.dp))
            VendorScorecardCard(scorecard = message.scorecard)
        }

        // Embedded Inline Approval Card
        if (message.approvalAction != null) {
            Spacer(modifier = Modifier.height(8.dp))
            InlineApprovalCard(
                action = message.approvalAction,
                onApprove = onApproveAction,
                onReject = onRejectAction,
                onEditAndApprove = onEditAndApproveAction
            )
        }

        // Embedded Citation Chips
        if (message.citationChips.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            CitationChipRow(chips = message.citationChips)
        }
    }
}
