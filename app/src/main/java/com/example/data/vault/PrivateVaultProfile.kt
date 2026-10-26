package com.example.data.vault

import org.json.JSONObject

/**
 * Encapsulates the user's private, on-device context.
 * Stored strictly in local SharedPreferences and NEVER committed to Git or hardcoded in repositories.
 */
data class PrivateVaultProfile(
    val displayName: String = "Builder",
    val roleAndDomain: String = "Founder & Researcher",
    val organizationOrLab: String = "R&D Innovation Lab",
    val dailyScheduleSheetId: String = "",
    val contentScheduleSheetId: String = "",
    val customContextNotes: String = ""
) {
    fun toJsonString(): String {
        val json = JSONObject()
        json.put("displayName", displayName)
        json.put("roleAndDomain", roleAndDomain)
        json.put("organizationOrLab", organizationOrLab)
        json.put("dailyScheduleSheetId", dailyScheduleSheetId)
        json.put("contentScheduleSheetId", contentScheduleSheetId)
        json.put("customContextNotes", customContextNotes)
        return json.toString(2)
    }

    fun toSystemPromptContext(): String {
        val sb = StringBuilder()
        sb.appendLine("USER CONTEXT (Loaded from On-Device Private Vault):")
        sb.appendLine("- Display Name: ${displayName.ifBlank { "User" }}")
        sb.appendLine("- Role & Domain: ${roleAndDomain.ifBlank { "General Engineering & Research" }}")
        sb.appendLine("- Organization / Lab: ${organizationOrLab.ifBlank { "Independent Lab" }}")
        if (dailyScheduleSheetId.isNotBlank()) {
            sb.appendLine("- Daily Schedule Sheet ID: $dailyScheduleSheetId")
        }
        if (contentScheduleSheetId.isNotBlank()) {
            sb.appendLine("- Content Schedule Sheet ID: $contentScheduleSheetId")
        }
        if (customContextNotes.isNotBlank()) {
            sb.appendLine("- Custom Context & Notes:\n$customContextNotes")
        }
        return sb.toString()
    }

    companion object {
        fun fromJsonString(jsonStr: String): Result<PrivateVaultProfile> {
            return runCatching {
                val json = JSONObject(jsonStr)
                PrivateVaultProfile(
                    displayName = json.optString("displayName", "Builder"),
                    roleAndDomain = json.optString("roleAndDomain", "Founder & Researcher"),
                    organizationOrLab = json.optString("organizationOrLab", "R&D Innovation Lab"),
                    dailyScheduleSheetId = json.optString("dailyScheduleSheetId", ""),
                    contentScheduleSheetId = json.optString("contentScheduleSheetId", ""),
                    customContextNotes = json.optString("customContextNotes", "")
                )
            }
        }
    }
}
