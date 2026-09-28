package com.example.citizencomplaintapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.citizencomplaintapp.ui.navigation.AppNavigation
import com.example.citizencomplaintapp.ui.theme.CitizenComplaintAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CitizenComplaintAppTheme {
                AppNavigation()
            }
        }
    }
}
