package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiService
import com.example.data.model.*
import com.example.data.repository.HariomBotRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class NavTab(val title: String, val icon: String) {
    CHAT("Command", "chat"),
    APPROVALS("Approvals", "shield"),
    ACTIVITY("Routines", "pulse"),
    MEMORY("Vault", "brain"),
    WORKSPACE("Workspace", "cloud")
}

class HariomBotViewModel(
    private val repository: HariomBotRepository = HariomBotRepository(),
    private val geminiService: GeminiService = GeminiService()
) : ViewModel() {

    // Selected Navigation Tab
    private val _selectedTab = MutableStateFlow(NavTab.CHAT)
    val selectedTab: StateFlow<NavTab> = _selectedTab.asStateFlow()

    // Active Channel (command-center or specific bot id)
    private val _activeChannelId = MutableStateFlow("command-center")
    val activeChannelId: StateFlow<String> = _activeChannelId.asStateFlow()

    // Input message text in composer
    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    // Bot Typing / Thinking State
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    // Voice Call Overlay State
    private val _isVoiceCallActive = MutableStateFlow(false)
    val isVoiceCallActive: StateFlow<Boolean> = _isVoiceCallActive.asStateFlow()

    private val _voiceTranscript = MutableStateFlow("")
    val voiceTranscript: StateFlow<String> = _voiceTranscript.asStateFlow()

    private val _isVoiceSpeaking = MutableStateFlow(false)
    val isVoiceSpeaking: StateFlow<Boolean> = _isVoiceSpeaking.asStateFlow()

    // Memory Search & Filter
    private val _memorySearchQuery = MutableStateFlow("")
    val memorySearchQuery: StateFlow<String> = _memorySearchQuery.asStateFlow()

    private val _selectedMemoryCategory = MutableStateFlow(MemoryCategory.ALL)
    val selectedMemoryCategory: StateFlow<MemoryCategory> = _selectedMemoryCategory.asStateFlow()

    // Interview Mode State
    private val _isInterviewActive = MutableStateFlow(false)
    val isInterviewActive: StateFlow<Boolean> = _isInterviewActive.asStateFlow()

    private val _interviewStep = MutableStateFlow(0)
    val interviewStep: StateFlow<Int> = _interviewStep.asStateFlow()

    val interviewQuestions = listOf(
        InterviewQuestion(
            step = 0,
            question = "What is the primary procurement bottleneck currently slowing down NocVita's oscillatory flow photobioreactor prototype?",
            domain = "NocVita & Biorefinery",
            rationale = "Helps @Reactor prioritize glassblowing vs. 3D-printing vendor outreach.",
            options = listOf(
                "Borosilicate glass column tolerances (OD 60mm)",
                "SS316 tri-clamp endcap machining lead time",
                "Biocompatible 3D-printed fluidic baffle autoclaving",
                "Peristaltic pump flow rate calibration"
            )
        ),
        InterviewQuestion(
            step = 1,
            question = "Which analytical instrument at MSU Baroda / I-STEM is highest priority for this week's sampling?",
            domain = "Research & Labs",
            rationale = "Helps @Helix lock in I-STEM / GBRC slot booking proforma invoices.",
            options = listOf(
                "ICP-MS (Heavy metal remediation quantification)",
                "MALDI-TOF (Microbial isolate identification)",
                "UV-Vis Spectrophotometer (Optical density & growth curve)",
                "Sanger Sequencing (eDNA verification)"
            )
        ),
        InterviewQuestion(
            step = 2,
            question = "What is our primary goal for this Saturday's LinkedIn thought-leadership post?",
            domain = "Content Engine",
            rationale = "Guides @Pulse on whether to write a technical carousel or biotech founder narrative.",
            options = listOf(
                "Technical carousel breakdown of oscillatory flow mixing",
                "Blue Biorefinery circular economy case study",
                "MSU Baroda stochastic microbial synchronization research teaser",
                "Vendor sourcing lessons for student biotech founders in Gujarat"
            )
        )
    )

    // Heartbeat Status
    private val _heartbeatStatus = MutableStateFlow<String?>(null)
    val heartbeatStatus: StateFlow<String?> = _heartbeatStatus.asStateFlow()

    // User notification banner
    private val _snackMessage = MutableStateFlow<String?>(null)
    val snackMessage: StateFlow<String?> = _snackMessage.asStateFlow()

    // Repository flows
    val bots = repository.bots
    val messages = repository.messages
    val approvals = repository.approvals
    val activities = repository.activities
    val routines = repository.routines
    val memories = repository.memories
    val skills = repository.skills
    val workspaceConnections = repository.workspaceConnections

    // Daily Schedule & Content Sheet IDs
    val dailyScheduleId: String get() = repository.dailyScheduleSheetId
    val contentScheduleId: String get() = repository.contentScheduleSheetId

    fun selectTab(tab: NavTab) {
        _selectedTab.value = tab
    }

    fun selectChannel(channelId: String) {
        _activeChannelId.value = channelId
    }

    fun onInputTextChanged(text: String) {
        _inputText.value = text
    }

    fun sendMessage(text: String? = null) {
        val query = text ?: _inputText.value
        if (query.isBlank()) return

        val channel = _activeChannelId.value
        val targetBotId = if (channel == "command-center") "atlas" else channel

        repository.sendMessage(channel, query)
        _inputText.value = ""
        _isGenerating.value = true

        viewModelScope.launch {
            // Update bot status to thinking
            repository.updateBotStatus(targetBotId, BotStatus.THINKING, "Analyzing workspace query...", "gemini_2.5_flash")

            // Small natural pacing delay
            delay(400)

            val history = repository.messages.value[channel] ?: emptyList()
            val result = geminiService.generateAgentReply(
                botId = targetBotId,
                userPrompt = query,
                conversationHistory = history,
                dailyScheduleId = repository.dailyScheduleSheetId,
                contentScheduleId = repository.contentScheduleSheetId
            )

            // Update bot status to working/done
            repository.updateBotStatus(targetBotId, BotStatus.WORKING, "Generated response & workspace sync", null)

            repository.addBotResponse(
                channelId = channel,
                botId = targetBotId,
                text = result.replyText,
                approvalAction = result.approvalAction,
                handoff = result.handoff,
                scorecard = result.scorecard,
                citationChips = result.citationChips
            )

            _isGenerating.value = false
            delay(1200)
            repository.updateBotStatus(targetBotId, BotStatus.IDLE, "Idle · Synced", null)
        }
    }

    // Voice Call Controls
    fun startVoiceCall() {
        _isVoiceCallActive.value = true
        _voiceTranscript.value = "Listening... Speak naturally to Atlas & teammates."
        _isVoiceSpeaking.value = false
    }

    fun endVoiceCall() {
        _isVoiceCallActive.value = false
        _voiceTranscript.value = ""
        _isVoiceSpeaking.value = false
    }

    fun handleVoiceInput(spokenText: String) {
        _voiceTranscript.value = spokenText
        _isVoiceSpeaking.value = false

        viewModelScope.launch {
            delay(300)
            val channel = _activeChannelId.value
            val targetBot = if (channel == "command-center") "atlas" else channel

            repository.sendMessage(channel, spokenText)

            val reply = geminiService.generateAgentReply(
                botId = targetBot,
                userPrompt = spokenText,
                conversationHistory = repository.messages.value[channel] ?: emptyList(),
                dailyScheduleId = repository.dailyScheduleSheetId,
                contentScheduleId = repository.contentScheduleSheetId
            )

            repository.addBotResponse(
                channelId = channel,
                botId = targetBot,
                text = reply.replyText,
                approvalAction = reply.approvalAction,
                citationChips = reply.citationChips
            )

            _isVoiceSpeaking.value = true
            _voiceTranscript.value = "${targetBot.replaceFirstChar { it.uppercase() }}: ${reply.replyText.take(120)}..."
        }
    }

    // Approval Gate Actions
    fun approveAction(actionId: String) {
        repository.approveAction(actionId)
        showSnack("✅ Action approved and executed via Google Workspace API")
    }

    fun rejectAction(actionId: String) {
        repository.rejectAction(actionId)
        showSnack("Action discarded from queue")
    }

    fun editAndApproveAction(actionId: String, updatedFields: Map<String, String>) {
        repository.editAndApproveAction(actionId, updatedFields)
        showSnack("✅ Action edited & executed with updated parameters")
    }

    // Routine Actions
    fun toggleRoutine(routineId: String) {
        repository.toggleRoutine(routineId)
    }

    fun runHeartbeatNow() {
        viewModelScope.launch {
            _heartbeatStatus.value = "Running read-only sweep across Gmail, Calendar & Sheets..."
            delay(700)
            val summary = repository.runHeartbeatNow()
            _heartbeatStatus.value = summary
            showSnack("Proactive Heartbeat completed! Check Command Center.")
            delay(3000)
            _heartbeatStatus.value = null
        }
    }

    // Activity Controls
    fun toggleTaskPause(taskId: String) {
        repository.toggleTaskPause(taskId)
    }

    // Memory Vault Actions
    fun setMemorySearch(query: String) {
        _memorySearchQuery.value = query
    }

    fun setMemoryCategory(category: MemoryCategory) {
        _selectedMemoryCategory.value = category
    }

    fun addMemory(fact: String, category: MemoryCategory, tags: List<String>) {
        repository.addMemory(fact, category, tags)
        showSnack("New memory learned & indexed in vault")
    }

    // Interview Mode
    fun startInterview() {
        _isInterviewActive.value = true
        _interviewStep.value = 0
    }

    fun answerInterview(answer: String) {
        val currentQ = interviewQuestions.getOrNull(_interviewStep.value)
        if (currentQ != null) {
            // Save learned memory
            val tag = when (currentQ.domain) {
                "NocVita & Biorefinery" -> MemoryCategory.NOCVITA
                "Research & Labs" -> MemoryCategory.RESEARCH_LABS
                else -> MemoryCategory.CONTENT
            }
            repository.addMemory(
                fact = "${currentQ.question} -> Learned answer: $answer",
                category = tag,
                tags = listOf("Interview", "Learned", currentQ.domain)
            )
        }

        if (_interviewStep.value < interviewQuestions.size - 1) {
            _interviewStep.value += 1
        } else {
            _isInterviewActive.value = false
            showSnack("🎉 Interview complete! All preferences saved into Memory Vault.")
        }
    }

    fun cancelInterview() {
        _isInterviewActive.value = false
    }

    // Save Skill
    fun saveSkill(title: String, slug: String, summary: String, trigger: String, tools: List<String>, instructions: String) {
        repository.addSkill(title, slug, summary, trigger, tools, instructions)
        showSnack("Workflow skill '$title' registered in Skills Vault")
    }

    fun updateSheetIds(dailyId: String, contentId: String) {
        repository.dailyScheduleSheetId = dailyId
        repository.contentScheduleSheetId = contentId
        showSnack("Google Sheet IDs updated for autonomous sync")
    }

    fun showSnack(msg: String) {
        _snackMessage.value = msg
        viewModelScope.launch {
            delay(2500)
            if (_snackMessage.value == msg) {
                _snackMessage.value = null
            }
        }
    }

    fun clearSnack() {
        _snackMessage.value = null
    }
}
