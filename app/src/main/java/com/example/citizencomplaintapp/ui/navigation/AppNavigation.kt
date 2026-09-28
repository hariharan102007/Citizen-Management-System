package com.example.citizencomplaintapp.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.citizencomplaintapp.ui.screens.MainScreen
import com.example.citizencomplaintapp.ui.screens.auth.LoginScreen
import com.example.citizencomplaintapp.ui.screens.citizen.ComplaintDetailsScreen
import com.example.citizencomplaintapp.ui.viewmodel.MainViewModel

@Composable
fun AppNavigation(viewModel: MainViewModel = viewModel()) {
    val navController = rememberNavController()
    val complaints by viewModel.complaints.collectAsState()
    
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { email ->
                    navController.navigate("main/$email") {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable("main/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            MainScreen(
                viewModel = viewModel,
                email = email,
                onNavigateToDetails = { id ->
                    navController.navigate(Screen.ComplaintDetails.createRoute(id))
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(Screen.ComplaintDetails.route) { backStackEntry ->
            val complaintId = backStackEntry.arguments?.getString("complaintId")
            val complaint = complaints.find { it.complaintId == complaintId }
            ComplaintDetailsScreen(
                complaint = complaint,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}