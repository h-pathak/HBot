package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiService
import com.example.data.model.*
import com.example.data.repository.HariomBotRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class NavTab(val title: String, val iconEmoji: String) {
    CHAT("Home", "🏠"),
    APPROVALS("High-Fives", "✨"),
    ACTIVITY("Activity", "🌱"),
    MEMORY("Vault", "🧠"),
    WORKSPACE("Sync", "🔗")
}

class HariomBotViewModel(
    private val repository: HariomBotRepository = HariomBotRepository(),
    private val geminiService: GeminiService = GeminiService()
) : ViewModel() {

    // Theme Mode: Cozy Daylight (false) vs Bioluminescent Night (true) - Default to Cozy Daylight per prompt!
    private val _isNightMode = MutableStateFlow(false)
    val isNightMode: StateFlow<Boolean> = _isNightMode.asStateFlow()

    // Layout Mode: Mobile Phone Shell (false) vs Studio Split View (true)
    private val _isStudioMode = MutableStateFlow(false)
    val isStudioMode: StateFlow<Boolean> = _isStudioMode.asStateFlow()

    // Confetti Celebration Trigger
    private val _confettiTrigger = MutableStateFlow(0L)
    val confettiTrigger: StateFlow<Long> = _confettiTrigger.asStateFlow()

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
            question = "Which hardware milestone for the NocVi photobioreactor is highest priority under the ₹60,000 Month-1 SSIP 2.0 allocation?",
            domain = "NocVita & SSIP 2.0",
            rationale = "Helps @Reactor prioritize Vexma 3D SLA inserts vs. borosilicate column procurement.",
            options = listOf(
                "Borosilicate 3.3 glass column (OD 60mm x 600mm)",
                "3D-printed SLA helical fluidic baffles (Vexma)",
                "CNC machined SS316 multi-port sanitary endcaps",
                "Peristaltic pump & flow sensor calibration"
            )
        ),
        InterviewQuestion(
            step = 1,
            question = "Which analytical instrument at MSU Baroda / I-STEM is highest priority for this week's riverine AMR sampling?",
            domain = "Research & Labs",
            rationale = "Helps @Helix prepare the I-STEM / GBRC portal takeover checklist & proforma invoice.",
            options = listOf(
                "ICP-MS (Heavy metal ions in Mahi & Vishwamitri rivers)",
                "MALDI-TOF (Identify 14 isolates with mean MAR = 0.29)",
                "Sanger / NGS Sequencing (Sedimentary eDNA for INQUA 2027)",
                "UV-Vis Spectrophotometer (Growth kinetics & OD680)"
            )
        ),
        InterviewQuestion(
            step = 2,
            question = "What angle should this Saturday's LinkedIn thought-leadership carousel focus on?",
            domain = "Content Engine",
            rationale = "Guides @Pulse on whether to write a technical fluidics breakdown or founder journey.",
            options = listOf(
                "Bioluminescent microalgae for decentralized urban wastewater",
                "Oscillatory flow mixing kinetics vs bubble columns",
                "How we won the ₹2,48,000 SSIP 2.0 grant at MSU Baroda",
                "Blue Biorefinery: Circular valorization of spent coffee grounds"
            )
        )
    )

    // Heartbeat Status
    private val _heartbeatStatus = MutableStateFlow<String?>(null)
    val heartbeatStatus: StateFlow<String?> = _heartbeatStatus.asStateFlow()

    // Notification banner
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

    val dailyScheduleId: String get() = repository.dailyScheduleSheetId
    val contentScheduleId: String get() = repository.contentScheduleSheetId

    fun toggleTheme() {
        _isNightMode.value = !_isNightMode.value
        showSnack(if (_isNightMode.value) "🌙 Bioluminescent Night theme" else "☀️ Cozy Matcha & Oatmilk theme")
    }

    fun toggleStudioMode() {
        _isStudioMode.value = !_isStudioMode.value
        showSnack(if (_isStudioMode.value) "🖥️ Studio Split View enabled" else "📱 Mobile Phone Mode enabled")
    }

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
            repository.updateBotStatus(targetBotId, BotStatus.THINKING, "Analyzing workspace query...", "gemini_2.5_flash")

            delay(350)

            val history = repository.messages.value[channel] ?: emptyList()
            val result = geminiService.generateAgentReply(
                botId = targetBotId,
                userPrompt = query,
                conversationHistory = history,
                dailyScheduleId = repository.dailyScheduleSheetId,
                contentScheduleId = repository.contentScheduleSheetId
            )

            repository.updateBotStatus(targetBotId, BotStatus.WORKING, "Generated response & synced", null)

            repository.addBotResponse(
                channelId = channel,
                botId = targetBotId,
                text = result.replyText,
                approvalAction = result.approvalAction,
                handoff = result.handoff,
                scorecard = result.scorecard,
                bentoBriefing = result.bentoBriefing,
                portalTakeover = result.portalTakeover,
                skillLearned = result.skillLearned,
                citationChips = result.citationChips,
                stepsUsed = result.stepsUsed
            )

            _isGenerating.value = false
            delay(1200)
            repository.updateBotStatus(targetBotId, BotStatus.IDLE, "Idle · Synced", null)
        }
    }

    // Voice Call Controls
    fun startVoiceCall() {
        _isVoiceCallActive.value = true
        _voiceTranscript.value = "Listening... Speak naturally with Atlas & your companions."
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
                bentoBriefing = reply.bentoBriefing,
                citationChips = reply.citationChips,
                stepsUsed = reply.stepsUsed
            )

            _isVoiceSpeaking.value = true
            _voiceTranscript.value = "${targetBot.replaceFirstChar { it.uppercase() }}: ${reply.replyText.take(120)}..."
        }
    }

    // Approval Gate Actions (with Confetti!)
    fun approveAction(actionId: String) {
        repository.approveAction(actionId)
        _confettiTrigger.value = System.currentTimeMillis()
        showSnack("✨ High-Five! Action approved & executed with confetti celebration!")
    }

    fun rejectAction(actionId: String) {
        repository.rejectAction(actionId)
        showSnack("Action skipped from review queue")
    }

    fun editAndApproveAction(actionId: String, updatedFields: Map<String, String>) {
        repository.editAndApproveAction(actionId, updatedFields)
        _confettiTrigger.value = System.currentTimeMillis()
        showSnack("✏️ Action edited & executed successfully!")
    }

    // Routines & Heartbeat Actions
    fun toggleRoutine(routineId: String) {
        repository.toggleRoutine(routineId)
    }

    fun runHeartbeatNow() {
        viewModelScope.launch {
            _heartbeatStatus.value = "Running read-only sweep across Gmail, Calendar & Sheets..."
            delay(700)
            val summary = repository.runHeartbeatNow()
            _heartbeatStatus.value = summary
            showSnack("🌿 Proactive workspace sweep complete! Check Command Center.")
            delay(3000)
            _heartbeatStatus.value = null
        }
    }

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
        showSnack("✨ New memory learned & indexed in vault")
    }

    // Interview Mode
    fun startInterview() {
        _isInterviewActive.value = true
        _interviewStep.value = 0
    }

    fun answerInterview(answer: String) {
        val currentQ = interviewQuestions.getOrNull(_interviewStep.value)
        if (currentQ != null) {
            val tag = when (currentQ.domain) {
                "NocVita & SSIP 2.0" -> MemoryCategory.NOCVITA
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

    fun saveSkill(title: String, slug: String, summary: String, trigger: String, tools: List<String>, instructions: String) {
        repository.addSkill(title, slug, summary, trigger, tools, instructions)
        showSnack("✨ Workflow skill '$title' registered in Skills Vault")
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
