package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Camera
import com.example.data.model.SecurityEvent
import com.example.ui.screens.ArchitectureGuideScreen
import com.example.ui.screens.CameraSetupScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DetectionSettingsScreen
import com.example.ui.screens.EventHistoryScreen
import com.example.ui.screens.EventPlayerScreen
import com.example.ui.screens.KnownPersonsScreen
import com.example.ui.screens.LiveCamerasScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.NotificationSettingsScreen
import com.example.ui.screens.StorageSettingsScreen
import com.example.ui.theme.SecurityNavyBorder
import com.example.ui.theme.SecurityNavyCard
import com.example.ui.theme.SecurityNavyDark
import com.example.ui.theme.SentinelCyan
import com.example.ui.viewmodel.CctvViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    viewModel: CctvViewModel,
    initialEventId: Long = -1L,
    modifier: Modifier = Modifier
) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsStateWithLifecycle()
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }
    var cameraToConfigure by remember { mutableStateOf<Camera?>(null) }
    var menuExpanded by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Collect UI messages
    LaunchedEffect(Unit) {
        viewModel.uiMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // Handle deep link to open specific event
    LaunchedEffect(initialEventId) {
        if (initialEventId > 0) {
            viewModel.selectEventById(initialEventId)
            currentScreen = Screen.EventPlayer
        }
    }

    // Back handling
    if (currentScreen != Screen.Dashboard && isAuthenticated) {
        BackHandler {
            currentScreen = Screen.Dashboard
        }
    }

    // If lock PIN enabled and not yet authenticated
    if (!isAuthenticated) {
        LoginScreen(
            onUnlocked = { /* unlocked */ },
            onVerifyPin = { pin -> viewModel.unlockWithPin(pin) }
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SecurityNavyDark,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentScreen.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SecurityNavyDark
                ),
                actions = {
                    // System Architecture Guide
                    IconButton(
                        onClick = { currentScreen = Screen.ArchitectureGuide },
                        modifier = Modifier.testTag("topbar_arch_guide_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = "Architecture Guide",
                            tint = if (currentScreen == Screen.ArchitectureGuide) SentinelCyan else Color.White
                        )
                    }

                    // Overflow Menu for All Screens
                    Box {
                        IconButton(
                            onClick = { menuExpanded = !menuExpanded },
                            modifier = Modifier.testTag("topbar_overflow_menu_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More settings",
                                tint = Color.White
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier.background(SecurityNavyCard)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Camera Setup", color = Color.White) },
                                leadingIcon = { Icon(Icons.Default.Tune, contentDescription = null, tint = SentinelCyan) },
                                onClick = {
                                    menuExpanded = false
                                    cameraToConfigure = null
                                    currentScreen = Screen.CameraSetup
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Detection & Zones", color = Color.White) },
                                leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = SentinelCyan) },
                                onClick = {
                                    menuExpanded = false
                                    currentScreen = Screen.DetectionSettings
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Notification Settings", color = Color.White) },
                                leadingIcon = { Icon(Icons.Default.Notifications, contentDescription = null, tint = SentinelCyan) },
                                onClick = {
                                    menuExpanded = false
                                    currentScreen = Screen.NotificationSettings
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Storage & Buffering", color = Color.White) },
                                leadingIcon = { Icon(Icons.Default.SdCard, contentDescription = null, tint = SentinelCyan) },
                                onClick = {
                                    menuExpanded = false
                                    currentScreen = Screen.StorageSettings
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Lock App", color = Color.White) },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFF43F5E)) },
                                onClick = {
                                    menuExpanded = false
                                    viewModel.lockApp()
                                }
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            val navItems = listOf(
                Screen.Dashboard,
                Screen.LiveCameras,
                Screen.EventHistory,
                Screen.KnownPersons,
                Screen.CameraSetup
            )

            NavigationBar(
                containerColor = SecurityNavyCard,
                tonalElevation = 8.dp
            ) {
                navItems.forEach { screen ->
                    val isSelected = currentScreen.route == screen.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (screen == Screen.CameraSetup) {
                                cameraToConfigure = null
                            }
                            currentScreen = screen
                        },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SentinelCyan,
                            selectedTextColor = SentinelCyan,
                            unselectedIconColor = Color(0xFF64748B),
                            unselectedTextColor = Color(0xFF64748B),
                            indicatorColor = SentinelCyan.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_${screen.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.Login -> {
                    LoginScreen(
                        onUnlocked = { currentScreen = Screen.Dashboard },
                        onVerifyPin = { pin -> viewModel.unlockWithPin(pin) }
                    )
                }
                Screen.Dashboard -> {
                    DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToLive = { currentScreen = Screen.LiveCameras },
                        onNavigateToCameraSetup = {
                            cameraToConfigure = null
                            currentScreen = Screen.CameraSetup
                        },
                        onNavigateToEvents = { currentScreen = Screen.EventHistory },
                        onNavigateToDetectionSettings = { currentScreen = Screen.DetectionSettings },
                        onNavigateToKnownPersons = { currentScreen = Screen.KnownPersons },
                        onPlayEvent = { event ->
                            viewModel.selectEvent(event)
                            currentScreen = Screen.EventPlayer
                        },
                        onViewCamera = { camera ->
                            viewModel.selectCamera(camera)
                            currentScreen = Screen.LiveCameras
                        }
                    )
                }
                Screen.LiveCameras -> {
                    LiveCamerasScreen(
                        viewModel = viewModel,
                        onConfigureCamera = { camera ->
                            cameraToConfigure = camera
                            currentScreen = Screen.CameraSetup
                        }
                    )
                }
                Screen.CameraSetup -> {
                    CameraSetupScreen(
                        viewModel = viewModel,
                        initialCamera = cameraToConfigure,
                        onFinished = { currentScreen = Screen.Dashboard }
                    )
                }
                Screen.DetectionSettings -> {
                    DetectionSettingsScreen(viewModel = viewModel)
                }
                Screen.EventHistory -> {
                    EventHistoryScreen(
                        viewModel = viewModel,
                        onPlayEvent = { event ->
                            viewModel.selectEvent(event)
                            currentScreen = Screen.EventPlayer
                        }
                    )
                }
                Screen.EventPlayer -> {
                    EventPlayerScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = Screen.EventHistory }
                    )
                }
                Screen.KnownPersons -> {
                    KnownPersonsScreen(viewModel = viewModel)
                }
                Screen.NotificationSettings -> {
                    NotificationSettingsScreen(viewModel = viewModel)
                }
                Screen.StorageSettings -> {
                    StorageSettingsScreen(viewModel = viewModel)
                }
                Screen.ArchitectureGuide -> {
                    ArchitectureGuideScreen()
                }
            }
        }
    }
}
