package com.example.citizencomplaintapp.ui.screens.officer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citizencomplaintapp.data.model.Officer
import com.example.citizencomplaintapp.ui.screens.citizen.ComplaintCard
import com.example.citizencomplaintapp.ui.theme.*
import com.example.citizencomplaintapp.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfficerDetailsScreen(
    officerId: String,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onComplaintClick: (String) -> Unit
) {
    val officers by viewModel.officers.collectAsState()
    val complaints by viewModel.complaints.collectAsState()
    
    val officer = officers.find { it.officerId == officerId }
    if (officer == null) return

    val assignedComplaints = complaints.filter { it.officerName == officer.name }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Officer Profile", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SecondaryBlue)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(innerPadding)
        ) {
            item {
                // Profile Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SecondaryBlue)
                        .padding(bottom = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = officer.name.take(1),
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = officer.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = officer.designation,
                            fontSize = 14.sp,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = PrimaryOrange,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = officer.department,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            item {
                // Contact Info
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .offset(y = (-20).dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Email, null, tint = SecondaryBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = officer.email, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, null, tint = SecondaryBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = officer.phoneNumber, fontSize = 14.sp)
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatBox(modifier = Modifier.weight(1f), label = "Assigned", value = officer.assignedComplaints.toString(), color = Color(0xFFFFF7E6))
                    StatBox(modifier = Modifier.weight(1f), label = "Resolved", value = officer.resolvedComplaints.toString(), color = Color(0xFFF0F7F4))
                    StatBox(modifier = Modifier.weight(1f), label = "Rating", value = "${officer.rating} ★", color = Color(0xFFE8F0F7))
                }
            }

            item {
                Text(
                    text = "Assigned Tasks (${assignedComplaints.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SecondaryBlue,
                    modifier = Modifier.padding(16.dp)
                )
            }

            if (assignedComplaints.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(text = "No tasks currently assigned.", color = TextGrey)
                    }
                }
            } else {
                items(assignedComplaints) { complaint ->
                    ComplaintCard(
                        complaint = complaint,
                        onClick = { onComplaintClick(complaint.complaintId) }
                    )
                }
            }
        }
    }
}

@Composable
fun StatBox(modifier: Modifier, label: String, value: String, color: Color) {
    Surface(
        modifier = modifier.height(60.dp),
        color = color,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = label, fontSize = 10.sp, color = TextGrey)
        }
    }
}
