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
    val showIosTransferDialog = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    val showPwaDialog = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    
    if (showPwaDialog.value) {
        AlertDialog(
            onDismissRequest = { showPwaDialog.value = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📱 Run on iPhone (Method 1: PWA)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = Color(0xFFE3F2FD),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF1565C0))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Method 1 Ready: Complete Progressive Web App created in /pwa folder!",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0D47A1)
                            )
                        }
                    }

                    Text("No Mac, Xcode, or App Store account required!:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("• Optimized specifically for iPhone Safari with full-screen standalone mode\n• Live iPhone GPS detection + Reverse Geocoding for all 38 TN Districts\n• Interactive Leaflet map with complaint pin dropping\n• Photo attachments from iPhone Camera & Photo Library\n• Offline cache using Service Worker & LocalStorage", fontSize = 12.sp, color = Color(0xFF333333))

                    HorizontalDivider()

                    Text("How to Install on iPhone in 30 Seconds:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Option A (Automated GitHub Pages):\nClick 'Push to GitHub' in AI Studio menu. The automated GitHub Action deploys the PWA immediately.\n\nOption B (Instant Free Drag & Drop):\n1. Click 'Export to ZIP' in AI Studio menu.\n2. Open app.netlify.com/drop in any browser.\n3. Drag the 'pwa' folder into the browser.\n4. Open the generated HTTPS URL on your iPhone in Safari.\n5. Tap Share (📤) > 'Add to Home Screen' (➕)!", fontSize = 12.sp, color = Color(0xFF444444))
                }
            },
            confirmButton = {
                Button(onClick = { showPwaDialog.value = false }) {
                    Text("Got it")
                }
            }
        )
    }

    if (showIosTransferDialog.value) {
        AlertDialog(
            onDismissRequest = { showIosTransferDialog.value = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📱 Native iPhone App (Path B)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Path B Completed: Full iOS project generated in /iosApp directory!",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1B5E20)
                            )
                        }
                    }

                    Text("Features Ported to iOS (SwiftUI & MapKit):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("• All 38 Tamil Nadu districts with administrative zones & civic wards\n• Interactive Apple MapKit pin dropping with live reverse geocoding\n• Apple CoreLocation live GPS detection with Tamil Nadu ward resolution\n• Offline-first civic complaint submission & community upvoting", fontSize = 12.sp, color = Color(0xFF333333))

                    HorizontalDivider()

                    Text("How to Install on your iPhone:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("1. In AI Studio menu (top right), choose 'Export to ZIP' or 'Push to GitHub'.\n2. Open the 'iosApp' folder on any Mac in Xcode (or use GitHub Actions workflow).\n3. Connect your iPhone via USB, select your Apple ID in Signing, and click Run!\n4. The app will install natively onto your iPhone home screen.", fontSize = 12.sp, color = Color(0xFF555555))
                }
            },
            confirmButton = {
                Button(onClick = { showIosTransferDialog.value = false }) {
                    Text("Got it")
                }
            }
        )
    }

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
                SettingsItem(
                    icon = Icons.Default.Phone,
                    title = "📱 Run on iPhone (Method 1: PWA)",
                    value = "No Mac Needed",
                    onClick = { showPwaDialog.value = true }
                )
                SettingsItem(
                    icon = Icons.Default.Share,
                    title = "💻 Native iOS App (Xcode / Mac)",
                    value = "Ready in /iosApp",
                    onClick = { showIosTransferDialog.value = true }
                )
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