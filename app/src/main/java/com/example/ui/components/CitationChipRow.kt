package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CitationChip
import com.example.ui.theme.*

@Composable
fun CitationChipRow(
    chips: List<CitationChip>,
    modifier: Modifier = Modifier
) {
    if (chips.isEmpty()) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        chips.forEach { chip ->
            Surface(
                color = Slate800,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(0.6.dp, Slate700)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val icon: ImageVector = when (chip.category.lowercase()) {
                        "sheet" -> Icons.Default.TableChart
                        "doc", "mail" -> Icons.Default.Description
                        "drive" -> Icons.Default.Folder
                        else -> Icons.Default.Language
                    }

                    Icon(
                        imageVector = icon,
                        contentDescription = chip.category,
                        tint = Cyan400,
                        modifier = Modifier.size(12.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = chip.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate300,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}
