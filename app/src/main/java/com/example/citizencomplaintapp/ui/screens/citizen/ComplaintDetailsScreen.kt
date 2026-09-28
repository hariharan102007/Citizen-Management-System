package com.example.citizencomplaintapp.ui.screens.citizen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.citizencomplaintapp.data.model.Complaint
import com.example.citizencomplaintapp.data.model.ComplaintStatus
import com.example.citizencomplaintapp.ui.utils.QrUtils
import com.example.citizencomplaintapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintDetailsScreen(
    complaint: Complaint?,
    onNavigateBack: () -> Unit
) {
    if (complaint == null) return

    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    
    // Generate QR Code content (e.g., a tracking link or just the ID)
    val qrBitmap = remember(complaint.complaintId) {
        QrUtils.generateQrCode("https://civicresolve.gov/track/${complaint.complaintId}")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Complaint Details", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Share */ }) {
                        Icon(Icons.Default.Share, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SecondaryBlue)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (complaint.isEscalated) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Escalated to ${complaint.escalationLevel} due to SLA breach.",
                            color = Color.Red,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Main Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(40.dp).background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Build, null, tint = Color.Red, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = complaint.categoryName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = complaint.department, color = TextGrey, fontSize = 11.sp)
                        }
                        PriorityBadge(complaint.priority)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = complaint.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = complaint.description, fontSize = 13.sp, color = TextGrey, lineHeight = 18.sp)

                    Spacer(modifier = Modifier.height(16.dp))
                    DetailRow(Icons.Default.LocationOn, complaint.location)
                    DetailRow(Icons.Default.DateRange, "Created: ${dateFormat.format(complaint.createdAt)}")
                    if (complaint.deadline != null) {
                        DetailRow(Icons.Default.Warning, "SLA Deadline: ${dateFormat.format(complaint.deadline)}")
                    }
                    DetailRow(Icons.Default.Person, if (complaint.isAnonymous) "Anonymous Citizen" else "Rajesh Kumar")
                    DetailRow(Icons.Default.AccountCircle, "Officer: ${complaint.officerName ?: "Not Assigned"}")

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = Color(0xFFF1F4F9), shape = RoundedCornerShape(16.dp)) {
                            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ThumbUp, null, tint = SecondaryBlue, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "${complaint.supportCount} Support", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SecondaryBlue)
                            }
                        }
                    }
                }
            }

            // QR Code Tracking Section
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "QR Tracking", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SecondaryBlue)
                        Text(
                            text = "Scan this code with any device to see the live status of this complaint.",
                            fontSize = 12.sp,
                            color = TextGrey,
                            lineHeight = 16.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    if (qrBitmap != null) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "Tracking QR Code",
                            modifier = Modifier
                                .size(80.dp)
                                .background(Color.White)
                        )
                    } else {
                        Box(
                            modifier = Modifier.size(80.dp).background(Color(0xFFF5F5F5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Warning, null, tint = Color.LightGray)
                        }
                    }
                }
            }

            if (complaint.imageUrl != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Evidence Proof", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SecondaryBlue)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(complaint.imageUrl),
                        contentDescription = "Evidence",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Real-Time Tracking", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SecondaryBlue)
            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Timeline
            val statuses = ComplaintStatus.entries
            statuses.forEachIndexed { index, status ->
                val historyItem = complaint.statusHistory.find { it.status == status }
                val isCompleted = historyItem != null
                val isCurrent = status == complaint.status
                
                TimelineItem(
                    title = status.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                    description = historyItem?.description ?: "Pending...",
                    isCompleted = isCompleted,
                    isCurrent = isCurrent,
                    isLast = index == statuses.size - 1,
                    time = historyItem?.let { SimpleDateFormat("HH:mm", Locale.getDefault()).format(it.timestamp) } ?: ""
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Updates & Remarks (${complaint.remarks.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SecondaryBlue)
            Spacer(modifier = Modifier.height(12.dp))

            complaint.remarks.forEach { remark ->
                RemarkItem(remark.author, remark.message, remark.timeAgo)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun DetailRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 12.sp, color = TextGrey)
    }
}

@Composable
fun TimelineItem(title: String, description: String, isCompleted: Boolean, isCurrent: Boolean, isLast: Boolean, time: String) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(32.dp).background(
                    if (isCompleted || isCurrent) (if (isCompleted) Color(0xFF00A36C) else SecondaryBlue) else Color(0xFFE8F0F7),
                    CircleShape
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else if (isCurrent) Icons.Default.Refresh else Icons.Default.DateRange,
                    contentDescription = null,
                    tint = if (isCompleted || isCurrent) Color.White else Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
            if (!isLast) {
                Box(modifier = Modifier.width(2.dp).weight(1f).background(if (isCompleted) Color(0xFF00A36C) else Color(0xFFE0E0E0)))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCurrent) SecondaryBlue else if (isCompleted) Color.Black else Color.Gray
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(text = time, fontSize = 10.sp, color = TextGrey)
            }
            Text(text = description, fontSize = 12.sp, color = TextGrey)
        }
    }
}

@Composable
fun RemarkItem(author: String, message: String, time: String) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.padding(12.dp)) {
            Box(modifier = Modifier.size(32.dp).background(Color(0xFFF1F4F9), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, null, tint = SecondaryBlue, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = author, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = time, fontSize = 10.sp, color = TextGrey)
                }
                Text(text = message, fontSize = 12.sp, color = TextGrey, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}