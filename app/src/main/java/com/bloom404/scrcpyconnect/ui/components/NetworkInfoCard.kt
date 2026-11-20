package com.bloom404.scrcpyconnect.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.SignalWifiOff
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.WifiFind
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloom404.scrcpyconnect.ui.theme.*
import com.bloom404.scrcpyconnect.viewmodel.NetworkInfo

@Composable
fun NetworkInfoCard(
    networkInfo: NetworkInfo,
    copiedVisible: Boolean,
    onCopyIp: () -> Unit,
    onShareInfo: () -> Unit,
    onOpenWifiSettings: () -> Unit
) {
    SectionCard {
        Column {
            SectionHeader(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ElectricCyanDim),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (networkInfo.isWifiConnected)
                                Icons.Rounded.Wifi else Icons.Rounded.WifiFind,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                title = "Wi-Fi & IP Address",
                subtitle = "Required for wireless mirroring"
            )

            Spacer(Modifier.height(14.dp))

            if (networkInfo.isWifiConnected && networkInfo.hasIp) {
                // Connected state
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(StatusReadyDim)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(StatusReady)
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Connected to ${networkInfo.ssid}",
                            fontSize = 12.sp,
                            color = StatusReady,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // IP Address display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceLight)
                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Your IP Address",
                            fontSize = 11.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = networkInfo.ipAddress,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = MonoFamily,
                            color = ElectricCyan,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onCopyIp,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricCyan,
                            contentColor = DeepObsidian
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Rounded.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Copy IP",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }

                            // "Copied!" overlay
                            AnimatedVisibility(
                                visible = copiedVisible,
                                enter = scaleIn(tween(200)) + fadeIn(tween(200)),
                                exit = scaleOut(tween(300)) + fadeOut(tween(300))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(StatusReady)
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                        tint = DeepObsidian,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = "Copied!",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepObsidian
                                    )
                                }
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = onShareInfo,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                            width = 1.dp
                        )
                    ) {
                        Icon(
                            Icons.Rounded.Share,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Share Info",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = ElectricCyan
                        )
                    }
                }

            } else {
                // Disconnected state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(StatusWarningDim)
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Rounded.SignalWifiOff,
                                contentDescription = null,
                                tint = StatusWarning,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "Wi-Fi Not Connected",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StatusWarning
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Connect to the same Wi-Fi network as your Windows PC to use wireless mirroring.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = onOpenWifiSettings,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StatusWarning,
                                contentColor = DeepObsidian
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Wifi,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Open Wi-Fi Settings",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
