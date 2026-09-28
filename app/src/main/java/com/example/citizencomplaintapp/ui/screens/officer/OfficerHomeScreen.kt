package com.example.citizencomplaintapp.ui.screens.officer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citizencomplaintapp.data.model.Complaint
import com.example.citizencomplaintapp.data.model.ComplaintStatus
import com.example.citizencomplaintapp.ui.screens.citizen.StatCard
import com.example.citizencomplaintapp.ui.theme.*
import com.example.citizencomplaintapp.ui.viewmodel.MainViewModel

@Composable
fun OfficerHomeScreen(
    viewModel: MainViewModel,
    onComplaintClick: (String) -> Unit
) {
    val complaints by viewModel.complaints.collectAsState()
    val officers by viewModel.officers.collectAsState()
    var selectedComplaintForUpdate by remember { mutableStateOf<Complaint?>(null) }
    
    if (selectedComplaintForUpdate != null) {
        StatusUpdateDialog(
            complaint = selectedComplaintForUpdate!!,
            officers = officers,
            onDismiss = { selectedComplaintForUpdate = null },
            onUpdate = { id, newStatus, assignedOfficer ->
                if (newStatus == ComplaintStatus.ASSIGNED && assignedOfficer != null) {
                    viewModel.assignOfficer(id, assignedOfficer)
                } else {
                    viewModel.updateComplaintStatus(id, newStatus)
                }
                selectedComplaintForUpdate = null
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SecondaryBlue)
                .padding(20.dp)
        ) {
            Column {
                Text(text = "Good afternoon,", color = Color.LightGray, fontSize = 14.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Priya 👋", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        color = Color(0xFFC8643C),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Build, null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Officer", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item {
                // Statistics Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Person,
                        count = "1",
                        label = "Assigned",
                        iconBg = Color(0xFFFFF7E6)
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Refresh,
                        count = "1",
                        label = "In Progress",
                        iconBg = Color(0xFFE8F0F7)
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.CheckCircle,
                        count = "5",
                        label = "Resolved",
                        iconBg = Color(0xFFF0F7F4)
                    )
                }
            }

            item {
                // AI Route Optimization Banner
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF2FF)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC7D2FE))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = Color(0xFF4F46E5))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "AI Route Optimized", color = Color(0xFF4F46E5), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "Shortest path for 3 site visits generated.", color = Color(0xFF4F46E5).copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = { /* TODO: Open Map */ }) {
                            Text("View Map", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            
            item {
                // Active Duty Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9EE)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE0B2))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.DateRange, null, tint = Color(0xFFB37400), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Active Duty", color = Color(0xFFB37400), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "You have 1 priority task requiring attention.", color = Color(0xFFB37400).copy(alpha = 0.7f), fontSize = 11.sp)
                        }
                    }
                }
            }
            
            item {
                Text(
                    text = "Current Assignment",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SecondaryBlue,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            items(complaints.filter { it.officerName == "Priya Singh" }) { complaint ->
                com.example.citizencomplaintapp.ui.screens.citizen.ComplaintCard(
                    complaint = complaint,
                    onClick = { onComplaintClick(complaint.complaintId) },
                    onUpdateClick = { selectedComplaintForUpdate = complaint }
                )
            }
        }
    }
}

@Composable
fun StatusUpdateDialog(
    complaint: Complaint,
    officers: List<com.example.citizencomplaintapp.data.model.Officer>,
    onDismiss: () -> Unit,
    onUpdate: (String, ComplaintStatus, String?) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(complaint.status) }
    var selectedOfficer by remember { mutableStateOf(complaint.officerName ?: "") }
    var expanded by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Update Status & Assignment", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(text = "Issue: ${complaint.title}", fontSize = 14.sp, color = TextGrey)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(text = "Select Status", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                ComplaintStatus.entries.forEach { status ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedStatus = status }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = status.name.lowercase().replaceFirstChar { it.uppercase() })
                    }
                }

                if (selectedStatus == ComplaintStatus.ASSIGNED) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Assign to Officer", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = selectedOfficer.ifEmpty { "Select Officer" })
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(Icons.Default.ArrowDropDown, null)
                            }
                        }
                        
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(0.7f).background(Color.White)
                        ) {
                            officers.forEach { officer ->
                                DropdownMenuItem(
                                    text = { 
                                        Column {
                                            Text(officer.name, fontWeight = FontWeight.Bold)
                                            Text(officer.department, fontSize = 10.sp, color = TextGrey)
                                        }
                                    },
                                    onClick = {
                                        selectedOfficer = officer.name
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    onUpdate(
                        complaint.complaintId, 
                        selectedStatus, 
                        if (selectedStatus == ComplaintStatus.ASSIGNED) selectedOfficer else null
                    ) 
                },
                enabled = selectedStatus != ComplaintStatus.ASSIGNED || selectedOfficer.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}