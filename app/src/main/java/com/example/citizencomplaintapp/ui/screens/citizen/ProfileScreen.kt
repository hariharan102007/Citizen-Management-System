package com.example.citizencomplaintapp.ui.screens.citizen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citizencomplaintapp.data.model.ComplaintStatus
import com.example.citizencomplaintapp.ui.theme.BackgroundLight
import com.example.citizencomplaintapp.ui.theme.SecondaryBlue
import com.example.citizencomplaintapp.ui.theme.TextGrey
import com.example.citizencomplaintapp.ui.viewmodel.MainViewModel

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    onLogout: () -> Unit
) {
    val complaints by viewModel.complaints.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SecondaryBlue)
    ) {
        // Profile Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFFE8F0F7), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "RK", color = SecondaryBlue, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                // Verification Badge
                Surface(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    color = Color(0xFF00A36C),
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp).padding(2.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Rajesh Kumar", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "citizen@test.com", color = Color.LightGray, fontSize = 14.sp)
            
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "Aadhaar Verified",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Stats Card Overlay
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundLight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    ProfileStatItem(Modifier.weight(1f), complaints.size.toString(), "Total")
                    ProfileStatItem(Modifier.weight(1f), complaints.count { it.status != ComplaintStatus.RESOLVED }.toString(), "Active")
                    ProfileStatItem(Modifier.weight(1f), complaints.count { it.status == ComplaintStatus.RESOLVED }.toString(), "Resolved")
                }

                Spacer(modifier = Modifier.height(32.dp))
                
                SectionTitle("ACCOUNT SETTINGS")
                SettingsItem(Icons.Default.Notifications, "Notifications", "Enabled")
                SettingsItem(Icons.Default.LocationOn, "Language", "English (India)")
                SettingsItem(Icons.Default.Lock, "Security & Privacy", "")

                Spacer(modifier = Modifier.height(24.dp))
                
                SectionTitle("SUPPORT & INFO")
                SettingsItem(Icons.Default.Info, "Help Center", "")
                SettingsItem(Icons.Default.Star, "Rate the App", "")
                SettingsItem(Icons.Default.ExitToApp, "Log Out", "", isDestructive = true, onClick = onLogout)
                
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "App Version 2.4.0 (Stable)",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = TextGrey,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ProfileStatItem(modifier: Modifier, count: String, label: String) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SecondaryBlue)
        Text(text = label, fontSize = 12.sp, color = TextGrey)
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = TextGrey,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    value: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        color = Color.White,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        if (isDestructive) Color(0xFFFFEBEE) else Color(0xFFF1F4F9),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    null,
                    tint = if (isDestructive) Color.Red else SecondaryBlue,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDestructive) Color.Red else Color.Black
            )
            Spacer(modifier = Modifier.weight(1f))
            if (value.isNotEmpty()) {
                Text(text = value, fontSize = 13.sp, color = TextGrey)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.LightGray)
        }
    }
}