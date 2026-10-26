package com.example.data.model

data class BentoPriorityItem(
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val badge: String,
    val isDone: Boolean = false
)

data class BentoBriefing(
    val greeting: String = "Good morning, Hariom 🌿",
    val dateText: String = "Today · Asia/Kolkata",
    val schedulePriority: BentoPriorityItem,
    val emailPriority: BentoPriorityItem,
    val grantLabPriority: BentoPriorityItem,
    val contentPriority: BentoPriorityItem
)

data class PortalChecklistItem(
    val id: String,
    val stepLabel: String,
    val copyableValue: String? = null,
    val isCompleted: Boolean = false
)

data class PortalTakeoverCard(
    val id: String,
    val portalName: String, // "I-STEM Analytical Facility" or "GBRC Sequencing" or "Editorial Manager"
    val purpose: String,
    val loginUrl: String,
    val credentialHint: String,
    val checklist: List<PortalChecklistItem>,
    val isLoggedOn: Boolean = false
)

data class SkillLearnedCard(
    val slug: String,
    val title: String,
    val summary: String,
    val requiredTools: List<String>,
    val triggerPattern: String,
    val savedAt: String
)
