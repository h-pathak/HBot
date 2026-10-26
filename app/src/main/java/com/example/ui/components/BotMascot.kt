package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BotStatus
import com.example.ui.theme.*
import kotlin.math.*

enum class MascotState {
    IDLE,
    LISTENING,
    THINKING,
    WORKING,
    WAITING_APPROVAL,
    CELEBRATING
}

fun BotStatus.toMascotState(): MascotState = when (this) {
    BotStatus.IDLE -> MascotState.IDLE
    BotStatus.THINKING -> MascotState.THINKING
    BotStatus.WORKING -> MascotState.WORKING
    BotStatus.WAITING_APPROVAL -> MascotState.WAITING_APPROVAL
    BotStatus.BLOCKED -> MascotState.WAITING_APPROVAL
    BotStatus.DONE -> MascotState.CELEBRATING
}

@Composable
fun BotMascot(
    botId: String,
    state: MascotState = MascotState.IDLE,
    size: Dp = 56.dp,
    showActionPill: Boolean = false,
    actionText: String? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mascot_anim_$botId")

    // 1. Idle Bobbing & Floating
    val bobOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (state == MascotState.WORKING) 450 else 2200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )

    // 2. Breathing Scale
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )

    // 3. Periodic Eye Blink (0 = open, 1 = shut)
    val blinkProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 4000
                0.0f at 0
                0.0f at 3700
                1.0f at 3850
                0.0f at 4000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "blink"
    )

    // 4. Orbit rotation for Sparkles & Halo
    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit"
    )

    // 5. Rising bubbles for Reactor
    val bubbleFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bubble"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(size)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = this.size.width
                val h = this.size.height
                val cx = w / 2f
                val cy = h / 2f + bobOffset

                when (botId.lowercase()) {
                    "atlas", "chief-of-staff" -> {
                        drawAtlasMascot(cx, cy, w, breathScale, blinkProgress, orbitAngle, state)
                    }
                    "reactor", "nocvita-ops" -> {
                        drawReactorMascot(cx, cy, w, breathScale, blinkProgress, bubbleFloat, state)
                    }
                    "helix", "bio-researcher" -> {
                        drawHelixMascot(cx, cy, w, breathScale, blinkProgress, orbitAngle, state)
                    }
                    "pulse", "content-studio" -> {
                        drawPulseMascot(cx, cy, w, breathScale, blinkProgress, bobOffset, state)
                    }
                    "forge", "antigravity-coder" -> {
                        drawForgeMascot(cx, cy, w, breathScale, blinkProgress, orbitAngle, state)
                    }
                    else -> {
                        // Command Center living sphere
                        drawCommandCenterMascot(cx, cy, w, breathScale, orbitAngle, state)
                    }
                }
            }

            // Waiting Approval indicator badge
            if (state == MascotState.WAITING_APPROVAL) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-2).dp)
                ) {
                    Surface(
                        color = StatusApproval,
                        shape = RoundedCornerShape(10.dp),
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            text = "✋ High-Five!",
                            color = DeepEspressoText,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Action Tool Badge Pill
        if (showActionPill && !actionText.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = when (state) {
                    MascotState.WORKING -> ReactorLight
                    MascotState.WAITING_APPROVAL -> AtlasLight
                    MascotState.THINKING -> HelixLight
                    else -> OatmilkBorderLight
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = actionText,
                    color = when (state) {
                        MascotState.WORKING -> ReactorDark
                        MascotState.WAITING_APPROVAL -> AtlasDark
                        MascotState.THINKING -> HelixDark
                        else -> WarmStoneText
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

// ==========================================
// 1. ATLAS (Star / Compass Sprite + Glasses + Halo)
// ==========================================
private fun DrawScope.drawAtlasMascot(
    cx: Float, cy: Float, size: Float,
    scale: Float, blink: Float, orbit: Float, state: MascotState
) {
    val r = size * 0.34f * scale

    // Floating Halo
    val haloY = cy - r * 1.25f
    drawOval(
        color = AtlasPrimary.copy(alpha = 0.85f),
        topLeft = Offset(cx - r * 0.75f, haloY - r * 0.2f),
        size = Size(r * 1.5f, r * 0.4f),
        style = Stroke(width = 3.dp.toPx())
    )

    // Sparkles when thinking or listening
    if (state == MascotState.THINKING || state == MascotState.LISTENING) {
        val rad = Math.toRadians(orbit.toDouble())
        val sx = cx + (r * 1.4f * cos(rad)).toFloat()
        val sy = cy + (r * 1.4f * sin(rad)).toFloat()
        drawCircle(color = AtlasPrimary, radius = 3.dp.toPx(), center = Offset(sx, sy))
    }

    // Star Body (soft 4-point compass star)
    val starPath = Path().apply {
        moveTo(cx, cy - r * 1.15f)
        quadraticTo(cx + r * 0.35f, cy - r * 0.35f, cx + r * 1.15f, cy)
        quadraticTo(cx + r * 0.35f, cy + r * 0.35f, cx, cy + r * 1.15f)
        quadraticTo(cx - r * 0.35f, cy + r * 0.35f, cx - r * 1.15f, cy)
        quadraticTo(cx - r * 0.35f, cy - r * 0.35f, cx, cy - r * 1.15f)
        close()
    }
    drawPath(starPath, brush = Brush.radialGradient(listOf(AtlasLight, AtlasPrimary), Offset(cx, cy), r * 1.2f))

    // Round Glasses Frame
    val glassR = r * 0.28f
    val glassDist = r * 0.35f
    drawCircle(color = DeepEspressoText, radius = glassR, center = Offset(cx - glassDist, cy - r * 0.05f), style = Stroke(2.5.dp.toPx()))
    drawCircle(color = DeepEspressoText, radius = glassR, center = Offset(cx + glassDist, cy - r * 0.05f), style = Stroke(2.5.dp.toPx()))
    drawLine(color = DeepEspressoText, start = Offset(cx - glassDist + glassR, cy - r * 0.05f), end = Offset(cx + glassDist - glassR, cy - r * 0.05f), strokeWidth = 2.5.dp.toPx())

    // Eyes
    drawMascotEyes(cx, cy - r * 0.05f, glassDist, glassR * 0.6f, blink, state)

    // Smile
    drawSmile(cx, cy + r * 0.35f, r * 0.25f, state)
}

// ==========================================
// 2. REACTOR (Bioluminescent Erlenmeyer Flask PBR + Glowing Bubbles)
// ==========================================
private fun DrawScope.drawReactorMascot(
    cx: Float, cy: Float, size: Float,
    scale: Float, blink: Float, bubbleFloat: Float, state: MascotState
) {
    val r = size * 0.36f * scale

    // Flask Glass Body Path
    val neckW = r * 0.35f
    val neckH = r * 0.5f
    val baseW = r * 1.1f
    val baseH = r * 0.95f

    val flaskPath = Path().apply {
        // Neck rim
        moveTo(cx - neckW, cy - baseH * 0.9f)
        lineTo(cx + neckW, cy - baseH * 0.9f)
        // Down neck
        lineTo(cx + neckW * 0.7f, cy - baseH * 0.4f)
        // Sloped flask body
        lineTo(cx + baseW, cy + baseH)
        // Rounded bottom
        quadraticTo(cx, cy + baseH * 1.15f, cx - baseW, cy + baseH)
        lineTo(cx - neckW * 0.7f, cy - baseH * 0.4f)
        close()
    }

    // Outer Glass Glow
    drawPath(
        flaskPath,
        brush = Brush.verticalGradient(listOf(ReactorLight.copy(alpha = 0.4f), ReactorPrimary.copy(alpha = 0.9f)))
    )
    drawPath(flaskPath, color = ReactorPrimary, style = Stroke(2.5.dp.toPx()))

    // Rising glowing microalgae bubbles inside
    val b1y = (cy + baseH * 0.7f) - (bubbleFloat * baseH * 0.8f)
    val b2y = (cy + baseH * 0.9f) - (((bubbleFloat + 0.5f) % 1f) * baseH * 0.9f)
    drawCircle(color = Color.White.copy(alpha = 0.85f), radius = 2.5.dp.toPx(), center = Offset(cx - r * 0.3f, b1y))
    drawCircle(color = ReactorLight, radius = 3.5.dp.toPx(), center = Offset(cx + r * 0.25f, b2y))

    // Mascot Eyes
    drawMascotEyes(cx, cy + baseH * 0.15f, r * 0.32f, r * 0.18f, blink, state)

    // Cute Cheeks
    drawCircle(color = ReactorDark.copy(alpha = 0.3f), radius = 3.dp.toPx(), center = Offset(cx - r * 0.55f, cy + baseH * 0.35f))
    drawCircle(color = ReactorDark.copy(alpha = 0.3f), radius = 3.dp.toPx(), center = Offset(cx + r * 0.55f, cy + baseH * 0.35f))

    // Smile
    drawSmile(cx, cy + baseH * 0.38f, r * 0.2f, state)
}

// ==========================================
// 3. HELIX (Pastel Violet Microbe / Double-Helix + Molecular Dots)
// ==========================================
private fun DrawScope.drawHelixMascot(
    cx: Float, cy: Float, size: Float,
    scale: Float, blink: Float, orbit: Float, state: MascotState
) {
    val r = size * 0.35f * scale

    // Orbiting molecular dots (DNA sync)
    val rad1 = Math.toRadians(orbit.toDouble())
    val rad2 = Math.toRadians((orbit + 180).toDouble())
    val dot1 = Offset(cx + (r * 1.25f * cos(rad1)).toFloat(), cy + (r * 0.6f * sin(rad1)).toFloat())
    val dot2 = Offset(cx + (r * 1.25f * cos(rad2)).toFloat(), cy + (r * 0.6f * sin(rad2)).toFloat())
    drawCircle(color = HelixPrimary, radius = 4.dp.toPx(), center = dot1)
    drawCircle(color = BioluminescentAqua, radius = 4.dp.toPx(), center = dot2)

    // Soft rounded bean / microbe body
    drawRoundRect(
        brush = Brush.radialGradient(listOf(HelixLight, HelixPrimary), Offset(cx, cy), r * 1.2f),
        topLeft = Offset(cx - r, cy - r * 0.95f),
        size = Size(r * 2f, r * 1.9f),
        cornerRadius = CornerRadius(r * 0.85f, r * 0.85f)
    )

    // Tiny Lab Badge on Chest
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(cx - r * 0.45f, cy + r * 0.45f),
        size = Size(r * 0.9f, r * 0.4f),
        cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
    )
    drawCircle(color = HelixPrimary, radius = 2.dp.toPx(), center = Offset(cx - r * 0.2f, cy + r * 0.65f))

    // Eyes
    drawMascotEyes(cx, cy - r * 0.15f, r * 0.35f, r * 0.17f, blink, state)

    // Smile
    drawSmile(cx, cy + r * 0.2f, r * 0.22f, state)
}

// ==========================================
// 4. PULSE (Coral Cloud Mascot Wearing Headphones)
// ==========================================
private fun DrawScope.drawPulseMascot(
    cx: Float, cy: Float, size: Float,
    scale: Float, blink: Float, bob: Float, state: MascotState
) {
    val r = size * 0.35f * scale

    // Fluffy cloud body (cluster of soft circles)
    drawCircle(color = PulseLight, radius = r * 0.7f, center = Offset(cx - r * 0.45f, cy + r * 0.1f))
    drawCircle(color = PulseLight, radius = r * 0.7f, center = Offset(cx + r * 0.45f, cy + r * 0.1f))
    drawCircle(color = PulsePrimary, radius = r * 0.85f, center = Offset(cx, cy))
    drawCircle(color = PulsePrimary.copy(alpha = 0.9f), radius = r * 0.65f, center = Offset(cx, cy + r * 0.2f))

    // Headphones band over head
    drawArc(
        color = DeepEspressoText,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(cx - r * 0.95f, cy - r * 1.15f),
        size = Size(r * 1.9f, r * 1.8f),
        style = Stroke(3.dp.toPx())
    )

    // Headphone ear pads
    drawRoundRect(
        color = PulseDark,
        topLeft = Offset(cx - r * 1.15f, cy - r * 0.3f),
        size = Size(r * 0.35f, r * 0.65f),
        cornerRadius = CornerRadius(r * 0.15f, r * 0.15f)
    )
    drawRoundRect(
        color = PulseDark,
        topLeft = Offset(cx + r * 0.8f, cy - r * 0.3f),
        size = Size(r * 0.35f, r * 0.65f),
        cornerRadius = CornerRadius(r * 0.15f, r * 0.15f)
    )

    // Eyes
    drawMascotEyes(cx, cy - r * 0.05f, r * 0.32f, r * 0.18f, blink, state)

    // Musical mouth / Smile
    drawSmile(cx, cy + r * 0.28f, r * 0.2f, state)
}

// ==========================================
// 5. FORGE (Sky-Cyan Robot Cube + Pixel Eyes + Tiny Wrench)
// ==========================================
private fun DrawScope.drawForgeMascot(
    cx: Float, cy: Float, size: Float,
    scale: Float, blink: Float, orbit: Float, state: MascotState
) {
    val r = size * 0.34f * scale

    // Antenna on top
    drawLine(color = ForgeDark, start = Offset(cx, cy - r), end = Offset(cx, cy - r * 1.35f), strokeWidth = 2.5.dp.toPx())
    drawCircle(color = BioluminescentAqua, radius = 3.5.dp.toPx(), center = Offset(cx, cy - r * 1.35f))

    // Rounded Cube Head
    drawRoundRect(
        brush = Brush.verticalGradient(listOf(ForgeLight, ForgePrimary)),
        topLeft = Offset(cx - r, cy - r * 0.9f),
        size = Size(r * 2f, r * 1.8f),
        cornerRadius = CornerRadius(r * 0.35f, r * 0.35f)
    )
    drawRoundRect(
        color = ForgeDark,
        topLeft = Offset(cx - r, cy - r * 0.9f),
        size = Size(r * 2f, r * 1.8f),
        cornerRadius = CornerRadius(r * 0.35f, r * 0.35f),
        style = Stroke(2.5.dp.toPx())
    )

    // Screen visor
    drawRoundRect(
        color = DeepEspressoText,
        topLeft = Offset(cx - r * 0.75f, cy - r * 0.55f),
        size = Size(r * 1.5f, r * 0.8f),
        cornerRadius = CornerRadius(r * 0.2f, r * 0.2f)
    )

    // Pixel Eyes (Cyan glowing squares)
    val eyeSize = r * 0.22f
    val eyeY = cy - r * 0.35f
    if (state == MascotState.CELEBRATING) {
        // Happy arch ^ ^
        drawLine(color = BioluminescentAqua, start = Offset(cx - r * 0.45f, eyeY + eyeSize * 0.5f), end = Offset(cx - r * 0.3f, eyeY - eyeSize * 0.3f), strokeWidth = 2.5.dp.toPx())
        drawLine(color = BioluminescentAqua, start = Offset(cx - r * 0.3f, eyeY - eyeSize * 0.3f), end = Offset(cx - r * 0.15f, eyeY + eyeSize * 0.5f), strokeWidth = 2.5.dp.toPx())
        drawLine(color = BioluminescentAqua, start = Offset(cx + r * 0.15f, eyeY + eyeSize * 0.5f), end = Offset(cx + r * 0.3f, eyeY - eyeSize * 0.3f), strokeWidth = 2.5.dp.toPx())
        drawLine(color = BioluminescentAqua, start = Offset(cx + r * 0.3f, eyeY - eyeSize * 0.3f), end = Offset(cx + r * 0.45f, eyeY + eyeSize * 0.5f), strokeWidth = 2.5.dp.toPx())
    } else if (blink > 0.6f) {
        // Blink dash
        drawLine(color = BioluminescentAqua, start = Offset(cx - r * 0.45f, eyeY), end = Offset(cx - r * 0.15f, eyeY), strokeWidth = 2.dp.toPx())
        drawLine(color = BioluminescentAqua, start = Offset(cx + r * 0.15f, eyeY), end = Offset(cx + r * 0.45f, eyeY), strokeWidth = 2.dp.toPx())
    } else {
        drawRect(color = BioluminescentAqua, topLeft = Offset(cx - r * 0.45f, eyeY - eyeSize / 2), size = Size(eyeSize, eyeSize))
        drawRect(color = BioluminescentAqua, topLeft = Offset(cx + r * 0.25f, eyeY - eyeSize / 2), size = Size(eyeSize, eyeSize))
    }

    // Holographic wrench indicator
    if (state == MascotState.WORKING) {
        rotate(orbit * 0.5f, Offset(cx + r * 1.1f, cy + r * 0.4f)) {
            drawLine(color = ForgeDark, start = Offset(cx + r * 0.9f, cy + r * 0.4f), end = Offset(cx + r * 1.3f, cy + r * 0.4f), strokeWidth = 3.dp.toPx())
        }
    }
}

// ==========================================
// 6. COMMAND CENTER (Living Glowing Synergy Orb)
// ==========================================
private fun DrawScope.drawCommandCenterMascot(
    cx: Float, cy: Float, size: Float,
    scale: Float, orbit: Float, state: MascotState
) {
    val r = size * 0.36f * scale

    // Quad-Color gradient aura
    drawCircle(
        brush = Brush.sweepGradient(
            listOf(AtlasPrimary, ReactorPrimary, HelixPrimary, PulsePrimary, AtlasPrimary)
        ),
        radius = r,
        center = Offset(cx, cy)
    )

    // Inner Core
    drawCircle(color = OatmilkSurface, radius = r * 0.75f, center = Offset(cx, cy))

    // Pulse rings
    drawCircle(
        color = BioluminescentAqua,
        radius = r * 0.45f,
        center = Offset(cx, cy),
        style = Stroke(3.dp.toPx())
    )
}

// ==========================================
// EYE & MOUTH DRAWING HELPERS
// ==========================================
private fun DrawScope.drawMascotEyes(
    cx: Float, cy: Float, dist: Float, r: Float, blink: Float, state: MascotState
) {
    if (state == MascotState.CELEBRATING) {
        // Happy curved arcs ^ ^
        drawArc(color = DeepEspressoText, startAngle = 180f, sweepAngle = 180f, useCenter = false, topLeft = Offset(cx - dist - r, cy - r), size = Size(r * 2, r * 1.5f), style = Stroke(2.5.dp.toPx()))
        drawArc(color = DeepEspressoText, startAngle = 180f, sweepAngle = 180f, useCenter = false, topLeft = Offset(cx + dist - r, cy - r), size = Size(r * 2, r * 1.5f), style = Stroke(2.5.dp.toPx()))
    } else if (blink > 0.6f) {
        // Blink flat lines
        drawLine(color = DeepEspressoText, start = Offset(cx - dist - r * 0.8f, cy), end = Offset(cx - dist + r * 0.8f, cy), strokeWidth = 2.5.dp.toPx())
        drawLine(color = DeepEspressoText, start = Offset(cx + dist - r * 0.8f, cy), end = Offset(cx + dist + r * 0.8f, cy), strokeWidth = 2.5.dp.toPx())
    } else {
        // Round animated eyes
        val eyeH = r * (1f - blink * 0.8f)
        val gazeOffset = if (state == MascotState.THINKING) Offset(0f, -r * 0.35f) else Offset.Zero

        // Left eye
        drawOval(color = DeepEspressoText, topLeft = Offset(cx - dist - r, cy - eyeH + gazeOffset.y), size = Size(r * 2, eyeH * 2))
        // Eye catchlight
        drawCircle(color = Color.White, radius = r * 0.35f, center = Offset(cx - dist - r * 0.3f, cy - eyeH * 0.3f + gazeOffset.y))

        // Right eye
        drawOval(color = DeepEspressoText, topLeft = Offset(cx + dist - r, cy - eyeH + gazeOffset.y), size = Size(r * 2, eyeH * 2))
        drawCircle(color = Color.White, radius = r * 0.35f, center = Offset(cx + dist - r * 0.3f, cy - eyeH * 0.3f + gazeOffset.y))
    }
}

private fun DrawScope.drawSmile(cx: Float, cy: Float, r: Float, state: MascotState) {
    if (state == MascotState.CELEBRATING || state == MascotState.WORKING) {
        // Open wide happy mouth
        drawArc(
            color = DeepEspressoText,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(cx - r, cy - r * 0.5f),
            size = Size(r * 2, r * 1.5f)
        )
    } else {
        // Gentle curved smile
        drawArc(
            color = DeepEspressoText,
            startAngle = 15f,
            sweepAngle = 150f,
            useCenter = false,
            topLeft = Offset(cx - r, cy - r * 0.6f),
            size = Size(r * 2, r * 1.2f),
            style = Stroke(2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
