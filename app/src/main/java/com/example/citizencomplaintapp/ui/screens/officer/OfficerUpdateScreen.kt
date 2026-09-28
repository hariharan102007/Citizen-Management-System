package com.example.citizencomplaintapp.ui.screens.officer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
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
import com.example.citizencomplaintapp.ui.screens.citizen.ComplaintCard
import com.example.citizencomplaintapp.ui.theme.*
import com.example.citizencomplaintapp.ui.viewmodel.MainViewModel

@Composable
fun OfficerUpdateScreen(viewModel: MainViewModel) {
    val complaints by viewModel.complaints.collectAsState()
    val officers by viewModel.officers.collectAsState()
    var selectedComplaintForUpdate by remember { mutableStateOf<Complaint?>(null) }
    
    // For demo, assume this officer is Priya Singh
    val assignedComplaints = complaints.filter { it.officerName == "Priya Singh" }

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
            Text(text = "Update My Tasks", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        
        if (assignedComplaints.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF00A36C),
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "All Clear!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = SecondaryBlue)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "No complaints currently assigned to you.", fontSize = 14.sp, color = TextGrey)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(assignedComplaints) { complaint ->
                    ComplaintCard(
                        complaint = complaint,
                        onClick = { },
                        onUpdateClick = { selectedComplaintForUpdate = complaint }
                    )
                }
            }
        }
    }
}