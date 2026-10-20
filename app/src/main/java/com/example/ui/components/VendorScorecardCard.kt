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
    Surface(
        color = Slate850,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ReactorColor.copy(alpha = 0.5f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📊 Vendor Comparison Scorecard",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = ReactorColor,
                        fontSize = 13.sp
                    )
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ReactorColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Vadodara / Gujarat",
                        color = ReactorColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = scorecard.title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Slate100,
                    fontSize = 12.sp
                ),
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = scorecard.component,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Slate400,
                    fontSize = 10.sp
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Horizontal scrolling comparison table
            Surface(
                color = Slate900,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(0.8.dp, Slate700)
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
                        Text("Vendor", color = Cyan400, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(110.dp))
                        Text("Price (INR)", color = Cyan400, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(75.dp))
                        Text("Material Grade", color = Cyan400, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(140.dp))
                        Text("Discount", color = Cyan400, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(85.dp))
                        Text("Lead Time", color = Cyan400, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(70.dp))
                        Text("Dispatch", color = Cyan400, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(110.dp))
                    }

                    HorizontalDivider(color = Slate700, thickness = 0.5.dp)

                    // Data rows
                    scorecard.rows.forEach { row ->
                        Row(
                            modifier = Modifier
                                .padding(vertical = 5.dp)
                                .background(if (row.isRecommended) Emerald500.copy(alpha = 0.1f) else Color.Transparent),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.width(110.dp)) {
                                Text(row.vendorName, color = Slate100, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                if (row.isRecommended) {
                                    Text("⭐ Best Match", color = Emerald400, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(row.unitPriceINR, color = if (row.isRecommended) Emerald400 else Slate300, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(75.dp))
                            Text(row.materialSpec, color = Slate300, fontSize = 10.sp, modifier = Modifier.width(140.dp))
                            Text(row.studentDiscount, color = Amber400, fontSize = 10.sp, modifier = Modifier.width(85.dp))
                            Text(row.leadTime, color = Slate300, fontSize = 10.sp, modifier = Modifier.width(70.dp))
                            Text(row.deliveryLocation, color = Slate300, fontSize = 10.sp, modifier = Modifier.width(110.dp))
                        }
                    }
                }
            }
        }
    }
}
