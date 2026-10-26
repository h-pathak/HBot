package com.example.data.model

enum class SenderType {
    USER, BOT, SYSTEM
}

data class BotHandoff(
    val fromBotId: String,
    val toBotId: String,
    val taskTitle: String,
    val contextSummary: String,
    val reasoning: String = "Automated domain delegation"
)

data class VendorScorecardRow(
    val vendorName: String,
    val item: String,
    val unitPriceINR: String,
    val materialSpec: String,
    val studentDiscount: String,
    val leadTime: String,
    val deliveryLocation: String,
    val isRecommended: Boolean = false
)

data class VendorScorecard(
    val title: String,
    val component: String,
    val budgetAllocation: String = "₹60,000 SSIP 2.0 Month-1",
    val rows: List<VendorScorecardRow>
)

data class CitationChip(
    val title: String,
    val category: String, // "Sheet", "Doc", "Drive", "Portal", "Search"
    val reference: String
)

data class ChatMessage(
    val id: String,
    val channelId: String,
    val senderType: SenderType,
    val botId: String? = null,
    val text: String,
    val timestamp: String,
    val approvalAction: ApprovalAction? = null,
    val handoff: BotHandoff? = null,
    val scorecard: VendorScorecard? = null,
    val bentoBriefing: BentoBriefing? = null,
    val portalTakeover: PortalTakeoverCard? = null,
    val skillLearned: SkillLearnedCard? = null,
    val citationChips: List<CitationChip> = emptyList(),
    val stepsUsed: List<String> = emptyList(),
    val isThinking: Boolean = false
)
