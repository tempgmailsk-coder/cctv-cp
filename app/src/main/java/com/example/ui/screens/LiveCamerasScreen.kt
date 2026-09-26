package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.cpplus.CpPlusModelPreset
import com.example.data.model.Camera
import com.example.ui.components.StatusPill
import com.example.ui.theme.SecurityAlertRed
import com.example.ui.theme.SecurityNavyBorder
import com.example.ui.theme.SecurityNavyCard
import com.example.ui.theme.SecurityNavyDark
import com.example.ui.theme.SecuritySafeGreen
import com.example.ui.theme.SentinelCyan
import com.example.ui.viewmodel.CctvViewModel
import kotlinx.coroutines.delay

@Composable
fun LiveCamerasScreen(
    viewModel: CctvViewModel,
    onConfigureCamera: (Camera) -> Unit,
    modifier: Modifier = Modifier
) {
    val cameras by viewModel.cameras.collectAsStateWithLifecycle()
    val selectedCamera by viewModel.selectedCamera.collectAsStateWithLifecycle()

    var isGridView by remember { mutableStateOf(false) }
    var isAudioMuted by remember { mutableStateOf(true) }
    var isRecordingManual by remember { mutableStateOf(false) }
    var streamQuality by remember { mutableStateOf("SUB") } // SUB (smooth) vs MAIN (HD)

    // Current camera to focus on
    val activeCamera = selectedCamera ?: cameras.firstOrNull()

    // Simulated live timestamp counter
    var liveSeconds by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            liveSeconds++
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SecurityNavyDark)
            .padding(16.dp)
    ) {
        // Top Toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isGridView) "Multi-Camera Grid" else (activeCamera?.name ?: "Live CCTV"),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isGridView) "${cameras.size} Channels Synced" else "${activeCamera?.ipAddress ?: ""} • RTSP CH-${activeCamera?.channel ?: 1}",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Quality toggle (SUB vs MAIN)
                if (!isGridView) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SecurityNavyCard)
                            .border(1.dp, SecurityNavyBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                streamQuality = if (streamQuality == "SUB") "MAIN" else "SUB"
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("stream_quality_toggle")
                    ) {
                        Text(
                            text = if (streamQuality == "MAIN") "HD (Main)" else "SD (Sub)",
                            color = SentinelCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Grid / Single View Toggle
                IconButton(
                    onClick = { isGridView = !isGridView },
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SecurityNavyCard)
                        .border(1.dp, SecurityNavyBorder, RoundedCornerShape(8.dp))
                        .size(36.dp)
                        .testTag("grid_view_toggle")
                ) {
                    Icon(
                        imageVector = if (isGridView) Icons.Default.Fullscreen else Icons.Default.GridView,
                        contentDescription = "Toggle Grid View",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isGridView) {
            // 2x2 Grid View
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(cameras) { camera ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 11f)
                            .border(
                                1.dp,
                                if (camera.id == activeCamera?.id) SentinelCyan else SecurityNavyBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                viewModel.selectCamera(camera)
                                isGridView = false
                            }
                            .testTag("grid_camera_${camera.id}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B132B))
                    ) {
                        Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatusPill(isOnline = camera.isOnline)
                                Text("CH${camera.channel}", color = Color(0xFF64748B), fontSize = 10.sp)
                            }

                            Box(modifier = Modifier.align(Alignment.Center)) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = "Camera stream",
                                    tint = SentinelCyan.copy(alpha = 0.5f),
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Text(
                                text = camera.name,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.align(Alignment.BottomStart)
                            )
                        }
                    }
                }
            }
        } else {
            // Single Camera View (Video player viewport + controls + PTZ)
            if (activeCamera != null) {
                // Live Viewport
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF030712))
                        .border(1.5.dp, SentinelCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .testTag("live_camera_viewport")
                ) {
                    // Simulated HUD Overlay
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StatusPill(isOnline = activeCamera.isOnline)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "25.0 FPS • 2048 Kbps",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            }
                            Text(
                                text = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date()),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Center AI Person Detection Box Simulator
                        if (activeCamera.personOnlyFilter) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .size(width = 110.dp, height = 90.dp)
                                    .border(1.dp, SentinelCyan, RoundedCornerShape(4.dp))
                                    .background(SentinelCyan.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.TopStart
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(bottomEnd = 4.dp))
                                        .background(SentinelCyan)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "SMD: Person 94%",
                                        color = Color.Black,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Bottom Viewport Info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CP PLUS ${activeCamera.modelPreset} • $streamQuality",
                                color = Color(0xFF64748B),
                                fontSize = 10.sp
                            )
                            if (isRecordingManual) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(SecurityAlertRed)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("REC", color = SecurityAlertRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Bar (Snapshot, Manual Clip Record, Audio, Deterrent)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ActionButton(
                        icon = Icons.Default.CameraAlt,
                        label = "Snapshot",
                        onClick = {
                            viewModel.triggerTestEvent(com.example.data.model.EventType.PERSON_DETECTED, activeCamera)
                        },
                        modifier = Modifier.weight(1f),
                        tag = "action_snapshot"
                    )

                    ActionButton(
                        icon = if (isRecordingManual) Icons.Default.RadioButtonChecked else Icons.Default.Videocam,
                        label = if (isRecordingManual) "Stop REC" else "Record Clip",
                        onClick = {
                            isRecordingManual = !isRecordingManual
                        },
                        tint = if (isRecordingManual) SecurityAlertRed else Color.White,
                        modifier = Modifier.weight(1f),
                        tag = "action_record"
                    )

                    ActionButton(
                        icon = if (isAudioMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                        label = if (isAudioMuted) "Unmute" else "Listening",
                        onClick = { isAudioMuted = !isAudioMuted },
                        tint = if (isAudioMuted) Color.White else SentinelCyan,
                        modifier = Modifier.weight(1f),
                        tag = "action_audio"
                    )

                    ActionButton(
                        icon = Icons.Default.Tune,
                        label = "Config",
                        onClick = { onConfigureCamera(activeCamera) },
                        modifier = Modifier.weight(1f),
                        tag = "action_config"
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // PTZ (Pan / Tilt / Zoom) Controller Pad
                val preset = CpPlusModelPreset.fromId(activeCamera.modelPreset)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SecurityNavyBorder, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SecurityNavyCard)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PTZ Controls (${if (preset.supportsPtz) "Active" else "Simulated"})",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Speed: 5",
                                color = Color(0xFF64748B),
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Directional D-Pad
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Zoom In/Out
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(
                                    onClick = { viewModel.sendPtz("ZoomIn") },
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E293B))
                                        .size(42.dp)
                                        .testTag("ptz_zoom_in")
                                ) {
                                    Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = SentinelCyan)
                                }
                                IconButton(
                                    onClick = { viewModel.sendPtz("ZoomOut") },
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E293B))
                                        .size(42.dp)
                                        .testTag("ptz_zoom_out")
                                ) {
                                    Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = SentinelCyan)
                                }
                            }

                            // D-Pad Cross
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                IconButton(
                                    onClick = { viewModel.sendPtz("Up") },
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color(0xFF1E293B))
                                        .size(44.dp)
                                        .testTag("ptz_up")
                                ) {
                                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Pan Up", tint = Color.White)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    IconButton(
                                        onClick = { viewModel.sendPtz("Left") },
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(Color(0xFF1E293B))
                                            .size(44.dp)
                                            .testTag("ptz_left")
                                    ) {
                                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Pan Left", tint = Color.White)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(SentinelCyan.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("PTZ", color = SentinelCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }

                                    IconButton(
                                        onClick = { viewModel.sendPtz("Right") },
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(Color(0xFF1E293B))
                                            .size(44.dp)
                                            .testTag("ptz_right")
                                    ) {
                                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Pan Right", tint = Color.White)
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.sendPtz("Down") },
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color(0xFF1E293B))
                                        .size(44.dp)
                                        .testTag("ptz_down")
                                ) {
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Pan Down", tint = Color.White)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Camera Switcher Row
                Text(
                    text = "Switch Camera Feed",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(cameras) { cam ->
                        val isCurrent = cam.id == activeCamera.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCurrent) SentinelCyan.copy(alpha = 0.2f) else SecurityNavyCard)
                                .border(
                                    1.dp,
                                    if (isCurrent) SentinelCyan else SecurityNavyBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.selectCamera(cam) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("select_cam_${cam.id}")
                        ) {
                            Text(
                                text = cam.name,
                                color = if (isCurrent) SentinelCyan else Color.White,
                                fontSize = 12.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    tag: String = ""
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SecurityNavyCard)
            .border(1.dp, SecurityNavyBorder, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp)
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, color = tint, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}
