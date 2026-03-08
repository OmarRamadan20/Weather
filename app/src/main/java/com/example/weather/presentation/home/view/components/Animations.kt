package com.example.weather.presentation.home.view.components

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun RainAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "random_rain")

    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rain_progress"
    )

    val rainDrops = remember {
        ArrayList<Triple<Float, Float, Float>>().apply {
            repeat(70) {
                add(
                    Triple(
                        Random.nextInt(0, 1000).toFloat(),
                        Random.nextInt(0, 1000).toFloat(),
                        Random.nextFloat() * (1.5f - 0.5f) + 0.5f
                    )
                )
            }
        }
    }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .alpha(0.45f)) {
        rainDrops.forEach { (xRatio, yOffsetRatio, speedMultiplier) ->
            val x = (xRatio / 1000f) * size.width
            val currentProgress = (progress * speedMultiplier + yOffsetRatio / 1000f) % 1f
            val y = currentProgress * size.height

            drawLine(
                color = Color.White,
                start = Offset(x, y),
                end = Offset(x, y + 45f),
                strokeWidth = 5f,
                cap = StrokeCap.Round
            )
        }
    }
}
@Composable
fun CloudAnimation() {
    val transition = rememberInfiniteTransition(label = "clouds")

    val fastOffset by transition.animateFloat(
        initialValue = -200f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "fastCloud"
    )

    val cloudPulse by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        CloudShapePro(
            size = 280.dp,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = fastOffset.dp, y = (-120).dp)
                .scale(cloudPulse)
                .alpha(0.6f)
        )

        CloudShapePro(
            size = 180.dp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-fastOffset / 2).dp, y = 80.dp)
                .scale(cloudPulse * 0.8f)
                .alpha(0.4f)
        )
    }
}

@Composable
fun CloudShapePro(size: Dp, modifier: Modifier = Modifier) {
    val cloudColorInner = Color(0xFFE0E0E0)
    val cloudColorOuter = Color(0xFFBDBDBD)

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(cloudColorOuter.copy(alpha = 0.5f), Color.Transparent)
                    ), CircleShape
                )
                .blur(40.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxSize(0.7f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(cloudColorInner.copy(alpha = 0.8f), Color.Transparent)
                    ), CircleShape
                )
                .blur(20.dp)
        )
    }
}
@Composable
fun SunnyAnimation(temp: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "pro_sun")

    val isExtremelyHot = temp > 28
    val rotationDuration = if (isExtremelyHot) 10000 else 20000
    val glowIntensity = if (isExtremelyHot) 0.8f else 0.4f

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(rotationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isExtremelyHot) 1.5f else 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isExtremelyHot) 2000 else 4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .size(if (isExtremelyHot) 500.dp else 400.dp)
                .align(Alignment.TopEnd)
                .offset(x = 100.dp, y = (-100).dp)
                .graphicsLayer(rotationZ = rotation, alpha = if (isExtremelyHot) 0.3f else 0.15f)
        ) {
            val rayCount = if (isExtremelyHot) 12 else 8
            val angleStep = 360f / rayCount
            for (i in 0 until rayCount) {
                drawArc(
                    color = Color.White,
                    startAngle = i * angleStep,
                    sweepAngle = if (isExtremelyHot) 20f else 15f,
                    useCenter = true,
                    size = size
                )
            }
        }

        Box(
            modifier = Modifier
                .size(if (isExtremelyHot) 300.dp else 250.dp)
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = (-50).dp)
                .graphicsLayer(scaleX = scale, scaleY = scale)
                .background(
                    Brush.radialGradient(
                        0.0f to Color.White.copy(alpha = glowIntensity),
                        0.6f to (if (isExtremelyHot) Color(0xFFFF7043) else Color(0xFFFFE082)).copy(
                            alpha = 0.3f
                        ),
                        1.0f to Color.Transparent
                    ),
                    CircleShape
                )
                .blur(if (isExtremelyHot) 80.dp else 60.dp)
        )
    }
}


@Composable
fun SnowAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "random_snow")

    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "snow_progress"
    )

    val wobble by infiniteTransition.animateFloat(
        initialValue = -25f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "snow_wobble"
    )

    val snowFlakes = remember {
        ArrayList<List<Float>>().apply {
            repeat(50) {
                add(
                    listOf(
                        Random.nextFloat(),
                        Random.nextFloat(),
                        Random.nextFloat() * 0.3f + 0.2f,
                        Random.nextFloat() * 7f + 5f
                    )
                )
            }
        }
    }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .alpha(0.8f)) {
        val width = size.width
        val height = size.height

        snowFlakes.forEach { flake ->
            val x = (flake[0] * width) + wobble
            val y = ((progress * flake[2] + flake[1]) % 1f) * height

            drawCircle(
                color = Color.White,
                radius = flake[3],
                center = Offset(x, y)
            )
        }
    }
}