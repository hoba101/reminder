package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GirlyCardBorder
import com.example.ui.theme.GirlyCardSurface
import com.example.ui.theme.GirlyPinkContainer
import com.example.ui.theme.GirlyPinkDark
import com.example.ui.theme.GirlyPinkDeep
import com.example.ui.theme.GirlyPinkHighlight
import com.example.ui.theme.GirlyPinkPrimary
import com.example.ui.theme.GirlyTextPrimary
import com.example.ui.theme.GirlyTextSecondary
import com.example.ui.theme.GirlyTextTertiary

@Composable
fun BotanicalProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    barColor: Color = GirlyPinkPrimary,
    trackColor: Color = GirlyPinkContainer
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600),
        label = "progressBar"
    )

    Box(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .clip(RoundedCornerShape(height / 2))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .clip(RoundedCornerShape(height / 2))
                .background(barColor)
        )
    }
}

@Composable
fun BotanicalProgressRing(
    progress: Float,
    modifier: Modifier = Modifier.size(96.dp),
    strokeWidth: Dp = 8.dp,
    ringColor: Color = GirlyPinkPrimary,
    trackColor: Color = GirlyPinkContainer,
    centerContent: @Composable () -> Unit
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800),
        label = "progressRing"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val stroke = strokeWidth.toPx()
            // Track
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            // Progress arc
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        centerContent()
    }
}

@Composable
fun BotanicalCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(22.dp),
    backgroundColor: Color = GirlyCardSurface,
    borderColor: Color = GirlyCardBorder,
    borderWidth: Dp = 1.dp,
    elevation: Dp = 1.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .shadow(elevation, shape, spotColor = GirlyPinkPrimary.copy(alpha = 0.12f))
            .clip(shape)
            .border(borderWidth, borderColor, shape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = shape,
        color = backgroundColor
    ) {
        content()
    }
}

@Composable
fun StatusPill(
    isCompleted: Boolean,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val (bgColor, textColor, textLabel, iconVector) = if (isCompleted) {
        Quadruple(
            GirlyPinkContainer,
            GirlyPinkDeep,
            "مكتمل",
            Icons.Default.Check
        )
    } else {
        Quadruple(
            Color(0xFFFFF6F8),
            GirlyTextSecondary,
            "متبقي",
            Icons.Outlined.RadioButtonUnchecked
        )
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, if (isCompleted) GirlyPinkHighlight else GirlyCardBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = if (compact) 8.dp else 12.dp, vertical = if (compact) 4.dp else 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = iconVector,
            contentDescription = textLabel,
            tint = textColor,
            modifier = Modifier.size(if (compact) 12.dp else 14.dp)
        )
        Text(
            text = textLabel,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = if (compact) 10.sp else 12.sp,
                fontWeight = FontWeight.Bold
            ),
            color = textColor
        )
    }
}

@Composable
fun BotanicalFilterChip(
    selected: Boolean,
    label: String,
    count: Int? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (selected) GirlyPinkPrimary else GirlyCardSurface
    val textColor = if (selected) Color.White else GirlyTextSecondary
    val borderColor = if (selected) GirlyPinkDark else GirlyCardBorder

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = bgColor,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = if (selected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                ),
                color = textColor
            )
            if (count != null) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (selected) GirlyPinkDeep else GirlyPinkContainer)
                        .padding(horizontal = 7.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (selected) Color.White else GirlyPinkDeep
                    )
                }
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
