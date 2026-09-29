package com.example.citizencomplaintapp.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.citizencomplaintapp.ui.navigation.Screen
import com.example.citizencomplaintapp.ui.screens.admin.AdminAssignScreen
import com.example.citizencomplaintapp.ui.screens.citizen.*
import com.example.citizencomplaintapp.ui.screens.officer.OfficerHomeScreen
import com.example.citizencomplaintapp.ui.screens.officer.OfficerListScreen
import com.example.citizencomplaintapp.ui.screens.officer.OfficerUpdateScreen
import com.example.citizencomplaintapp.ui.screens.officer.OfficerDetailsScreen
import com.example.citizencomplaintapp.ui.viewmodel.MainViewModel
import com.example.citizencomplaintapp.ui.theme.SecondaryBlue

@Composable
fun MainScreen(viewModel: MainViewModel, email: String, onNavigateToDetails: (String) -> Unit, onLogout: () -> Unit) {
    val navController = rememberNavController()
    val role = remember(email) {
        if (email.contains("officer") || email.contains("admin")) "Officer" else "Citizen"
    }
    
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, role = role)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                if (role == "Officer") {
                    OfficerHomeScreen(viewModel, onNavigateToDetails)
                } else {
                    HomeScreen(
                        viewModel = viewModel, 
                        onComplaintClick = onNavigateToDetails,
                        onReportIssueClick = { navController.navigate(Screen.Submit.route) }
                    )
                }
            }
            composable(Screen.Complaints.route) {
                ComplaintsScreen(viewModel, role, onNavigateToDetails)
            }
            composable(Screen.Map.route) {
                MapScreen(
                    viewModel = viewModel,
                    onNavigateToDetails = onNavigateToDetails
                )
            }
            composable("chat") {
                ChatbotScreen()
            }
            composable("officers") {
                OfficerListScreen(viewModel) { officerId ->
                    navController.navigate(Screen.OfficerDetails.createRoute(officerId))
                }
            }
            composable(Screen.OfficerDetails.route) { backStackEntry ->
                val officerId = backStackEntry.arguments?.getString("officerId") ?: ""
                OfficerDetailsScreen(
                    officerId = officerId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onComplaintClick = onNavigateToDetails
                )
            }
            composable("stats") {
                // Officer now has access to the assignment and analytics dashboard
                AdminAssignScreen(viewModel)
            }
            composable(Screen.Submit.route) {
                if (role == "Officer") {
                    OfficerUpdateScreen(viewModel)
                } else {
                    SubmitComplaintScreen(viewModel) {
                        navController.navigate(Screen.Home.route)
                    }
                }
            }
            composable(Screen.Profile.route) {
                ProfileScreen(viewModel, onLogout)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, role: String) {
    val items = when (role) {
        "Officer" -> listOf(
            NavigationItem("Home", Screen.Home.route, Icons.Default.Home),
            NavigationItem("Tasks", Screen.Complaints.route, Icons.AutoMirrored.Filled.List),
            NavigationItem("Officers", "officers", Icons.Default.Person),
            NavigationItem("Stats", "stats", Icons.Default.Info),
            NavigationItem("Map", Screen.Map.route, Icons.Default.LocationOn),
            NavigationItem("Profile", Screen.Profile.route, Icons.Default.Person)
        )
        else -> listOf(
            NavigationItem("Home", Screen.Home.route, Icons.Default.Home),
            NavigationItem("Map", Screen.Map.route, Icons.Default.LocationOn),
            NavigationItem("Submit", Screen.Submit.route, Icons.Default.Add),
            NavigationItem("Chat", "chat", Icons.Default.Face),
            NavigationItem("Profile", Screen.Profile.route, Icons.Default.Person)
        )
    }
    
    NavigationBar(
        containerColor = Color.White,
        contentColor = SecondaryBlue
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(text = item.title, fontSize = 9.sp) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SecondaryBlue,
                    selectedTextColor = SecondaryBlue,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color(0xFFF0F0F0)
                )
            )
        }
    }
}

data class NavigationItem(val title: String, val route: String, val icon: ImageVector)