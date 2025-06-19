package com.example.dreamapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dreamapp.data.Dream
import com.example.dreamapp.ui.screens.DreamDetailScreen
import com.example.dreamapp.ui.screens.DreamEditScreen
import com.example.dreamapp.ui.screens.DreamListScreen

sealed class Screen(val route: String) {
    object DreamList : Screen("dream_list")
    object DreamDetail : Screen("dream_detail/{dreamId}") {
        fun createRoute(dreamId: Long) = "dream_detail/$dreamId"
    }
    object DreamEdit : Screen("dream_edit/{dreamId}") {
        fun createRoute(dreamId: Long? = null) = "dream_edit/${dreamId ?: "new"}"
    }
}

@Composable
fun DreamNavigation(
    navController: NavHostController = rememberNavController(),
    dreams: List<Dream>,
    onSaveDream: (Dream) -> Unit,
    onDeleteDream: (Dream) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.DreamList.route
    ) {
        composable(Screen.DreamList.route) {
            DreamListScreen(
                dreams = dreams,
                onDreamClick = { dream ->
                    navController.navigate(Screen.DreamDetail.createRoute(dream.id))
                },
                onAddDreamClick = {
                    navController.navigate(Screen.DreamEdit.createRoute())
                },
                onDeleteDream = onDeleteDream
            )
        }
        
        composable(
            route = Screen.DreamDetail.route,
            arguments = listOf(
                navArgument("dreamId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val dreamId = backStackEntry.arguments?.getLong("dreamId") ?: 0L
            val dream = dreams.find { it.id == dreamId }
            
            dream?.let { foundDream ->
                DreamDetailScreen(
                    dream = foundDream,
                    onBack = { navController.popBackStack() },
                    onEdit = { dreamToEdit ->
                        navController.navigate(Screen.DreamEdit.createRoute(dreamToEdit.id))
                    }
                )
            }
        }
        
        composable(
            route = Screen.DreamEdit.route,
            arguments = listOf(
                navArgument("dreamId") { 
                    type = NavType.StringType 
                    nullable = true 
                    defaultValue = "new"
                }
            )
        ) { backStackEntry ->
            val dreamIdParam = backStackEntry.arguments?.getString("dreamId")
            val dream = if (dreamIdParam == "new") {
                null
            } else {
                dreams.find { it.id == dreamIdParam?.toLongOrNull() }
            }
            
            DreamEditScreen(
                dream = dream,
                onSave = { newDream ->
                    onSaveDream(newDream)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
} 