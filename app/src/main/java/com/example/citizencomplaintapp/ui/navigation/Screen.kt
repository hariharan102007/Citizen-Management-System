package com.example.citizencomplaintapp.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Complaints : Screen("complaints")
    object Submit : Screen("submit")
    object Profile : Screen("profile")
    object Map : Screen("map")
    object ComplaintDetails : Screen("complaint_details/{complaintId}") {
        fun createRoute(complaintId: String) = "complaint_details/$complaintId"
    }
    object OfficerDetails : Screen("officer_details/{officerId}") {
        fun createRoute(officerId: String) = "officer_details/$officerId"
    }
}