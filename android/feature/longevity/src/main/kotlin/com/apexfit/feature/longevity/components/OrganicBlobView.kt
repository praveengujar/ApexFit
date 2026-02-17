package com.apexfit.feature.longevity.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apexfit.core.designsystem.theme.LongevityGreen
import com.apexfit.core.designsystem.theme.LongevityOrange
import com.apexfit.core.designsystem.theme.TextPrimary
import com.apexfit.core.designsystem.theme.TextSecondary
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun OrganicBlobView(
    apexFitAge: Double,
    yearsYoungerOlder: Double,
    size: Dp = 260.dp,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "blob")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "blobPhase",
    )

    val blobColor = if (yearsYoungerOlder < 0) LongevityGreen else LongevityOrange

    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val baseRadius = this.size.minDimension / 2f

            // Outer glow
            drawBlobPath(
                center = center,
                baseRadius = baseRadius * 0.55f,
                harmonics = 5,
                amplitude = 0.10f,
                phase = phase,
                brush = Brush.radialGradient(
                    colors = listOf(
                        blobColor.copy(alpha = 0.3f),
                        Color.Transparent,
                    ),
                    center = center,
                    radius = baseRadius * 0.55f,
                ),
            )

            // Main blob
            drawBlobPath(
                center = center,
                baseRadius = baseRadius * 0.48f,
                harmonics = 5,
                amplitude = 0.10f,
                phase = phase,
                brush = Brush.radialGradient(
                    colors = listOf(
                        blobColor.copy(alpha = 0.15f),
                        blobColor.copy(alpha = 0.6f),
                        blobColor.copy(alpha = 0.8f),
                        blobColor.copy(alpha = 0.3f),
                        Color.Transparent,
                    ),
                    center = center,
                    radius = baseRadius * 0.48f,
                ),
            )

            // Particles
            drawParticles(center, baseRadius * 0.45f, phase, blobColor)
        }

        // Center text
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = String.format("%.1f", apexFitAge),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
            )
            Text(
                text = "APEXFIT AGE",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                letterSpacing = 1.5.sp,
            )
            if (abs(yearsYoungerOlder) > 0.1) {
                Spacer(Modifier.height(2.dp))
                val absYears = abs(yearsYoungerOlder)
                val suffix = if (yearsYoungerOlder < 0) "years younger" else "years older"
                Text(
                    text = String.format("%.1f %s", absYears, suffix),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (yearsYoungerOlder < 0) LongevityGreen else LongevityOrange,
                )
            }
        }
    }
}

private fun DrawScope.drawBlobPath(
    center: Offset,
    baseRadius: Float,
    harmonics: Int,
    amplitude: Float,
    phase: Float,
    brush: Brush,
) {
    val path = Path()
    val steps = 120

    for (i in 0..steps) {
        val angle = (i.toFloat() / steps) * 2f * Math.PI.toFloat()
        var r = baseRadius

        for (k in 1..harmonics) {
            val freq = k.toFloat()
            val phaseShift = phase * (0.3f + freq * 0.15f)
            val amp = amplitude / freq
            r += baseRadius * amp * sin(freq * angle + phaseShift)
        }

        val x = center.x + r * cos(angle)
        val y = center.y + r * sin(angle)

        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    path.close()
    drawPath(path, brush)
}

private fun DrawScope.drawParticles(
    center: Offset,
    radius: Float,
    phase: Float,
    color: Color,
) {
    for (i in 0 until 30) {
        val seed = i * 137.5
        val angle = ((seed + phase * 20.0) % 360.0).toFloat()
        val r = radius * (0.2f + 0.7f * abs(sin((seed * 0.1 + phase).toFloat())))
        val x = center.x + r * cos(Math.toRadians(angle.toDouble())).toFloat()
        val y = center.y + r * sin(Math.toRadians(angle.toDouble())).toFloat()
        val particleSize = 2f + 3f * abs(sin((seed * 0.3 + phase * 2).toFloat()))
        val opacity = 0.3f + 0.5f * abs(sin((seed * 0.2 + phase * 3).toFloat()))

        drawCircle(
            color = color.copy(alpha = opacity),
            radius = particleSize,
            center = Offset(x, y),
        )
    }
}
