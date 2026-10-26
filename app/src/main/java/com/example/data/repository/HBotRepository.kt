package com.example.data.repository

import com.example.data.model.*
import com.example.data.vault.PrivateVaultProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class HBotRepository(
    initialVaultProfile: PrivateVaultProfile = PrivateVaultProfile()
) {

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    private fun currentTime(): String = timeFormat.format(Date())

    // Dynamic User Context from Private Vault
    private val _vaultProfile = MutableStateFlow(initialVaultProfile)
    val vaultProfile: StateFlow<PrivateVaultProfile> = _vaultProfile.asStateFlow()

    // Sheet IDs dynamically read from Private Vault (default to empty strings)
    var dailyScheduleSheetId: String = initialVaultProfile.dailyScheduleSheetId
    var contentScheduleSheetId: String = initialVaultProfile.contentScheduleSheetId

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

    init {
        seedInitialData()
    }

    fun syncVaultProfile(profile: PrivateVaultProfile) {
        _vaultProfile.value = profile
        dailyScheduleSheetId = profile.dailyScheduleSheetId
        contentScheduleSheetId = profile.contentScheduleSheetId
    }

    private fun seedInitialData() {
        val userName = _vaultProfile.value.displayName.ifBlank { "Founder" }
        val orgLab = _vaultProfile.value.organizationOrLab.ifBlank { "R&D Lab" }

        // 1. Initial Approvals (Sanitized & Generic)
        val initialApprovals = listOf(
            ApprovalAction(
                id = "appr-fab-01",
                requestingBotId = "reactor",
                actionType = ApprovalType.GMAIL_DRAFT,
                title = "RFQ: Prototype Helical Baffles & Borosilicate Fittings",
                targetSummary = "quotes@supplier-a.example.com, sales@supplier-b.example.com",
                diffPreview = "+ Requested student/startup 15% discount for prototype batch\n+ Phase-1 prototyping tranche ceiling: ₹60,000",
                budgetTag = "R&D Prototyping Allocation: ₹60,000",
                fields = mapOf(
                    "To" to "quotes@supplier-a.example.com, sales@supplier-b.example.com",
                    "Subject" to "RFQ: 3D SLA Helical Baffles & Borosilicate Adapters (Prototype v2)",
                    "Project Reference" to "R&D Prototyping Grant · Phase 1",
                    "Target Budget" to "₹60,000 Phase-1 prototyping tranche",
                    "Material Spec" to "Biocompatible Clear Resin (autoclavable) & Borosilicate sleeves",
                    "Delivery To" to "[Configure Lab/Office Address in Private Vault]",
                    "Body" to "Dear Supplier Sales Teams,\n\nWe are prototyping a custom fluidic column. Please provide itemized pricing, GST invoice capability, and lead time for 12 units of our 3D-printed helical baffle inserts.\n\nWarm regards,\n$userName\n$orgLab"
                ),
                status = ApprovalStatus.PENDING,
                createdAt = "Today, 08:35 AM"
            ),
            ApprovalAction(
                id = "appr-lab-02",
                requestingBotId = "helix",
                actionType = ApprovalType.CALENDAR_SLOT,
                title = "Reserve Analytical Instrumentation Slots (Environmental Water Testing)",
                targetSummary = "Calendar Slot & Portal Reservation",
                diffPreview = "+ Add 10:30 AM - 1:00 PM Thursday ICP-MS slot\n+ 14 environmental isolates preparation checklist attached",
                budgetTag = "Research Instrumentation Fund",
                fields = mapOf(
                    "Facility" to "Central Analytical Instrumentation Facility",
                    "Analyses" to "ICP-MS (Heavy metal ion trace analysis) & MALDI-TOF (Isolate identification)",
                    "Date & Time" to "Thursday · 10:30 AM - 1:00 PM",
                    "Proforma Fee" to "₹4,200 (Academic / R&D Category)",
                    "Sample Note" to "14 riverine bacterial isolates prepared in triplicate."
                ),
                status = ApprovalStatus.PENDING,
                createdAt = "Today, 08:42 AM"
            ),
            ApprovalAction(
                id = "appr-content-03",
                requestingBotId = "pulse",
                actionType = ApprovalType.SHEET_ROW_UPDATE,
                title = "Log Weekly Content: 'Bioprocess Engineering for Decentralized Water Remediation'",
                targetSummary = "Content Schedule Sheet (Row 43)",
                diffPreview = "+ Blog outline & stats logged to Google Sheet\n+ Technical summary draft ready for review",
                budgetTag = "Content Studio",
                fields = mapOf(
                    "Sheet ID" to contentScheduleSheetId.ifBlank { "[Configured in Private Vault]" },
                    "Research Stage" to "SEO Keyword sweep & verified literature citations complete",
                    "Writing Stage" to "1,400-word Google Doc draft scheduled",
                    "Publishing Stage" to "Multi-channel technical brief on bioprocess kinetics"
                ),
                status = ApprovalStatus.PENDING,
                createdAt = "Today, 09:15 AM"
            )
        )
        _approvals.value = initialApprovals

        // 2. Scorecard Data for Hardware Prototyping
        val pbrScorecard = VendorScorecard(
            title = "Fluidic Column & Helical Baffles Fabrication",
            component = "Borosilicate Tube (OD 60mm) + 3D SLA Helical Baffles + Tri-clamps",
            budgetAllocation = "R&D Phase-1 Tranche: ₹60,000",
            rows = listOf(
                VendorScorecardRow(
                    vendorName = "Supplier A Labortek",
                    item = "Borosilicate 3.3 + Tri-clamp Endcaps",
                    unitPriceINR = "₹8,450",
                    materialSpec = "Duran 3.3 / SS316 CNC",
                    studentDiscount = "15% Applied",
                    leadTime = "4-5 Days",
                    deliveryLocation = "Local Courier",
                    isRecommended = true
                ),
                VendorScorecardRow(
                    vendorName = "Supplier B Tech",
                    item = "3D SLA Baffles in Biocompatible Resin",
                    unitPriceINR = "₹9,800",
                    materialSpec = "Clear Autoclavable Photopolymer",
                    studentDiscount = "10% Startup",
                    leadTime = "3 Days",
                    deliveryLocation = "Central Hub",
                    isRecommended = true
                ),
                VendorScorecardRow(
                    vendorName = "Supplier C Acrylics",
                    item = "Precision Acrylic Outer Enclosure",
                    unitPriceINR = "₹6,200",
                    materialSpec = "8mm Cast Acrylic (UV-pass)",
                    studentDiscount = "None",
                    leadTime = "5 Days",
                    deliveryLocation = "Regional Dispatch",
                    isRecommended = false
                ),
                VendorScorecardRow(
                    vendorName = "Supplier D BioFlow",
                    item = "Fluidic Extraction Tubing & Pump",
                    unitPriceINR = "₹11,500",
                    materialSpec = "Pt-cured medical silicone",
                    studentDiscount = "Standard Rate",
                    leadTime = "6 Days",
                    deliveryLocation = "Standard Courier",
                    isRecommended = false
                )
            )
        )

        // 3. Bento Briefing Protocol block
        val bentoData = BentoBriefing(
            greeting = "Good morning, $userName 🌿",
            dateText = "Today · Active Session",
            schedulePriority = BentoPriorityItem(
                title = "Laboratory Research Session",
                subtitle = "3:30 PM - 5:30 PM · Optical density & culture sampling",
                iconEmoji = "🔬",
                badge = "High Priority"
            ),
            emailPriority = BentoPriorityItem(
                title = "3 Unread Vendor Quotes",
                subtitle = "Supplier proforma estimates for prototype components",
                iconEmoji = "📬",
                badge = "Needs Review"
            ),
            grantLabPriority = BentoPriorityItem(
                title = "R&D Prototyping Allocation",
                subtitle = "₹2,48,000 allocated · Incubation & milestone tracking",
                iconEmoji = "🌱",
                badge = "Active"
            ),
            contentPriority = BentoPriorityItem(
                title = "Weekly Content Pipeline",
                subtitle = "Technical publication outline ready for Google Docs",
                iconEmoji = "✍️",
                badge = "In Progress"
            )
        )

        // 4. Portal Takeover Checklist for External Portals
        val portalChecklist = PortalTakeoverCard(
            id = "ptc-inst-01",
            portalName = "Central Analytical Equipment Portal",
            purpose = "Reserve ICP-MS heavy metal run (River sediment isolates)",
            loginUrl = "https://external-portal.example.org/login",
            credentialHint = "Institutional Account: [Configured in Private Vault]",
            checklist = listOf(
                PortalChecklistItem("step-1", "Navigate to portal login page & solve CAPTCHA image", isCompleted = false),
                PortalChecklistItem("step-2", "Select Analytical Category: ICP-MS (High Resolution)", "ICP-MS HR", isCompleted = true),
                PortalChecklistItem("step-3", "Paste Project Reference Code", "[Configured in Vault]", isCompleted = true),
                PortalChecklistItem("step-4", "Generate proforma invoice & download payment slip (₹4,200)", isCompleted = false)
            )
        )

        // 5. Initial Chat Messages
        val commandCenterMsgs = listOf(
            ChatMessage(
                id = "msg-100",
                channelId = "command-center",
                senderType = SenderType.BOT,
                botId = "atlas",
                text = "🌿 **Good morning, $userName!** Atlas here with your workspace sync.\n\nAll 5 specialist companions are active in the Command Center. I've compiled your morning priorities across hardware, research, and publications below:",
                timestamp = "08:30 AM",
                bentoBriefing = bentoData,
                stepsUsed = listOf(
                    "Read Daily Schedule from workspace",
                    "Scanned inbox for supplier quotes and active threads",
                    "Verified Calendar schedule appointments"
                ),
                citationChips = listOf(
                    CitationChip("Daily Schedule", "Sheet", dailyScheduleSheetId.ifBlank { "Vault Config" }),
                    CitationChip("Project Grant Doc", "Doc", "Phase-1"),
                    CitationChip("Lab Schedule", "Calendar", "cal-event")
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
                    taskTitle = "Audit Hardware Quotes & Compare Vendors for Column Prototype",
                    contextSummary = "Extracted specifications from CAD model (OD 60mm borosilicate, SS316 tri-clamp, 3D SLA baffles). Compare shortlisted suppliers under allocated budget.",
                    reasoning = "Reactor specializes in hardware fabrication and supplier negotiations."
                )
            ),
            ChatMessage(
                id = "msg-102",
                channelId = "command-center",
                senderType = SenderType.BOT,
                botId = "reactor",
                text = "🫧 **Reactor on duty!** I audited our component quotes for the column assembly. **Supplier A** and **Supplier B** offer optimal rates under our ₹60,000 Phase-1 budget:",
                timestamp = "08:35 AM",
                scorecard = pbrScorecard,
                stepsUsed = listOf(
                    "Parsed 3D SLA biocompatible resin quotation",
                    "Extracted ISO 3585 borosilicate sleeve specifications",
                    "Calculated educational/startup discount savings (₹2,100)"
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
                text = "🧬 **Helix reporting:**\n\n• **Environmental Surveillance:** Bacterial isolates from river sediment showed expected resistance markers. Sensed an analytical opening on the central facility portal for Thursday morning.\n• **Portal Takeover Card:** Because external equipment portals require human login & CAPTCHA verification, here is your interactive takeover checklist:",
                timestamp = "09:02 AM",
                portalTakeover = portalChecklist,
                stepsUsed = listOf(
                    "Checked facility slot calendar",
                    "Formatted sample prep protocol for ICP-MS",
                    "Loaded institutional billing profile"
                ),
                citationChips = listOf(
                    CitationChip("Equipment Portal", "Portal", "instrument-portal"),
                    CitationChip("AMR Surveillance Data", "Doc", "surveillance-v2")
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
                    text = "🌟 **Atlas Chief of Staff:** Your central workspace coordinator. Type `/brief` for a fresh overview or tap the 🔒 Vault icon to configure your private credentials.",
                    timestamp = "08:25 AM"
                )
            ),
            "reactor" to listOf(
                ChatMessage(
                    id = "msg-r1",
                    channelId = "reactor",
                    senderType = SenderType.BOT,
                    botId = "reactor",
                    text = "🫧 **Reactor:** Hardware & procurement station active. Sourcing borosilicate columns, SS316 multi-port caps, and SLA baffles.",
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
                    text = "🧬 **Helix:** Research desk active. Tracking environmental isolates, thesis manuscripts, and external instrument bookings.",
                    timestamp = "08:40 AM"
                )
            ),
            "pulse" to listOf(
                ChatMessage(
                    id = "msg-p1",
                    channelId = "pulse",
                    senderType = SenderType.BOT,
                    botId = "pulse",
                    text = "🎧 **Pulse:** Content studio on track. Operating the weekly publishing cycle on the Content Schedule Google Sheet.",
                    timestamp = "08:45 AM"
                )
            ),
            "forge" to listOf(
                ChatMessage(
                    id = "msg-f1",
                    channelId = "forge",
                    senderType = SenderType.BOT,
                    botId = "forge",
                    text = "🤖 **Forge:** Antigravity coding bridge ready. Simulating bioprocess flow Reynolds numbers and preparing reproducible code.",
                    timestamp = "08:50 AM"
                )
            )
        )

        // 6. Activities
        _activities.value = listOf(
            ActivityTask(
                id = "act-01",
                title = "Fluidic Column Prototyping",
                botId = "reactor",
                status = ActivityStatus.RUNNING,
                progress = 0.70f,
                currentStep = "Waiting for human approval to dispatch RFQ to shortlisted vendors",
                stepIndex = 3,
                totalSteps = 4,
                logs = listOf(
                    "08:12 AM - Checked CAD dimension tolerances for helical baffle inserts",
                    "08:25 AM - Scanned fabrication suppliers for student/startup discounts",
                    "08:35 AM - Compiled multi-vendor scorecard under Phase-1 allocation",
                    "08:36 AM - Formatted RFQ email and pushed to High-Fives gate"
                ),
                sourcesCited = listOf(
                    "Drive: Baffles_v3.cad",
                    "Grant Allocation Sheet (Row 6)",
                    "Prototyping Supplier Catalog"
                )
            ),
            ActivityTask(
                id = "act-02",
                title = "Metagenomic & Heavy Metal Profiling",
                botId = "helix",
                status = ActivityStatus.RUNNING,
                progress = 0.85f,
                currentStep = "ICP-MS booking window ready for Thursday",
                stepIndex = 4,
                totalSteps = 5,
                logs = listOf(
                    "Oct 20 - Analyzed 14 MALDI-TOF isolates from river sample sites",
                    "Oct 22 - Calculated MAR index across sampling stations",
                    "Today - Prepared analytical portal takeover checklist"
                ),
                sourcesCited = listOf(
                    "Spectrometry Analysis Data",
                    "Equipment Portal Facility Tracker",
                    "Manuscript: AMR Surveillance in River Basins"
                )
            ),
            ActivityTask(
                id = "act-03",
                title = "Weekly Publication Cycle",
                botId = "pulse",
                status = ActivityStatus.RUNNING,
                progress = 0.45f,
                currentStep = "Drafting Google Doc article with verified statistics",
                stepIndex = 2,
                totalSteps = 5,
                logs = listOf(
                    "08:40 AM - Queried scientific literature for recent bioprocess kinetics data",
                    "09:10 AM - Generated 5 high-retention technical hooks for professional network",
                    "09:25 AM - Drafted row update for Content Schedule Google Sheet"
                ),
                sourcesCited = listOf(
                    "Content Schedule Sheet",
                    "Literature Search: Bioreactor kinetics"
                )
            )
        )

        // 7. Standing Routines
        _routines.value = listOf(
            Routine(
                id = "rtn-01",
                title = "Daily 8:30 AM Morning Briefing",
                botId = "atlas",
                scheduleText = "Every day · 08:30 AM IST",
                description = "Autonomous read-only sweep across inbox, Daily Schedule Sheet, Calendar, and pending approvals. Displays Bento summary in Command Center.",
                isEnabled = true,
                lastRunText = "Today at 08:30 AM IST",
                status = RoutineStatus.SUCCESS
            ),
            Routine(
                id = "rtn-02",
                title = "Tue/Fri 4:00 PM Hardware RFQ & Budget Tracker",
                botId = "reactor",
                scheduleText = "Tuesdays & Fridays · 04:00 PM IST",
                description = "Scans inbox for supplier responses, checks prototyping invoice compliance, and updates the vendor comparison scorecard.",
                isEnabled = true,
                lastRunText = "Friday at 04:00 PM IST",
                status = RoutineStatus.IDLE
            ),
            Routine(
                id = "rtn-03",
                title = "Wed 2:00 PM Lab & Manuscript Pulse",
                botId = "helix",
                scheduleText = "Wednesdays · 02:00 PM IST",
                description = "Checks journal submission portals and analytical instrumentation slot availabilities.",
                isEnabled = true,
                lastRunText = "Wednesday at 02:00 PM IST",
                status = RoutineStatus.IDLE
            ),
            Routine(
                id = "rtn-04",
                title = "Thu 10:00 AM Content Studio Kickoff",
                botId = "pulse",
                scheduleText = "Thursdays · 10:00 AM IST",
                description = "Reads Content Schedule Sheet, initiates topic research, prepares Google Doc draft, and generates publishing outline.",
                isEnabled = true,
                lastRunText = "Thursday at 10:00 AM IST",
                status = RoutineStatus.IDLE
            )
        )

        // 8. Memories (Clean & Generic)
        _memories.value = listOf(
            MemoryItem(
                id = "mem-rnd-01",
                fact = "Active R&D Innovation Project: Hardware prototyping and fabrication budgets tracked in expense ledger.",
                category = MemoryCategory.HARDWARE_RND,
                confidence = 100,
                source = "Project Sanction Documentation",
                timestamp = "Oct 20, 2026",
                tags = listOf("Hardware", "Prototyping", "Budget")
            ),
            MemoryItem(
                id = "mem-bioref-02",
                fact = "Bioprocess Project: Mannooligosaccharides (MOS) extraction from agricultural and spent residues for bio-formulations.",
                category = MemoryCategory.HARDWARE_RND,
                confidence = 98,
                source = "R&D Partnership Brief",
                timestamp = "Oct 18, 2026",
                tags = listOf("Biorefinery", "MOS", "Extraction")
            ),
            MemoryItem(
                id = "mem-amr-03",
                fact = "Research & Manuscripts: Stochastic synchronization in microbial systems, environmental AMR surveillance, and sedimentary metagenomics.",
                category = MemoryCategory.RESEARCH_LABS,
                confidence = 99,
                source = "Research Dossier",
                timestamp = "Oct 22, 2026",
                tags = listOf("Microbiology", "Metagenomics", "Manuscript")
            ),
            MemoryItem(
                id = "mem-pbr-04",
                fact = "Fluidic column engineering specs: Borosilicate glass tube OD 60mm, wall 3.2mm, length 600mm, SS316 multi-port tri-clamp sanitary endcaps, and 3D SLA biocompatible fluidic baffles.",
                category = MemoryCategory.HARDWARE_RND,
                confidence = 98,
                source = "CAD Specs",
                timestamp = "Oct 19, 2026",
                tags = listOf("Borosilicate 3.3", "SS316", "3D SLA", "Dimensions")
            ),
            MemoryItem(
                id = "mem-sheets-05",
                fact = "Google Workspace Connected Sheets: Configurable on-device in the 🔒 Private Vault to preserve zero-PII security.",
                category = MemoryCategory.PERSONAL_PREFS,
                confidence = 100,
                source = "Private Vault Config",
                timestamp = "Oct 24, 2026",
                tags = listOf("Google Sheets", "Private Vault", "Zero-PII")
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

        // 9. Skills Library
        _skills.value = listOf(
            SkillItem(
                id = "skl-01",
                slug = "vendor-rfq-comparison",
                title = "Multi-Vendor RFQ & Quote Comparison",
                summary = "Draft technical quotation requests, itemize materials, and build a side-by-side comparison scorecard.",
                triggerRule = "When asking for parts procurement, glassblowing, laser-cutting, or 3D-printing quotes.",
                requiredTools = listOf("gmail/search", "drive/read_cad", "sheets/vendor_scorecard"),
                instructions = """
                    1. Extract exact CAD tolerances, material grades, and quantities.
                    2. Search inbox for historical quotes from shortlisted suppliers.
                    3. Compile a comparison table with: Unit Price (INR), Material Spec, Startup/Student Discount, Lead Time, and Dispatch.
                    4. Draft technical RFQ email and hold for human approval before sending.
                """.trimIndent(),
                lastExecuted = "Today, 08:35 AM"
            ),
            SkillItem(
                id = "skl-02",
                slug = "grant-bill-compliance",
                title = "Project Bill & Voucher Compliance",
                summary = "Audit vendor invoices for project accounting and reporting.",
                triggerRule = "When reviewing proforma invoices, GST receipts, or allocation ceilings.",
                requiredTools = listOf("gmail/extract_invoice", "sheets/grant_ledger"),
                instructions = """
                    1. Check GSTIN validity for supplier.
                    2. Ensure quotation item matches approved project prototyping tranche.
                    3. Attach startup/student declaration and route for approval.
                """.trimIndent(),
                lastExecuted = "Yesterday, 04:15 PM"
            ),
            SkillItem(
                id = "skl-03",
                slug = "portal-slot-prep",
                title = "Analytical Equipment Slot Prep",
                summary = "Check analytical equipment availability and prepare proforma invoice verification.",
                triggerRule = "When scheduling ICP-MS, MALDI-TOF, or NGS sequencing runs.",
                requiredTools = listOf("portal/check", "calendar/event_draft"),
                instructions = """
                    1. Check target instrument availability.
                    2. If login or CAPTCHA appears, trigger Portal Takeover Checklist for user.
                    3. Calculate user charges according to academic or institutional tier.
                    4. Create pending calendar block in Approvals Gate. Never finalize booking payment automatically.
                """.trimIndent(),
                lastExecuted = "Today, 09:02 AM"
            ),
            SkillItem(
                id = "skl-04",
                slug = "weekly-content-cycle",
                title = "Weekly Content Cycle (Blog, Professional Post, Media)",
                summary = "Execute the multi-day content research, writing, and scheduling loop.",
                triggerRule = "Triggered by weekly routine or on-demand /content command.",
                requiredTools = listOf("sheets/read_content_calendar", "docs/create_draft", "web/search_scholar"),
                instructions = """
                    1. Read Content Schedule Google Sheet for the current week's anchor theme.
                    2. Day 1: Perform SEO & citation research on academic literature.
                    3. Day 2: Write complete 1,400-word publication draft with subheadings in Google Docs.
                    4. Day 3: Draft 1 thought-leadership post + 5 carousel slides.
                    5. Day 4: Draft visual captions with hooks and hashtag sets.
                    6. Submit all drafts to Approvals queue.
                """.trimIndent(),
                lastExecuted = "Today, 09:15 AM"
            ),
            SkillItem(
                id = "skl-05",
                slug = "biomolecular-modeling-prep",
                title = "Biomolecular Modeling & Structure Prep",
                summary = "Format input FASTA sequences and ligand SMILES for structural biology runs.",
                triggerRule = "When running protein-ligand docking or conformational simulations.",
                requiredTools = listOf("keep/read_notes", "api/prepare_input"),
                instructions = """
                    1. Parse protein sequence and target ligand structure.
                    2. Prepare configuration input files.
                    3. Log parameters into notes for research documentation.
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
                details = "Scanned inbox: 3 supplier quotes triaged. Outbound drafts held for approval."
            ),
            WorkspaceConnection(
                id = "ws-calendar",
                type = WorkspaceServiceType.CALENDAR,
                isConnected = true,
                statusText = "Connected · Active Timezone",
                lastSyncText = "Synced 4 mins ago",
                details = "Upcoming: 3:30 PM Laboratory Session. Events require approval."
            ),
            WorkspaceConnection(
                id = "ws-sheets",
                type = WorkspaceServiceType.SHEETS,
                isConnected = true,
                statusText = "Connected · Sheets Linked",
                lastSyncText = "Synced 8 mins ago",
                resourceId = if (dailyScheduleSheetId.isNotBlank()) "$dailyScheduleSheetId | $contentScheduleSheetId" else "Configure in Vault",
                details = "Daily Schedule & Content Schedule ready (configurable in Private Vault)."
            ),
            WorkspaceConnection(
                id = "ws-drive",
                type = WorkspaceServiceType.DRIVE,
                isConnected = true,
                statusText = "Connected · Workspace Files",
                lastSyncText = "Synced 12 mins ago",
                details = "Indexed 42 CAD files, 14 manuscript drafts, and project vouchers."
            ),
            WorkspaceConnection(
                id = "ws-tasks",
                type = WorkspaceServiceType.TASKS,
                isConnected = true,
                statusText = "Connected · Active Tasks",
                lastSyncText = "Synced 6 mins ago",
                details = "Synced with Google Tasks list 'Workspace Tasks'."
            ),
            WorkspaceConnection(
                id = "ws-keep",
                type = WorkspaceServiceType.KEEP,
                isConnected = true,
                statusText = "Connected · Research Notes & Ideas",
                lastSyncText = "Synced 15 mins ago",
                details = "Synchronizing lab notes and modeling parameters."
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
                    executedAt = "Executed at $now",
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
                    executedAt = "Edited & executed at $now",
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
                            executedAt = "Executed at ${currentTime()}"
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
        val userName = _vaultProfile.value.displayName.ifBlank { "User" }
        _routines.value = _routines.value.map {
            it.copy(
                lastRunText = "Today at $now",
                status = RoutineStatus.SUCCESS
            )
        }

        // Add heartbeat notification in Command Center
        val heartbeatMsg = ChatMessage(
            id = "msg-${System.currentTimeMillis()}",
            channelId = "command-center",
            senderType = SenderType.BOT,
            botId = "atlas",
            text = "🌿 **Proactive Workspace Sweep Complete ($now)**\n\n• **Inbox:** Supplier quotes synced under project allocation.\n• **Daily Schedule:** Laboratory session confirmed.\n• **Content Schedule:** Weekly article draft verified.\n• **High-Fives Gate:** ${_approvals.value.count { it.status == ApprovalStatus.PENDING }} action cards pending your authorization.",
            timestamp = now,
            stepsUsed = listOf(
                "Scanned inbox for new RFQ follow-ups",
                "Queried Daily Schedule",
                "Checked Content Schedule"
            ),
            citationChips = listOf(
                CitationChip("Daily Schedule", "Sheet", dailyScheduleSheetId.ifBlank { "Vault Config" }),
                CitationChip("Calendar", "Calendar", "cal-sync"),
                CitationChip("Content Calendar", "Sheet", contentScheduleSheetId.ifBlank { "Vault Config" })
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
