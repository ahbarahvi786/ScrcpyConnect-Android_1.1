package com.bloom404.scrcpyconnect.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.ScreenShare
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloom404.scrcpyconnect.ui.components.GuideContent
import com.bloom404.scrcpyconnect.ui.components.GuideTabSelector
import com.bloom404.scrcpyconnect.ui.components.NetworkInfoCard
import com.bloom404.scrcpyconnect.ui.components.SectionCard
import com.bloom404.scrcpyconnect.ui.components.SectionHeader
import com.bloom404.scrcpyconnect.ui.components.StatusCheckRow
import com.bloom404.scrcpyconnect.ui.theme.*
import com.bloom404.scrcpyconnect.viewmodel.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepObsidian)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            // ── App Header ──
            AppHeader()

            Spacer(Modifier.height(24.dp))

            // ── System Readiness Dashboard ──
            ReadinessDashboard(
                devOptionsEnabled = state.readiness.developerOptionsEnabled,
                usbDebuggingEnabled = state.readiness.usbDebuggingEnabled,
                wirelessDebuggingEnabled = state.readiness.wirelessDebuggingEnabled,
                wirelessDebuggingSupported = state.readiness.wirelessDebuggingSupported,
                context = context
            )

            Spacer(Modifier.height(16.dp))

            // ── Wi-Fi & IP Address Card ──
            NetworkInfoCard(
                networkInfo = state.networkInfo,
                copiedVisible = state.copiedToast,
                onCopyIp = {
                    copyToClipboard(context, state.networkInfo.ipAddress)
                    triggerHaptic(context)
                    viewModel.showCopiedToast()
                },
                onShareInfo = {
                    shareConnectionInfo(
                        context,
                        state.networkInfo.ipAddress,
                        state.networkInfo.ssid
                    )
                },
                onOpenWifiSettings = {
                    openWifiSettings(context)
                }
            )

            Spacer(Modifier.height(16.dp))

            // ── Setup Guides ──
            SectionCard {
                Column {
                    SectionHeader(
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(IndigoDim),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Rounded.Book,
                                    contentDescription = null,
                                    tint = Indigo,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        title = "Setup Guide",
                        subtitle = "Follow the steps for your connection type"
                    )

                    Spacer(Modifier.height(14.dp))

                    GuideTabSelector(
                        selectedTab = state.selectedGuide,
                        onTabSelected = { viewModel.selectGuide(it) }
                    )

                    Spacer(Modifier.height(14.dp))

                    GuideContent(
                        selectedTab = state.selectedGuide,
                        devOptionsEnabled = state.readiness.developerOptionsEnabled,
                        usbDebuggingEnabled = state.readiness.usbDebuggingEnabled,
                        ipAddress = state.networkInfo.ipAddress
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        // ── Footer Branding ──
        FooterBranding()
    }
}

@Composable
private fun AppHeader() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ElectricCyanDim),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.ScreenShare,
                contentDescription = null,
                tint = ElectricCyan,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column {
            Text(
                text = "ScrcpyConnect",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Prepare your phone for PC mirroring",
                fontSize = 12.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun ReadinessDashboard(
    devOptionsEnabled: Boolean,
    usbDebuggingEnabled: Boolean,
    wirelessDebuggingEnabled: Boolean,
    wirelessDebuggingSupported: Boolean,
    context: Context
) {
    SectionCard {
        Column {
            SectionHeader(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(IndigoDim),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.Security,
                            contentDescription = null,
                            tint = Indigo,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                title = "System Readiness",
                subtitle = "Live status • Auto-refreshes every 2s"
            )

            Spacer(Modifier.height(14.dp))

            // Developer Options
            StatusCheckRow(
                label = "Developer Options",
                isEnabled = devOptionsEnabled,
                subtitle = if (!devOptionsEnabled)
                    "Tap 'Build Number' 7 times in About Phone" else null,
                actionLabel = if (!devOptionsEnabled) "About Phone" else "Open",
                onAction = {
                    if (!devOptionsEnabled) {
                        openAboutPhone(context)
                    } else {
                        openDeveloperOptions(context)
                    }
                }
            )

            Spacer(Modifier.height(8.dp))

            // USB Debugging
            StatusCheckRow(
                label = "USB Debugging",
                isEnabled = usbDebuggingEnabled,
                subtitle = if (!usbDebuggingEnabled && devOptionsEnabled)
                    "Enable in Developer Options" else null,
                actionLabel = if (devOptionsEnabled) "Open" else null,
                onAction = if (devOptionsEnabled) {
                    { openDeveloperOptions(context) }
                } else null
            )

            Spacer(Modifier.height(8.dp))

            // Wireless Debugging
            if (wirelessDebuggingSupported) {
                StatusCheckRow(
                    label = "Wireless Debugging",
                    isEnabled = wirelessDebuggingEnabled,
                    subtitle = "Android 11+ • Required for cable-free mirroring",
                    actionLabel = if (devOptionsEnabled) "Open" else null,
                    onAction = if (devOptionsEnabled) {
                        { openDeveloperOptions(context) }
                    } else null
                )
            } else {
                StatusCheckRow(
                    label = "Wireless Debugging",
                    isEnabled = false,
                    subtitle = "Requires Android 11+ (API 30). Use Method A (USB) for wireless."
                )
            }
        }
    }
}

@Composable
private fun FooterBranding() {
    val infiniteTransition = rememberInfiniteTransition(label = "heartPulse")
    val heartAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MidnightSlate)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Made with",
                    fontSize = 12.sp,
                    color = TextMuted
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    Icons.Rounded.Favorite,
                    contentDescription = "love",
                    tint = StatusError,
                    modifier = Modifier
                        .size(14.dp)
                        .alpha(heartAlpha)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "by Bloom404",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ElectricCyan
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Developed by ©ABtechify",
                fontSize = 11.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Utility Functions ──

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("IP Address", text))
}

private fun triggerHaptic(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    } catch (_: Exception) { }
}

private fun shareConnectionInfo(context: Context, ip: String, ssid: String) {
    val text = buildString {
        appendLine("📱 ScrcpyConnect — Connection Info")
        appendLine("━━━━━━━━━━━━━━━━━━━━━")
        appendLine("Wi-Fi: $ssid")
        appendLine("IP Address: $ip")
        appendLine("Wireless ADB: $ip:5555")
        appendLine("━━━━━━━━━━━━━━━━━━━━━")
        appendLine("Enter the IP above in ScrcpyConnect PC app to start mirroring.")
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share Connection Info"))
}

private fun openAboutPhone(context: Context) {
    try {
        context.startActivity(
            Intent(Settings.ACTION_DEVICE_INFO_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    } catch (_: Exception) {
        context.startActivity(
            Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}

private fun openDeveloperOptions(context: Context) {
    try {
        context.startActivity(
            Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    } catch (_: Exception) {
        context.startActivity(
            Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}

private fun openWifiSettings(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.startActivity(
                Intent(Settings.Panel.ACTION_WIFI).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } else {
            context.startActivity(
                Intent(Settings.ACTION_WIFI_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    } catch (_: Exception) {
        context.startActivity(
            Intent(Settings.ACTION_WIRELESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}
