package com.example.smartcropkenya

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.delay

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Auth : Screen("auth_screen")
    object Dashboard : Screen("dashboard_screen/{userName}/{isNewUser}") {
        fun createRoute(userName: String, isNewUser: Boolean) = "dashboard_screen/$userName/$isNewUser"
    }
    object MainInput : Screen("main_input_screen")
    object Results : Screen("results_screen")
}

@Composable
fun SmartCropAppNavHost() {
    val navController = rememberNavController()
    val viewModel: SmartCropViewModel = viewModel()  // shared ViewModel across screens

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        composable(Screen.Splash.route) {
            SplashScreen(onSplashFinished = {
                navController.navigate(Screen.Auth.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Auth.route) {
            AuthScreen(onLoginSuccess = { userName, isNewUser ->
                navController.navigate(Screen.Dashboard.createRoute(userName, isNewUser)) {
                    popUpTo(Screen.Auth.route) { inclusive = true }
                }
            })
        }

        composable(
            route = Screen.Dashboard.route,
            arguments = listOf(
                navArgument("userName") { type = NavType.StringType },
                navArgument("isNewUser") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "Farmer"
            val isNewUser = backStackEntry.arguments?.getBoolean("isNewUser") ?: false

            DashboardScreen(
                userName = userName,
                isNewUser = isNewUser,
                onNavigateToInput = { navController.navigate(Screen.MainInput.route) }
            )
        }

        composable(Screen.MainInput.route) {
            MainInputScreen(
                onNavigateToResults = { navController.navigate(Screen.Results.route) },
                viewModel = viewModel
            )
        }

        composable(Screen.Results.route) {
            ResultsScreen(
                viewModel = viewModel,
                onNavigateHome = {
                    viewModel.resetToInput()
                    navController.popBackStack(Screen.Dashboard.route, inclusive = false)
                }
            )
        }
    }
}

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    LaunchedEffect(key1 = true) {
        delay(2000L)
        onSplashFinished()
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🌱", fontSize = 80.sp)
            Text(
                text = "SmartCrop Kenya",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}