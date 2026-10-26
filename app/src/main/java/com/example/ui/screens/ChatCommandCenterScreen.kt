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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
    val theme = LocalCozyTheme.current
    val listState = rememberLazyListState()
    var showSlashCommands by remember { mutableStateOf(false) }
    var isBentoCollapsed by remember { mutableStateOf(false) }

    val quickActionChips = listOf(
        Pair("⚡ Morning Brief", "/brief"),
        Pair("🫧 Compare Supplier RFQs", "/rfq"),
        Pair("🌱 Project Grant Status", "/grant"),
        Pair("🧬 Check Lab & Facility Slots", "/lab-check"),
        Pair("🔬 Biomolecular Modeling", "/boltz"),
        Pair("✍️ Weekly Content Cycle", "/content"),
        Pair("🎙️ Context Interview", "/interview")
    )

    val slashCommands = listOf(
        Pair("/brief", "Run complete morning workspace & schedule briefing"),
        Pair("/rfq", "Generate supplier RFQ email for prototype components"),
        Pair("/grant", "Check project grant status, milestones & prototyping budget"),
        Pair("/boltz", "Prepare biomolecular structure & docking setup"),
        Pair("/content", "Trigger weekly content pipeline sweep"),
        Pair("/lab-check", "Check external analytical facility slots availability"),
        Pair("/learn", "Extract discussed workflow and save as new SKILL in Vault"),
        Pair("/interview", "Start 1-question-at-a-time Business Context Interview")
    )

    LaunchedEffect(messages.size, isGenerating) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.canvasBg)
    ) {
        // Collapsible Bento Morning Brief Header (When on command-center)
        if (channelId == "command-center") {
            val latestBento = messages.mapNotNull { it.bentoBriefing }.lastOrNull()
            if (latestBento != null) {
                Surface(
                    color = theme.canvasBg,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isBentoCollapsed = !isBentoCollapsed }
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "🌿 Morning Brief Bento",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = theme.accentPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isBentoCollapsed) "(Tap to expand)" else "(Tap to collapse)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = theme.textSecondary,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            Icon(
                                imageVector = if (isBentoCollapsed) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                                contentDescription = null,
                                tint = theme.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        AnimatedVisibility(visible = !isBentoCollapsed) {
                            BentoBriefingView(
                                bento = latestBento,
                                modifier = Modifier.padding(top = 4.dp, bottom = 6.dp)
                            )
                        }
                    }
                }
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
            contentPadding = PaddingValues(top = 8.dp, bottom = 12.dp)
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
                        CozyBotMessageBubble(
                            message = msg,
                            onApproveAction = onApproveAction,
                            onRejectAction = onRejectAction,
                            onEditAndApproveAction = onEditAndApproveAction
                        )
                    }
                }
            }

            // Typing / Thinking Indicator with living mascot
            if (isGenerating) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    ) {
                        BotMascot(
                            botId = activeBot?.id ?: "atlas",
                            state = MascotState.THINKING,
                            size = 32.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "${activeBot?.name ?: "Agent"} is analyzing workspace & drafting reply...",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = theme.accentPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // Slash command autocomplete drawer
        AnimatedVisibility(
            visible = showSlashCommands || inputText.startsWith("/"),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Surface(
                color = theme.cardSurface,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, theme.cardBorder),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "⚡ Quick Slash Commands",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = theme.textSecondary,
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
                                color = theme.accentPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.width(90.dp)
                            )
                            Text(
                                text = desc,
                                color = theme.textPrimary,
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
            items(quickActionChips) { (label, cmd) ->
                Surface(
                    color = theme.cardSurface,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, theme.cardBorder),
                    modifier = Modifier.clickable { onSendMessage(cmd) }
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = theme.textPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Bottom Composer Bar
        Surface(
            color = theme.canvasBg,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 0.5.dp, color = theme.cardBorder)
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
                        .background(if (showSlashCommands) theme.accentPrimary else theme.cardSurface)
                        .border(0.8.dp, theme.cardBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = "Commands",
                        tint = if (showSlashCommands) Color.White else theme.textSecondary,
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
                            color = theme.textSecondary,
                            fontSize = 13.sp
                        )
                    },
                    singleLine = false,
                    maxLines = 4,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = theme.textPrimary, fontSize = 13.sp),
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = theme.accentPrimary,
                        unfocusedBorderColor = theme.cardBorder,
                        focusedContainerColor = theme.cardSurface,
                        unfocusedContainerColor = theme.cardSurface
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
                        .background(theme.cardSurface)
                        .border(0.8.dp, theme.cardBorder, CircleShape)
                        .testTag("mic_dictation_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Dictate / Call",
                        tint = theme.accentPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Send Button
                FilledIconButton(
                    onClick = { onSendMessage(null) },
                    enabled = inputText.isNotBlank() && !isGenerating,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = theme.accentPrimary,
                        disabledContainerColor = theme.cardSurface
                    ),
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank()) Color.White else theme.textMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CozyBotMessageBubble(
    message: ChatMessage,
    onApproveAction: (String) -> Unit,
    onRejectAction: (String) -> Unit,
    onEditAndApproveAction: (String, Map<String, String>) -> Unit
) {
    val theme = LocalCozyTheme.current
    val bot = Bot.ROSTER.find { it.id == message.botId } ?: Bot.ROSTER[0]
    var showSteps by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        // Mascot avatar + Name Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        ) {
            BotMascot(
                botId = bot.id,
                state = bot.status.toMascotState(),
                size = 28.dp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = bot.name,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = theme.textPrimary,
                    fontSize = 12.sp
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message.timestamp,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = theme.textSecondary,
                    fontSize = 9.sp
                )
            )
        }

        // Main text card
        if (message.text.isNotBlank()) {
            Surface(
                color = theme.cardSurface,
                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, theme.cardBorder),
                shadowElevation = 1.dp,
                modifier = Modifier.widthIn(max = 340.dp)
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = theme.textPrimary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    ),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }
        }

        // Collapsible "Sources & Steps Used" pill
        if (message.stepsUsed.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = theme.cardElevated,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(0.6.dp, theme.cardBorder),
                modifier = Modifier.clickable { showSteps = !showSteps }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔍 ${message.stepsUsed.size} Steps & Sources Used",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = theme.accentPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (showSteps) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = theme.accentPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            AnimatedVisibility(visible = showSteps) {
                Surface(
                    color = theme.cardElevated,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(0.6.dp, theme.cardBorder),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(top = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        message.stepsUsed.forEach { step ->
                            Text(
                                text = "• $step",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = theme.textSecondary,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }

        // Embedded Bento Briefing
        if (message.bentoBriefing != null) {
            Spacer(modifier = Modifier.height(8.dp))
            BentoBriefingView(bento = message.bentoBriefing)
        }

        // Embedded Vendor Scorecard
        if (message.scorecard != null) {
            Spacer(modifier = Modifier.height(8.dp))
            VendorScorecardCard(scorecard = message.scorecard)
        }

        // Embedded Portal Takeover Card
        if (message.portalTakeover != null) {
            Spacer(modifier = Modifier.height(8.dp))
            PortalTakeoverCard(card = message.portalTakeover)
        }

        // Embedded Skill Learned Card
        if (message.skillLearned != null) {
            Spacer(modifier = Modifier.height(8.dp))
            SkillLearnedCard(skill = message.skillLearned)
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

@Composable
fun UserMessageBubble(
    text: String,
    timestamp: String
) {
    val theme = LocalCozyTheme.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Surface(
            color = theme.accentPrimary,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 4.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
            shadowElevation = 2.dp,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                ),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = timestamp,
            style = MaterialTheme.typography.labelSmall.copy(
                color = theme.textSecondary,
                fontSize = 9.sp
            ),
            modifier = Modifier.padding(end = 4.dp)
        )
    }
}
