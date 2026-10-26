package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SkillLearnedCard
import com.example.ui.theme.*

@Composable
fun SkillLearnedCard(
    skill: SkillLearnedCard,
    modifier: Modifier = Modifier
) {
    val theme = LocalCozyTheme.current

    Surface(
        color = theme.cardSurface,
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, AtlasPrimary.copy(alpha = 0.6f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = AtlasPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "✨ New Skill Collected!",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AtlasPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Surface(
                    color = AtlasLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = skill.savedAt,
                        color = AtlasDark,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
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
                        text = "Trigger: ${skill.triggerPattern}",
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
