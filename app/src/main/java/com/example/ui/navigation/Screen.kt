package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Login : Screen("login", "Security Lock", Icons.Default.Lock)
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Security)
    object LiveCameras : Screen("live_cameras", "Live Cameras", Icons.Default.Videocam)
    object CameraSetup : Screen("camera_setup", "Camera Setup", Icons.Default.Tune)
    object DetectionSettings : Screen("detection_settings", "Detection", Icons.Default.Tune)
    object EventHistory : Screen("event_history", "Events", Icons.Default.History)
    object EventPlayer : Screen("event_player", "Clip Player", Icons.Default.Videocam)
    object KnownPersons : Screen("known_persons", "Known Faces", Icons.Default.Face)
    object NotificationSettings : Screen("notification_settings", "Alerts", Icons.Default.Notifications)
    object StorageSettings : Screen("storage_settings", "Storage", Icons.Default.SdCard)
    object ArchitectureGuide : Screen("architecture_guide", "System Docs", Icons.Default.Info)
}
