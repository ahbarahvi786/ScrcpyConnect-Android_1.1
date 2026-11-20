package com.bloom404.scrcpyconnect.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloom404.scrcpyconnect.ui.theme.*

@Composable
fun StatusCheckRow(
    label: String,
    isEnabled: Boolean,
    subtitle: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    val dotColor by animateColorAsState(
        targetValue = if (isEnabled) StatusReady else StatusError,
        animationSpec = tween(500),
        label = "dotColor"
    )

    val bgColor by animateColorAsState(
        targetValue = if (isEnabled) StatusReadyDim else StatusErrorDim,
        animationSpec = tween(500),
        label = "bgColor"
    )

    val checkScale by animateFloatAsState(
        targetValue = if (isEnabled) 1f else 0.8f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "checkScale"
    )

    // Pulsing dot animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pulsing dot
        Box(
            modifier = Modifier
                .size(10.dp)
                .alpha(if (isEnabled) 1f else pulseAlpha)
                .clip(CircleShape)
                .background(dotColor)
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 14.sp
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        // Status badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isEnabled) StatusReadyDim else StatusErrorDim)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = if (isEnabled) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
                contentDescription = null,
                tint = dotColor,
                modifier = Modifier
                    .size(16.dp)
                    .scale(checkScale)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = if (isEnabled) "Enabled" else "Disabled",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = dotColor
            )
        }

        // Action button
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.width(6.dp))
            TextButton(onClick = onAction) {
                Icon(
                    Icons.Rounded.OpenInNew,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = actionLabel,
                    fontSize = 11.sp,
                    color = ElectricCyan,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .background(MidnightSlate)
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun SectionHeader(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        icon()
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }
    }
}
