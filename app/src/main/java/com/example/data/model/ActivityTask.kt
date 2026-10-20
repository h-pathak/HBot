package com.example.data.model

enum class ActivityStatus(val label: String) {
    RUNNING("Active"),
    PAUSED("Paused"),
    COMPLETED("Completed"),
    FAILED("Interrupted")
}

data class ActivityTask(
    val id: String,
    val title: String,
    val botId: String,
    val status: ActivityStatus,
    val progress: Float, // 0.0 to 1.0
    val currentStep: String,
    val stepIndex: Int,
    val totalSteps: Int,
    val logs: List<String>,
    val sourcesCited: List<String>,
    val canPause: Boolean = true,
    val canRedirect: Boolean = true
)

enum class RoutineStatus {
    IDLE, RUNNING, SUCCESS
}

data class Routine(
    val id: String,
    val title: String,
    val botId: String,
    val scheduleText: String,
    val description: String,
    val isEnabled: Boolean = true,
    val lastRunText: String = "Today, 08:30 AM (Asia/Kolkata)",
    val status: RoutineStatus = RoutineStatus.IDLE
)
