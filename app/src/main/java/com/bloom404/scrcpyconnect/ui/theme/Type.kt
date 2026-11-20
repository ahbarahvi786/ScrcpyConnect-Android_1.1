package com.bloom404.scrcpyconnect.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val MonoFamily = FontFamily.Monospace

val AppTypography = Typography(
    headlineMedium = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
    ),
    titleMedium = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary
    ),
    bodyMedium = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
        color = TextSecondary
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        color = TextMuted
    ),
    labelLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary
    ),
    labelMedium = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = TextSecondary
    )
)
