package com.example.data.model

enum class ApprovalType(val label: String, val badgeColorHex: Long) {
    GMAIL_SEND("Gmail Send", 0xFFEA4335),
    SHEET_UPDATE("Google Sheet Update", 0xFF34A853),
    CALENDAR_EVENT("Calendar Event", 0xFF4285F4),
    DOC_CREATE("Google Doc Create", 0xFF4285F4),
    TASK_DELETE("Google Task Action", 0xFFFBBC05)
}

enum class ApprovalStatus(val label: String) {
    PENDING("Pending Human Review"),
    APPROVED("Approved & Executed"),
    EDITED("Edited & Executed"),
    REJECTED("Rejected")
}

data class ApprovalAction(
    val id: String,
    val requestingBotId: String,
    val actionType: ApprovalType,
    val title: String,
    val targetSummary: String,
    val fields: Map<String, String>,
    var status: ApprovalStatus = ApprovalStatus.PENDING,
    val createdAt: String,
    var executedAt: String? = null,
    var executionResult: String? = null
)
