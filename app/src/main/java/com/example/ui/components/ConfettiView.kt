package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.ui.theme.*
import kotlin.random.Random

data class ConfettiParticle(
    val startX: Float,
    val startY: Float,
    val velocityX: Float,
    val velocityY: Float,
    val color: Color,
    val size: Float,
    val rotationSpeed: Float
)

@Composable
fun ConfettiView(
    trigger: Long, // timestamp or counter
    modifier: Modifier = Modifier
) {
    if (trigger == 0L) return

    val particles = remember(trigger) {
        val colors = listOf(
            AtlasPrimary, ReactorPrimary, HelixPrimary, PulsePrimary, ForgePrimary,
            BioluminescentAqua, BioluminescentAmber, PastelLavender, PastelMint
        )
        List(40) {
            val angle = Random.nextFloat() * Math.PI.toFloat() * 2f
            val speed = Random.nextFloat() * 450f + 250f
            ConfettiParticle(
                startX = 0.5f,
                startY = 0.45f,
                velocityX = kotlin.math.cos(angle) * speed,
                velocityY = kotlin.math.sin(angle) * speed - 150f,
                color = colors[Random.nextInt(colors.size)],
                size = Random.nextFloat() * 10f + 8f,
                rotationSpeed = Random.nextFloat() * 360f - 180f
            )
        }
    }

    val progress = remember(trigger) { Animatable(0f) }

    LaunchedEffect(trigger) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500, easing = LinearOutSlowInEasing)
        )
    }

    if (progress.value < 1f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val t = progress.value
            val alpha = (1f - t).coerceIn(0f, 1f)
            val gravity = 600f * t * t

            particles.forEach { p ->
                val px = size.width * p.startX + p.velocityX * t
                val py = size.height * p.startY + p.velocityY * t + gravity
                rotate(p.rotationSpeed * t, Offset(px, py)) {
                    drawRect(
                        color = p.color.copy(alpha = alpha),
                        topLeft = Offset(px - p.size / 2, py - p.size / 2),
                        size = androidx.compose.ui.geometry.Size(p.size, p.size * 0.6f)
                    )
                }
            }
        }
    }
}
