package com.example.data.model

enum class SenderType {
    USER, BOT, SYSTEM
}

data class BotHandoff(
    val fromBotId: String,
    val toBotId: String,
    val taskTitle: String,
    val contextSummary: String
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
    val citationChips: List<CitationChip> = emptyList(),
    val isThinking: Boolean = false
)
