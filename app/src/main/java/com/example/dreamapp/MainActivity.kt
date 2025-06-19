package com.example.dreamapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.dreamapp.navigation.MainNavigation
import com.example.dreamapp.ui.theme.DreamAppTheme
import com.example.dreamapp.viewmodel.DreamViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: DreamViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DreamAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val dreams by viewModel.dreams.collectAsState()
                    
                    MainNavigation(
                        dreams = dreams,
                        onSaveDream = { dream ->
                            viewModel.addDream(dream)
                        }
                    )
                }
            }
        }
    }
}