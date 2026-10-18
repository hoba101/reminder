package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FactCheck
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Biotech
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.Diamond
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GirlyPinkPrimary

object BotanicalIcons {

    fun getIconForSubject(iconKey: String): ImageVector {
        return when (iconKey) {
            "chemistry" -> Icons.Outlined.Science
            "minerals" -> Icons.Outlined.Diamond
            "math" -> Icons.Outlined.Calculate
            "physics" -> Icons.Outlined.Speed
            "zoology" -> Icons.Outlined.Biotech
            "geology" -> Icons.Outlined.Public
            "english" -> Icons.AutoMirrored.Outlined.MenuBook
            "computer" -> Icons.Outlined.Computer
            "quality" -> Icons.AutoMirrored.Outlined.FactCheck
            else -> Icons.Outlined.Eco
        }
    }
}

/**
 * A delicate girly cherry blossom flower line vector icon.
 * Pure vector canvas art - absolutely no emojis.
 */
@Composable
fun GirlyFlowerLineIcon(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = GirlyPinkPrimary,
    strokeWidth: Dp = 1.6.dp
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val radius = w * 0.15f

        // Center Pistil
        drawCircle(
            color = tint,
            radius = radius * 0.55f,
            center = Offset(cx, cy),
            style = Stroke(width = strokeWidth.toPx())
        )

        // 5 graceful rounded petals
        val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        for (i in 0 until 5) {
            val angle = Math.toRadians((i * 72.0) - 90.0)
            val px = cx + (radius * 1.55f * Math.cos(angle)).toFloat()
            val py = cy + (radius * 1.55f * Math.sin(angle)).toFloat()
            drawCircle(
                color = tint,
                radius = radius * 0.72f,
                center = Offset(px, py),
                style = stroke
            )
        }
    }
}

/**
 * A cute aesthetic sparkling star line vector.
 */
@Composable
fun GirlySparkleLineIcon(
    modifier: Modifier = Modifier.size(18.dp),
    tint: Color = GirlyPinkPrimary,
    strokeWidth: Dp = 1.5.dp
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        val path = Path().apply {
            moveTo(cx, 0f)
            quadraticTo(cx, cy, w, cy)
            quadraticTo(cx, cy, cx, h)
            quadraticTo(cx, cy, 0f, cy)
            quadraticTo(cx, cy, cx, 0f)
            close()
        }

        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

/**
 * A delicate minimalist line heart vector.
 */
@Composable
fun GirlyHeartLineIcon(
    modifier: Modifier = Modifier.size(20.dp),
    tint: Color = GirlyPinkPrimary,
    strokeWidth: Dp = 1.6.dp
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.82f)
            cubicTo(
                w * 0.15f, h * 0.55f,
                0f, h * 0.35f,
                0f, h * 0.22f
            )
            cubicTo(
                0f, h * 0.08f,
                w * 0.18f, 0f,
                w * 0.35f, 0f
            )
            cubicTo(
                w * 0.44f, 0f,
                w * 0.5f, h * 0.08f,
                w * 0.5f, h * 0.15f
            )
            cubicTo(
                w * 0.5f, h * 0.08f,
                w * 0.56f, 0f,
                w * 0.65f, 0f
            )
            cubicTo(
                w * 0.82f, 0f,
                w, h * 0.08f,
                w, h * 0.22f
            )
            cubicTo(
                w, h * 0.35f,
                w * 0.85f, h * 0.55f,
                w * 0.5f, h * 0.82f
            )
            close()
        }

        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun BotanicalSproutLineIcon(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = GirlyPinkPrimary,
    strokeWidth: Dp = 1.75.dp
) {
    GirlyFlowerLineIcon(modifier = modifier, tint = tint, strokeWidth = strokeWidth)
}

/**
 * A gentle minimalist decorative divider for girly cards.
 */
@Composable
fun BotanicalBranchDivider(
    modifier: Modifier = Modifier,
    tint: Color = GirlyPinkPrimary.copy(alpha = 0.35f)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val midY = h / 2f

        drawLine(
            color = tint,
            start = Offset(0f, midY),
            end = Offset(w, midY),
            strokeWidth = 1.dp.toPx(),
            cap = StrokeCap.Round
        )

        val step = w / 5f
        for (i in 1..4) {
            val cx = i * step
            val isUpper = i % 2 == 1
            val petalHeight = if (isUpper) -h * 0.35f else h * 0.35f

            val petal = Path().apply {
                moveTo(cx, midY)
                quadraticTo(cx + 6.dp.toPx(), midY + petalHeight * 1.2f, cx + 12.dp.toPx(), midY + petalHeight)
                quadraticTo(cx + 6.dp.toPx(), midY, cx, midY)
            }
            drawPath(petal, color = tint, style = Stroke(width = 1.2.dp.toPx()))
        }
    }
}
