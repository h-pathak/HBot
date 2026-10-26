package com.example.data.vault

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages local, on-device encrypted or private SharedPreferences for user profile context.
 * Ensures zero-PII is committed to git or stored remotely without user consent.
 */
class PrivateVaultManager(context: Context) {

    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _vaultProfile = MutableStateFlow(loadProfile())
    val vaultProfile: StateFlow<PrivateVaultProfile> = _vaultProfile.asStateFlow()

    private fun loadProfile(): PrivateVaultProfile {
        return PrivateVaultProfile(
            displayName = prefs.getString(KEY_DISPLAY_NAME, "Builder") ?: "Builder",
            roleAndDomain = prefs.getString(KEY_ROLE_DOMAIN, "Founder & Researcher") ?: "Founder & Researcher",
            organizationOrLab = prefs.getString(KEY_ORG_LAB, "R&D Innovation Lab") ?: "R&D Innovation Lab",
            dailyScheduleSheetId = prefs.getString(KEY_DAILY_SHEET_ID, "") ?: "",
            contentScheduleSheetId = prefs.getString(KEY_CONTENT_SHEET_ID, "") ?: "",
            customContextNotes = prefs.getString(KEY_CUSTOM_NOTES, "") ?: ""
        )
    }

    fun saveProfile(profile: PrivateVaultProfile) {
        prefs.edit()
            .putString(KEY_DISPLAY_NAME, profile.displayName)
            .putString(KEY_ROLE_DOMAIN, profile.roleAndDomain)
            .putString(KEY_ORG_LAB, profile.organizationOrLab)
            .putString(KEY_DAILY_SHEET_ID, profile.dailyScheduleSheetId)
            .putString(KEY_CONTENT_SHEET_ID, profile.contentScheduleSheetId)
            .putString(KEY_CUSTOM_NOTES, profile.customContextNotes)
            .apply()
        _vaultProfile.value = profile
    }

    fun importFromJson(jsonString: String): Result<PrivateVaultProfile> {
        return PrivateVaultProfile.fromJsonString(jsonString).onSuccess { parsed ->
            saveProfile(parsed)
        }
    }

    fun exportToJson(): String {
        return _vaultProfile.value.toJsonString()
    }

    fun clearAllData() {
        prefs.edit().clear().apply()
        val defaultProfile = PrivateVaultProfile()
        _vaultProfile.value = defaultProfile
    }

    companion object {
        private const val PREFS_NAME = "hbot_private_vault_prefs"
        private const val KEY_DISPLAY_NAME = "key_display_name"
        private const val KEY_ROLE_DOMAIN = "key_role_domain"
        private const val KEY_ORG_LAB = "key_org_lab"
        private const val KEY_DAILY_SHEET_ID = "key_daily_sheet_id"
        private const val KEY_CONTENT_SHEET_ID = "key_content_sheet_id"
        private const val KEY_CUSTOM_NOTES = "key_custom_notes"
    }
}
