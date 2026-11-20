package com.bloom404.scrcpyconnect.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.Cable
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Router
import androidx.compose.material.icons.rounded.SettingsRemote
import androidx.compose.material.icons.rounded.Usb
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloom404.scrcpyconnect.ui.theme.*
import com.bloom404.scrcpyconnect.viewmodel.GuideTab

@Composable
fun GuideTabSelector(
    selectedTab: GuideTab,
    onTabSelected: (GuideTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MidnightSlate)
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        GuideTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            val bgColor = if (isSelected) ElectricCyan else Color.Transparent
            val textColor = if (isSelected) DeepObsidian else TextSecondary

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(bgColor)
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (tab == GuideTab.WIRED) Icons.Rounded.Usb else Icons.Rounded.Wifi,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (tab == GuideTab.WIRED) "Wired (USB)" else "Wireless (Wi-Fi)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun GuideContent(
    selectedTab: GuideTab,
    devOptionsEnabled: Boolean,
    usbDebuggingEnabled: Boolean,
    ipAddress: String
) {
    AnimatedContent(
        targetState = selectedTab,
        transitionSpec = {
            val direction = if (targetState == GuideTab.WIRELESS) 1 else -1
            (slideInHorizontally(tween(350)) { direction * it / 3 } +
                    fadeIn(tween(350)))
                .togetherWith(
                    slideOutHorizontally(tween(350)) { -direction * it / 3 } +
                            fadeOut(tween(350))
                )
        },
        label = "guideTransition"
    ) { tab ->
        when (tab) {
            GuideTab.WIRED -> WiredGuide(devOptionsEnabled, usbDebuggingEnabled)
            GuideTab.WIRELESS -> WirelessGuide(ipAddress)
        }
    }
}

@Composable
private fun WiredGuide(
    devOptionsEnabled: Boolean,
    usbDebuggingEnabled: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        GuideStepCard(
            stepNumber = 1,
            icon = Icons.Rounded.SettingsRemote,
            iconBg = IndigoDim,
            iconTint = Indigo,
            title = "Enable Developer Options & USB Debugging",
            description = "Open Settings → About Phone → tap 'Build Number' 7 times. Then enable USB Debugging in Developer Options.",
            isComplete = devOptionsEnabled && usbDebuggingEnabled
        )

        GuideStepCard(
            stepNumber = 2,
            icon = Icons.Rounded.Cable,
            iconBg = ElectricCyanDim,
            iconTint = ElectricCyan,
            title = "Connect via USB Data Cable",
            description = "Use a high-quality USB data cable (not a charge-only cable). Connect your phone to your PC.",
            warning = "⚠ Charge-only cables won't work — ensure your cable supports data transfer."
        )

        GuideStepCard(
            stepNumber = 3,
            icon = Icons.Rounded.PhoneAndroid,
            iconBg = StatusWarningDim,
            iconTint = StatusWarning,
            title = "Set USB Mode & Allow Debugging",
            description = "If prompted, set USB mode to 'File Transfer' or 'Android Auto'. Then tap 'Allow' on the 'Allow USB debugging?' RSA key dialog on your phone."
        )

        GuideStepCard(
            stepNumber = 4,
            icon = Icons.Rounded.CheckCircle,
            iconBg = StatusReadyDim,
            iconTint = StatusReady,
            title = "Start Wired Mirroring",
            description = "In the ScrcpyConnect PC app, click 'Start Wired Mirroring'. Your phone screen will appear on your PC!"
        )
    }
}

@Composable
private fun WirelessGuide(ipAddress: String) {
    val displayIp = ipAddress.ifBlank { "<your_ip>" }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        GuideStepCard(
            stepNumber = 1,
            icon = Icons.Rounded.Router,
            iconBg = IndigoDim,
            iconTint = Indigo,
            title = "Same Wi-Fi Network",
            description = "Ensure your phone and PC are connected to the exact same Wi-Fi router/subnet."
        )

        GuideStepCard(
            stepNumber = 2,
            icon = Icons.Rounded.Usb,
            iconBg = ElectricCyanDim,
            iconTint = ElectricCyan,
            title = "Method A — Easiest (Requires USB Briefly)",
            description = "Connect your phone via USB, click 'Enable Wireless Mode' in the PC app, then unplug the USB cable. The wireless connection persists until reboot.",
            tag = "Recommended"
        )

        GuideStepCard(
            stepNumber = 3,
            icon = Icons.Rounded.Wifi,
            iconBg = StatusWarningDim,
            iconTint = StatusWarning,
            title = "Method B — Cable-Free (Android 11+)",
            description = "Open Developer Options → Wireless Debugging → enable it. Note the IP address & Port shown. If pairing for the first time, use the 6-digit pairing code and enter it in the PC app.",
            tag = "Cable-Free"
        )

        GuideStepCard(
            stepNumber = 4,
            icon = Icons.Rounded.CheckCircle,
            iconBg = StatusReadyDim,
            iconTint = StatusReady,
            title = "Connect & Mirror",
            description = "Copy your IP address ($displayIp:5555) into the PC app's connection field and click 'Start Wireless Mirroring'."
        )
    }
}

@Composable
private fun GuideStepCard(
    stepNumber: Int,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    description: String,
    isComplete: Boolean = false,
    warning: String? = null,
    tag: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = 1.dp,
                color = if (isComplete) StatusReady.copy(alpha = 0.3f) else CardBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .background(if (isComplete) StatusReadyDim else MidnightSlate)
            .padding(14.dp)
    ) {
        Row {
            // Step number circle
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isComplete) StatusReady else iconBg),
                contentAlignment = Alignment.Center
            ) {
                if (isComplete) {
                    Icon(
                        Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = DeepObsidian,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "$stepNumber",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = iconTint
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isComplete) StatusReady else TextPrimary,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (tag != null) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ElectricCyanDim)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricCyan
                            )
                        }
                    }

                    if (isComplete) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(StatusReadyDim)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "✓ Done",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusReady
                            )
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )

                if (warning != null) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(StatusWarningDim)
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Rounded.Info,
                            contentDescription = null,
                            tint = StatusWarning,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = warning,
                            fontSize = 11.sp,
                            color = StatusWarning,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
