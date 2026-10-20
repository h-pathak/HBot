package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.ApprovalStatus
import com.example.ui.components.TopRosterBar
import com.example.ui.components.VoiceCallOverlay
import com.example.ui.screens.*
import com.example.ui.theme.*

@Composable
fun HariomBotApp(
    viewModel: HariomBotViewModel = viewModel()
) {
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

    // BackHandler: Handle sub-screen state backstack navigation
    BackHandler(enabled = isVoiceCallActive || selectedTab != NavTab.CHAT || activeChannelId != "command-center") {
        when {
            isVoiceCallActive -> viewModel.endVoiceCall()
            selectedTab != NavTab.CHAT -> viewModel.selectTab(NavTab.CHAT)
            activeChannelId != "command-center" -> viewModel.selectChannel("command-center")
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Slate900,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopRosterBar(
                bots = bots,
                selectedChannelId = activeChannelId,
                onSelectChannel = { channelId ->
                    viewModel.selectChannel(channelId)
                    viewModel.selectTab(NavTab.CHAT)
                },
                onOpenVoiceCall = { viewModel.startVoiceCall() },
                modifier = Modifier.statusBarsPadding()
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Slate950,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 0.5.dp, color = Slate800)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar")
            ) {
                NavTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    val icon: ImageVector = when (tab) {
                        NavTab.CHAT -> Icons.Default.Chat
                        NavTab.APPROVALS -> Icons.Default.Security
                        NavTab.ACTIVITY -> Icons.Default.Schedule
                        NavTab.MEMORY -> Icons.Default.Psychology
                        NavTab.WORKSPACE -> Icons.Default.Workspaces
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (tab == NavTab.APPROVALS && pendingApprovalsCount > 0) {
                                        Badge(containerColor = Amber500, contentColor = Slate950) {
                                            Text(
                                                text = pendingApprovalsCount.toString(),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = tab.title,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Slate950,
                            selectedTextColor = Cyan400,
                            indicatorColor = Cyan400,
                            unselectedIconColor = Slate400,
                            unselectedTextColor = Slate400
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Content depending on selected tab
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
                            viewModel.sendMessage("Run procedure: ${skill.title} (/learn)")
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
        }
    }
}
