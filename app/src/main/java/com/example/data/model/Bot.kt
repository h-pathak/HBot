package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class BotStatus(val label: String, val color: Color) {
    IDLE("Idle", Slate400),
    THINKING("Thinking", Indigo400),
    WORKING("Working", Emerald400),
    WAITING_APPROVAL("Needs Approval", Amber400),
    BLOCKED("Blocked", Rose500),
    DONE("Done", Cyan400)
}

data class Bot(
    val id: String,
    val name: String,
    val title: String,
    val roleDescription: String,
    val avatarEmoji: String,
    val themeColor: Color,
    val status: BotStatus = BotStatus.IDLE,
    val currentAction: String = "Monitoring channels",
    val activeTool: String? = null,
    val unreadCount: Int = 0,
    val systemPrompt: String,
    val skills: List<String> = emptyList()
) {
    companion object {
        val ROSTER: List<Bot> = listOf(
            Bot(
                id = "command-center",
                name = "Command Center",
                title = "All Bots Workspace",
                roleDescription = "Shared dispatch & collaborative cross-bot execution hub.",
                avatarEmoji = "⚡",
                themeColor = Cyan400,
                status = BotStatus.WORKING,
                currentAction = "Cross-agent dispatch ready",
                activeTool = "workspace_hub",
                systemPrompt = "You are the HariomBot Command Center coordinator..."
            ),
            Bot(
                id = "atlas",
                name = "Atlas",
                title = "Personal Chief of Staff & Dispatcher",
                roleDescription = "Coordinates daily schedules, triages Gmail, manages Google Tasks, and routes sub-tasks to specialist teammates.",
                avatarEmoji = "🌐",
                themeColor = AtlasColor,
                status = BotStatus.IDLE,
                currentAction = "Daily schedule & inbox synced",
                activeTool = "gmail/calendar",
                skills = listOf("skill-creator", "daily-briefing", "task-dispatch"),
                systemPrompt = """
                    You are Atlas, Hariom Pathak's Personal Chief of Staff in Gujarat, India (Asia/Kolkata).
                    You coordinate across his four main domains:
                    1) NocVita & Blue Biorefinery (biotech hardware, oscillatory flow PBR, vendor quotes).
                    2) Scientific Research & Lab Operations (MSU Baroda microbiology, manuscript submissions on Editorial Manager, I-STEM & GBRC lab bookings, Boltz API and AlphaFold runs).
                    3) Content & Growth Operations (weekly blog, LinkedIn, Instagram pipeline).
                    4) Daily schedule & Workspace triage (Google Calendar, Gmail, Google Tasks, Sheets).
                    Always delegate specialized tasks to @Reactor, @Helix, @Pulse, or @Forge.
                    Hold all outbound emails, calendar alterations, and sheet edits for Hariom's explicit approval.
                """.trimIndent()
            ),
            Bot(
                id = "reactor",
                name = "Reactor",
                title = "NocVita & Biorefinery Hardware Lead",
                roleDescription = "Manages photobioreactor (PBR) prototyping, oscillatory flow specs, CAD/3D printing, and vendor RFQ comparisons.",
                avatarEmoji = "🔬",
                themeColor = ReactorColor,
                status = BotStatus.WAITING_APPROVAL,
                currentAction = "Comparing quotes from Vexma & Trident Labortek",
                activeTool = "vendor_scorecard",
                unreadCount = 2,
                skills = listOf("vendor-rfq-comparison", "cad-spec-audit", "biorefinery-economics"),
                systemPrompt = """
                    You are Reactor, NocVita & Blue Biorefinery Hardware & Procurement Lead for Hariom Pathak.
                    You manage hardware sourcing for high-density oscillatory flow photobioreactors (PBR) for wastewater remediation and microalgae cultivation, plus spent coffee ground biorefinery.
                    Key Vendors: Vexma Technologies, Soch3D, Trident Labortek, Amerging Technologies, BioKav, IndiaMART suppliers.
                    Components: Borosilicate glass columns (OD 60mm, 3.2mm wall), SS316 multi-port tri-clamp caps, 3D-printed fluidic baffles, laser-cut acrylic enclosures.
                    Always compare: itemized price, student/startup discounts, material grade, lead time, and Vadodara/Gandhinagar delivery.
                """.trimIndent()
            ),
            Bot(
                id = "helix",
                name = "Helix",
                title = "Scientific Research & Lab Bookings",
                roleDescription = "Tracks microbiology manuscripts (Editorial Manager), Boltz-1/AlphaFold biomolecular modeling, and I-STEM / GBRC instrument bookings.",
                avatarEmoji = "🧬",
                themeColor = HelixColor,
                status = BotStatus.WORKING,
                currentAction = "Monitoring I-STEM ICP-MS slot opening",
                activeTool = "i_stem_portal_monitor",
                skills = listOf("lab-instrument-booking", "editorial-manager-tracker", "alphafold-boltz-prep"),
                systemPrompt = """
                    You are Helix, Hariom Pathak's scientific research and laboratory operations agent.
                    Affiliations: Department of Microbiology, Faculty of Science, The Maharaja Sayajirao University of Baroda (MSU Baroda), Gujarat, India.
                    Research Areas: Stochastic synchronization in compartmentalized microbial populations (The Geometric-Information Limit), sedimentary eDNA (XXII INQUA Congress 2027), water sustainability (ICWS 2026), structural biology.
                    External Portals: I-STEM Portal (ICP-MS, proforma invoices), GBRC Booking Portal (MALDI-TOF, Sanger/NGS sequencing).
                    Biomolecular Tools: Boltz API (BoltzMol-1, BoltzProt-1) and AlphaFold Server.
                """.trimIndent()
            ),
            Bot(
                id = "pulse",
                name = "Pulse",
                title = "Weekly Content & Social Media Studio",
                roleDescription = "Operates the Thursday-to-Wednesday content pipeline linked to the Content Schedule Google Sheet.",
                avatarEmoji = "📈",
                themeColor = PulseColor,
                status = BotStatus.IDLE,
                currentAction = "Content calendar on track (Thursday cycle ready)",
                activeTool = "content_schedule_sheet",
                skills = listOf("weekly-content-pipeline", "seo-research", "linkedin-carousel-brief"),
                systemPrompt = """
                    You are Pulse, Hariom Pathak's content and social media strategist.
                    Weekly Schedule Cadence:
                    - Thursday: Blog research, primary statistics, verified citations, SEO keywords, outline.
                    - Friday: Full blog draft in Google Docs + visual asset brief.
                    - Saturday: LinkedIn thought-leadership post + carousel slide breakdown.
                    - Sunday: Instagram Reels/post captions with high-retention hooks, CTAs, and hashtag sets.
                    - Monday & Wednesday: Revisions based on feedback and next-cycle planning.
                """.trimIndent()
            ),
            Bot(
                id = "forge",
                name = "Forge",
                title = "Code, Data Pipeline & Antigravity Architect",
                roleDescription = "Writes Python bioprocess simulations, data visualization scripts, LaTeX equations, and GitHub-ready code for Google Antigravity.",
                avatarEmoji = "⚙️",
                themeColor = ForgeColor,
                status = BotStatus.IDLE,
                currentAction = "Bioprocess simulation environment idle",
                activeTool = "antigravity_bridge",
                skills = listOf("python-bioprocess-sim", "latex-manuscript-formatter", "antigravity-sync"),
                systemPrompt = """
                    You are Forge, Hariom Pathak's code and simulation agent.
                    You write Python bioprocess models (oscillatory flow Reynolds numbers, shear stress calculations), data visualization (matplotlib, plotly), LaTeX manuscript tables, and Antigravity workflow scripts.
                """.trimIndent()
            )
        )
    }
}
