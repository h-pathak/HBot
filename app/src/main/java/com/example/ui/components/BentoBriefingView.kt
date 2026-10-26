package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BentoBriefing
import com.example.data.model.BentoPriorityItem
import com.example.ui.theme.*

@Composable
fun BentoBriefingView(
    bento: BentoBriefing,
    modifier: Modifier = Modifier
) {
    val theme = LocalCozyTheme.current

    Surface(
        color = theme.cardSurface,
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, theme.cardBorder),
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = bento.greeting,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = theme.textPrimary,
                            fontSize = 15.sp
                        )
                    )
                    Text(
                        text = bento.dateText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = theme.textSecondary,
                            fontSize = 11.sp
                        )
                    )
                }

                Surface(
                    color = theme.accentPrimary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "✨ Today's Focus",
                        color = theme.accentPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2x2 Bento Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BentoTile(
                    item = bento.schedulePriority,
                    accentColor = AtlasPrimary,
                    modifier = Modifier.weight(1f)
                )
                BentoTile(
                    item = bento.emailPriority,
                    accentColor = ReactorPrimary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BentoTile(
                    item = bento.grantLabPriority,
                    accentColor = HelixPrimary,
                    modifier = Modifier.weight(1f)
                )
                BentoTile(
                    item = bento.contentPriority,
                    accentColor = PulsePrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun BentoTile(
    item: BentoPriorityItem,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val theme = LocalCozyTheme.current

    Surface(
        color = theme.cardElevated,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, theme.cardBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = item.iconEmoji, fontSize = 16.sp)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.badge,
                        color = accentColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = theme.textPrimary,
                    fontSize = 11.sp
                ),
                maxLines = 1
            )

            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = theme.textSecondary,
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                ),
                maxLines = 2,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
