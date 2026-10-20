package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class HariomBotRepository {

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    private fun currentTime(): String = timeFormat.format(Date())

    // Bots State
    private val _bots = MutableStateFlow<List<Bot>>(Bot.ROSTER)
    val bots: StateFlow<List<Bot>> = _bots.asStateFlow()

    // Messages State keyed by channelId
    private val _messages = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())
    val messages: StateFlow<Map<String, List<ChatMessage>>> = _messages.asStateFlow()

    // Approvals State
    private val _approvals = MutableStateFlow<List<ApprovalAction>>(emptyList())
    val approvals: StateFlow<List<ApprovalAction>> = _approvals.asStateFlow()

    // Activity Tasks State (Dots Activity View)
    private val _activities = MutableStateFlow<List<ActivityTask>>(emptyList())
    val activities: StateFlow<List<ActivityTask>> = _activities.asStateFlow()

    // Routines State
    private val _routines = MutableStateFlow<List<Routine>>(emptyList())
    val routines: StateFlow<List<Routine>> = _routines.asStateFlow()

    // Memories State
    private val _memories = MutableStateFlow<List<MemoryItem>>(emptyList())
    val memories: StateFlow<List<MemoryItem>> = _memories.asStateFlow()

    // Skills State
    private val _skills = MutableStateFlow<List<SkillItem>>(emptyList())
    val skills: StateFlow<List<SkillItem>> = _skills.asStateFlow()

    // Workspace Connections State
    private val _workspaceConnections = MutableStateFlow<List<WorkspaceConnection>>(emptyList())
    val workspaceConnections: StateFlow<List<WorkspaceConnection>> = _workspaceConnections.asStateFlow()

    // Sheet IDs
    var dailyScheduleSheetId: String = "1dAi_Schedule_AsiaKolkata_MSU_NocVita"
    var contentScheduleSheetId: String = "1cNt_ContentCalendar_ThuWed_Cycle"

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        val now = currentTime()

        // 1. Initial Approvals
        val initialApprovals = listOf(
            ApprovalAction(
                id = "appr-001",
                requestingBotId = "reactor",
                actionType = ApprovalType.GMAIL_SEND,
                title = "RFQ Follow-Up to Trident Labortek",
                targetSummary = "sales@tridentlabortek.com",
                fields = mapOf(
                    "To" to "sales@tridentlabortek.com",
                    "Subject" to "RFQ Quotation Follow-up: Borosilicate Column & SS316 Tri-clamp Caps - NocVita Prototyping",
                    "Item" to "Borosilicate Glass Column OD 60mm x 600mm x 3.2mm (Qty: 2)",
                    "Material Grade" to "Borosilicate 3.3, Thermal shock resistant, ISO 3585",
                    "Delivery To" to "NocVita Lab, Vadodara, Gujarat 390002",
                    "Quoted Price" to "₹8,450 (includes 15% academic/startup discount)",
                    "Body" to "Dear Sales Team at Trident Labortek,\n\nThank you for the proforma quote for the 60mm OD Borosilicate Column and SS316 multi-port tri-clamp caps. We would like to confirm if delivery to our Vadodara lab can be dispatched by Friday, and if GST invoicing under NocVita can be provided.\n\nRegards,\nHariom Pathak\nFounder, NocVita"
                ),
                status = ApprovalStatus.PENDING,
                createdAt = "Today, 08:35 AM"
            ),
            ApprovalAction(
                id = "appr-002",
                requestingBotId = "atlas",
                actionType = ApprovalType.SHEET_UPDATE,
                title = "Update Daily Schedule (MSU Baroda Lab Slot)",
                targetSummary = "Daily Schedule Sheet (Row 14)",
                fields = mapOf(
                    "Sheet ID" to dailyScheduleSheetId,
                    "Tab" to "Today - Asia/Kolkata",
                    "Time Block" to "15:30 - 17:30 IST",
                    "Activity" to "Microbiology Lab (MSU Baroda) - Stochastic culture optical density checks & sampling",
                    "Priority" to "High"
                ),
                status = ApprovalStatus.PENDING,
                createdAt = "Today, 08:42 AM"
            ),
            ApprovalAction(
                id = "appr-003",
                requestingBotId = "helix",
                actionType = ApprovalType.CALENDAR_EVENT,
                title = "Schedule I-STEM ICP-MS Booking Window",
                targetSummary = "Google Calendar (Asia/Kolkata)",
                fields = mapOf(
                    "Event Title" to "I-STEM Analytical Facility: ICP-MS Water Remediation Sample Testing",
                    "Date & Time" to "Thursday, Oct 23 · 10:00 AM - 12:30 PM IST",
                    "Location" to "Central Analytical Lab / I-STEM Slot Booking",
                    "Notes" to "Proforma invoice ₹4,200 generated. Bring filtered microalgae digest samples."
                ),
                status = ApprovalStatus.PENDING,
                createdAt = "Today, 09:15 AM"
            ),
            ApprovalAction(
                id = "appr-004",
                requestingBotId = "pulse",
                actionType = ApprovalType.DOC_CREATE,
                title = "Create Friday Blog Draft in Google Docs",
                targetSummary = "Google Docs / Content Engine",
                fields = mapOf(
                    "Doc Title" to "The Geometric-Information Limit: Synchronization in Microbial Populations",
                    "Target Folder" to "Google Drive / Content Schedule / 2026-Cycle",
                    "Word Count Target" to "1,400 words",
                    "Includes" to "Literature citations, Boltz-1 binding hypothesis, 3 carousel slide briefs"
                ),
                status = ApprovalStatus.PENDING,
                createdAt = "Today, 09:30 AM"
            )
        )
        _approvals.value = initialApprovals

        // 2. Scorecard Data for NocVita PBR Hardware
        val pbrScorecard = VendorScorecard(
            title = "NocVita Photobioreactor Column & Tri-Clamp Caps",
            component = "Borosilicate 3.3 Tube (OD 60mm, H 600mm) + SS316 Multi-port Caps",
            rows = listOf(
                VendorScorecardRow(
                    vendorName = "Trident Labortek",
                    item = "Borosilicate 3.3 + SS316 Caps",
                    unitPriceINR = "₹8,450",
                    materialSpec = "Schott Duran 3.3 / SS316L CNC",
                    studentDiscount = "15% Applied",
                    leadTime = "4-5 Days",
                    deliveryLocation = "Vadodara (Direct Courier)",
                    isRecommended = true
                ),
                VendorScorecardRow(
                    vendorName = "Vexma Technologies",
                    item = "3D SLA Baffles + Cap Prototypes",
                    unitPriceINR = "₹9,800",
                    materialSpec = "Biocompatible Clear Resin",
                    studentDiscount = "10% Applied",
                    leadTime = "3-4 Days",
                    deliveryLocation = "Gandhinagar / Vadodara",
                    isRecommended = false
                ),
                VendorScorecardRow(
                    vendorName = "Soch3D",
                    item = "Laser-cut Acrylic Enclosures",
                    unitPriceINR = "₹6,200",
                    materialSpec = "8mm Cast Acrylic (UV-pass)",
                    studentDiscount = "None",
                    leadTime = "6 Days",
                    deliveryLocation = "Vadodara Hub",
                    isRecommended = false
                ),
                VendorScorecardRow(
                    vendorName = "Amerging Tech / BioKav",
                    item = "Peristaltic Pump & Silicone Tubing",
                    unitPriceINR = "₹11,500",
                    materialSpec = "Medical grade silicone (Pt-cured)",
                    studentDiscount = "5% Startup",
                    leadTime = "7 Days",
                    deliveryLocation = "Gujarat courier",
                    isRecommended = false
                )
            )
        )

        // 3. Initial Chat Messages
        val commandCenterMsgs = listOf(
            ChatMessage(
                id = "msg-101",
                channelId = "command-center",
                senderType = SenderType.BOT,
                botId = "atlas",
                text = "🌅 **Good morning Hariom!** Completed 08:30 AM workspace heartbeat check (Asia/Kolkata).\n\n• **Gmail Triage:** 4 unread threads processed. Identified proforma quotation from Trident Labortek and an editorial update from *Nonlinear Science*.\n• **Daily Schedule:** MSU Baroda microbiology lab block at 3:30 PM. Weather in Vadodara is 31°C, clear.\n• **Task Handoff:** Delegating the PBR column vendor quotation audit to @Reactor.",
                timestamp = "08:30 AM",
                citationChips = listOf(
                    CitationChip("Daily Schedule Sheet", "Sheet", dailyScheduleSheetId),
                    CitationChip("Gmail: Trident Quote", "Mail", "thread-789"),
                    CitationChip("MSU Baroda Lab Slot", "Calendar", "cal-event-14")
                )
            ),
            ChatMessage(
                id = "msg-102",
                channelId = "command-center",
                senderType = SenderType.SYSTEM,
                text = "",
                timestamp = "08:31 AM",
                handoff = BotHandoff(
                    fromBotId = "atlas",
                    toBotId = "reactor",
                    taskTitle = "Audit Hardware Quotes & Compare Vendors for NocVita PBR Column",
                    contextSummary = "Extracted specifications from Drive CAD (OD 60mm x 600mm, SS316 tri-clamp). Check Vexma, Soch3D, and Trident Labortek."
                )
            ),
            ChatMessage(
                id = "msg-103",
                channelId = "command-center",
                senderType = SenderType.BOT,
                botId = "reactor",
                text = "🔬 **Reactor on duty.** Audited the quotes for NocVita's oscillatory flow photobioreactor column.\n\nHere is the comparative scorecard for our Borosilicate 3.3 column and CNC-machined SS316 multi-port caps. **Trident Labortek** has the best price-to-spec ratio with their 15% academic/startup discount and direct dispatch to Vadodara.",
                timestamp = "08:35 AM",
                scorecard = pbrScorecard
            ),
            ChatMessage(
                id = "msg-104",
                channelId = "command-center",
                senderType = SenderType.BOT,
                botId = "reactor",
                text = "Because this involves an outbound email to vendor sales, our **Read vs. Write Security Boundary** has queued this draft in the **Approvals Gate**. You can review and execute it below or in Tab 2:",
                timestamp = "08:36 AM",
                approvalAction = initialApprovals[0]
            ),
            ChatMessage(
                id = "msg-105",
                channelId = "command-center",
                senderType = SenderType.BOT,
                botId = "helix",
                text = "🧬 **Helix reporting:**\n\n1. **I-STEM Portal Alert:** Sensed new slot availability for ICP-MS at the central analytical facility for Thursday. Created pending calendar booking card.\n2. **GBRC Portal:** MALDI-TOF identification queue for spent coffee grounds bacterial isolates is scheduled.\n3. **Manuscript Status:** *The Geometric-Information Limit: Stochastic Synchronization in Compartmentalized Microbial Populations* — Editorial Manager shows 'Under Review' (Nonlinear Science). No revision requests yet.",
                timestamp = "09:02 AM",
                citationChips = listOf(
                    CitationChip("I-STEM Booking Portal", "Portal", "istem-portal.gov.in"),
                    CitationChip("GBRC Portal", "Portal", "gbrc.res.in"),
                    CitationChip("Editorial Manager: NS-2026", "Doc", "em-track-552")
                )
            ),
            ChatMessage(
                id = "msg-106",
                channelId = "command-center",
                senderType = SenderType.BOT,
                botId = "pulse",
                text = "📈 **Pulse Content Studio:**\n\nContent Schedule Sheet is on track for the **Thursday-to-Wednesday cycle**:\n• **Thursday (Today):** Researching verified statistics & SEO keyword density on bio-inspired wastewater treatment and microalgae bioreactors.\n• **Friday (Tomorrow):** Full Google Doc draft + visual carousel brief.\n• **Saturday:** LinkedIn post on NocVita's oscillatory flow fluidics advantages.",
                timestamp = "09:18 AM",
                citationChips = listOf(
                    CitationChip("Content Schedule Sheet", "Sheet", contentScheduleSheetId)
                )
            )
        )

        // Reactor direct channel
        val reactorMsgs = listOf(
            ChatMessage(
                id = "msg-r1",
                channelId = "reactor",
                senderType = SenderType.BOT,
                botId = "reactor",
                text = "👋 Hello Hariom. I am tracking all hardware components for **NocVita** and the **Blue Biorefinery**.\n\nCurrent priorities:\n1. Borosilicate glass columns (OD 60mm, wall 3.2mm, ISO 3585)\n2. SS316 multi-port tri-clamp endcaps\n3. Helical oscillatory flow fluidic baffles (Vexma 3D SLA)\n\nPending action: Trident Labortek RFQ follow-up is waiting in the Approvals queue.",
                timestamp = "08:32 AM",
                scorecard = pbrScorecard
            )
        )

        // Helix direct channel
        val helixMsgs = listOf(
            ChatMessage(
                id = "msg-h1",
                channelId = "helix",
                senderType = SenderType.BOT,
                botId = "helix",
                text = "🧬 Welcome to the Research & Lab Operations channel.\n\n• **MSU Baroda:** Department of Microbiology research on stochastic synchronization.\n• **Upcoming Deadlines:** ICWS 2026 (Water Sustainability) & XXII INQUA Congress 2027.\n• **Biomolecular modeling:** Boltz-1 (BoltzMol-1 / BoltzProt-1) ready for receptor-ligand docking notes.\n• **Portal Takeover Checklist:** Whenever logging into I-STEM or GBRC with CAPTCHA, I will prompt you for a quick browser takeover.",
                timestamp = "08:40 AM"
            )
        )

        // Pulse direct channel
        val pulseMsgs = listOf(
            ChatMessage(
                id = "msg-p1",
                channelId = "pulse",
                senderType = SenderType.BOT,
                botId = "pulse",
                text = "📊 Content Studio active.\n\nOur weekly cadence:\n• **Thu:** Deep-dive research & outline\n• **Fri:** Full Google Doc draft\n• **Sat:** LinkedIn carousel breakdown\n• **Sun:** High-retention Instagram Reels captions & hooks\n• **Mon/Wed:** Polish, metrics review, next cycle queue.",
                timestamp = "08:45 AM"
            )
        )

        // Forge direct channel
        val forgeMsgs = listOf(
            ChatMessage(
                id = "msg-f1",
                channelId = "forge",
                senderType = SenderType.BOT,
                botId = "forge",
                text = "⚙️ Forge ready.\n\nI generate:\n• Python bioprocess simulations (Reynolds numbers, shear stress equations for oscillatory baffled reactors)\n• LaTeX equations & tables for manuscripts\n• GitHub-ready specs syncing with **Google Antigravity** via Tab 5.",
                timestamp = "08:50 AM"
            )
        )

        // Atlas direct channel
        val atlasMsgs = listOf(
            ChatMessage(
                id = "msg-a1",
                channelId = "atlas",
                senderType = SenderType.BOT,
                botId = "atlas",
                text = "🌐 Chief of Staff Atlas here.\n\nI monitor your Daily Schedule, Google Calendar, Google Tasks, and Gmail. Use `/briefing` for a complete workspace status report or `/interview` to run the 1-question-at-a-time business context capture.",
                timestamp = "08:25 AM"
            )
        )

        _messages.value = mapOf(
            "command-center" to commandCenterMsgs,
            "atlas" to atlasMsgs,
            "reactor" to reactorMsgs,
            "helix" to helixMsgs,
            "pulse" to pulseMsgs,
            "forge" to forgeMsgs
        )

        // 4. Ongoing Responsibilities (Activity Tasks - Dots Activity View)
        _activities.value = listOf(
            ActivityTask(
                id = "act-01",
                title = "NocVita Borosilicate Column Procurement",
                botId = "reactor",
                status = ActivityStatus.RUNNING,
                progress = 0.65f,
                currentStep = "Awaiting human approval to dispatch RFQ to Trident Labortek",
                stepIndex = 3,
                totalSteps = 4,
                logs = listOf(
                    "08:12 AM - Scanned Google Drive for latest PBR CAD dimension tolerances",
                    "08:22 AM - Searched Gmail for historical glassblower quotes in Vadodara",
                    "08:34 AM - Compiled 4-way vendor scorecard (Trident, Vexma, Soch3D, BioKav)",
                    "08:36 AM - Formatted RFQ follow-up email and pushed to Approvals Gate"
                ),
                sourcesCited = listOf(
                    "Drive: NocVita_PBR_v2.cad",
                    "Gmail: RFQ_Trident_Proforma_789.pdf",
                    "Vendor Catalog: Schott Duran 3.3 Specs"
                )
            ),
            ActivityTask(
                id = "act-02",
                title = "MSU Baroda eDNA Manuscript Submission",
                botId = "helix",
                status = ActivityStatus.RUNNING,
                progress = 0.85f,
                currentStep = "Tracking reviewer response on Nonlinear Science portal",
                stepIndex = 4,
                totalSteps = 5,
                logs = listOf(
                    "Oct 18 - Manuscript uploaded to Editorial Manager (ID: NS-2026-88)",
                    "Oct 19 - Status changed to 'Under Review'",
                    "Today - Verified reviewer deadline (Nov 15) and tracked cross-citations"
                ),
                sourcesCited = listOf(
                    "Editorial Manager: NS-2026-88",
                    "Docs: The_Geometric_Information_Limit_v4.docx"
                )
            ),
            ActivityTask(
                id = "act-03",
                title = "Weekly Content Cycle (Blog & LinkedIn)",
                botId = "pulse",
                status = ActivityStatus.RUNNING,
                progress = 0.40f,
                currentStep = "Drafting Friday Google Doc article with verified statistics",
                stepIndex = 2,
                totalSteps = 5,
                logs = listOf(
                    "08:40 AM - Analyzed trending topics in wastewater remediation on Google Scholar",
                    "09:10 AM - Generated 5 high-retention LinkedIn hooks for Saturday post",
                    "09:25 AM - Outlined 1,400-word blog post on oscillatory flow fluidics"
                ),
                sourcesCited = listOf(
                    "Sheet: Content Schedule (Row 42)",
                    "Web: Water Research & Bioreactor kinetics 2026"
                )
            )
        )

        // 5. Standing Routines
        _routines.value = listOf(
            Routine(
                id = "rtn-01",
                title = "Daily 8:30 AM Morning Briefing",
                botId = "atlas",
                scheduleText = "Every day · 08:30 AM IST (Asia/Kolkata)",
                description = "Autonomous read-only sweep of Gmail, Daily Schedule Sheet, Google Calendar, and pending approvals. Posts summary in Command Center.",
                isEnabled = true,
                lastRunText = "Today at 08:30 AM IST",
                status = RoutineStatus.SUCCESS
            ),
            Routine(
                id = "rtn-02",
                title = "Tue/Fri 4:00 PM NocVita Vendor & Quote Tracker",
                botId = "reactor",
                scheduleText = "Tuesdays & Fridays · 04:00 PM IST",
                description = "Scans Gmail for vendor quotes, cross-checks material specifications, updates scorecard, and queues RFQ replies for human review.",
                isEnabled = true,
                lastRunText = "Friday at 04:00 PM IST",
                status = RoutineStatus.IDLE
            ),
            Routine(
                id = "rtn-03",
                title = "Thursday 10:00 AM Weekly Content Kickoff",
                botId = "pulse",
                scheduleText = "Thursdays · 10:00 AM IST",
                description = "Reads Content Schedule Sheet, performs SEO & research sweep, outlines Friday blog draft, and schedules Saturday/Sunday social posts.",
                isEnabled = true,
                lastRunText = "Today at 10:00 AM IST",
                status = RoutineStatus.IDLE
            )
        )

        // 6. Memories (Cross-session knowledge bank)
        _memories.value = listOf(
            MemoryItem(
                id = "mem-01",
                fact = "NocVita develops oscillatory flow photobioreactors (PBR) featuring helical baffles for high-density microalgae cultivation (Chlorella sp.) and wastewater remediation.",
                category = MemoryCategory.NOCVITA,
                confidence = 99,
                source = "NocVita Master Architecture Doc",
                timestamp = "Oct 18, 2026",
                tags = listOf("PBR", "Hardware", "Microalgae", "Oscillatory Flow")
            ),
            MemoryItem(
                id = "mem-02",
                fact = "PBR Column physical specs: Borosilicate glass tube OD 60mm, wall 3.2mm, length 600mm, with SS316 multi-port tri-clamp sanitary endcaps.",
                category = MemoryCategory.NOCVITA,
                confidence = 98,
                source = "Drive: NocVita_PBR_v2.cad",
                timestamp = "Oct 19, 2026",
                tags = listOf("Borosilicate", "SS316", "Dimensions", "Trident")
            ),
            MemoryItem(
                id = "mem-03",
                fact = "Hariom Pathak is a researcher at Department of Microbiology, Faculty of Science, The Maharaja Sayajirao University of Baroda (MSU Baroda), Gujarat.",
                category = MemoryCategory.RESEARCH_LABS,
                confidence = 100,
                source = "User Profile",
                timestamp = "Oct 15, 2026",
                tags = listOf("MSU Baroda", "Microbiology", "Vadodara")
            ),
            MemoryItem(
                id = "mem-04",
                fact = "Research manuscript 'The Geometric-Information Limit: Stochastic Synchronization in Compartmentalized Microbial Populations' is under review at Nonlinear Science.",
                category = MemoryCategory.RESEARCH_LABS,
                confidence = 97,
                source = "Editorial Manager: NS-2026-88",
                timestamp = "Oct 19, 2026",
                tags = listOf("Manuscript", "Nonlinear Science", "Synchronization")
            ),
            MemoryItem(
                id = "mem-05",
                fact = "Upcoming targeted conferences: ICWS 2026 (Water Sustainability) and XXII INQUA Congress 2027 (sedimentary eDNA findings).",
                category = MemoryCategory.RESEARCH_LABS,
                confidence = 95,
                source = "Calendar Deadlines",
                timestamp = "Oct 16, 2026",
                tags = listOf("ICWS 2026", "INQUA 2027", "eDNA")
            ),
            MemoryItem(
                id = "mem-06",
                fact = "Weekly Content Cadence: Thursday (Research/Outline) -> Friday (Google Doc Draft) -> Saturday (LinkedIn Carousel) -> Sunday (Instagram Reels) -> Mon/Wed (Feedback & Cycle Update).",
                category = MemoryCategory.CONTENT,
                confidence = 99,
                source = "Content Schedule Google Sheet",
                timestamp = "Oct 17, 2026",
                tags = listOf("Cadence", "LinkedIn", "Instagram", "Blog")
            ),
            MemoryItem(
                id = "mem-07",
                fact = "Primary user location is Vadodara/Gandhinagar, Gujarat, India (Timezone: Asia/Kolkata). Always format time in IST with 12h/24h precision.",
                category = MemoryCategory.PERSONAL_PREFS,
                confidence = 100,
                source = "System Configuration",
                timestamp = "Oct 15, 2026",
                tags = listOf("Timezone", "Asia/Kolkata", "Gujarat")
            ),
            MemoryItem(
                id = "mem-08",
                fact = "Strict Security Gate: Read tools run autonomously during chat and routines; all write/mutating actions across Gmail, Calendar, Sheets, Docs, and Tasks MUST pause for human approval.",
                category = MemoryCategory.PERSONAL_PREFS,
                confidence = 100,
                source = "Security Policy",
                timestamp = "Oct 15, 2026",
                tags = listOf("Security", "Approvals", "Auto-Review")
            )
        )

        // 7. Skills Library (SKILL.md Reusable procedures)
        _skills.value = listOf(
            SkillItem(
                id = "skl-01",
                slug = "vendor-rfq-comparison",
                title = "Multi-Vendor RFQ & Quote Comparison",
                summary = "Draft technical quotation requests, itemize materials, and build a side-by-side comparison scorecard.",
                triggerRule = "When Hariom asks for parts procurement, glassblowing, laser-cutting, or 3D-printing quotes.",
                requiredTools = listOf("gmail/search", "drive/read_cad", "sheets/vendor_scorecard"),
                instructions = """
                    1. Extract exact CAD tolerances, material grades (e.g. Borosilicate 3.3, SS316, Cast Acrylic), and quantities.
                    2. Search Gmail for historical quotes from Trident Labortek, Vexma, Soch3D, BioKav, Amerging Tech, or IndiaMART.
                    3. Compile a markdown table comparing: Unit Price (INR), Material Spec, Student/Startup Discount, Lead Time, and Vadodara/Gandhinagar Delivery.
                    4. Draft technical RFQ email and hold for human approval before sending.
                """.trimIndent(),
                lastExecuted = "Today, 08:35 AM"
            ),
            SkillItem(
                id = "skl-02",
                slug = "lab-instrument-booking",
                title = "I-STEM & GBRC Instrument Slot Prep",
                summary = "Check analytical equipment availability and prepare proforma invoice verification.",
                triggerRule = "When scheduling ICP-MS, MALDI-TOF, or NGS sequencing runs.",
                requiredTools = listOf("istem_portal/check", "gbrc_portal/check", "calendar/event_draft"),
                instructions = """
                    1. Check target instrument availability (ICP-MS for heavy metal wastewater remediation, MALDI-TOF for microbial isolate ID).
                    2. If login or CAPTCHA appears, trigger Portal Takeover Checklist for Hariom.
                    3. Calculate user charges according to MSU Baroda academic category.
                    4. Create pending calendar block in Approvals Gate. Never finalize booking payment automatically.
                """.trimIndent(),
                lastExecuted = "Today, 09:15 AM"
            ),
            SkillItem(
                id = "skl-03",
                slug = "weekly-content-pipeline",
                title = "Weekly Content Cycle (Blog, LinkedIn, Instagram)",
                summary = "Execute the Thursday-to-Wednesday content research, writing, and scheduling loop.",
                triggerRule = "Triggered by Thursday 10:00 AM routine or on-demand /content command.",
                requiredTools = listOf("sheets/read_content_calendar", "docs/create_draft", "web/search_scholar"),
                instructions = """
                    1. Read Content Schedule Google Sheet for the current week's anchor theme.
                    2. Thursday: Perform SEO & citation research on Google Scholar/arXiv.
                    3. Friday: Write complete 1,200-1,500 word blog draft with subheadings and graphics brief in Google Docs.
                    4. Saturday: Draft 1 high-retention LinkedIn post (Hook, Problem, Technical Solution, Carousel outline, CTA).
                    5. Sunday: Draft Instagram Reels captions with timestamps and hashtag sets.
                    6. Submit all drafts to Approvals queue.
                """.trimIndent(),
                lastExecuted = "Today, 09:30 AM"
            ),
            SkillItem(
                id = "skl-04",
                slug = "skill-creator",
                title = "Teach a Task / Skill Creator (/learn)",
                summary = "Turn a demonstrated or discussed workflow into a permanent, reusable saved skill.",
                triggerRule = "When Hariom types /learn or asks to save a recurring workflow as a skill.",
                requiredTools = listOf("memory/extract_rules", "skills/save_skill"),
                instructions = """
                    1. Extract the trigger condition, input parameters, decision boundaries, and required tools from conversation history.
                    2. Draft a structured SKILL.md specification with title, summary, step-by-step execution procedure, and safety gates.
                    3. Present the drafted skill card in chat and save into the Firestore skills collection upon approval.
                """.trimIndent(),
                lastExecuted = "Yesterday, 06:14 PM"
            ),
            SkillItem(
                id = "skl-05",
                slug = "find-a-document",
                title = "Search & Read Workspace Files",
                summary = "Search connected Google Drive folders and Sheets before answering.",
                triggerRule = "Whenever context requires NocVita CAD, MSU manuscript text, or schedule rows.",
                requiredTools = listOf("drive/search_files", "sheets/read_rows"),
                instructions = """
                    1. Search Google Drive roots: NocVita, Research, Content Schedule, Schedule.
                    2. Read the full file content rather than hallucinating from titles.
                    3. Cite the exact file name and link chip in the message output.
                """.trimIndent(),
                lastExecuted = "Today, 08:30 AM"
            )
        )

        // 8. Workspace Connections
        _workspaceConnections.value = listOf(
            WorkspaceConnection(
                id = "ws-gmail",
                type = WorkspaceServiceType.GMAIL,
                isConnected = true,
                statusText = "Connected · Active Sync",
                lastSyncText = "Synced 2 mins ago",
                details = "Scanned inbox: 4 unread threads triaged. Outbound emails strictly gated by Approvals."
            ),
            WorkspaceConnection(
                id = "ws-calendar",
                type = WorkspaceServiceType.CALENDAR,
                isConnected = true,
                statusText = "Connected · Asia/Kolkata",
                lastSyncText = "Synced 5 mins ago",
                details = "Upcoming: 3:30 PM MSU Baroda Microbiology Lab Slot. Events require human approval to create/edit."
            ),
            WorkspaceConnection(
                id = "ws-sheets",
                type = WorkspaceServiceType.SHEETS,
                isConnected = true,
                statusText = "Connected · 2 Sheets Linked",
                lastSyncText = "Synced 10 mins ago",
                resourceId = "$dailyScheduleSheetId | $contentScheduleSheetId",
                details = "Daily Schedule Sheet (linked) & Content Schedule Sheet (linked). Row edits held for review."
            ),
            WorkspaceConnection(
                id = "ws-drive",
                type = WorkspaceServiceType.DRIVE,
                isConnected = true,
                statusText = "Connected · Roots: NocVita, Research",
                lastSyncText = "Synced 15 mins ago",
                details = "Indexed 38 CAD/spec files, 12 manuscript drafts, and vendor quotation PDFs."
            ),
            WorkspaceConnection(
                id = "ws-tasks",
                type = WorkspaceServiceType.TASKS,
                isConnected = true,
                statusText = "Connected · 5 Active Tasks",
                lastSyncText = "Synced 8 mins ago",
                details = "Synced with Google Tasks list 'Hariom Pathak Workspace'."
            ),
            WorkspaceConnection(
                id = "ws-keep",
                type = WorkspaceServiceType.KEEP,
                isConnected = true,
                statusText = "Connected · Boltz Notes & Ideas",
                lastSyncText = "Synced 20 mins ago",
                details = "Synchronizing lab notes and biomolecular modeling parameters."
            )
        )
    }

    // Message Actions
    fun sendMessage(channelId: String, text: String, botId: String? = null) {
        val userMsg = ChatMessage(
            id = "msg-${System.currentTimeMillis()}",
            channelId = channelId,
            senderType = SenderType.USER,
            text = text,
            timestamp = currentTime()
        )
        val currentList = _messages.value[channelId] ?: emptyList()
        val updatedMap = _messages.value.toMutableMap()
        updatedMap[channelId] = currentList + userMsg
        _messages.value = updatedMap
    }

    fun addBotResponse(
        channelId: String,
        botId: String,
        text: String,
        approvalAction: ApprovalAction? = null,
        handoff: BotHandoff? = null,
        scorecard: VendorScorecard? = null,
        citationChips: List<CitationChip> = emptyList()
    ) {
        val botMsg = ChatMessage(
            id = "msg-${System.currentTimeMillis()}",
            channelId = channelId,
            senderType = SenderType.BOT,
            botId = botId,
            text = text,
            timestamp = currentTime(),
            approvalAction = approvalAction,
            handoff = handoff,
            scorecard = scorecard,
            citationChips = citationChips
        )
        val currentList = _messages.value[channelId] ?: emptyList()
        val updatedMap = _messages.value.toMutableMap()
        updatedMap[channelId] = currentList + botMsg
        _messages.value = updatedMap

        // If an approval is included, add it to approvals collection too
        if (approvalAction != null) {
            val curApps = _approvals.value
            if (curApps.none { it.id == approvalAction.id }) {
                _approvals.value = listOf(approvalAction) + curApps
            }
        }
    }

    // Approval Actions
    fun approveAction(actionId: String) {
        val now = currentTime()
        _approvals.value = _approvals.value.map { action ->
            if (action.id == actionId) {
                action.copy(
                    status = ApprovalStatus.APPROVED,
                    executedAt = "Executed at $now IST",
                    executionResult = "Successfully performed via Google Workspace API."
                )
            } else action
        }
        // Update inline cards in messages
        updateMessageApproval(actionId, ApprovalStatus.APPROVED)
    }

    fun rejectAction(actionId: String) {
        _approvals.value = _approvals.value.map { action ->
            if (action.id == actionId) {
                action.copy(
                    status = ApprovalStatus.REJECTED,
                    executionResult = "Action discarded by user."
                )
            } else action
        }
        updateMessageApproval(actionId, ApprovalStatus.REJECTED)
    }

    fun editAndApproveAction(actionId: String, updatedFields: Map<String, String>) {
        val now = currentTime()
        _approvals.value = _approvals.value.map { action ->
            if (action.id == actionId) {
                action.copy(
                    fields = updatedFields,
                    status = ApprovalStatus.EDITED,
                    executedAt = "Edited & executed at $now IST",
                    executionResult = "Executed with updated user parameters."
                )
            } else action
        }
        updateMessageApproval(actionId, ApprovalStatus.EDITED)
    }

    private fun updateMessageApproval(actionId: String, status: ApprovalStatus) {
        val updatedMap = _messages.value.mapValues { (_, msgList) ->
            msgList.map { msg ->
                if (msg.approvalAction?.id == actionId) {
                    msg.copy(
                        approvalAction = msg.approvalAction.copy(
                            status = status,
                            executedAt = "Executed at ${currentTime()} IST"
                        )
                    )
                } else msg
            }
        }
        _messages.value = updatedMap
    }

    // Routines & Heartbeat Actions
    fun toggleRoutine(routineId: String) {
        _routines.value = _routines.value.map { rtn ->
            if (rtn.id == routineId) rtn.copy(isEnabled = !rtn.isEnabled) else rtn
        }
    }

    fun runHeartbeatNow(): String {
        val now = currentTime()
        _routines.value = _routines.value.map {
            it.copy(
                lastRunText = "Today at $now IST",
                status = RoutineStatus.SUCCESS
            )
        }

        // Add heartbeat notification in Command Center
        val heartbeatMsg = ChatMessage(
            id = "msg-${System.currentTimeMillis()}",
            channelId = "command-center",
            senderType = SenderType.BOT,
            botId = "atlas",
            text = "💓 **Proactive Heartbeat Executed ($now IST)**\n\n• **Gmail Sweep:** Scanned unread messages. No critical blockers.\n• **Daily Schedule Sheet:** Row 14 verified for 3:30 PM MSU Baroda lab session.\n• **Content Sheet:** Week 42 topic verified.\n• **Approvals:** ${_approvals.value.count { it.status == ApprovalStatus.PENDING }} pending human action items remain in queue.",
            timestamp = now,
            citationChips = listOf(
                CitationChip("Daily Schedule Sheet", "Sheet", dailyScheduleSheetId),
                CitationChip("Google Calendar", "Calendar", "cal-sync"),
                CitationChip("Content Calendar", "Sheet", contentScheduleSheetId)
            )
        )
        val curList = _messages.value["command-center"] ?: emptyList()
        val updated = _messages.value.toMutableMap()
        updated["command-center"] = curList + heartbeatMsg
        _messages.value = updated

        return "Heartbeat completed: Gmail, Calendar, and Sheets scanned autonomously."
    }

    // Activity Controls
    fun toggleTaskPause(taskId: String) {
        _activities.value = _activities.value.map { task ->
            if (task.id == taskId) {
                val newStatus = if (task.status == ActivityStatus.RUNNING) ActivityStatus.PAUSED else ActivityStatus.RUNNING
                task.copy(status = newStatus)
            } else task
        }
    }

    // Memory Actions
    fun addMemory(fact: String, category: MemoryCategory, tags: List<String>) {
        val newMem = MemoryItem(
            id = "mem-${System.currentTimeMillis()}",
            fact = fact,
            category = category,
            confidence = 96,
            source = "Direct User Addition",
            timestamp = dateFormat.format(Date()),
            tags = tags
        )
        _memories.value = listOf(newMem) + _memories.value
    }

    // Skill Actions
    fun addSkill(title: String, slug: String, summary: String, triggerRule: String, tools: List<String>, instructions: String) {
        val newSkill = SkillItem(
            id = "skl-${System.currentTimeMillis()}",
            slug = slug,
            title = title,
            summary = summary,
            triggerRule = triggerRule,
            requiredTools = tools,
            instructions = instructions,
            lastExecuted = "Just saved"
        )
        _skills.value = listOf(newSkill) + _skills.value
    }

    // Update Bot status dynamically
    fun updateBotStatus(botId: String, status: BotStatus, currentAction: String, activeTool: String? = null) {
        _bots.value = _bots.value.map { bot ->
            if (bot.id == botId) {
                bot.copy(status = status, currentAction = currentAction, activeTool = activeTool)
            } else bot
        }
    }
}
