package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.example.data.model.VendorScorecard
import com.example.ui.theme.*

@Composable
fun VendorScorecardCard(
    scorecard: VendorScorecard,
    modifier: Modifier = Modifier
) {
    val theme = LocalCozyTheme.current

    Surface(
        color = theme.cardSurface,
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, ReactorPrimary.copy(alpha = 0.6f)),
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🫧 Vendor Comparison Scorecard",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = ReactorPrimary,
                            fontSize = 13.sp
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ReactorPrimary.copy(alpha = 0.15f))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Shortlisted Suppliers",
                        color = ReactorPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = scorecard.title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = theme.textPrimary,
                    fontSize = 12.sp
                ),
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = "${scorecard.component} · Budget: ${scorecard.budgetAllocation}",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = theme.textSecondary,
                    fontSize = 10.sp
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Horizontal scrolling comparison table
            Surface(
                color = theme.cardElevated,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(0.8.dp, theme.cardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(8.dp)
                ) {
                    // Header row
                    Row(
                        modifier = Modifier.padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Vendor", color = theme.accentPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(115.dp))
                        Text("Price (INR)", color = theme.accentPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(75.dp))
                        Text("Material Spec", color = theme.accentPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(140.dp))
                        Text("Discount", color = theme.accentPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(85.dp))
                        Text("Lead Time", color = theme.accentPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(70.dp))
                        Text("Dispatch", color = theme.accentPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(110.dp))
                    }

                    HorizontalDivider(color = theme.cardBorder, thickness = 0.5.dp)

                    // Data rows
                    scorecard.rows.forEach { row ->
                        Row(
                            modifier = Modifier
                                .padding(vertical = 5.dp)
                                .background(if (row.isRecommended) ReactorLight.copy(alpha = 0.35f) else Color.Transparent),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.width(115.dp)) {
                                Text(row.vendorName, color = theme.textPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                if (row.isRecommended) {
                                    Text("⭐ Best Match", color = ReactorPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(row.unitPriceINR, color = if (row.isRecommended) ReactorPrimary else theme.textPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(75.dp))
                            Text(row.materialSpec, color = theme.textSecondary, fontSize = 10.sp, modifier = Modifier.width(140.dp))
                            Text(row.studentDiscount, color = AtlasPrimary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(85.dp))
                            Text(row.leadTime, color = theme.textSecondary, fontSize = 10.sp, modifier = Modifier.width(70.dp))
                            Text(row.deliveryLocation, color = theme.textSecondary, fontSize = 10.sp, modifier = Modifier.width(110.dp))
                        }
                    }
                }
            }
        }
    }
}
