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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.EventType
import com.example.data.model.SecurityEvent
import com.example.ui.components.SecurityBadge
import com.example.ui.theme.SecurityAlertRed
import com.example.ui.theme.SecurityNavyBorder
import com.example.ui.theme.SecurityNavyCard
import com.example.ui.theme.SecurityNavyDark
import com.example.ui.theme.SentinelCyan
import com.example.ui.viewmodel.CctvViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EventPlayerScreen(
    viewModel: CctvViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedEvent by viewModel.selectedEvent.collectAsStateWithLifecycle()
    val event = selectedEvent ?: SecurityEvent(
        cameraId = 1,
        cameraName = "Front Gate (CP-PLUS)",
        timestamp = System.currentTimeMillis() - 600000,
        eventType = EventType.PERSON_DETECTED,
        personName = "Delivery Courier",
        details = "CP PLUS Smart Motion 5s pre-event + 15s post-event buffer"
    )

    val totalDurationSeconds = event.clipDurationSeconds.toFloat()
    var currentProgressSeconds by remember { mutableFloatStateOf(0f) }
    var isPlaying by remember { mutableStateOf(true) }
    var playbackSpeed by remember { mutableFloatStateOf(1f) }
    var downloadConfirmed by remember { mutableStateOf(false) }

    // Video playback simulation loop
    LaunchedEffect(isPlaying, playbackSpeed) {
        while (isPlaying) {
            delay(100)
            currentProgressSeconds += 0.1f * playbackSpeed
            if (currentProgressSeconds >= totalDurationSeconds) {
                currentProgressSeconds = totalDurationSeconds
                isPlaying = false
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SecurityNavyDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Navigation Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("player_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Security Incident Clip",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${event.cameraName} • Clip #${event.id}",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Video Viewport Player
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF030712))
                    .border(1.5.dp, SentinelCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .testTag("event_video_viewport")
            ) {
                // Video Screen simulation with HUD
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SecurityBadge(eventType = event.eventType)
                        Text(
                            text = "${String.format(Locale.US, "%.1f", currentProgressSeconds)}s / ${totalDurationSeconds.toInt()}s",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Detection target box at trigger moment (around 5s - 15s)
                    if (currentProgressSeconds >= 3.0f && currentProgressSeconds <= 18.0f) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .size(width = 120.dp, height = 90.dp)
                                .border(
                                    width = 1.5.dp,
                                    color = if (event.eventType == EventType.UNKNOWN_PERSON) SecurityAlertRed else SentinelCyan,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .background(
                                    if (event.eventType == EventType.UNKNOWN_PERSON) SecurityAlertRed.copy(alpha = 0.1f)
                                    else SentinelCyan.copy(alpha = 0.1f)
                                )
                        ) {
                            Text(
                                text = "${event.personName ?: "Subject"} (${(event.confidence * 100).toInt()}%)",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.7f))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Bottom Player Controls within Viewport
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PRE-BUFFER [0-5s] • EVENT TRIGGER [5-20s]",
                            color = Color(0xFF64748B),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xCC000000))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${playbackSpeed}x",
                                color = SentinelCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Timeline Scrubber & Speed Controls
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SecurityNavyCard)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Timeline Scrubber Slider
                    Slider(
                        value = currentProgressSeconds,
                        onValueChange = {
                            currentProgressSeconds = it
                            if (currentProgressSeconds < totalDurationSeconds) {
                                isPlaying = true
                            }
                        },
                        valueRange = 0f..totalDurationSeconds,
                        colors = SliderDefaults.colors(
                            thumbColor = SentinelCyan,
                            activeTrackColor = SentinelCyan,
                            inactiveTrackColor = Color(0xFF334155)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("video_timeline_slider")
                    )

                    // Control Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rewind 5s
                        IconButton(onClick = {
                            currentProgressSeconds = (currentProgressSeconds - 5f).coerceAtLeast(0f)
                        }) {
                            Icon(Icons.Default.FastRewind, contentDescription = "Rewind 5s", tint = Color.White)
                        }

                        // Play/Pause Main Button
                        IconButton(
                            onClick = {
                                if (currentProgressSeconds >= totalDurationSeconds) {
                                    currentProgressSeconds = 0f
                                    isPlaying = true
                                } else {
                                    isPlaying = !isPlaying
                                }
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(SentinelCyan)
                                .size(48.dp)
                                .testTag("play_pause_btn")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.Black
                            )
                        }

                        // Forward 5s
                        IconButton(onClick = {
                            currentProgressSeconds = (currentProgressSeconds + 5f).coerceAtMost(totalDurationSeconds)
                        }) {
                            Icon(Icons.Default.FastForward, contentDescription = "Forward 5s", tint = Color.White)
                        }

                        // Speed Toggle (0.5x, 1x, 2x)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E293B))
                                .clickable {
                                    playbackSpeed = when (playbackSpeed) {
                                        1f -> 2f
                                        2f -> 0.5f
                                        else -> 1f
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("playback_speed_btn")
                        ) {
                            Text(
                                text = "${playbackSpeed}x Speed",
                                color = SentinelCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Incident Clip Information
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SecurityNavyBorder, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SecurityNavyCard)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Clip Metadata & Forensics",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    MetaRow("Camera Source", event.cameraName)
                    MetaRow("Recorded Time", SimpleDateFormat("yyyy-MM-dd HH:mm:ss a", Locale.getDefault()).format(Date(event.timestamp)))
                    MetaRow("Detection Class", event.eventType.displayName)
                    MetaRow("AI Confidence", "${(event.confidence * 100).toInt()}%")
                    MetaRow("Clip Duration", "20 seconds (5s pre-event + 15s post-event)")
                    MetaRow("File Size", "${event.fileSizeBytes / (1024 * 1024)} MB (H.264 / AAC)")
                    MetaRow("Details", event.details.ifEmpty { "SMD Human filter validated. Foliage & weather noise eliminated." })
                }
            }
        }

        // Action Buttons: Save, Download, Share, Delete
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        downloadConfirmed = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("download_clip_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = SentinelCyan),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = if (downloadConfirmed) Icons.Default.Check else Icons.Default.Download,
                        contentDescription = "Download clip",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (downloadConfirmed) "Saved to Storage" else "Download Clip",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                OutlinedButton(
                    onClick = {
                        viewModel.toggleBookmark(event)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("bookmark_clip_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SecurityNavyBorder),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = if (event.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = SentinelCyan
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (event.isBookmarked) "Saved ★" else "Bookmark",
                        fontSize = 11.sp
                    )
                }

                IconButton(
                    onClick = {
                        viewModel.deleteEvent(event)
                        onBack()
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SecurityAlertRed.copy(alpha = 0.2f))
                        .size(42.dp)
                        .testTag("delete_clip_btn")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete clip", tint = SecurityAlertRed)
                }
            }
        }
    }
}

@Composable
fun MetaRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color(0xFF94A3B8), fontSize = 11.sp)
        Text(text = value, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}
