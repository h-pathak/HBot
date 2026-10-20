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
            .background(Slate900)
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
                    text = "Memory & Skills Vault",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate100,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = "Cross-Session Knowledge & Reusable Procedures",
                    style = MaterialTheme.typography.labelSmall.copy(color = Cyan400)
                )
            }

            // Top action buttons
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = onStartInterview,
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo500),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("interview_me_button")
                ) {
                    Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Interview Me", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Slate800)
                        .testTag("add_memory_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Memory", tint = Cyan400, modifier = Modifier.size(20.dp))
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
                label = { Text("Memory Bank (${memories.size})", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Cyan400,
                    selectedLabelColor = Slate950,
                    containerColor = Slate800,
                    labelColor = Slate300
                ),
                shape = RoundedCornerShape(16.dp)
            )

            FilterChip(
                selected = activeSubTab == "SKILLS",
                onClick = { activeSubTab = "SKILLS" },
                label = { Text("Skills Library (${skills.size})", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Indigo500,
                    selectedLabelColor = Color.White,
                    containerColor = Slate800,
                    labelColor = Slate300
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
                    color = Slate850,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, Indigo400),
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
                            Text(
                                text = "🧠 Business Context Operator (Step ${currentQ.step + 1} of ${interviewQuestions.size})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Indigo400,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            TextButton(onClick = onCancelInterview) {
                                Text("Dismiss", color = Slate400, fontSize = 11.sp)
                            }
                        }

                        Text(
                            text = currentQ.question,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Slate100,
                                fontSize = 13.sp
                            )
                        )

                        Text(
                            text = currentQ.rationale,
                            style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 11.sp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Multi-choice quick answer options
                        currentQ.options.forEach { option ->
                            Surface(
                                color = Slate900,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(0.6.dp, Slate700),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clickable { onAnswerInterview(option) }
                            ) {
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Cyan400,
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
                placeholder = { Text("Search facts, CAD specs, and guidelines...", color = Slate400, fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate400) },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall.copy(color = Slate100),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Cyan400,
                    unfocusedBorderColor = Slate700,
                    focusedContainerColor = Slate850,
                    unfocusedContainerColor = Slate850
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
                            selectedContainerColor = Slate700,
                            selectedLabelColor = Cyan400,
                            containerColor = Slate800,
                            labelColor = Slate400
                        ),
                        shape = RoundedCornerShape(12.dp)
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
                    MemoryItemCard(memory = memory)
                }
            }
        } else {
            // Skills Library Tab
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(skills, key = { it.id }) { skill ->
                    SkillItemCard(
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
        var newCat by remember { mutableStateOf(MemoryCategory.NOCVITA) }
        var newTags by remember { mutableStateOf("Manual, Note") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Learned Memory", color = Slate100, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newFact,
                        onValueChange = { newFact = it },
                        label = { Text("Fact / Rule / Preference", color = Slate400) },
                        textStyle = MaterialTheme.typography.bodySmall.copy(color = Slate100),
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newTags,
                        onValueChange = { newTags = it },
                        label = { Text("Tags (comma separated)", color = Slate400) },
                        textStyle = MaterialTheme.typography.bodySmall.copy(color = Slate100),
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
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan400)
                ) {
                    Text("Save Memory", color = Slate950, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate850
        )
    }
}

@Composable
fun MemoryItemCard(memory: MemoryItem) {
    Surface(
        color = Slate850,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, Slate700),
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
                        .clip(RoundedCornerShape(6.dp))
                        .background(Cyan400.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = memory.category.label,
                        color = Cyan400,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = Slate900,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${memory.confidence}% Confidence",
                        color = Emerald400,
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
                    color = Slate100,
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
                    style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 9.sp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    memory.tags.take(3).forEach { tag ->
                        Surface(
                            color = Slate900,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "#$tag",
                                color = Slate300,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SkillItemCard(
    skill: SkillItem,
    onTrigger: () -> Unit
) {
    Surface(
        color = Slate850,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Indigo500.copy(alpha = 0.5f)),
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
                        tint = Indigo400,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = skill.slug,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Indigo400,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Button(
                    onClick = onTrigger,
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo500),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Run Skill", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = skill.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Slate100,
                    fontSize = 14.sp
                )
            )

            Text(
                text = skill.summary,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Slate300,
                    fontSize = 11.sp
                ),
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = Slate900,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Trigger rule: ${skill.triggerRule}",
                        style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 10.sp)
                    )
                    Text(
                        text = "Tools: ${skill.requiredTools.joinToString(", ")}",
                        style = MaterialTheme.typography.labelSmall.copy(color = Cyan400, fontSize = 10.sp),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
