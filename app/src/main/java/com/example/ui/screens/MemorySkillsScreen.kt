package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.BotMascot
import com.example.ui.components.MascotState
import com.example.ui.theme.*

@Composable
fun MemorySkillsScreen(
    memories: List<MemoryItem>,
    skills: List<SkillItem>,
    searchQuery: String,
    selectedCategory: MemoryCategory,
    isInterviewActive: Boolean,
    interviewStep: Int,
    interviewQuestions: List<InterviewQuestion>,
    onSearchChanged: (String) -> Unit,
    onCategorySelected: (MemoryCategory) -> Unit,
    onAddMemory: (String, MemoryCategory, List<String>) -> Unit,
    onStartInterview: () -> Unit,
    onAnswerInterview: (String) -> Unit,
    onCancelInterview: () -> Unit,
    onTriggerSkill: (SkillItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalCozyTheme.current
    var showAddDialog by remember { mutableStateOf(false) }
    var activeSubTab by remember { mutableStateOf("MEMORIES") } // "MEMORIES" or "SKILLS"

    val filteredMemories = memories.filter { item ->
        val matchesCategory = selectedCategory == MemoryCategory.ALL || item.category == selectedCategory
        val matchesSearch = searchQuery.isBlank() ||
                item.fact.contains(searchQuery, ignoreCase = true) ||
                item.tags.any { it.contains(searchQuery, ignoreCase = true) }
        matchesCategory && matchesSearch
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.canvasBg)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("memory_skills_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Brain & Skills Vault 🧠",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = theme.textPrimary,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = "Persistent Memory & Collectible Procedures",
                    style = MaterialTheme.typography.labelSmall.copy(color = theme.accentPrimary)
                )
            }

            // Top action buttons
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = onStartInterview,
                    colors = ButtonDefaults.buttonColors(containerColor = AtlasPrimary),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("interview_me_button")
                ) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = DeepEspressoText, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Interview Me", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DeepEspressoText)
                }

                IconButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(theme.cardSurface)
                        .border(0.8.dp, theme.cardBorder, RoundedCornerShape(10.dp))
                        .testTag("add_memory_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Memory", tint = theme.accentPrimary, modifier = Modifier.size(20.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sub-Tab Switcher: Memories vs Skills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = activeSubTab == "MEMORIES",
                onClick = { activeSubTab = "MEMORIES" },
                label = { Text("Memory Bank (${memories.size})", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = theme.accentPrimary,
                    selectedLabelColor = Color.White,
                    containerColor = theme.cardSurface,
                    labelColor = theme.textSecondary
                ),
                shape = RoundedCornerShape(16.dp)
            )

            FilterChip(
                selected = activeSubTab == "SKILLS",
                onClick = { activeSubTab = "SKILLS" },
                label = { Text("Skills Deck (${skills.size})", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = HelixPrimary,
                    selectedLabelColor = Color.White,
                    containerColor = theme.cardSurface,
                    labelColor = theme.textSecondary
                ),
                shape = RoundedCornerShape(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // INTERVIEW MODE BANNER (When active)
        AnimatedVisibility(visible = isInterviewActive) {
            val currentQ = interviewQuestions.getOrNull(interviewStep)
            if (currentQ != null) {
                Surface(
                    color = theme.cardSurface,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, AtlasPrimary),
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                BotMascot(botId = "atlas", state = MascotState.LISTENING, size = 26.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Atlas Interview · Q${currentQ.step + 1}/${interviewQuestions.size}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = AtlasPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            TextButton(onClick = onCancelInterview) {
                                Text("Dismiss", color = theme.textSecondary, fontSize = 11.sp)
                            }
                        }

                        Text(
                            text = currentQ.question,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = theme.textPrimary,
                                fontSize = 13.sp
                            )
                        )

                        Text(
                            text = currentQ.rationale,
                            style = MaterialTheme.typography.labelSmall.copy(color = theme.textSecondary, fontSize = 11.sp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        currentQ.options.forEach { option ->
                            Surface(
                                color = theme.cardElevated,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(0.6.dp, theme.cardBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clickable { onAnswerInterview(option) }
                            ) {
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = theme.textPrimary,
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (activeSubTab == "MEMORIES") {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChanged,
                placeholder = { Text("Search memories, projects, specifications...", color = theme.textSecondary, fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = theme.textSecondary) },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall.copy(color = theme.textPrimary),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = theme.accentPrimary,
                    unfocusedBorderColor = theme.cardBorder,
                    focusedContainerColor = theme.cardSurface,
                    unfocusedContainerColor = theme.cardSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(MemoryCategory.entries) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { onCategorySelected(cat) },
                        label = { Text(cat.label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = theme.accentPrimary.copy(alpha = 0.2f),
                            selectedLabelColor = theme.accentPrimary,
                            containerColor = theme.cardSurface,
                            labelColor = theme.textSecondary
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Memory List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredMemories, key = { it.id }) { memory ->
                    CozyMemoryCard(memory = memory)
                }
            }
        } else {
            // Skills Deck Tab
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(skills, key = { it.id }) { skill ->
                    CozySkillCard(
                        skill = skill,
                        onTrigger = { onTriggerSkill(skill) }
                    )
                }
            }
        }
    }

    // Add Memory Dialog
    if (showAddDialog) {
        var newFact by remember { mutableStateOf("") }
        var newCat by remember { mutableStateOf(MemoryCategory.HARDWARE_RND) }
        var newTags by remember { mutableStateOf("Hardware, Prototype, Specs") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Learned Memory", color = theme.textPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newFact,
                        onValueChange = { newFact = it },
                        label = { Text("Fact / Rule / Preference", color = theme.textSecondary) },
                        textStyle = MaterialTheme.typography.bodySmall.copy(color = theme.textPrimary),
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newTags,
                        onValueChange = { newTags = it },
                        label = { Text("Tags (comma separated)", color = theme.textSecondary) },
                        textStyle = MaterialTheme.typography.bodySmall.copy(color = theme.textPrimary),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newFact.isNotBlank()) {
                            val tagList = newTags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            onAddMemory(newFact, newCat, tagList)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.accentPrimary)
                ) {
                    Text("Save Memory", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = theme.textSecondary)
                }
            },
            containerColor = theme.cardSurface
        )
    }
}

@Composable
fun CozyMemoryCard(memory: MemoryItem) {
    val theme = LocalCozyTheme.current

    Surface(
        color = theme.cardSurface,
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, theme.cardBorder),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(theme.accentPrimary.copy(alpha = 0.15f))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = memory.category.label,
                        color = theme.accentPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = theme.cardElevated,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${memory.confidence}% Confidence",
                        color = ReactorPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = memory.fact,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = theme.textPrimary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Source: ${memory.source} · ${memory.timestamp}",
                    style = MaterialTheme.typography.labelSmall.copy(color = theme.textSecondary, fontSize = 9.sp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    memory.tags.take(3).forEach { tag ->
                        Surface(
                            color = theme.cardElevated,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "#$tag",
                                color = theme.textSecondary,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CozySkillCard(
    skill: SkillItem,
    onTrigger: () -> Unit
) {
    val theme = LocalCozyTheme.current

    Surface(
        color = theme.cardSurface,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, HelixPrimary.copy(alpha = 0.5f)),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        tint = HelixPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = skill.slug,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = HelixPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Button(
                    onClick = onTrigger,
                    colors = ButtonDefaults.buttonColors(containerColor = HelixPrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Trigger Skill", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = skill.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = theme.textPrimary,
                    fontSize = 14.sp
                )
            )

            Text(
                text = skill.summary,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = theme.textSecondary,
                    fontSize = 11.sp
                ),
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = theme.cardElevated,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(0.6.dp, theme.cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Trigger rule: ${skill.triggerRule}",
                        style = MaterialTheme.typography.labelSmall.copy(color = theme.textSecondary, fontSize = 10.sp)
                    )
                    Text(
                        text = "Tools: ${skill.requiredTools.joinToString(", ")}",
                        style = MaterialTheme.typography.labelSmall.copy(color = theme.accentPrimary, fontSize = 10.sp),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
