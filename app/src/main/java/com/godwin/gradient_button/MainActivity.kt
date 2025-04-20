package com.godwin.gradient_button

import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.SweepGradient
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godwin.gradient_button.ui.theme.Gradient_buttonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Gradient_buttonTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        GradientAnimatedButton(
                            icon = Icons.Filled.Star,
                            label = "Button",
                            labelPressedStyle = TextStyle(
                                fontWeight = FontWeight.Bold, fontSize = 30.sp
                            ),
                            iconSize = 30.dp, borderRadius = 30.dp,
                            labelStyle = TextStyle(fontSize = 30.sp),
                            iconPressedColor = Color.Yellow,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GradientAnimatedButton(
    animationDuration: Int = 2000,
    borderRadius: Dp = 20.dp,
    buttonColor: Color = Color(0xE61E1637),
    animatingColors: List<Color> = listOf(Color(0xFFcc00ff), Color(0xFF4d00ff), Color(0xFF00b2ff)),
    padding: Dp = 10.dp,
    strokeWidth: Dp = 2.dp,
    onClick: () -> Unit = {},
    labelStyle: TextStyle = TextStyle(
        fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Medium
    ),
    label: String? = null,
    icon: ImageVector? = null,
    labelPressedStyle: TextStyle = labelStyle,
    iconColor: Color = Color.White,
    iconPressedColor: Color = Color.Yellow,
    iconSize: Dp = 20.dp,
) {
    var isPressed by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "Angle animation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "Angle animation"
    )
    Box(
        modifier = Modifier.clip(RoundedCornerShape(borderRadius))
    ) {
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            val center = Offset(size.width / 2, size.height / 2)

            val androidColors = mutableListOf(
                buttonColor,
                *animatingColors.toTypedArray(),
                buttonColor, buttonColor,
            ).map { it.toArgb() }.toIntArray()

            val sweepGradient = SweepGradient(
                center.x, center.y, androidColors, null
            )

            val matrix = Matrix().apply {
                preRotate(angle, center.x, center.y)
            }
            sweepGradient.setLocalMatrix(matrix)

            val paint = Paint().asFrameworkPaint().apply {
                isAntiAlias = true
                shader = sweepGradient
            }

            drawContext.canvas.nativeCanvas.drawRect(
                RectF(0f, 0f, size.width, size.height), paint
            )
        }
        Box(
            modifier = Modifier.padding(if (isPressed) 0.dp else strokeWidth)
        ) {
            Box(modifier = Modifier
                .clickable {
                    onClick()
                    isPressed = !isPressed
                }
                .background(
                    color = buttonColor.copy(alpha = if (isPressed) 0.9f else 1f),
                    shape = RoundedCornerShape(borderRadius)
                )
                .padding(padding), contentAlignment = Alignment.Center) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isPressed) iconPressedColor else iconColor,
                            modifier = Modifier.size(iconSize)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    if (label != null) {
                        Text(
                            text = label, style = if (isPressed) labelPressedStyle else labelStyle
                        )
                    }
                }
            }
        }
    }
}
