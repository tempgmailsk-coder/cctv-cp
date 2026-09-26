package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.EventType
import com.example.ui.theme.SecurityAlertRed
import com.example.ui.theme.SecurityNavyBorder
import com.example.ui.theme.SecurityNavyCard
import com.example.ui.theme.SecurityNavyDark
import com.example.ui.theme.SentinelCyan
import com.example.ui.viewmodel.CctvViewModel

@Composable
fun NotificationSettingsScreen(
    viewModel: CctvViewModel,
    modifier: Modifier = Modifier
) {
    val preferences = viewModel.preferences
    val cameras by viewModel.cameras.collectAsStateWithLifecycle()

    var masterNotifications by remember { mutableStateOf(preferences.notificationsEnabled) }
    var notifyPerson by remember { mutableStateOf(preferences.notifyPerson) }
    var notifyUnknown by remember { mutableStateOf(preferences.notifyUnknownPerson) }
    var notifyMotion by remember { mutableStateOf(preferences.notifyMotion) }

    var quietHoursEnabled by remember { mutableStateOf(preferences.quietHoursEnabled) }
    var quietStart by remember { mutableStateOf(preferences.quietHoursStart) }
    var quietEnd by remember { mutableStateOf(preferences.quietHoursEnd) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SecurityNavyDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Instant Alert Notifications",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Configure push notification triggers, quiet hours, and camera filters",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // Master Switch
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SecurityNavyBorder, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SecurityNavyCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (masterNotifications) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                            contentDescription = "Notification status",
                            tint = if (masterNotifications) SentinelCyan else Color(0xFF94A3B8),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Push Notifications Master",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (masterNotifications) "Receiving alerts with 20s clip preview" else "All alerts muted",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = masterNotifications,
                        onCheckedChange = {
                            masterNotifications = it
                            preferences.notificationsEnabled = it
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SentinelCyan,
                            checkedTrackColor = Color(0xFF004D63)
                        ),
                        modifier = Modifier.testTag("master_notification_switch")
                    )
                }
            }
        }

        // Notification Types (Person, Unknown Person, Motion)
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
                        text = "Detection Alert Types",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Person Detection
                    NotificationToggleRow(
                        title = "Person / Human Detected",
                        description = "Immediate notification when a human is identified by CP PLUS SMD",
                        checked = notifyPerson,
                        onCheckedChange = {
                            notifyPerson = it
                            preferences.notifyPerson = it
                        },
                        tag = "notify_person_switch"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Unknown Person
                    NotificationToggleRow(
                        title = "Unknown Person Warning (High Priority)",
                        description = "Alerts immediately when an unrecognized face is spotted",
                        checked = notifyUnknown,
                        onCheckedChange = {
                            notifyUnknown = it
                            preferences.notifyUnknownPerson = it
                        },
                        tag = "notify_unknown_switch"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // General Motion
                    NotificationToggleRow(
                        title = "General Motion Alerts",
                        description = "Alerts for all movement (not recommended if windy / outdoors)",
                        checked = notifyMotion,
                        onCheckedChange = {
                            notifyMotion = it
                            preferences.notifyMotion = it
                        },
                        tag = "notify_motion_switch"
                    )
                }
            }
        }

        // Camera Specific Notification Toggles
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
                        text = "Cameras Allowed to Send Alerts",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Select individual cameras that are authorized to dispatch push notifications.",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                    )

                    cameras.forEach { camera ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(camera.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Text("CH-${camera.channel} • ${camera.ipAddress}", color = Color(0xFF64748B), fontSize = 10.sp)
                            }
                            Switch(
                                checked = camera.isArmed,
                                onCheckedChange = { viewModel.toggleCameraArmed(camera) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = SentinelCyan,
                                    checkedTrackColor = Color(0xFF004D63)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Quiet Hours Schedule
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Bedtime, contentDescription = "Quiet Hours", tint = SentinelCyan)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Quiet Hours / Do Not Disturb", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Suppress sound & vibration during sleep hours (events still record)", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            }
                        }

                        Switch(
                            checked = quietHoursEnabled,
                            onCheckedChange = {
                                quietHoursEnabled = it
                                preferences.quietHoursEnabled = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SentinelCyan,
                                checkedTrackColor = Color(0xFF004D63)
                            ),
                            modifier = Modifier.testTag("quiet_hours_switch")
                        )
                    }

                    if (quietHoursEnabled) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = quietStart,
                                onValueChange = {
                                    quietStart = it
                                    preferences.quietHoursStart = it
                                },
                                label = { Text("Start Time (24h)") },
                                placeholder = { Text("22:00") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("quiet_start_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = SentinelCyan,
                                    unfocusedBorderColor = SecurityNavyBorder
                                )
                            )

                            OutlinedTextField(
                                value = quietEnd,
                                onValueChange = {
                                    quietEnd = it
                                    preferences.quietHoursEnd = it
                                },
                                label = { Text("End Time (24h)") },
                                placeholder = { Text("06:00") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("quiet_end_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = SentinelCyan,
                                    unfocusedBorderColor = SecurityNavyBorder
                                )
                            )
                        }
                    }
                }
            }
        }

        // Test Push Notification Trigger
        item {
            Button(
                onClick = {
                    viewModel.triggerTestEvent(EventType.PERSON_DETECTED)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("trigger_test_notification_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = SentinelCyan),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Dispatch Test Notification Alert",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun NotificationToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String = ""
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(description, color = Color(0xFF94A3B8), fontSize = 11.sp, modifier = Modifier.padding(top = 1.dp))
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SentinelCyan,
                checkedTrackColor = Color(0xFF004D63)
            ),
            modifier = Modifier.testTag(tag)
        )
    }
}
