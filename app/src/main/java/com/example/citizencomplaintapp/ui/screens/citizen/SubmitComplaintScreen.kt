package com.example.citizencomplaintapp.ui.screens.citizen

import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.citizencomplaintapp.data.model.Priority
import com.example.citizencomplaintapp.ui.theme.BackgroundLight
import com.example.citizencomplaintapp.ui.theme.PrimaryOrange
import com.example.citizencomplaintapp.ui.theme.SecondaryBlue
import com.example.citizencomplaintapp.ui.theme.TextGrey
import com.example.citizencomplaintapp.ui.viewmodel.MainViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitComplaintScreen(
    viewModel: MainViewModel,
    onSubmitted: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Roads & Potholes") }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var isAnonymous by remember { mutableStateOf(false) }
    var isAiAnalyzing by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var duplicateComplaint by remember { mutableStateOf<com.example.citizencomplaintapp.data.model.Complaint?>(null) }

    val context = LocalContext.current
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedImageUri = it }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri = tempPhotoUri
        }
    }

    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(null)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Choose Image Source") },
            text = { Text("How would you like to add proof?") },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    val file = createImageFile()
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    tempPhotoUri = uri
                    cameraLauncher.launch(uri)
                }) {
                    Icon(Icons.Default.Call, null) // Using available icon
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Take Photo")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    imagePickerLauncher.launch("image/*")
                }) {
                    Icon(Icons.Default.Add, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gallery")
                }
            }
        )
    }

    LaunchedEffect(title) {
        if (title.length > 10) {
            duplicateComplaint = viewModel.checkDuplicate(title)
        } else {
            duplicateComplaint = null
        }
    }

    val categories = listOf(
        CategoryItem("Roads & Potholes", "Public Works", Icons.Default.Build),
        CategoryItem("Water Supply", "Water Department", Icons.Default.Build),
        CategoryItem("Electricity", "Power Department", Icons.Default.Build),
        CategoryItem("Sanitation", "Municipal Corp", Icons.Default.Build),
        CategoryItem("Streetlights", "Power Department", Icons.Default.Build),
        CategoryItem("Environment", "Pollution Control", Icons.Default.Build),
        CategoryItem("Traffic", "Traffic Police", Icons.Default.Build),
        CategoryItem("Public Safety", "Police Dept", Icons.Default.Build)
    )

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
                Text(text = "Submit Complaint", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = "Report an issue to the concerned department", color = Color.LightGray, fontSize = 12.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Complaint Title *", fontWeight = FontWeight.Bold, color = SecondaryBlue, fontSize = 14.sp)
                Spacer(modifier = Modifier.weight(1f))
                if (title.length > 5) {
                    TextButton(
                        onClick = {
                            isAiAnalyzing = true
                            val result = viewModel.analyzeComplaintText(title)
                            selectedCategory = result.first
                            selectedPriority = result.second
                            isAiAnalyzing = false
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF4F46E5))
                    ) {
                        Icon(Icons.Default.Star, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AI Smart Detect", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            duplicateComplaint?.let { duplicate ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFFB74D))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null, tint = Color(0xFFEF6C00))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Similar Complaint Found", fontWeight = FontWeight.Bold, color = Color(0xFFE65100), fontSize = 14.sp)
                        }
                        Text(
                            text = "A similar issue was reported recently: '${duplicate.title}'. Do you want to support the existing one instead?",
                            fontSize = 12.sp,
                            color = Color(0xFFBF360C),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { /* TODO: Support existing */ }) {
                                Text("Support Existing", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Brief title of your issue", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Text(text = "Description *", fontWeight = FontWeight.Bold, color = SecondaryBlue, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Describe the issue in detail (min 20 chars)", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth().height(120.dp).padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Text(text = "Category *", fontWeight = FontWeight.Bold, color = SecondaryBlue, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
            
            categories.chunked(2).forEach { pair ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    pair.forEach { cat ->
                        CategoryCard(
                            item = cat,
                            isSelected = selectedCategory == cat.name,
                            modifier = Modifier.weight(1f).padding(bottom = 8.dp),
                            onClick = { selectedCategory = cat.name }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Priority Level", fontWeight = FontWeight.Bold, color = SecondaryBlue, fontSize = 14.sp)
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Priority.entries.forEach { p ->
                    val isSelected = selectedPriority == p
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedPriority = p },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) PrimaryOrange else Color.White
                        )
                    ) {
                        Box(modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                text = p.name.lowercase().replaceFirstChar { it.uppercase() },
                                color = if (isSelected) Color.White else Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Text(text = "Location *", fontWeight = FontWeight.Bold, color = SecondaryBlue, fontSize = 14.sp)
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                placeholder = { Text("Enter or detect your location", color = Color.LightGray) },
                trailingIcon = { 
                    Row {
                        IconButton(onClick = { /* TODO: Speech to Text */ }) {
                            Icon(Icons.Default.Call, null, tint = SecondaryBlue)
                        }
                        Icon(Icons.Default.LocationOn, null, tint = SecondaryBlue, modifier = Modifier.padding(12.dp))
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Text(text = "Evidence / Photo", fontWeight = FontWeight.Bold, color = SecondaryBlue, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(BorderStroke(1.dp, Color.LightGray), RoundedCornerShape(12.dp))
                    .clickable { showImageSourceDialog = true },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri == null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(40.dp))
                        Text("Add Photo Proof", color = TextGrey, fontSize = 14.sp)
                        Text("Click to browse gallery", color = Color.LightGray, fontSize = 11.sp)
                    }
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUri),
                        contentDescription = "Selected Evidence",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { selectedImageUri = null },
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White)
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                Checkbox(checked = isAnonymous, onCheckedChange = { isAnonymous = it })
                Text(text = "Report Anonymously", fontSize = 14.sp, color = SecondaryBlue)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.submitComplaint(
                        title, 
                        description, 
                        selectedCategory, 
                        "Public Works", 
                        location, 
                        selectedPriority, 
                        isAnonymous,
                        selectedImageUri?.toString()
                    )
                    onSubmitted()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
            ) {
                Text(text = "Submit Complaint", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun CategoryCard(item: CategoryItem, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = if (isSelected) BorderStroke(1.5.dp, PrimaryOrange) else null
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(36.dp).background(Color(0xFFE8F0F7), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(item.icon, null, tint = SecondaryBlue, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = item.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = item.dept, fontSize = 10.sp, color = TextGrey)
            }
        }
    }
}

data class CategoryItem(val name: String, val dept: String, val icon: ImageVector)