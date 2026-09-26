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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.cpplus.CpPlusModelPreset
import com.example.data.model.Camera
import com.example.ui.theme.SecurityAlertRed
import com.example.ui.theme.SecurityNavyBorder
import com.example.ui.theme.SecurityNavyCard
import com.example.ui.theme.SecurityNavyDark
import com.example.ui.theme.SecuritySafeGreen
import com.example.ui.theme.SentinelCyan
import com.example.ui.viewmodel.CctvViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraSetupScreen(
    viewModel: CctvViewModel,
    initialCamera: Camera? = null,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cameras by viewModel.cameras.collectAsStateWithLifecycle()
    val isTesting by viewModel.isTestingConnection.collectAsStateWithLifecycle()
    val testResult by viewModel.testConnectionResult.collectAsStateWithLifecycle()

    var selectedCameraId by remember { mutableStateOf(initialCamera?.id ?: 0L) }
    var name by remember(selectedCameraId) {
        mutableStateOf(initialCamera?.name ?: "Front Gate (CP-PLUS)")
    }
    var ipAddress by remember(selectedCameraId) {
        mutableStateOf(initialCamera?.ipAddress ?: "192.168.1.108")
    }
    var rtspPort by remember(selectedCameraId) {
        mutableStateOf((initialCamera?.rtspPort ?: 554).toString())
    }
    var httpPort by remember(selectedCameraId) {
        mutableStateOf((initialCamera?.httpPort ?: 80).toString())
    }
    var channel by remember(selectedCameraId) {
        mutableStateOf((initialCamera?.channel ?: 1).toString())
    }
    var username by remember(selectedCameraId) {
        mutableStateOf(initialCamera?.username ?: "admin")
    }
    var password by remember(selectedCameraId) {
        mutableStateOf(initialCamera?.passwordEncrypted ?: "CpPlus@2026")
    }
    var selectedPreset by remember(selectedCameraId) {
        mutableStateOf(CpPlusModelPreset.fromId(initialCamera?.modelPreset ?: "CP_PLUS_ORANGE"))
    }
    var streamType by remember(selectedCameraId) {
        mutableStateOf(initialCamera?.streamType ?: "SUB")
    }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var presetExpanded by remember { mutableStateOf(false) }

    fun populateWithCamera(cam: Camera) {
        selectedCameraId = cam.id
        name = cam.name
        ipAddress = cam.ipAddress
        rtspPort = cam.rtspPort.toString()
        httpPort = cam.httpPort.toString()
        channel = cam.channel.toString()
        username = cam.username
        password = cam.passwordEncrypted
        selectedPreset = CpPlusModelPreset.fromId(cam.modelPreset)
        streamType = cam.streamType
        viewModel.clearConnectionTestResult()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SecurityNavyDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Text(
                text = if (selectedCameraId > 0) "Configure CP PLUS Camera" else "Add New CP PLUS Camera",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Supports CP PLUS Orange, Cosmic, Indigo, Ezykam, RTSP and ONVIF streams",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        // Camera selector bar if multiple exist
        if (cameras.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedCameraId == 0L) SentinelCyan else SecurityNavyCard)
                            .clickable {
                                selectedCameraId = 0L
                                name = "New CP PLUS Camera"
                                ipAddress = "192.168.1.120"
                                rtspPort = "554"
                                httpPort = "80"
                                channel = "1"
                                username = "admin"
                                password = ""
                                viewModel.clearConnectionTestResult()
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("select_add_new_camera")
                    ) {
                        Text(
                            text = "+ Add New",
                            color = if (selectedCameraId == 0L) Color.Black else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    cameras.forEach { cam ->
                        val isSelected = cam.id == selectedCameraId
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) SentinelCyan else SecurityNavyCard)
                                .clickable { populateWithCamera(cam) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("select_edit_camera_${cam.id}")
                        ) {
                            Text(
                                text = cam.name,
                                color = if (isSelected) Color.Black else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Model Preset Dropdown
        item {
            ExposedDropdownMenuBox(
                expanded = presetExpanded,
                onExpandedChange = { presetExpanded = !presetExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedPreset.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("CP PLUS Model Preset") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = presetExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .testTag("model_preset_dropdown"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = SentinelCyan,
                        unfocusedBorderColor = SecurityNavyBorder,
                        focusedLabelColor = SentinelCyan,
                        unfocusedLabelColor = Color(0xFF94A3B8)
                    )
                )

                ExposedDropdownMenu(
                    expanded = presetExpanded,
                    onDismissRequest = { presetExpanded = false },
                    modifier = Modifier.background(SecurityNavyCard)
                ) {
                    CpPlusModelPreset.entries.forEach { preset ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(preset.displayName, color = Color.White, fontWeight = FontWeight.Bold)
                                    Text(preset.description, color = Color(0xFF94A3B8), fontSize = 11.sp)
                                }
                            },
                            onClick = {
                                selectedPreset = preset
                                httpPort = preset.defaultHttpPort.toString()
                                rtspPort = preset.defaultRtspPort.toString()
                                presetExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Camera Name Field
        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Camera Location / Name") },
                placeholder = { Text("e.g. Front Gate, Driveway, Backyard") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("camera_name_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = SentinelCyan,
                    unfocusedBorderColor = SecurityNavyBorder,
                    focusedLabelColor = SentinelCyan,
                    unfocusedLabelColor = Color(0xFF94A3B8)
                )
            )
        }

        // IP Address & Channel Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = ipAddress,
                    onValueChange = { ipAddress = it },
                    label = { Text("DVR / Camera IP") },
                    placeholder = { Text("192.168.1.108") },
                    modifier = Modifier
                        .weight(2f)
                        .testTag("camera_ip_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = SentinelCyan,
                        unfocusedBorderColor = SecurityNavyBorder,
                        focusedLabelColor = SentinelCyan,
                        unfocusedLabelColor = Color(0xFF94A3B8)
                    )
                )

                OutlinedTextField(
                    value = channel,
                    onValueChange = { channel = it },
                    label = { Text("Channel") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("camera_channel_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = SentinelCyan,
                        unfocusedBorderColor = SecurityNavyBorder,
                        focusedLabelColor = SentinelCyan,
                        unfocusedLabelColor = Color(0xFF94A3B8)
                    )
                )
            }
        }

        // Ports Row (RTSP 554 & HTTP 80/37777)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = rtspPort,
                    onValueChange = { rtspPort = it },
                    label = { Text("RTSP Port") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("camera_rtsp_port_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = SentinelCyan,
                        unfocusedBorderColor = SecurityNavyBorder,
                        focusedLabelColor = SentinelCyan,
                        unfocusedLabelColor = Color(0xFF94A3B8)
                    )
                )

                OutlinedTextField(
                    value = httpPort,
                    onValueChange = { httpPort = it },
                    label = { Text("HTTP / SDK Port") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("camera_http_port_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = SentinelCyan,
                        unfocusedBorderColor = SecurityNavyBorder,
                        focusedLabelColor = SentinelCyan,
                        unfocusedLabelColor = Color(0xFF94A3B8)
                    )
                )
            }
        }

        // Credentials: Username & Password
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("camera_username_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = SentinelCyan,
                        unfocusedBorderColor = SecurityNavyBorder,
                        focusedLabelColor = SentinelCyan,
                        unfocusedLabelColor = Color(0xFF94A3B8)
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password visibility",
                                tint = Color(0xFF94A3B8)
                            )
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("camera_password_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = SentinelCyan,
                        unfocusedBorderColor = SecurityNavyBorder,
                        focusedLabelColor = SentinelCyan,
                        unfocusedLabelColor = Color(0xFF94A3B8)
                    )
                )
            }
        }

        // Generated RTSP URL Preview
        item {
            val previewCam = Camera(
                name = name,
                ipAddress = ipAddress,
                rtspPort = rtspPort.toIntOrNull() ?: 554,
                httpPort = httpPort.toIntOrNull() ?: 80,
                channel = channel.toIntOrNull() ?: 1,
                username = username,
                passwordEncrypted = password,
                modelPreset = selectedPreset.id,
                streamType = streamType
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SecurityNavyCard),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "CP PLUS RTSP Stream URL",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = previewCam.buildRtspUrl(passwordDecrypted = if (password.isNotEmpty()) "******" else ""),
                        color = SentinelCyan,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Test Connection Button & Result
        item {
            val previewCam = Camera(
                name = name,
                ipAddress = ipAddress,
                rtspPort = rtspPort.toIntOrNull() ?: 554,
                httpPort = httpPort.toIntOrNull() ?: 80,
                channel = channel.toIntOrNull() ?: 1,
                username = username,
                passwordEncrypted = password,
                modelPreset = selectedPreset.id,
                streamType = streamType
            )

            OutlinedButton(
                onClick = { viewModel.testConnection(previewCam) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("test_connection_btn"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SentinelCyan),
                border = androidx.compose.foundation.BorderStroke(1.dp, SentinelCyan)
            ) {
                if (isTesting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = SentinelCyan
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pinging CP PLUS DVR...")
                } else {
                    Icon(Icons.Default.Refresh, contentDescription = "Test connection")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Test CP PLUS Connection")
                }
            }

            if (testResult != null) {
                val result = testResult!!
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (result.isSuccessful) Color(0xFF064E3B) else Color(0xFF450A0A)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (result.isSuccessful) Icons.Default.Check else Icons.Default.Warning,
                            contentDescription = "Result status",
                            tint = if (result.isSuccessful) SecuritySafeGreen else SecurityAlertRed
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (result.isSuccessful) "Connection Successful" else "Connection Failed",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${result.message} • Latency: ${result.latencyMs}ms",
                                color = Color(0xFFE2E8F0),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Save & Delete Buttons
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (selectedCameraId > 0) {
                    OutlinedButton(
                        onClick = {
                            val camToDelete = cameras.firstOrNull { it.id == selectedCameraId }
                            if (camToDelete != null) {
                                viewModel.deleteCamera(camToDelete) {
                                    onFinished()
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("delete_camera_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SecurityAlertRed),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SecurityAlertRed)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Delete")
                    }
                }

                Button(
                    onClick = {
                        val cameraToSave = Camera(
                            id = selectedCameraId,
                            name = name.ifEmpty { "CP PLUS Camera" },
                            ipAddress = ipAddress.ifEmpty { "192.168.1.108" },
                            rtspPort = rtspPort.toIntOrNull() ?: 554,
                            httpPort = httpPort.toIntOrNull() ?: 80,
                            channel = channel.toIntOrNull() ?: 1,
                            username = username,
                            passwordEncrypted = password,
                            modelPreset = selectedPreset.id,
                            streamType = streamType
                        )
                        viewModel.saveCamera(cameraToSave) {
                            onFinished()
                        }
                    },
                    modifier = Modifier
                        .weight(2f)
                        .testTag("save_camera_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = SentinelCyan),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (selectedCameraId > 0) "Update Camera" else "Save Camera",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
