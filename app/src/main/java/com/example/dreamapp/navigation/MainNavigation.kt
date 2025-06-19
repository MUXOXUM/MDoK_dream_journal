package com.example.dreamapp.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.dreamapp.data.Dream
import com.example.dreamapp.ui.screens.*
import com.example.dreamapp.viewmodel.AuthViewModel
import com.example.dreamapp.viewmodel.AuthState
import com.google.firebase.auth.FirebaseUser

sealed class MainScreen(val route: String, val title: String, val icon: @Composable () -> Unit) {
    object Dreams : MainScreen("dreams", "Дневник", { Icon(Icons.Default.List, contentDescription = null) })
    object Statistics : MainScreen("statistics", "Статистика", { Icon(Icons.Default.Info, contentDescription = null) })
    object Auth : MainScreen("auth", "Вход", { Icon(Icons.Default.Person, contentDescription = null) })
    object Settings : MainScreen("settings", "Настройки", { Icon(Icons.Default.Settings, contentDescription = null) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(
    navController: NavHostController = rememberNavController(),
    dreams: List<Dream>,
    onSaveDream: (Dream) -> Unit,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.authState.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()

    val screens = listOf(
        MainScreen.Dreams,
        MainScreen.Statistics,
        MainScreen.Auth,
        MainScreen.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = screen.icon,
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MainScreen.Dreams.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(MainScreen.Dreams.route) {
                DreamNavigation(
                    dreams = dreams,
                    onSaveDream = onSaveDream
                )
            }
            
            composable(MainScreen.Statistics.route) {
                StatisticsScreen(dreams = dreams)
            }
            
            composable(MainScreen.Auth.route) {
                when (authState) {
                    is AuthState.Authenticated -> {
                        val user = (authState as AuthState.Authenticated).user
                        AccountScreen(user = user, onSignOut = { authViewModel.signOut() })
                    }
                    else -> {
                        AuthScreen(
                            onSignIn = { email, password ->
                                authViewModel.signIn(email, password)
                            },
                            onSignUp = { email, password, displayName ->
                                authViewModel.signUp(email, password, displayName)
                            },
                            onResetPassword = { email ->
                                authViewModel.resetPassword(email)
                            },
                            isLoading = isLoading,
                            errorMessage = errorMessage
                        )
                    }
                }
            }
            
            composable(MainScreen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}

@Composable
fun AccountScreen(user: FirebaseUser, onSignOut: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Вы вошли как:", style = MaterialTheme.typography.titleMedium)
        Text(user.email ?: user.displayName ?: "Без email", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.padding(16.dp))
        Button(onClick = onSignOut) {
            Text("Выйти из аккаунта")
        }
    }
} 