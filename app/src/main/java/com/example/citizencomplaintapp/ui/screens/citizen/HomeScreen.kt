package com.example.citizencomplaintapp.ui.screens.citizen

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.citizencomplaintapp.data.model.Complaint
import com.example.citizencomplaintapp.data.model.ComplaintStatus
import com.example.citizencomplaintapp.data.model.Priority
import com.example.citizencomplaintapp.ui.theme.*
import com.example.citizencomplaintapp.ui.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onComplaintClick: (String) -> Unit,
    onReportIssueClick: () -> Unit
) {
    val complaints by viewModel.complaints.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Top Profile Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SecondaryBlue)
                .padding(20.dp)
        ) {
            Column {
                Text(text = "Good morning,", color = Color.LightGray, fontSize = 14.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Rajesh 👋", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, tint = PrimaryOrange, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Citizen", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
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
                        icon = Icons.Default.List,
                        count = complaints.size.toString(),
                        label = "Total",
                        iconBg = Color(0xFFE8F0F7)
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Refresh,
                        count = complaints.count { it.status != ComplaintStatus.RESOLVED }.toString(),
                        label = "Active",
                        iconBg = Color(0xFFFFF7E6)
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.CheckCircle,
                        count = complaints.count { it.status == ComplaintStatus.RESOLVED }.toString(),
                        label = "Resolved",
                        iconBg = Color(0xFFF0F7F4)
                    )
                }
            }
            
            item {
                // Feature Banner: New Complaint
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { onReportIssueClick() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryOrange)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Report a New Issue", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Submit complaint with AI classification", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        }
                        Box(
                            modifier = Modifier.size(48.dp).background(Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, null, tint = Color.White)
                        }
                    }
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Recent Complaints", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SecondaryBlue)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "View All →", color = Color.Blue, fontSize = 14.sp)
                }
            }
            
            items(complaints.take(3)) { complaint ->
                ComplaintCard(complaint, onClick = { onComplaintClick(complaint.complaintId) }, onUpvote = { viewModel.upvoteComplaint(complaint.complaintId) })
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier, icon: ImageVector, count: String, label: String, iconBg: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(iconBg, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = SecondaryBlue, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = count, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = label, fontSize = 11.sp, color = TextGrey)
        }
    }
}

@Composable
fun ComplaintCard(complaint: Complaint, onClick: () -> Unit, onUpvote: () -> Unit = {}, onUpdateClick: (() -> Unit)? = null) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Build, null, tint = Color.Red, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = complaint.complaintId, fontSize = 11.sp, color = TextGrey)
                    Text(text = complaint.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                if (onUpdateClick != null) {
                    TextButton(
                        onClick = onUpdateClick,
                        colors = ButtonDefaults.textButtonColors(contentColor = PrimaryOrange),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Update", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text(text = "5 days ago", fontSize = 11.sp, color = TextGrey)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = complaint.description,
                fontSize = 12.sp,
                color = TextGrey,
                maxLines = 2,
                lineHeight = 18.sp
            )

            if (complaint.imageUrl != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Image(
                    painter = rememberAsyncImagePainter(complaint.imageUrl),
                    contentDescription = "Evidence",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = complaint.location, fontSize = 11.sp, color = TextGrey)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(complaint.status)
                Spacer(modifier = Modifier.width(8.dp))
                PriorityBadge(complaint.priority)
                Spacer(modifier = Modifier.weight(1f))
                
                IconButton(onClick = onUpvote, modifier = Modifier.size(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                         Icon(Icons.Default.ThumbUp, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                         Spacer(modifier = Modifier.width(4.dp))
                         Text(text = complaint.supportCount.toString(), fontSize = 12.sp, color = TextGrey)
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: ComplaintStatus) {
    val color = when(status) {
        ComplaintStatus.RESOLVED -> Color(0xFFE8F5E9)
        ComplaintStatus.SUBMITTED -> Color(0xFFE3F2FD)
        else -> Color(0xFFFFF3E0)
    }
    val textColor = when(status) {
        ComplaintStatus.RESOLVED -> Color(0xFF2E7D32)
        ComplaintStatus.SUBMITTED -> Color(0xFF1976D2)
        else -> Color(0xFFEF6C00)
    }
    
    Surface(
        color = color,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = "• ${status.name.lowercase().replaceFirstChar { it.uppercase() }}",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun PriorityBadge(priority: Priority) {
    Surface(
        color = when(priority) {
            Priority.CRITICAL, Priority.EMERGENCY -> Color(0xFFFFEBEE)
            Priority.HIGH -> Color(0xFFFFF3E0)
            else -> Color(0xFFF1F4F9)
        },
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = priority.name.lowercase().replaceFirstChar { it.uppercase() },
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = when(priority) {
                Priority.CRITICAL, Priority.EMERGENCY -> Color(0xFFC62828)
                Priority.HIGH -> Color(0xFFEF6C00)
                else -> Color(0xFF455A64)
            }
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(viewModel = MainViewModel(), onComplaintClick = {}, onReportIssueClick = {})
}
