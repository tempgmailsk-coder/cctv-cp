package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleanHands
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.theme.SecurityAlertRed
import com.example.ui.theme.SecurityNavyBorder
import com.example.ui.theme.SecurityNavyCard
import com.example.ui.theme.SecurityNavyDark
import com.example.ui.theme.SentinelCyan
import com.example.ui.viewmodel.CctvViewModel

@Composable
fun StorageSettingsScreen(
    viewModel: CctvViewModel,
    modifier: Modifier = Modifier
) {
    val preferences = viewModel.preferences
    val events by viewModel.allEvents.collectAsStateWithLifecycle()

    var preBufferSeconds by remember { mutableFloatStateOf(preferences.preBufferSeconds.toFloat()) }
    var postBufferSeconds by remember { mutableFloatStateOf(preferences.postBufferSeconds.toFloat()) }
    var retentionDays by remember { mutableIntStateOf(preferences.retentionDays) }
    var autoDelete by remember { mutableStateOf(preferences.autoDeleteOldClips) }

    val totalClipDuration = preBufferSeconds.toInt() + postBufferSeconds.toInt()
    val totalStorageUsedMb = (events.size * 4.5f).toInt()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SecurityNavyDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Storage & Clip Buffering",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Manage pre-event buffering, post-event recording, and clip retention",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // Storage Usage Meter
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SecurityNavyBorder, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SecurityNavyCard)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SdCard, contentDescription = "Storage", tint = SentinelCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Local Storage Allocated", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("$totalStorageUsedMb MB / 32 GB", color = SentinelCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = (totalStorageUsedMb / 32000f).coerceIn(0.02f, 1f),
                        color = SentinelCyan,
                        trackColor = Color(0xFF1E293B),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${events.size} event clips archived • Average 4.5MB per 20s clip",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Clip Buffering Controls (Pre & Post buffer requirement)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SecurityNavyBorder, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SecurityNavyCard)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Automatic Incident Clip Window",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SentinelCyan.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Total: ${totalClipDuration}s Clip",
                                color = SentinelCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "When person detection occurs, the app saves seconds before the event from circular memory plus following action.",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    // Pre-buffer Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pre-Event Buffer (Before Trigger)", color = Color.White, fontSize = 12.sp)
                        Text("${preBufferSeconds.toInt()} seconds", color = SentinelCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = preBufferSeconds,
                        onValueChange = {
                            preBufferSeconds = it
                            preferences.preBufferSeconds = it.toInt()
                        },
                        valueRange = 2f..10f,
                        steps = 7,
                        colors = SliderDefaults.colors(
                            thumbColor = SentinelCyan,
                            activeTrackColor = SentinelCyan,
                            inactiveTrackColor = Color(0xFF334155)
                        ),
                        modifier = Modifier.testTag("pre_buffer_slider")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Post-buffer Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Post-Event Buffer (After Trigger)", color = Color.White, fontSize = 12.sp)
                        Text("${postBufferSeconds.toInt()} seconds", color = SentinelCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = postBufferSeconds,
                        onValueChange = {
                            postBufferSeconds = it
                            preferences.postBufferSeconds = it.toInt()
                        },
                        valueRange = 10f..30f,
                        steps = 19,
                        colors = SliderDefaults.colors(
                            thumbColor = SentinelCyan,
                            activeTrackColor = SentinelCyan,
                            inactiveTrackColor = Color(0xFF334155)
                        ),
                        modifier = Modifier.testTag("post_buffer_slider")
                    )
                }
            }
        }

        // Clip Retention Period
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
                        text = "Video Retention Period",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Automatically purges old unbookmarked video recordings after specified days to conserve memory.",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(7, 14, 30, 90).forEach { days ->
                            val isSelected = retentionDays == days
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) SentinelCyan else Color(0xFF1E293B))
                                    .clickable {
                                        retentionDays = days
                                        preferences.retentionDays = days
                                    }
                                    .padding(vertical = 10.dp)
                                    .testTag("retention_${days}_days"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${days}D",
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Auto-delete expired clips", color = Color.White, fontSize = 13.sp)
                        Switch(
                            checked = autoDelete,
                            onCheckedChange = {
                                autoDelete = it
                                preferences.autoDeleteOldClips = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SentinelCyan,
                                checkedTrackColor = Color(0xFF004D63)
                            ),
                            modifier = Modifier.testTag("auto_delete_switch")
                        )
                    }
                }
            }
        }

        // Cleanup Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.cleanupOldStorage() },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("cleanup_storage_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SentinelCyan),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SentinelCyan),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Clean Expired Clips", fontSize = 11.sp)
                }

                Button(
                    onClick = { viewModel.clearAllEvents() },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("clear_all_storage_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = SecurityAlertRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Purge All Clips", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
