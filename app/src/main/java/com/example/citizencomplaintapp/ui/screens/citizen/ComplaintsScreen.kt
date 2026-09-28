package com.example.citizencomplaintapp.ui.screens.citizen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.example.citizencomplaintapp.ui.screens.officer.StatusUpdateDialog
import com.example.citizencomplaintapp.ui.theme.BackgroundLight
import com.example.citizencomplaintapp.ui.theme.SecondaryBlue
import com.example.citizencomplaintapp.ui.theme.TextGrey
import com.example.citizencomplaintapp.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintsScreen(
    viewModel: MainViewModel,
    role: String,
    onComplaintClick: (String) -> Unit
) {
    val complaints by viewModel.complaints.collectAsState()
    val officers by viewModel.officers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
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
    
    val filters = listOf("All", "Submitted", "Assigned", "In Progress", "Resolved", "Closed")

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
                Text(text = "My Complaints", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = "${complaints.size} complaints", color = Color.LightGray, fontSize = 12.sp)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by title, ID or category...", color = Color.Gray, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
        }

        // Filter Chips
        LazyRow(
            modifier = Modifier.padding(vertical = 12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { filter ->
                val isSelected = selectedFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = filter },
                    label = { Text(text = filter, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SecondaryBlue,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = SecondaryBlue
                    ),
                    border = null,
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        // List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(complaints) { complaint ->
                ComplaintCard(
                    complaint = complaint,
                    onClick = { onComplaintClick(complaint.complaintId) },
                    onUpdateClick = if (role == "Officer") { { selectedComplaintForUpdate = complaint } } else null
                )
            }
        }
    }
}