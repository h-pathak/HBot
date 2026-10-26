package com.example.data.model

enum class MemoryCategory(val label: String, val tag: String) {
    ALL("All Memories", "ALL"),
    HARDWARE_RND("Hardware & Prototyping", "HARDWARE"),
    RESEARCH_LABS("Research & Labs", "RESEARCH"),
    CONTENT("Content Pipeline", "CONTENT"),
    PERSONAL_PREFS("Personal & Preferences", "PREFS")
}

data class MemoryItem(
    val id: String,
    val fact: String,
    val category: MemoryCategory,
    val confidence: Int, // e.g. 98%
    val source: String,
    val timestamp: String,
    val tags: List<String> = emptyList()
)

data class InterviewQuestion(
    val step: Int,
    val question: String,
    val domain: String,
    val rationale: String,
    val options: List<String>
)
