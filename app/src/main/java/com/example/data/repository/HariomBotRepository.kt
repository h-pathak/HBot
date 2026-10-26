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

    // Activity Tasks State
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

    companion object {
        const val DAILY_SCHEDULE_SHEET_ID = "1hqA9AyxxWv6B9HlPPfLumetfhSzduUZc22dPD0AzxYk"
        const val CONTENT_SCHEDULE_SHEET_ID = "1XpHiWo6J_RVBsNN5ZgnWKUr9FDgX_cTNhIDYKZvHMEw"
    }

    // Sheet IDs specified in user prompt
    var dailyScheduleSheetId: String = DAILY_SCHEDULE_SHEET_ID
    var contentScheduleSheetId: String = CONTENT_SCHEDULE_SHEET_ID

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        val now = currentTime()

        // 1. Initial Approvals (Matching Prompt 3)
        val initialApprovals = listOf(
            ApprovalAction(
                id = "appr-ssip-01",
                requestingBotId = "reactor",
                actionType = ApprovalType.GMAIL_DRAFT,
                title = "RFQ: 3D SLA Baffles & Borosilicate Fittings (SSIP 2.0 Allocation)",
                targetSummary = "quotes@vexma.com, sales@soch3d.com",
                diffPreview = "+ Requested student-startup 15% discount for NocVi prototype\n+ Month-1 SSIP 2.0 allocation budget ceiling: ₹60,000",
                budgetTag = "SSIP 2.0 Budget: ₹2,48,000 Total (Ref: 532)",
                fields = mapOf(
                    "To" to "quotes@vexma.com, sales@soch3d.com",
                    "Subject" to "RFQ: 3D SLA Oscillatory Baffles & Borosilicate Adapters - NocVita (MSU Baroda SSIP 2.0)",
                    "Grant Reference" to "SSIP 2.0/2026-27/532 · STBI Savli Incubation",
                    "Target Budget" to "₹60,000 Month-1 prototyping tranche",
                    "Material Spec" to "Biocompatible Clear Resin (autoclavable) & Borosilicate 3.3 sleeves",
                    "Delivery To" to "NocVita Lab, Vadodara, Gujarat 390002",
                    "Body" to "Dear Vexma Technologies & Soch3D Sales Teams,\n\nWe are prototyping the NocVi bioluminescent oscillatory flow photobioreactor under our approved SSIP 2.0 grant at MSU Baroda (Ref: SSIP 2.0/2026-27/532). Please provide itemized student-startup pricing, GST invoice capability, and lead time for 12 units of our 3D-printed helical baffle inserts.\n\nWarm regards,\nHariom Pathak\nFounder, NocVita"
                ),
                status = ApprovalStatus.PENDING,
                createdAt = "Today, 08:35 AM"
            ),
            ApprovalAction(
                id = "appr-lab-02",
                requestingBotId = "helix",
                actionType = ApprovalType.CALENDAR_SLOT,
                title = "Reserve GBRC & I-STEM Analytical Slots (Riverine AMR Water Testing)",
                targetSummary = "Google Calendar (Asia/Kolkata) & GBRC Booking",
                diffPreview = "+ Add 10:30 AM - 1:00 PM Thursday ICP-MS slot\n+ 14 MALDI-TOF isolates preparation checklist attached",
                budgetTag = "MSU Microbiology Research Fund",
                fields = mapOf(
                    "Facility" to "GBRC Gandhinagar & I-STEM Central Facility",
                    "Analyses" to "ICP-MS (Heavy metal ions in Mahi/Vishwamitri rivers) & MALDI-TOF (MAR = 0.29 isolates)",
                    "Date & Time" to "Thursday · 10:30 AM - 1:00 PM IST",
                    "Proforma Fee" to "₹4,200 (Academic University Category)",
                    "Sample Note" to "14 riverine bacterial isolates prepared in triplicate."
                ),
                status = ApprovalStatus.PENDING,
                createdAt = "Today, 08:42 AM"
            ),
            ApprovalAction(
                id = "appr-content-03",
                requestingBotId = "pulse",
                actionType = ApprovalType.SHEET_ROW_UPDATE,
                title = "Log Week 43 Content: 'Bioluminescent Microalgae for Decentralized Wastewater'",
                targetSummary = "Content Schedule Sheet (Row 43)",
                diffPreview = "+ Blog outline & stats logged to Google Sheet\n+ LinkedIn Carousel draft ready for Saturday review",
                budgetTag = "Content Studio",
                fields = mapOf(
                    "Sheet ID" to contentScheduleSheetId,
                    "Thursday Stage" to "SEO Keyword sweep & verified literature citations complete",
                    "Friday Stage" to "1,400-word Google Doc draft scheduled",
                    "Saturday Stage" to "5-slide LinkedIn Carousel on NocVi bioreactor kinetics",
                    "Sunday Stage" to "2 Instagram Reels hooks on student biotech innovation in Gujarat"
                ),
                status = ApprovalStatus.PENDING,
                createdAt = "Today, 09:15 AM"
            )
        )
        _approvals.value = initialApprovals

        // 2. Scorecard Data for NocVita PBR Hardware
        val pbrScorecard = VendorScorecard(
            title = "NocVi Bioluminescent Photobioreactor Column & Baffles",
            component = "Borosilicate 3.3 Tube (OD 60mm) + 3D SLA Helical Baffles + SS316 Tri-clamps",
            budgetAllocation = "SSIP 2.0 Month-1 Tranche: ₹60,000",
            rows = listOf(
                VendorScorecardRow(
                    vendorName = "Trident Labortek",
                    item = "Borosilicate 3.3 + SS316 Tri-clamp Caps",
                    unitPriceINR = "₹8,450",
                    materialSpec = "Schott Duran 3.3 / SS316 CNC",
                    studentDiscount = "15% Applied (SSIP)",
                    leadTime = "4-5 Days",
                    deliveryLocation = "Vadodara (Direct courier)",
                    isRecommended = true
                ),
                VendorScorecardRow(
                    vendorName = "Vexma Technologies",
                    item = "3D SLA Baffles in Biocompatible Resin",
                    unitPriceINR = "₹9,800",
                    materialSpec = "Clear Autoclavable Photopolymer",
                    studentDiscount = "10% Startup",
                    leadTime = "3 Days",
                    deliveryLocation = "Vadodara Hub",
                    isRecommended = true
                ),
                VendorScorecardRow(
                    vendorName = "Soch3D",
                    item = "Precision Acrylic Outer Enclosure",
                    unitPriceINR = "₹6,200",
                    materialSpec = "8mm Cast Acrylic (UV-pass)",
                    studentDiscount = "None",
                    leadTime = "5 Days",
                    deliveryLocation = "Gandhinagar Dispatch",
                    isRecommended = false
                ),
                VendorScorecardRow(
                    vendorName = "Bills Biotech / BioKav",
                    item = "Spent Coffee Extraction Tube & Pump",
                    unitPriceINR = "₹11,500",
                    materialSpec = "Pt-cured medical silicone",
                    studentDiscount = "Startup Partner",
                    leadTime = "6 Days",
                    deliveryLocation = "Savli / Vadodara",
                    isRecommended = false
                )
            )
        )

        // 3. Bento Briefing Protocol block
        val bentoData = BentoBriefing(
            greeting = "Good morning, Hariom 🌿",
            dateText = "Monday · Asia/Kolkata (Vadodara)",
            schedulePriority = BentoPriorityItem(
                title = "MSU Microbiology Lab Slot",
                subtitle = "3:30 PM - 5:30 PM · Optical density & stochastic culture sampling",
                iconEmoji = "🔬",
                badge = "High Priority"
            ),
            emailPriority = BentoPriorityItem(
                title = "3 Unread Vendor Quotes",
                subtitle = "Trident Labortek & Vexma proforma estimates for SSIP 2.0 prototype",
                iconEmoji = "📬",
                badge = "Needs Review"
            ),
            grantLabPriority = BentoPriorityItem(
                title = "SSIP 2.0 Grant (Ref: 532)",
                subtitle = "₹2,48,000 allocated · STBI Savli incubation & Sandoz validation",
                iconEmoji = "🌱",
                badge = "Approved"
            ),
            contentPriority = BentoPriorityItem(
                title = "Week 43 Content Pipeline",
                subtitle = "Bioluminescent microalgae thesis outline ready for Google Docs",
                iconEmoji = "✍️",
                badge = "Thursday Kickoff"
            )
        )

        // 4. Portal Takeover Checklist for I-STEM / GBRC
        val portalChecklist = PortalTakeoverCard(
            id = "ptc-istem-01",
            portalName = "I-STEM Central Analytical Portal",
            purpose = "Reserve ICP-MS heavy metal run (Mahi & Vishwamitri river isolates)",
            loginUrl = "https://istem-portal.gov.in/login",
            credentialHint = "Institutional Account: MSU Baroda Department of Microbiology",
            checklist = listOf(
                PortalChecklistItem("step-1", "Navigate to I-STEM login page & solve CAPTCHA image", isCompleted = false),
                PortalChecklistItem("step-2", "Select Analytical Category: ICP-MS (High Resolution)", "ICP-MS HR-2026", isCompleted = true),
                PortalChecklistItem("step-3", "Paste Project Ref: SSIP 2.0/2026-27/532", "SSIP 2.0/2026-27/532", isCompleted = true),
                PortalChecklistItem("step-4", "Generate proforma invoice & download payment slip (₹4,200)", isCompleted = false)
            )
        )

        // 5. Initial Chat Messages (Living conversation)
        val commandCenterMsgs = listOf(
            ChatMessage(
                id = "msg-100",
                channelId = "command-center",
                senderType = SenderType.BOT,
                botId = "atlas",
                text = "🌿 **Good morning, Hariom!** Atlas here with your morning sync.\n\nAll 5 specialist companions are active in the Command Center. I've compiled your morning priorities across **NocVita**, your **SSIP 2.0 Grant**, and your **MSU Baroda research** below:",
                timestamp = "08:30 AM",
                bentoBriefing = bentoData,
                stepsUsed = listOf(
                    "Read Daily Schedule Sheet (Row 14)",
                    "Scanned Gmail inbox for SSIP 2.0 & vendor threads",
                    "Verified Google Calendar Asia/Kolkata appointments"
                ),
                citationChips = listOf(
                    CitationChip("Daily Schedule Sheet", "Sheet", dailyScheduleSheetId),
                    CitationChip("SSIP 2.0 Grant Doc", "Doc", "Ref: 532"),
                    CitationChip("MSU Baroda Lab Slot", "Calendar", "cal-event-14")
                )
            ),
            ChatMessage(
                id = "msg-101",
                channelId = "command-center",
                senderType = SenderType.SYSTEM,
                text = "",
                timestamp = "08:31 AM",
                handoff = BotHandoff(
                    fromBotId = "atlas",
                    toBotId = "reactor",
                    taskTitle = "Audit Hardware Quotes & Compare Vendors for NocVi PBR Prototype",
                    contextSummary = "Extracted specifications from Drive CAD (OD 60mm borosilicate, SS316 tri-clamp, 3D SLA baffles). Check Vexma, Soch3D, and Trident Labortek under SSIP 2.0 budget.",
                    reasoning = "Reactor specializes in bioprocess hardware fabrication and vendor negotiations."
                )
            ),
            ChatMessage(
                id = "msg-102",
                channelId = "command-center",
                senderType = SenderType.BOT,
                botId = "reactor",
                text = "🫧 **Reactor on duty!** I audited our component quotes for the NocVi photobioreactor column. **Trident Labortek** and **Vexma** give us the optimal student-startup rate under our ₹60,000 Month-1 SSIP budget:",
                timestamp = "08:35 AM",
                scorecard = pbrScorecard,
                stepsUsed = listOf(
                    "Parsed Vexma 3D SLA biocompatible resin quote",
                    "Extracted Trident Labortek ISO 3585 borosilicate specs",
                    "Calculated student-startup discount savings (₹2,100)"
                )
            ),
            ChatMessage(
                id = "msg-103",
                channelId = "command-center",
                senderType = SenderType.BOT,
                botId = "reactor",
                text = "Per our **Read vs. Write Security Boundary**, I have held the outbound RFQ email in the **High-Fives (Approvals)** gate. You can review and execute it with one tap:",
                timestamp = "08:36 AM",
                approvalAction = initialApprovals[0]
            ),
            ChatMessage(
                id = "msg-104",
                channelId = "command-center",
                senderType = SenderType.BOT,
                botId = "helix",
                text = "🧬 **Helix reporting:**\n\n• **Riverine Surveillance:** Our 14 bacterial isolates from Mahi, Vishwamitri & Bhogavo rivers showed a mean MAR index of 0.29. Sensed an ICP-MS opening on the I-STEM portal for Thursday morning.\n• **Portal Takeover Card:** Because I-STEM requires human login & CAPTCHA verification, I have set up this interactive takeover checklist for you:",
                timestamp = "09:02 AM",
                portalTakeover = portalChecklist,
                stepsUsed = listOf(
                    "Checked I-STEM facility slot calendar",
                    "Formatted sample prep protocol for ICP-MS",
                    "Loaded MSU Baroda institutional billing profile"
                ),
                citationChips = listOf(
                    CitationChip("I-STEM Portal", "Portal", "istem-portal.gov.in"),
                    CitationChip("Mahi/Vishwamitri MAR Data", "Doc", "AMR-Surveillance-v2")
                )
            )
        )

        _messages.value = mapOf(
            "command-center" to commandCenterMsgs,
            "atlas" to listOf(
                ChatMessage(
                    id = "msg-a1",
                    channelId = "atlas",
                    senderType = SenderType.BOT,
                    botId = "atlas",
                    text = "🌟 **Atlas Chief of Staff:** Your daily coordinator across NocVita, MSU Baroda, and SSIP 2.0. Type `/brief` for a fresh overview or `/interview` to capture current thoughts.",
                    timestamp = "08:25 AM"
                )
            ),
            "reactor" to listOf(
                ChatMessage(
                    id = "msg-r1",
                    channelId = "reactor",
                    senderType = SenderType.BOT,
                    botId = "reactor",
                    text = "🫧 **Reactor:** Hardware & procurement station active. Sourcing borosilicate 3.3 columns, SS316 multi-port caps, and SLA baffles under the ₹2,48,000 SSIP 2.0 grant.",
                    timestamp = "08:32 AM",
                    scorecard = pbrScorecard
                )
            ),
            "helix" to listOf(
                ChatMessage(
                    id = "msg-h1",
                    channelId = "helix",
                    senderType = SenderType.BOT,
                    botId = "helix",
                    text = "🧬 **Helix:** Research desk active. Tracking riverine AMR isolates (mean MAR = 0.29), 'The Geometric-Information Limit' manuscript, and I-STEM / GBRC instrument bookings.",
                    timestamp = "08:40 AM"
                )
            ),
            "pulse" to listOf(
                ChatMessage(
                    id = "msg-p1",
                    channelId = "pulse",
                    senderType = SenderType.BOT,
                    botId = "pulse",
                    text = "🎧 **Pulse:** Content studio on track. Operating the Thursday-to-Wednesday cycle on the Content Schedule Google Sheet.",
                    timestamp = "08:45 AM"
                )
            ),
            "forge" to listOf(
                ChatMessage(
                    id = "msg-f1",
                    channelId = "forge",
                    senderType = SenderType.BOT,
                    botId = "forge",
                    text = "🤖 **Forge:** Antigravity coding bridge ready. Simulating oscillatory flow Reynolds numbers (Re_o > 300) and preparing GitHub sync files.",
                    timestamp = "08:50 AM"
                )
            )
        )

        // 6. Activities (Dots Activity View)
        _activities.value = listOf(
            ActivityTask(
                id = "act-01",
                title = "NocVi PBR Baffle Prototyping (SSIP 2.0)",
                botId = "reactor",
                status = ActivityStatus.RUNNING,
                progress = 0.70f,
                currentStep = "Waiting for human approval to dispatch RFQ to Vexma & Soch3D",
                stepIndex = 3,
                totalSteps = 4,
                logs = listOf(
                    "08:12 AM - Checked CAD dimension tolerances for helical baffle inserts",
                    "08:25 AM - Scanned Vadodara 3D SLA suppliers for student/startup discounts",
                    "08:35 AM - Compiled multi-vendor scorecard under SSIP 2.0 allocation",
                    "08:36 AM - Formatted RFQ email and pushed to High-Fives gate"
                ),
                sourcesCited = listOf(
                    "Drive: NocVi_PBR_Baffles_v3.cad",
                    "SSIP Grant Allocation Sheet (Row 6)",
                    "Vexma Prototyping Catalog 2026"
                )
            ),
            ActivityTask(
                id = "act-02",
                title = "Riverine AMR & Heavy Metal Profiling",
                botId = "helix",
                status = ActivityStatus.RUNNING,
                progress = 0.85f,
                currentStep = "I-STEM ICP-MS booking window ready for Thursday",
                stepIndex = 4,
                totalSteps = 5,
                logs = listOf(
                    "Oct 20 - Analyzed 14 MALDI-TOF isolates from Mahi, Vishwamitri & Bhogavo rivers",
                    "Oct 22 - Calculated MAR index (mean = 0.29 across heavy-metal sites)",
                    "Today - Prepared I-STEM analytical portal takeover checklist"
                ),
                sourcesCited = listOf(
                    "GBRC MALDI-TOF Spectra: GBRC-2026-M42",
                    "I-STEM Portal Facility Tracker",
                    "Manuscript: AMR Surveillance in Gujarat Rivers"
                )
            ),
            ActivityTask(
                id = "act-03",
                title = "Thursday Content Cycle (Bioluminescent Algae)",
                botId = "pulse",
                status = ActivityStatus.RUNNING,
                progress = 0.45f,
                currentStep = "Drafting Friday Google Doc article with verified statistics",
                stepIndex = 2,
                totalSteps = 5,
                logs = listOf(
                    "08:40 AM - Queried Google Scholar for recent microalgae carbon sequestration data",
                    "09:10 AM - Generated 5 high-retention LinkedIn hooks for Saturday post",
                    "09:25 AM - Drafted row update for Content Schedule Google Sheet"
                ),
                sourcesCited = listOf(
                    "Content Schedule Sheet (Row 43)",
                    "Web: Water Research & Bioreactor kinetics 2026"
                )
            )
        )

        // 7. Standing Routines (Matching Prompt 4.3)
        _routines.value = listOf(
            Routine(
                id = "rtn-01",
                title = "Daily 8:30 AM Morning Briefing",
                botId = "atlas",
                scheduleText = "Every day · 08:30 AM IST (Asia/Kolkata)",
                description = "Autonomous read-only sweep across Gmail, Daily Schedule Sheet, Google Calendar, and pending approvals. Displays Bento summary in Command Center.",
                isEnabled = true,
                lastRunText = "Today at 08:30 AM IST",
                status = RoutineStatus.SUCCESS
            ),
            Routine(
                id = "rtn-02",
                title = "Tue/Fri 4:00 PM NocVita RFQ & SSIP Tracker",
                botId = "reactor",
                scheduleText = "Tuesdays & Fridays · 04:00 PM IST",
                description = "Scans Gmail for supplier responses, checks SSIP 2.0 prototyping bill compliance, and updates the vendor comparison scorecard.",
                isEnabled = true,
                lastRunText = "Friday at 04:00 PM IST",
                status = RoutineStatus.IDLE
            ),
            Routine(
                id = "rtn-03",
                title = "Wed 2:00 PM Lab & Manuscript Pulse",
                botId = "helix",
                scheduleText = "Wednesdays · 02:00 PM IST",
                description = "Checks Editorial Manager journal reviews (Nonlinear Science) and GBRC / I-STEM analytical slot availabilities.",
                isEnabled = true,
                lastRunText = "Wednesday at 02:00 PM IST",
                status = RoutineStatus.IDLE
            ),
            Routine(
                id = "rtn-04",
                title = "Thu 10:00 AM Content Studio Kickoff",
                botId = "pulse",
                scheduleText = "Thursdays · 10:00 AM IST",
                description = "Reads Content Schedule Sheet, initiates SEO sweep, prepares Friday Google Doc draft, and generates LinkedIn carousel outline.",
                isEnabled = true,
                lastRunText = "Thursday at 10:00 AM IST",
                status = RoutineStatus.IDLE
            )
        )

        // 8. Memories (Matching Prompt 3.1)
        _memories.value = listOf(
            MemoryItem(
                id = "mem-ssip-01",
                fact = "Approved ₹2,48,000 SSIP 2.0 Grant (Ref: SSIP 2.0/2026-27/532) at MSU Baroda for 'Bioluminescent Photobioreactor' (NocVi) with co-innovator Arnav Dhamdhere and mentor Dr. Devarshi Gajjar. Incubated at STBI Savli with problem validation from Sandoz Agri-India.",
                category = MemoryCategory.NOCVITA,
                confidence = 100,
                source = "SSIP 2.0 Grant Sanction Letter",
                timestamp = "Oct 20, 2026",
                tags = listOf("SSIP 2.0", "NocVi", "STBI Savli", "Sandoz", "₹2,48,000")
            ),
            MemoryItem(
                id = "mem-blue-02",
                fact = "Blue Biorefinery Project: Developing mannooligosaccharides (MOS) extraction from spent coffee grounds in collaboration with Bills Biotech.",
                category = MemoryCategory.NOCVITA,
                confidence = 98,
                source = "Bills Biotech Partnership Brief",
                timestamp = "Oct 18, 2026",
                tags = listOf("Blue Biorefinery", "Coffee Grounds", "MOS", "Bills Biotech")
            ),
            MemoryItem(
                id = "mem-amr-03",
                fact = "Research & Manuscripts: Co-authoring 'The Geometric-Information Limit' (stochastic synchronization in compartmentalized microbial populations), riverine AMR & heavy-metal surveillance across Mahi, Vishwamitri & Bhogavo rivers (14 MALDI-TOF isolates, mean MAR = 0.29), and sedimentary eDNA for XXII INQUA Congress 2027.",
                category = MemoryCategory.RESEARCH_LABS,
                confidence = 99,
                source = "MSU Microbiology Research Dossier",
                timestamp = "Oct 22, 2026",
                tags = listOf("MSU Baroda", "MAR Index", "Mahi River", "INQUA 2027", "eDNA")
            ),
            MemoryItem(
                id = "mem-pbr-04",
                fact = "PBR column engineering specs: Borosilicate glass tube OD 60mm, wall 3.2mm, length 600mm, SS316 multi-port tri-clamp sanitary endcaps, and 3D SLA biocompatible fluidic baffles.",
                category = MemoryCategory.NOCVITA,
                confidence = 98,
                source = "Drive: NocVi_PBR_v3.cad",
                timestamp = "Oct 19, 2026",
                tags = listOf("Borosilicate 3.3", "SS316", "3D SLA", "Dimensions")
            ),
            MemoryItem(
                id = "mem-sheets-05",
                fact = "Google Workspace Connected Sheets: Daily Schedule Sheet (ID: 1hqA9AyxxWv6B9HlPPfLumetfhSzduUZc22dPD0AzxYk) and Content Schedule Sheet (ID: 1XpHiWo6J_RVBsNN5ZgnWKUr9FDgX_cTNhIDYKZvHMEw).",
                category = MemoryCategory.PERSONAL_PREFS,
                confidence = 100,
                source = "System Environment",
                timestamp = "Oct 24, 2026",
                tags = listOf("Google Sheets", "Daily Schedule", "Content Schedule")
            ),
            MemoryItem(
                id = "mem-security-06",
                fact = "Strict Read vs. Write Security Gate: Autonomous execution for all read operations; human approval mandatory before any outbound email, calendar modification, or sheet edit.",
                category = MemoryCategory.PERSONAL_PREFS,
                confidence = 100,
                source = "Security Governance",
                timestamp = "Oct 15, 2026",
                tags = listOf("Security", "Approvals Gate", "Zero-Trust")
            )
        )

        // 9. Skills Library (Matching Prompt 4.4)
        _skills.value = listOf(
            SkillItem(
                id = "skl-01",
                slug = "vendor-rfq-comparison",
                title = "Multi-Vendor RFQ & Quote Comparison",
                summary = "Draft technical quotation requests, itemize materials, and build a side-by-side comparison scorecard.",
                triggerRule = "When Hariom asks for parts procurement, glassblowing, laser-cutting, or 3D-printing quotes.",
                requiredTools = listOf("gmail/search", "drive/read_cad", "sheets/vendor_scorecard"),
                instructions = """
                    1. Extract exact CAD tolerances, material grades (Borosilicate 3.3, SS316, 3D SLA resin), and quantities.
                    2. Search Gmail for historical quotes from Trident Labortek, Vexma, Soch3D, BioKav, or IndiaMART.
                    3. Compile a comparison table with: Unit Price (INR), Material Spec, Student/Startup Discount, Lead Time, and Vadodara/Gandhinagar Delivery.
                    4. Draft technical RFQ email and hold for human approval before sending.
                """.trimIndent(),
                lastExecuted = "Today, 08:35 AM"
            ),
            SkillItem(
                id = "skl-02",
                slug = "ssip-bill-compliance",
                title = "SSIP 2.0 Bill & Voucher Compliance",
                summary = "Audit vendor invoices for MSU Baroda SSIP 2.0 grant accounting and STBI Savli reporting.",
                triggerRule = "When reviewing proforma invoices, GST receipts, or allocation ceilings.",
                requiredTools = listOf("gmail/extract_invoice", "sheets/ssip_ledger"),
                instructions = """
                    1. Check GSTIN validity for supplier.
                    2. Ensure quotation item matches approved SSIP 2.0 prototyping tranche.
                    3. Attach student-startup declaration and route for approval.
                """.trimIndent(),
                lastExecuted = "Yesterday, 04:15 PM"
            ),
            SkillItem(
                id = "skl-03",
                slug = "istem-gbrc-slot-prep",
                title = "I-STEM & GBRC Instrument Slot Prep",
                summary = "Check analytical equipment availability and prepare proforma invoice verification.",
                triggerRule = "When scheduling ICP-MS, MALDI-TOF, or NGS sequencing runs.",
                requiredTools = listOf("istem_portal/check", "gbrc_portal/check", "calendar/event_draft"),
                instructions = """
                    1. Check target instrument availability (ICP-MS for heavy metals, MALDI-TOF for microbial ID).
                    2. If login or CAPTCHA appears, trigger Portal Takeover Checklist for Hariom.
                    3. Calculate user charges according to MSU Baroda academic category.
                    4. Create pending calendar block in Approvals Gate. Never finalize booking payment automatically.
                """.trimIndent(),
                lastExecuted = "Today, 09:02 AM"
            ),
            SkillItem(
                id = "skl-04",
                slug = "weekly-content-cycle",
                title = "Weekly Content Cycle (Blog, LinkedIn, Instagram)",
                summary = "Execute the Thursday-to-Wednesday content research, writing, and scheduling loop.",
                triggerRule = "Triggered by Thursday 10:00 AM routine or on-demand /content command.",
                requiredTools = listOf("sheets/read_content_calendar", "docs/create_draft", "web/search_scholar"),
                instructions = """
                    1. Read Content Schedule Google Sheet for the current week's anchor theme.
                    2. Thursday: Perform SEO & citation research on Google Scholar.
                    3. Friday: Write complete 1,400-word blog draft with subheadings in Google Docs.
                    4. Saturday: Draft 1 high-retention LinkedIn post + 5 carousel slides.
                    5. Sunday: Draft Instagram Reels captions with hooks and hashtag sets.
                    6. Submit all drafts to Approvals queue.
                """.trimIndent(),
                lastExecuted = "Today, 09:15 AM"
            ),
            SkillItem(
                id = "skl-05",
                slug = "boltz-alphafold-prep",
                title = "Boltz API & AlphaFold Biomolecular Modeling",
                summary = "Format input FASTA sequences and ligand SMILES for structural biology runs.",
                triggerRule = "When running protein-ligand docking or conformational simulations.",
                requiredTools = listOf("keep/read_notes", "boltz_api/prepare_input"),
                instructions = """
                    1. Parse protein sequence and target ligand structure.
                    2. Prepare BoltzMol-1 and BoltzProt-1 configuration files.
                    3. Log parameters into Google Keep for MSU Baroda thesis documentation.
                """.trimIndent(),
                lastExecuted = "Oct 21, 2026"
            )
        )

        // 10. Workspace Connections
        _workspaceConnections.value = listOf(
            WorkspaceConnection(
                id = "ws-gmail",
                type = WorkspaceServiceType.GMAIL,
                isConnected = true,
                statusText = "Connected · Active Sync",
                lastSyncText = "Synced 2 mins ago",
                details = "Scanned inbox: 3 vendor quotes triaged (Vexma, Soch3D, Trident). Outbound drafts held for approval."
            ),
            WorkspaceConnection(
                id = "ws-calendar",
                type = WorkspaceServiceType.CALENDAR,
                isConnected = true,
                statusText = "Connected · Asia/Kolkata",
                lastSyncText = "Synced 4 mins ago",
                details = "Upcoming: 3:30 PM MSU Baroda Microbiology Lab Slot. Events require approval."
            ),
            WorkspaceConnection(
                id = "ws-sheets",
                type = WorkspaceServiceType.SHEETS,
                isConnected = true,
                statusText = "Connected · 2 Sheets Linked",
                lastSyncText = "Synced 8 mins ago",
                resourceId = "$dailyScheduleSheetId | $contentScheduleSheetId",
                details = "Daily Schedule (1hqA9A...) & Content Schedule (1XpHiW...) synced."
            ),
            WorkspaceConnection(
                id = "ws-drive",
                type = WorkspaceServiceType.DRIVE,
                isConnected = true,
                statusText = "Connected · Roots: NocVita, Research, SSIP",
                lastSyncText = "Synced 12 mins ago",
                details = "Indexed 42 CAD files (NocVi PBR), 14 manuscript drafts, and SSIP 2.0 grant vouchers."
            ),
            WorkspaceConnection(
                id = "ws-tasks",
                type = WorkspaceServiceType.TASKS,
                isConnected = true,
                statusText = "Connected · 4 Active Tasks",
                lastSyncText = "Synced 6 mins ago",
                details = "Synced with Google Tasks list 'Hariom Pathak Workspace'."
            ),
            WorkspaceConnection(
                id = "ws-keep",
                type = WorkspaceServiceType.KEEP,
                isConnected = true,
                statusText = "Connected · Boltz Notes & Ideas",
                lastSyncText = "Synced 15 mins ago",
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
        bentoBriefing: BentoBriefing? = null,
        portalTakeover: PortalTakeoverCard? = null,
        skillLearned: SkillLearnedCard? = null,
        citationChips: List<CitationChip> = emptyList(),
        stepsUsed: List<String> = emptyList()
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
            bentoBriefing = bentoBriefing,
            portalTakeover = portalTakeover,
            skillLearned = skillLearned,
            citationChips = citationChips,
            stepsUsed = stepsUsed
        )
        val currentList = _messages.value[channelId] ?: emptyList()
        val updatedMap = _messages.value.toMutableMap()
        updatedMap[channelId] = currentList + botMsg
        _messages.value = updatedMap

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
        updateMessageApproval(actionId, ApprovalStatus.APPROVED)
    }

    fun rejectAction(actionId: String) {
        _approvals.value = _approvals.value.map { action ->
            if (action.id == actionId) {
                action.copy(
                    status = ApprovalStatus.REJECTED,
                    executionResult = "Action skipped by user."
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
            text = "🌿 **Proactive Workspace Sweep Complete ($now IST)**\n\n• **Gmail:** 3 supplier quotes synced under SSIP 2.0 allocation.\n• **Daily Schedule Sheet:** 3:30 PM MSU Baroda microbiology session confirmed.\n• **Content Schedule Sheet:** Week 43 blog draft verified.\n• **High-Fives Gate:** ${_approvals.value.count { it.status == ApprovalStatus.PENDING }} action cards pending your authorization.",
            timestamp = now,
            stepsUsed = listOf(
                "Scanned Gmail inbox for new RFQ follow-ups",
                "Queried Daily Schedule Sheet row 14",
                "Checked Content Schedule Sheet row 43"
            ),
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

        return "Proactive Workspace Sweep complete: All sheets, inbox, and calendar scanned."
    }

    fun toggleTaskPause(taskId: String) {
        _activities.value = _activities.value.map { task ->
            if (task.id == taskId) {
                val newStatus = if (task.status == ActivityStatus.RUNNING) ActivityStatus.PAUSED else ActivityStatus.RUNNING
                task.copy(status = newStatus)
            } else task
        }
    }

    fun addMemory(fact: String, category: MemoryCategory, tags: List<String>) {
        val newMem = MemoryItem(
            id = "mem-${System.currentTimeMillis()}",
            fact = fact,
            category = category,
            confidence = 98,
            source = "Direct User Addition",
            timestamp = dateFormat.format(Date()),
            tags = tags
        )
        _memories.value = listOf(newMem) + _memories.value
    }

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

    fun updateBotStatus(botId: String, status: BotStatus, currentAction: String, activeTool: String? = null) {
        _bots.value = _bots.value.map { bot ->
            if (bot.id == botId) {
                bot.copy(status = status, currentAction = currentAction, activeTool = activeTool)
            } else bot
        }
    }
}
