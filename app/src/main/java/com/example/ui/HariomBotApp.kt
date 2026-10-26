package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.ApprovalStatus
import com.example.ui.components.ConfettiView
import com.example.ui.components.TopRosterBar
import com.example.ui.components.VoiceCallOverlay
import com.example.ui.screens.*
import com.example.ui.theme.*

@Composable
fun HariomBotApp(
    viewModel: HariomBotViewModel = viewModel()
) {
    val isNightMode by viewModel.isNightMode.collectAsStateWithLifecycle()
    val isStudioMode by viewModel.isStudioMode.collectAsStateWithLifecycle()
    val confettiTrigger by viewModel.confettiTrigger.collectAsStateWithLifecycle()

    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val activeChannelId by viewModel.activeChannelId.collectAsStateWithLifecycle()
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val isVoiceCallActive by viewModel.isVoiceCallActive.collectAsStateWithLifecycle()
    val voiceTranscript by viewModel.voiceTranscript.collectAsStateWithLifecycle()
    val isVoiceSpeaking by viewModel.isVoiceSpeaking.collectAsStateWithLifecycle()

    val bots by viewModel.bots.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val approvals by viewModel.approvals.collectAsStateWithLifecycle()
    val activities by viewModel.activities.collectAsStateWithLifecycle()
    val routines by viewModel.routines.collectAsStateWithLifecycle()
    val memories by viewModel.memories.collectAsStateWithLifecycle()
    val skills by viewModel.skills.collectAsStateWithLifecycle()
    val connections by viewModel.workspaceConnections.collectAsStateWithLifecycle()

    val memoryQuery by viewModel.memorySearchQuery.collectAsStateWithLifecycle()
    val memoryCategory by viewModel.selectedMemoryCategory.collectAsStateWithLifecycle()
    val isInterviewActive by viewModel.isInterviewActive.collectAsStateWithLifecycle()
    val interviewStep by viewModel.interviewStep.collectAsStateWithLifecycle()
    val heartbeatStatus by viewModel.heartbeatStatus.collectAsStateWithLifecycle()
    val snackMessage by viewModel.snackMessage.collectAsStateWithLifecycle()

    val pendingApprovalsCount = approvals.count { it.status == ApprovalStatus.PENDING }
    val currentMessages = messages[activeChannelId] ?: emptyList()
    val activeBot = bots.find { it.id == activeChannelId }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackMessage) {
        snackMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnack()
        }
    }

    // Predictive BackHandler
    BackHandler(enabled = isVoiceCallActive || selectedTab != NavTab.CHAT || activeChannelId != "command-center") {
        when {
            isVoiceCallActive -> viewModel.endVoiceCall()
            selectedTab != NavTab.CHAT -> viewModel.selectTab(NavTab.CHAT)
            activeChannelId != "command-center" -> viewModel.selectChannel("command-center")
        }
    }

    HariomBotTheme(isNightMode = isNightMode) {
        val theme = LocalCozyTheme.current

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(theme.canvasBg)
        ) {
            // Radial mesh gradient aura blurs in background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.radialGradient(
                            listOf(theme.meshMint, Color.Transparent),
                            center = androidx.compose.ui.geometry.Offset(100f, 150f),
                            radius = 600f
                        )
                    )
            )

            // Centered device container: in Studio Mode it widens; in Phone Mode it's max-w-[440px]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .widthIn(max = if (isStudioMode) 1000.dp else 450.dp)
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent,
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    topBar = {
                        TopRosterBar(
                            bots = bots,
                            selectedChannelId = activeChannelId,
                            isNightMode = isNightMode,
                            isStudioMode = isStudioMode,
                            onSelectChannel = { channelId ->
                                viewModel.selectChannel(channelId)
                                viewModel.selectTab(NavTab.CHAT)
                            },
                            onToggleTheme = { viewModel.toggleTheme() },
                            onToggleStudioMode = { viewModel.toggleStudioMode() },
                            onOpenVoiceCall = { viewModel.startVoiceCall() },
                            modifier = Modifier.statusBarsPadding()
                        )
                    },
                    bottomBar = {
                        // Floating Glassmorphic Pill Bottom Dock
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .navigationBarsPadding(),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                color = theme.dockBg,
                                shape = RoundedCornerShape(32.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, theme.cardBorder),
                                shadowElevation = 10.dp,
                                modifier = Modifier
                                    .widthIn(max = 410.dp)
                                    .testTag("floating_dock")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 6.dp, vertical = 6.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    NavTab.entries.forEach { tab ->
                                        val isSelected = selectedTab == tab

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(22.dp))
                                                .clickable { viewModel.selectTab(tab) }
                                                .background(
                                                    if (isSelected) theme.accentPrimary.copy(alpha = 0.16f) else Color.Transparent
                                                )
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                BadgedBox(
                                                    badge = {
                                                        if (tab == NavTab.APPROVALS && pendingApprovalsCount > 0) {
                                                            Badge(
                                                                containerColor = AtlasPrimary,
                                                                contentColor = DeepEspressoText
                                                            ) {
                                                                Text(
                                                                    text = pendingApprovalsCount.toString(),
                                                                    fontWeight = FontWeight.Bold,
                                                                    fontSize = 9.sp
                                                                )
                                                            }
                                                        }
                                                    }
                                                ) {
                                                    Text(text = tab.iconEmoji, fontSize = 16.sp)
                                                }

                                                if (isSelected) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = tab.title,
                                                        color = theme.accentPrimary,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        if (isStudioMode) {
                            // 2-Column Studio Split Mode (Chat on Left, Active Tab on Right)
                            Row(modifier = Modifier.fillMaxSize()) {
                                Box(modifier = Modifier.weight(1.1f)) {
                                    ChatCommandCenterScreen(
                                        channelId = activeChannelId,
                                        messages = currentMessages,
                                        activeBot = activeBot,
                                        inputText = inputText,
                                        isGenerating = isGenerating,
                                        onInputTextChanged = { viewModel.onInputTextChanged(it) },
                                        onSendMessage = { viewModel.sendMessage(it) },
                                        onApproveAction = { viewModel.approveAction(it) },
                                        onRejectAction = { viewModel.rejectAction(it) },
                                        onEditAndApproveAction = { id, fields -> viewModel.editAndApproveAction(id, fields) },
                                        onOpenVoiceCall = { viewModel.startVoiceCall() }
                                    )
                                }
                                VerticalDivider(color = theme.cardBorder, thickness = 1.dp)
                                Box(modifier = Modifier.weight(0.9f)) {
                                    when (selectedTab) {
                                        NavTab.APPROVALS -> {
                                            ApprovalsScreen(
                                                approvals = approvals,
                                                onApproveAction = { viewModel.approveAction(it) },
                                                onRejectAction = { viewModel.rejectAction(it) },
                                                onEditAndApproveAction = { id, fields -> viewModel.editAndApproveAction(id, fields) }
                                            )
                                        }
                                        NavTab.ACTIVITY -> {
                                            ActivityRoutinesScreen(
                                                activities = activities,
                                                routines = routines,
                                                heartbeatStatus = heartbeatStatus,
                                                onRunHeartbeat = { viewModel.runHeartbeatNow() },
                                                onToggleRoutine = { viewModel.toggleRoutine(it) },
                                                onToggleTaskPause = { viewModel.toggleTaskPause(it) }
                                            )
                                        }
                                        NavTab.MEMORY -> {
                                            MemorySkillsScreen(
                                                memories = memories,
                                                skills = skills,
                                                searchQuery = memoryQuery,
                                                selectedCategory = memoryCategory,
                                                isInterviewActive = isInterviewActive,
                                                interviewStep = interviewStep,
                                                interviewQuestions = viewModel.interviewQuestions,
                                                onSearchChanged = { viewModel.setMemorySearch(it) },
                                                onCategorySelected = { viewModel.setMemoryCategory(it) },
                                                onAddMemory = { fact, cat, tags -> viewModel.addMemory(fact, cat, tags) },
                                                onStartInterview = { viewModel.startInterview() },
                                                onAnswerInterview = { viewModel.answerInterview(it) },
                                                onCancelInterview = { viewModel.cancelInterview() },
                                                onTriggerSkill = { skill ->
                                                    viewModel.sendMessage("Run procedure: ${skill.title}")
                                                }
                                            )
                                        }
                                        NavTab.WORKSPACE -> {
                                            WorkspaceAntigravityScreen(
                                                connections = connections,
                                                dailyScheduleId = viewModel.dailyScheduleId,
                                                contentScheduleId = viewModel.contentScheduleId,
                                                onUpdateSheetIds = { daily, content -> viewModel.updateSheetIds(daily, content) },
                                                onShowSnack = { viewModel.showSnack(it) }
                                            )
                                        }
                                        NavTab.CHAT -> {
                                            ActivityRoutinesScreen(
                                                activities = activities,
                                                routines = routines,
                                                heartbeatStatus = heartbeatStatus,
                                                onRunHeartbeat = { viewModel.runHeartbeatNow() },
                                                onToggleRoutine = { viewModel.toggleRoutine(it) },
                                                onToggleTaskPause = { viewModel.toggleTaskPause(it) }
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // Standard 1-Column Mobile PWA Mode
                            when (selectedTab) {
                                NavTab.CHAT -> {
                                    ChatCommandCenterScreen(
                                        channelId = activeChannelId,
                                        messages = currentMessages,
                                        activeBot = activeBot,
                                        inputText = inputText,
                                        isGenerating = isGenerating,
                                        onInputTextChanged = { viewModel.onInputTextChanged(it) },
                                        onSendMessage = { viewModel.sendMessage(it) },
                                        onApproveAction = { viewModel.approveAction(it) },
                                        onRejectAction = { viewModel.rejectAction(it) },
                                        onEditAndApproveAction = { id, fields -> viewModel.editAndApproveAction(id, fields) },
                                        onOpenVoiceCall = { viewModel.startVoiceCall() }
                                    )
                                }
                                NavTab.APPROVALS -> {
                                    ApprovalsScreen(
                                        approvals = approvals,
                                        onApproveAction = { viewModel.approveAction(it) },
                                        onRejectAction = { viewModel.rejectAction(it) },
                                        onEditAndApproveAction = { id, fields -> viewModel.editAndApproveAction(id, fields) }
                                    )
                                }
                                NavTab.ACTIVITY -> {
                                    ActivityRoutinesScreen(
                                        activities = activities,
                                        routines = routines,
                                        heartbeatStatus = heartbeatStatus,
                                        onRunHeartbeat = { viewModel.runHeartbeatNow() },
                                        onToggleRoutine = { viewModel.toggleRoutine(it) },
                                        onToggleTaskPause = { viewModel.toggleTaskPause(it) }
                                    )
                                }
                                NavTab.MEMORY -> {
                                    MemorySkillsScreen(
                                        memories = memories,
                                        skills = skills,
                                        searchQuery = memoryQuery,
                                        selectedCategory = memoryCategory,
                                        isInterviewActive = isInterviewActive,
                                        interviewStep = interviewStep,
                                        interviewQuestions = viewModel.interviewQuestions,
                                        onSearchChanged = { viewModel.setMemorySearch(it) },
                                        onCategorySelected = { viewModel.setMemoryCategory(it) },
                                        onAddMemory = { fact, cat, tags -> viewModel.addMemory(fact, cat, tags) },
                                        onStartInterview = { viewModel.startInterview() },
                                        onAnswerInterview = { viewModel.answerInterview(it) },
                                        onCancelInterview = { viewModel.cancelInterview() },
                                        onTriggerSkill = { skill ->
                                            viewModel.selectTab(NavTab.CHAT)
                                            viewModel.sendMessage("Run procedure: ${skill.title}")
                                        }
                                    )
                                }
                                NavTab.WORKSPACE -> {
                                    WorkspaceAntigravityScreen(
                                        connections = connections,
                                        dailyScheduleId = viewModel.dailyScheduleId,
                                        contentScheduleId = viewModel.contentScheduleId,
                                        onUpdateSheetIds = { daily, content -> viewModel.updateSheetIds(daily, content) },
                                        onShowSnack = { viewModel.showSnack(it) }
                                    )
                                }
                            }
                        }

                        // Floating Live Voice Call Overlay
                        AnimatedVisibility(
                            visible = isVoiceCallActive,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            VoiceCallOverlay(
                                isSpeaking = isVoiceSpeaking,
                                transcript = voiceTranscript,
                                onVoiceInput = { viewModel.handleVoiceInput(it) },
                                onClose = { viewModel.endVoiceCall() }
                            )
                        }

                        // Confetti Celebration Layer
                        ConfettiView(trigger = confettiTrigger)
                    }
                }
            }
        }
    }
}
