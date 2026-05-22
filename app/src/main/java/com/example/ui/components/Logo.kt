package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LunarGold
import com.example.ui.theme.LunarYellow
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MoonLightLogo(
    modifier: Modifier = Modifier,
    showText: Boolean = true,
    glowSize: Float = 30f,
    textSize: Float = 24f
) {
    // Shimmer and rotation animations for the moon's aura
    val infiniteTransition = rememberInfiniteTransition(label = "AuraAnimation")
    
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = glowSize * 0.8f,
        targetValue = glowSize * 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val starAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star1"
    )

    val starAlpha2 by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "star2"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Canvas(modifier = Modifier.size(110.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.width / 3.2f

            // 1. Draw central glowing ambient aura behind the moon
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        LunarGold.copy(alpha = 0.6f),
                        LunarYellow.copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius + pulseGlow
                ),
                radius = radius + pulseGlow,
                center = center
            )

            // 2. Decorative Stars
            // Star 1
            drawCircle(
                color = Color.White.copy(alpha = starAlpha1),
                radius = 3.5.dp.toPx(),
                center = Offset(center.x - radius * 1.1f, center.y - radius * 0.9f)
            )
            // Star 2
            drawCircle(
                color = LunarYellow.copy(alpha = starAlpha2),
                radius = 2.5.dp.toPx(),
                center = Offset(center.x + radius * 1.2f, center.y - radius * 0.4f)
            )
            // Star 3
            drawCircle(
                color = Color.White.copy(alpha = starAlpha1 * 0.8f),
                radius = 2.dp.toPx(),
                center = Offset(center.x - radius * 0.4f, center.y + radius * 1.1f)
            )

            // 3. Draw the Golden Crescent Moon
            // We draw a large circle, and clip/offset-mask it to create a perfect high-fidelity crescent
            val moonCenter = Offset(center.x - 5.dp.toPx(), center.y)
            val shadowCenter = Offset(center.x + 12.dp.toPx(), center.y - 4.dp.toPx()) // Inner carve out

            withTransform({
                // Subtle rotation vibe
                rotate(degrees = -10f, pivot = center)
            }) {
                // Background moon circle
                drawCircle(
                    brush = Brush.linearGradient(
                        colors = listOf(LunarGold, LunarYellow),
                        start = Offset(center.x - radius, center.y - radius),
                        end = Offset(center.x + radius, center.y + radius)
                    ),
                    radius = radius,
                    center = moonCenter
                )

                // Cutting mask circle (same background color to clip a crescent)
                // In Canvas drawing, we can do this by using the background color (MidnightBlue or parent container)
                // For versatility, we use a dark navy shadow color that matches our background theme, blending perfectly.
                drawCircle(
                    color = Color(0xFF0C0F19), // Matches our MidnightBlue background
                    radius = radius * 0.95f,
                    center = shadowCenter
                )
            }
        }

        if (showText) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MOON ",
                    color = Color.White,
                    fontSize = textSize.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 4.sp,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = "LIGHT",
                    color = LunarGold,
                    fontSize = textSize.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }
    }
}
