package com.example.citizencomplaintapp.ui.screens.admin

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.citizencomplaintapp.data.model.Complaint
import com.example.citizencomplaintapp.data.model.ComplaintStatus
import com.example.citizencomplaintapp.data.model.Priority
import com.example.citizencomplaintapp.ui.theme.*
import com.example.citizencomplaintapp.ui.viewmodel.MainViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter

@Composable
fun AdminAssignScreen(viewModel: MainViewModel) {
    val complaints by viewModel.complaints.collectAsState()
    val stats by viewModel.departmentStats.collectAsState()
    val awaitingAssignment = complaints.filter { it.status == ComplaintStatus.SUBMITTED }
    var showAssignDialog by remember { mutableStateOf(false) }
    var selectedComplaintId by remember { mutableStateOf<String?>(null) }
    var showAnalytics by remember { mutableStateOf(false) }

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Officer Dashboard", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { showAnalytics = !showAnalytics }) {
                    Icon(if (showAnalytics) Icons.Default.List else Icons.Default.Info, null, tint = Color.White)
                }
            }
        }

        if (showAnalytics) {
            AdminAnalyticsScreen(stats)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBE9E7))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, tint = Color(0xFFD84315))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "${awaitingAssignment.size} complaints awaiting assignment",
                                color = Color(0xFFD84315),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                items(awaitingAssignment) { complaint ->
                    AssignCard(complaint) {
                        selectedComplaintId = complaint.complaintId
                        showAssignDialog = true
                    }
                }
            }
        }
    }

    if (showAssignDialog) {
        SelectOfficerDialog(
            onDismiss = { showAssignDialog = false },
            onOfficerSelected = { officer ->
                showAssignDialog = false
            }
        )
    }
}

@Composable
fun AdminAnalyticsScreen(stats: Map<String, Float>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(text = "Department Performance Ranking", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SecondaryBlue)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth().height(300.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                PerformanceChart(stats)
            }
        }

        item {
            Text(text = "Predictive Analytics", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SecondaryBlue)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF2FF))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFF4F46E5))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "AI Insights", fontWeight = FontWeight.Bold, color = Color(0xFF4F46E5))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Road complaints likely to increase next month in Zone 5 based on monsoon trends.",
                        fontSize = 14.sp,
                        color = SecondaryBlue
                    )
                }
            }
        }
    }
}

@Composable
fun PerformanceChart(stats: Map<String, Float>) {
    val entries = stats.values.mapIndexed { index, value -> BarEntry(index.toFloat(), value) }
    val labels = stats.keys.toList()

    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                description.isEnabled = false
                legend.isEnabled = false
                setDrawGridBackground(false)
                setDrawBarShadow(false)
                
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return labels.getOrNull(value.toInt()) ?: ""
                        }
                    }
                    granularity = 1f
                }
                
                axisLeft.apply {
                    setDrawGridLines(true)
                    axisMinimum = 0f
                    axisMaximum = 100f
                }
                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            val dataSet = BarDataSet(entries, "Resolution Rate %").apply {
                color = SecondaryBlue.toArgb()
                valueTextSize = 10f
                setDrawValues(true)
            }
            chart.data = BarData(dataSet)
            chart.invalidate()
        },
        modifier = Modifier.fillMaxSize().padding(16.dp)
    )
}

@Composable
fun AssignCard(complaint: Complaint, onAssignClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = complaint.complaintId, fontSize = 11.sp, color = TextGrey)
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    color = if (complaint.priority == Priority.CRITICAL || complaint.priority == Priority.EMERGENCY) PriorityCritical else PriorityHigh,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = complaint.priority.name.lowercase().replaceFirstChar { it.uppercase() },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (complaint.priority == Priority.CRITICAL || complaint.priority == Priority.EMERGENCY) PriorityCriticalText else PriorityHighText
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = complaint.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = "${complaint.categoryName} • ${complaint.location}", fontSize = 12.sp, color = TextGrey, modifier = Modifier.padding(top = 4.dp))

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAssignClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryBlue)
            ) {
                Text("Assign Officer", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectOfficerDialog(onDismiss: () -> Unit, onOfficerSelected: (String) -> Unit) {
    val officers = listOf(
        OfficerItem("Priya Singh", "Public Works", "P"),
        OfficerItem("Rahul Verma", "Water Department", "R"),
        OfficerItem("Anita Sharma", "Power Department", "A"),
        OfficerItem("Mohan Das", "Municipal Corp", "M"),
        OfficerItem("Suresh Kumar", "Traffic Police", "S")
    )

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.fillMaxSize(),
        content = {
            Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Select Officer", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SecondaryBlue)
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, null)
                        }
                    }
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    LazyColumn {
                        items(officers) { officer ->
                            OfficerRow(officer) { onOfficerSelected(officer.name) }
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF0F0F0))
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun OfficerRow(officer: OfficerItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(SecondaryBlue, androidx.compose.foundation.shape.CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = officer.initial, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = officer.name, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(text = officer.dept, fontSize = 12.sp, color = TextGrey)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.LightGray)
    }
}

data class OfficerItem(val name: String, val dept: String, val initial: String)