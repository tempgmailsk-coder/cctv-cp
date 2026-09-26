package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.repository.CctvRepository
import com.example.service.CctvSurveillanceService
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SecurityNavyDark
import com.example.ui.viewmodel.CctvViewModel
import com.example.ui.viewmodel.CctvViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = CctvRepository(applicationContext)

        // Automatically start background surveillance service
        if (repository.preferences.isSystemArmed) {
            CctvSurveillanceService.startService(this)
        }

        val eventIdExtra = intent?.getLongExtra("OPEN_EVENT_ID", -1L) ?: -1L

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SecurityNavyDark
                ) {
                    // Request notification permission for Android 13+ (Tiramisu)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val permissionLauncher = rememberLauncherForActivityResult(
                            ActivityResultContracts.RequestPermission()
                        ) {}

                        LaunchedEffect(Unit) {
                            if (ContextCompat.checkSelfPermission(
                                    this@MainActivity,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    }

                    val viewModel: CctvViewModel = viewModel(
                        factory = CctvViewModelFactory(repository)
                    )

                    AppNavigation(
                        viewModel = viewModel,
                        initialEventId = eventIdExtra
                    )
                }
            }
        }
    }
}
