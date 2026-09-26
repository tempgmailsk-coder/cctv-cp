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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.Camera
import com.example.ui.components.MotionZoneGrid
import com.example.ui.theme.SecurityNavyBorder
import com.example.ui.theme.SecurityNavyCard
import com.example.ui.theme.SecurityNavyDark
import com.example.ui.theme.SecuritySafeGreen
import com.example.ui.theme.SentinelCyan
import com.example.ui.viewmodel.CctvViewModel

@Composable
fun DetectionSettingsScreen(
    viewModel: CctvViewModel,
    modifier: Modifier = Modifier
) {
    val cameras by viewModel.cameras.collectAsStateWithLifecycle()
    val selectedCamera by viewModel.selectedCamera.collectAsStateWithLifecycle()

    val currentCamera = selectedCamera ?: cameras.firstOrNull()

    var personOnlyFilter by remember(currentCamera?.id) {
        mutableStateOf(currentCamera?.personOnlyFilter ?: true)
    }
    var sensitivity by remember(currentCamera?.id) {
        mutableFloatStateOf((currentCamera?.sensitivity ?: 75).toFloat())
    }
    var selectedZones by remember(currentCamera?.id) {
        val initialZoneString = currentCamera?.detectionZonesGrid ?: "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15"
        val zones = initialZoneString.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet()
        mutableStateOf(zones)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SecurityNavyDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Detection & AI Settings",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Configure hardware Smart Motion Detection (SMD) & intrusion zones",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // Camera Switcher
        item {
            Text(
                text = "Select Camera",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(cameras) { cam ->
                    val isSelected = cam.id == currentCamera?.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) SentinelCyan else SecurityNavyCard)
                            .clickable { viewModel.selectCamera(cam) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("detection_select_cam_${cam.id}")
                    ) {
                        Text(
                            text = cam.name,
                            color = if (isSelected) Color.Black else Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (currentCamera != null) {
            // Human / Person Only Filter Toggle
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Smart Human / Person Detection",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Filter out environmental noise: wind-blown trees, moving shadows, rain, and pets.",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Switch(
                                checked = personOnlyFilter,
                                onCheckedChange = { personOnlyFilter = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = SentinelCyan,
                                    checkedTrackColor = Color(0xFF004D63)
                                ),
                                modifier = Modifier.testTag("person_filter_toggle")
                            )
                        }

                        if (personOnlyFilter) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF064E3B).copy(alpha = 0.4f))
                                    .padding(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.FilterAlt,
                                        contentDescription = "Active filter",
                                        tint = SecuritySafeGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "CP PLUS Hardware SMD Active: Human classification enabled.",
                                        color = SecuritySafeGreen,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Sensitivity Slider
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
                            Text(
                                text = "Motion Sensitivity",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${sensitivity.toInt()}%",
                                color = SentinelCyan,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Higher sensitivity detects minor distant movement; lower sensitivity prevents false alarms.",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                        )

                        Slider(
                            value = sensitivity,
                            onValueChange = { sensitivity = it },
                            valueRange = 10f..100f,
                            steps = 18,
                            colors = SliderDefaults.colors(
                                thumbColor = SentinelCyan,
                                activeTrackColor = SentinelCyan,
                                inactiveTrackColor = Color(0xFF334155)
                            ),
                            modifier = Modifier.testTag("sensitivity_slider")
                        )
                    }
                }
            }

            // Motion Detection Zones Visual Grid
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
                                text = "Motion Detection Zones (4x4 Matrix)",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${selectedZones.size}/16 Armed",
                                color = SentinelCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = "Tap grid cells to activate/deactivate monitoring areas. Deactivated cells (dark) ignore all movement (e.g. swaying branches).",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        // Interactive Zone Grid
                        MotionZoneGrid(
                            selectedZones = selectedZones,
                            onZoneToggled = { zoneIndex ->
                                selectedZones = if (selectedZones.contains(zoneIndex)) {
                                    selectedZones - zoneIndex
                                } else {
                                    selectedZones + zoneIndex
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    selectedZones = (0..15).toSet()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("select_all_zones_btn"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Text("Arm All (16)", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    selectedZones = emptySet()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("clear_all_zones_btn"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8))
                            ) {
                                Text("Clear All", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Save Settings Button
            item {
                Button(
                    onClick = {
                        val updatedCamera = currentCamera.copy(
                            personOnlyFilter = personOnlyFilter,
                            sensitivity = sensitivity.toInt(),
                            detectionZonesGrid = selectedZones.sorted().joinToString(",")
                        )
                        viewModel.saveCamera(updatedCamera) {}
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_detection_settings_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = SentinelCyan),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Save Detection Rules for ${currentCamera.name}",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
