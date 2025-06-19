package com.example.dreamapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.dreamapp.data.Dream
import com.example.dreamapp.ui.screens.*

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
    onSaveDream: (Dream) -> Unit
) {
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
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
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
                AuthScreen()
            }
            
            composable(MainScreen.Settings.route) {
                SettingsScreen()
            }
        }
    }
} 