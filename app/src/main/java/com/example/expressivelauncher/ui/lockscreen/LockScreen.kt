package com.example.expressivelauncher.ui.lockscreen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.expressivelauncher.data.LockScreenPrefs
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LockScreen(onDismiss: () -> Unit, onCamera: () -> Unit) {
    val context = LocalContext.current
    val prefs by LockScreenPrefs.state.collectAsState()
    val accent = Color(prefs.accent)

    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = System.currentTimeMillis()
            delay(1000)
        }
    }

    val wallpaper = remember {
        runCatching {
            val d = android.app.WallpaperManager.getInstance(context).drawable
                ?: error("no wallpaper")
            d.toBitmap().asImageBitmap()
        }.getOrNull()
    }

    BackHandler(onBack = onDismiss)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1220))
            .pointerInput(onDismiss) {
                detectVerticalDragGestures { _, dy ->
                    if (dy < -24f) onDismiss()
                }
            }
    ) {
        if (wallpaper != null) {
            Image(
                wallpaper,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().blur(22.dp),
            )
        } else {
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364)))
                )
            )
        }
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(Color.Transparent, Color.Black.copy(0.20f), Color.Black.copy(0.72f))
                )
            )
        )

        Text(
            "Демонстрационный экран · биометрия не используется",
            color = Color.White.copy(0.5f),
            fontSize = 11.sp,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 26.dp),
        )

        Column(
            Modifier.align(Alignment.Center).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ClockFace(prefs.clock, accent, prefs.seconds, now)
        }

        Column(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(Modifier.fillMaxWidth().height(132.dp)) {
                Fingerprint(
                    accent = accent,
                    onCamera = onCamera,
                    modifier = Modifier.align(Alignment.Center),
                )
                FilledTonalIconButton(
                    onClick = onCamera,
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 32.dp),
                ) { Icon(Icons.Rounded.PhotoCamera, contentDescription = "Камера") }
                FilledTonalIconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 32.dp),
                ) { Icon(Icons.Rounded.ArrowForward, contentDescription = "Открыть стол") }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Нажмите отпечаток — камера · Свайп вверх — рабочий стол",
                color = Color.White.copy(0.62f),
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun ClockFace(style: Int, accent: Color, seconds: Boolean, now: Long) {
    val time = SimpleDateFormat(if (seconds) "HH:mm:ss" else "HH:mm", Locale.getDefault()).format(Date(now))
    val rawDate = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date(now))
    val date = rawDate.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    when (style) {
        0 -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(date, color = Color.White.copy(0.78f), fontSize = 14.sp, letterSpacing = 2.sp)
            Text(
                time, color = Color.White, fontSize = 86.sp,
                fontWeight = FontWeight.Light, letterSpacing = (-3).sp,
            )
            Box(Modifier.size(56.dp, 3.dp).clip(RoundedCornerShape(2.dp)).background(accent))
        }

        1 -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                time, color = Color.White, fontSize = 76.sp,
                fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
            )
            Text(date, color = accent, fontSize = 15.sp)
        }

        else -> Surface(
            shape = RoundedCornerShape(30.dp),
            color = Color.White.copy(0.12f),
        ) {
            Column(
                Modifier.padding(horizontal = 34.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(time, color = Color.White, fontSize = 62.sp, fontWeight = FontWeight.Medium)
                Text(date, color = accent, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun Fingerprint(accent: Color, onCamera: () -> Unit, modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "fingerprint")
    val pulse by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.07f,
        animationSpec = infiniteRepeatable(tween(1700, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse",
    )
    var scanning by remember { mutableStateOf(false) }
    LaunchedEffect(scanning) {
        if (scanning) {
            delay(420)
            scanning = false
            onCamera()
        }
    }
    val scale by animateFloatAsState(
        targetValue = if (scanning) 0.92f else pulse,
        animationSpec = tween(200),
        label = "scale",
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier
                .size(118.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(Color.White.copy(0.10f))
                .clickable(interactionSource = null, indication = null) { scanning = true },
            contentAlignment = Alignment.Center,
        ) {
            Canvas(Modifier.size(96.dp)) {
                val c = center
                for (i in 0 until 5) {
                    val r = (42 - i * 7).dp.toPx()
                    drawArc(
                        color = accent.copy(alpha = 0.95f - i * 0.13f),
                        startAngle = 200f,
                        sweepAngle = 140f,
                        useCenter = false,
                        topLeft = Offset(c.x - r, c.y - r),
                        size = Size(r * 2, r * 2),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
                    )
                }
                drawCircle(color = accent, radius = 3.dp.toPx(), center = c)
            }
            if (scanning) {
                Box(Modifier.matchParentSize().clip(CircleShape).background(accent.copy(alpha = 0.18f)))
            }
        }
        Spacer(Modifier.height(6.dp))
        Text("Отпечаток", color = Color.White.copy(0.72f), fontSize = 12.sp)
    }
}
