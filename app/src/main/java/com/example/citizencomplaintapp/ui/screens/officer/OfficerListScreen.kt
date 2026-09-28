package com.example.citizencomplaintapp.ui.screens.officer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citizencomplaintapp.data.model.Officer
import com.example.citizencomplaintapp.ui.theme.BackgroundLight
import com.example.citizencomplaintapp.ui.theme.PrimaryOrange
import com.example.citizencomplaintapp.ui.theme.SecondaryBlue
import com.example.citizencomplaintapp.ui.theme.TextGrey
import com.example.citizencomplaintapp.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfficerListScreen(
    viewModel: MainViewModel,
    onOfficerClick: (String) -> Unit
) {
    val officers by viewModel.officers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredOfficers = officers.filter {
        it.name.contains(searchQuery, ignoreCase = true) || 
        it.department.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // ... (Header and Search Bar code remains same) ...
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SecondaryBlue)
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Personnel Directory",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Manage and view all department officers",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by name or department...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredOfficers) { officer ->
                OfficerCard(officer, onClick = { onOfficerClick(officer.officerId) })
            }
        }
    }
}

@Composable
fun OfficerCard(officer: Officer, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Profile Avatar Placeholder
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF0F2F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = officer.name.take(1),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryBlue
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = officer.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = officer.designation,
                        fontSize = 13.sp,
                        color = PrimaryOrange,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Surface(
                    color = Color(0xFFE8F0F7),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = officer.department,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryBlue
                    )
                }
            }
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.LightGray.copy(alpha = 0.5f)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem(Icons.Default.Email, officer.email)
                InfoItem(Icons.Default.Phone, officer.phoneNumber)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatBadge(label = "Active", value = officer.assignedComplaints.toString(), color = Color(0xFFFFF7E6))
                StatBadge(label = "Resolved", value = officer.resolvedComplaints.toString(), color = Color(0xFFF0F7F4))
                StatBadge(label = "Rating", value = "${officer.rating} ★", color = Color(0xFFE8F0F7))
            }
        }
    }
}

@Composable
fun InfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextGrey)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, fontSize = 12.sp, color = TextGrey)
    }
}

@Composable
fun StatBadge(label: String, value: String, color: Color) {
    Surface(
        modifier = Modifier.height(48.dp),
        color = color,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = label, fontSize = 9.sp, color = TextGrey)
        }
    }
}
