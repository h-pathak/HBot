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
                systemPrompt = "You are the HBot Command Center coordinator..."
            ),
            Bot(
                id = "atlas",
                name = "Atlas",
                title = "Personal Chief of Staff & Dispatcher",
                roleDescription = "Coordinates daily schedules, triages communications, manages tasks, and routes sub-tasks to specialist teammates.",
                avatarEmoji = "🌐",
                themeColor = AtlasColor,
                status = BotStatus.IDLE,
                currentAction = "Daily schedule & inbox synced",
                activeTool = "gmail/calendar",
                skills = listOf("skill-creator", "daily-briefing", "task-dispatch"),
                systemPrompt = """
                    You are Atlas, Personal Chief of Staff and Central Dispatcher.
                    You coordinate across the user's primary operating domains:
                    1) Hardware & Prototyping (specifications, vendor quotes, procurement).
                    2) Scientific Research & Lab Operations (experiments, manuscript tracking, equipment bookings, computational modeling).
                    3) Content & Growth Operations (weekly publications, technical briefs, article pipeline).
                    4) Daily schedule & Workspace triage (Calendar, inbox, tasks, sheets).
                    Always delegate specialized tasks to @Reactor, @Helix, @Pulse, or @Forge.
                    Hold all outbound emails, calendar alterations, and sheet edits for explicit human approval.
                """.trimIndent()
            ),
            Bot(
                id = "reactor",
                name = "Reactor",
                title = "Hardware & Prototyping Lead",
                roleDescription = "Manages prototyping hardware, oscillatory flow & fluidic specs, CAD/3D printing, and vendor RFQ comparisons.",
                avatarEmoji = "🔬",
                themeColor = ReactorColor,
                status = BotStatus.WAITING_APPROVAL,
                currentAction = "Comparing fabrication quotes from shortlisted suppliers",
                activeTool = "vendor_scorecard",
                unreadCount = 2,
                skills = listOf("vendor-rfq-comparison", "cad-spec-audit", "biorefinery-economics"),
                systemPrompt = """
                    You are Reactor, Hardware & Prototyping Lead.
                    You manage hardware sourcing for high-density bioreactors, custom fluidic hardware, microalgae cultivation, and biomass processing.
                    Components: Borosilicate glass columns, SS316 multi-port tri-clamp caps, 3D-printed fluidic baffles, laser-cut acrylic enclosures.
                    Always compare: itemized price, startup/educational discounts, material grade, tolerance specs, lead times, and dispatch logistics.
                """.trimIndent()
            ),
            Bot(
                id = "helix",
                name = "Helix",
                title = "Scientific Research & Lab Bookings",
                roleDescription = "Tracks scientific manuscripts, computational modeling workflows, and external instrumentation & facility bookings.",
                avatarEmoji = "🧬",
                themeColor = HelixColor,
                status = BotStatus.WORKING,
                currentAction = "Monitoring analytical instrument slot openings",
                activeTool = "portal_monitor",
                skills = listOf("lab-instrument-booking", "editorial-manager-tracker", "alphafold-boltz-prep"),
                systemPrompt = """
                    You are Helix, scientific research and laboratory operations specialist.
                    Research Areas: Stochastic synchronization in microbial systems, environmental metagenomics, water sustainability, structural biology.
                    External Portals: Instrument booking portals, analytical facilities (ICP-MS, MALDI-TOF, NGS sequencing), manuscript submission systems.
                    Biomolecular Tools: Biomolecular modeling APIs and structure prediction servers.
                """.trimIndent()
            ),
            Bot(
                id = "pulse",
                name = "Pulse",
                title = "Weekly Content & Social Media Studio",
                roleDescription = "Operates the multi-channel editorial and publication pipeline linked to the Content Schedule Google Sheet.",
                avatarEmoji = "📈",
                themeColor = PulseColor,
                status = BotStatus.IDLE,
                currentAction = "Content calendar on track (weekly cycle ready)",
                activeTool = "content_schedule_sheet",
                skills = listOf("weekly-content-pipeline", "seo-research", "linkedin-carousel-brief"),
                systemPrompt = """
                    You are Pulse, content and social media strategist.
                    Weekly Schedule Cadence:
                    - Day 1: Topic research, primary statistics, verified citations, SEO keywords, outline.
                    - Day 2: Full publication draft in Google Docs + visual asset brief.
                    - Day 3: Professional network thought-leadership post + carousel slide breakdown.
                    - Day 4: Video / visual captions with high-retention hooks, CTAs, and tag sets.
                    - Days 5-7: Revisions based on feedback and next-cycle planning.
                """.trimIndent()
            ),
            Bot(
                id = "forge",
                name = "Forge",
                title = "Code, Data Pipeline & Systems Architect",
                roleDescription = "Writes bioprocess simulations, data visualization scripts, LaTeX formulas, and reproducible code.",
                avatarEmoji = "⚙️",
                themeColor = ForgeColor,
                status = BotStatus.IDLE,
                currentAction = "Simulation and scripting environment idle",
                activeTool = "antigravity_bridge",
                skills = listOf("python-bioprocess-sim", "latex-manuscript-formatter", "antigravity-sync"),
                systemPrompt = """
                    You are Forge, code and technical systems agent.
                    You write bioprocess simulation models (Reynolds numbers, shear stress calculations), data visualization (matplotlib, plotly), LaTeX manuscript tables, and automation scripts.
                """.trimIndent()
            )
        )
    }
}
