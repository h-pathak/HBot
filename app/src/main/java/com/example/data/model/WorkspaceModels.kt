package com.example.data.model

enum class WorkspaceServiceType(val displayName: String, val iconName: String) {
    GMAIL("Gmail Triage & Drafts", "mail"),
    CALENDAR("Google Calendar (Asia/Kolkata)", "calendar"),
    SHEETS("Google Sheets (Schedules)", "table"),
    DRIVE("Google Drive (CAD & Docs)", "folder"),
    TASKS("Google Tasks", "check"),
    KEEP("Google Keep (Quick Notes)", "note")
}

data class WorkspaceConnection(
    val id: String,
    val type: WorkspaceServiceType,
    val isConnected: Boolean,
    val statusText: String,
    val lastSyncText: String,
    val resourceId: String? = null,
    val details: String
)
