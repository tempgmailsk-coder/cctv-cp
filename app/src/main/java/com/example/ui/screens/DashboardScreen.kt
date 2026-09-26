package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Camera
import com.example.data.model.EventType
import com.example.data.model.SecurityEvent
import com.example.ui.components.SecurityBadge
import com.example.ui.components.StatusPill
import com.example.ui.theme.SecurityAlertRed
import com.example.ui.theme.SecurityNavyBorder
import com.example.ui.theme.SecurityNavyCard
import com.example.ui.theme.SecurityNavyCardElevated
import com.example.ui.theme.SecuritySafeGreen
import com.example.ui.theme.SecurityWarningAmber
import com.example.ui.theme.SentinelCyan
import com.example.ui.viewmodel.CctvViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: CctvViewModel,
    onNavigateToLive: () -> Unit,
    onNavigateToCameraSetup: () -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToDetectionSettings: () -> Unit,
    onNavigateToKnownPersons: () -> Unit,
    onPlayEvent: (SecurityEvent) -> Unit,
    onViewCamera: (Camera) -> Unit,
    modifier: Modifier = Modifier
) {
    val isArmed by viewModel.isSystemArmed.collectAsStateWithLifecycle()
    val cameras by viewModel.cameras.collectAsStateWithLifecycle()
    val recentEvents by viewModel.recentEvents.collectAsStateWithLifecycle()
    val allEvents by viewModel.allEvents.collectAsStateWithLifecycle()

    val onlineCamerasCount = cameras.count { it.isOnline }
    val armedCamerasCount = cameras.count { it.isArmed }
    val todayEventsCount = allEvents.size
    val unknownPersonsCount = allEvents.count { it.eventType == EventType.UNKNOWN_PERSON }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Master Security Armed Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isArmed) SentinelCyan.copy(alpha = 0.5f) else SecurityWarningAmber.copy(alpha = 0.5f),
                        RoundedCornerShape(16.dp)
                    )
                    .testTag("system_armed_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isArmed) Color(0xFF0F1E33) else Color(0xFF261914)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isArmed) SentinelCyan.copy(alpha = 0.2f) else SecurityWarningAmber.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isArmed) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = "Armed status",
                                tint = if (isArmed) SentinelCyan else SecurityWarningAmber,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isArmed) "SURVEILLANCE ARMED" else "SYSTEM DISARMED",
                                    color = if (isArmed) SentinelCyan else SecurityWarningAmber,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Text(
                                text = if (isArmed) "$armedCamerasCount of ${cameras.size} CP PLUS cameras active" else "Detection alerts paused",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    Switch(
                        checked = isArmed,
                        onCheckedChange = { viewModel.toggleSystemArmed() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SentinelCyan,
                            checkedTrackColor = Color(0xFF00384D),
                            uncheckedThumbColor = SecurityWarningAmber,
                            uncheckedTrackColor = Color(0xFF332014)
                        ),
                        modifier = Modifier.testTag("armed_toggle_switch")
                    )
                }
            }
        }

        // 4 KPI Summary Metric Tiles
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricTile(
                    title = "Cameras",
                    value = "$onlineCamerasCount/${cameras.size}",
                    subtitle = "Online",
                    color = SentinelCyan,
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    title = "Events",
                    value = "$todayEventsCount",
                    subtitle = "Total Clips",
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    title = "Unknown",
                    value = "$unknownPersonsCount",
                    subtitle = "Suspicious",
                    color = if (unknownPersonsCount > 0) SecurityAlertRed else SecuritySafeGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Test Triggers (Simulate CP PLUS SMD events for verification)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SecurityNavyCard)
                    .border(1.dp, SecurityNavyBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Real-Time Event Simulators",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Instant Test",
                        color = SentinelCyan,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "Trigger real-time person detection or unknown face event to verify push notifications and video buffering.",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.triggerTestEvent(EventType.PERSON_DETECTED) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("test_person_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("👤 Person", fontSize = 11.sp, maxLines = 1)
                    }

                    Button(
                        onClick = { viewModel.triggerTestEvent(EventType.UNKNOWN_PERSON) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("test_unknown_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = SecurityAlertRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("⚠️ Unknown", fontSize = 11.sp, maxLines = 1)
                    }

                    Button(
                        onClick = { viewModel.triggerTestEvent(EventType.ZONE_INTRUSION) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("test_intrusion_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("🚨 Intrusion", fontSize = 11.sp, maxLines = 1)
                    }
                }
            }
        }

        // Live Cameras Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Live CCTV Feeds",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(
                    onClick = onNavigateToLive,
                    modifier = Modifier.testTag("view_all_live_btn")
                ) {
                    Text("Multi-Grid View →", color = SentinelCyan, fontSize = 13.sp)
                }
            }
        }

        // Horizontal Carousel of Camera Preview Cards
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(cameras) { camera ->
                    CameraPreviewCard(
                        camera = camera,
                        onClick = {
                            viewModel.selectCamera(camera)
                            onViewCamera(camera)
                        }
                    )
                }
                item {
                    // Add Camera Card
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .aspectRatio(16f / 10f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SecurityNavyCard)
                            .border(1.dp, Color(0xFF263554), RoundedCornerShape(12.dp))
                            .clickable { onNavigateToCameraSetup() }
                            .testTag("dashboard_add_camera_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Camera",
                                tint = SentinelCyan,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Add Camera", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Recent Detections Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Security Events",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(
                    onClick = onNavigateToEvents,
                    modifier = Modifier.testTag("view_all_events_btn")
                ) {
                    Text("View History (${allEvents.size}) →", color = SentinelCyan, fontSize = 13.sp)
                }
            }
        }

        // Recent Detection Cards
        if (recentEvents.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No detection events recorded yet.\nArm the system to start monitoring.",
                        color = Color(0xFF64748B),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(recentEvents.take(4)) { event ->
                EventCard(
                    event = event,
                    onPlay = {
                        viewModel.selectEvent(event)
                        onPlayEvent(event)
                    },
                    onBookmark = { viewModel.toggleBookmark(event) }
                )
            }
        }
    }
}

