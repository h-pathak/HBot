package com.example.data.model

enum class ApprovalType(val label: String, val badgeColorHex: Long) {
    GMAIL_DRAFT("Gmail Outbound Draft", 0xFFEA4335),
    SHEET_ROW_UPDATE("Google Sheet Row Update", 0xFF10B981),
    CALENDAR_SLOT("Calendar Slot Reservation", 0xFF4285F4),
    GRANT_DOC_NOTE("SSIP 2.0 Grant Doc Note", 0xFF8B5CF6),
    TASK_DELETE("Google Task Action", 0xFFF59E0B)
}

enum class ApprovalStatus(val label: String) {
    PENDING("High-Five Needed!"),
    APPROVED("Approved & Executed ✨"),
    EDITED("Edited & Executed ✏️"),
    REJECTED("Skipped ✕")
}

data class ApprovalAction(
    val id: String,
    val requestingBotId: String,
    val actionType: ApprovalType,
    val title: String,
    val targetSummary: String,
    val diffPreview: String? = null,
    val budgetTag: String? = null,
    val fields: Map<String, String>,
    var status: ApprovalStatus = ApprovalStatus.PENDING,
    val createdAt: String,
    var executedAt: String? = null,
    var executionResult: String? = null
)
