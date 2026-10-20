package com.example.data.model

data class SkillItem(
    val id: String,
    val slug: String,
    val title: String,
    val summary: String,
    val triggerRule: String,
    val requiredTools: List<String>,
    val instructions: String,
    val lastExecuted: String = "Ready"
)