@Composable
fun MetricTile(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SecurityNavyCard)
            .border(1.dp, SecurityNavyBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(text = title, color = Color(0xFF94A3B8), fontSize = 11.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = color, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = Color(0xFF64748B), fontSize = 10.sp)
        }
    }
}

@Composable
fun CameraPreviewCard(
    camera: Camera,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(200.dp)
            .aspectRatio(16f / 10f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, SecurityNavyBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("camera_preview_${camera.id}")
    ) {
        // Simulated Camera View Grid / Scanning Line
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusPill(isOnline = camera.isOnline)
                Text(
                    text = "CH-${camera.channel}",
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = "Watch live",
                    tint = SentinelCyan.copy(alpha = 0.8f),
                    modifier = Modifier.size(36.dp)
                )
            }

            Column {
                Text(
                    text = camera.name,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${camera.ipAddress}:${camera.rtspPort}",
                    color = Color(0xFF64748B),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun EventCard(
    event: SecurityEvent,
    onPlay: () -> Unit,
    onBookmark: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormat = SimpleDateFormat("hh:mm:ss a • dd MMM", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(event.timestamp))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, SecurityNavyBorder, RoundedCornerShape(12.dp))
            .clickable { onPlay() }
            .testTag("event_card_${event.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SecurityNavyCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Simulated Thumbnail with duration pill
            Box(
                modifier = Modifier
                    .size(width = 88.dp, height = 62.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = SentinelCyan,
                    modifier = Modifier.size(28.dp)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xCC000000))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "${event.clipDurationSeconds}s",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                SecurityBadge(eventType = event.eventType)

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = event.cameraName,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (event.personName != null) {
                    Text(
                        text = event.personName,
                        color = if (event.eventType == EventType.UNKNOWN_PERSON) SecurityAlertRed else SentinelCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = formattedTime,
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp
                )
            }

            IconButton(
                onClick = onBookmark,
                modifier = Modifier.testTag("bookmark_event_${event.id}")
            ) {
                Icon(
                    imageVector = if (event.isBookmarked) Icons.Default.CheckCircle else Icons.Default.History,
                    contentDescription = "Bookmark",
                    tint = if (event.isBookmarked) SentinelCyan else Color(0xFF64748B)
                )
            }
        }
    }
}
