package com.example.dreamapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.dreamapp.navigation.MainNavigation
import com.example.dreamapp.ui.theme.DreamAppTheme
import com.example.dreamapp.viewmodel.AuthViewModel
import com.example.dreamapp.viewmodel.DreamViewModel
import com.example.dreamapp.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val dreamViewModel: DreamViewModel by viewModels {
        DreamViewModel.provideFactory(application, authViewModel)
    }
    private val settingsViewModel: SettingsViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            // Можно показать Snackbar/Toast если не разрешено
        }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        setContent {
            DreamAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val dreams by dreamViewModel.dreams.collectAsState()
                    
                    MainNavigation(
                        dreams = dreams,
                        onSaveDream = { dream ->
                            dreamViewModel.addDream(dream)
                        },
                        onDeleteDream = { dream ->
                            dreamViewModel.deleteDream(dream)
                        },
                        authViewModel = authViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }
}