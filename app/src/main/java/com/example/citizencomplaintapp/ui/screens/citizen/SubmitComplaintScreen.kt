package com.example.citizencomplaintapp.ui.screens.citizen

import android.Manifest
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import com.example.citizencomplaintapp.data.model.TNDistrict
import com.example.citizencomplaintapp.data.model.TNRegion
import com.example.citizencomplaintapp.data.model.TamilNaduData
import com.example.citizencomplaintapp.ui.theme.BackgroundLight
import com.example.citizencomplaintapp.ui.theme.PrimaryOrange
import com.example.citizencomplaintapp.ui.theme.SecondaryBlue
import com.example.citizencomplaintapp.ui.theme.TextGrey
import com.example.citizencomplaintapp.ui.utils.LocationUtils
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
    var detectedLatitude by remember { mutableStateOf<Double?>(null) }
    var detectedLongitude by remember { mutableStateOf<Double?>(null) }
    var isDetectingLocation by remember { mutableStateOf(false) }
    var locationStatusMessage by remember { mutableStateOf<String?>(null) }
    var showTamilNaduDistrictsDialog by remember { mutableStateOf(false) }
    var showMapPicker by remember { mutableStateOf(false) }
    var selectedTNDistrict by remember { mutableStateOf(TamilNaduData.ALL_38_DISTRICTS[0]) } // Chennai default
    var districtSearchQuery by remember { mutableStateOf("") }
    var selectedRegionFilter by remember { mutableStateOf(TNRegion.ALL) }
    var customStreetAddress by remember { mutableStateOf("") }

    var selectedCategory by remember { mutableStateOf("Roads & Potholes") }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var isAnonymous by remember { mutableStateOf(false) }
    var isAiAnalyzing by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var duplicateComplaint by remember { mutableStateOf<com.example.citizencomplaintapp.data.model.Complaint?>(null) }

    val context = LocalContext.current

    fun startLocationDetection() {
        isDetectingLocation = true
        locationStatusMessage = "Connecting to device GPS..."
        LocationUtils.getRealLocation(context) { result ->
            isDetectingLocation = false
            if (result != null) {
                location = result.address
                detectedLatitude = result.latitude
                detectedLongitude = result.longitude
                selectedTNDistrict = TamilNaduData.findNearestDistrict(result.latitude, result.longitude)
                
                if (result.isTamilNaduLocation || result.isIndianLocation) {
                    locationStatusMessage = "Live GPS: ${result.districtName}, Tamil Nadu (${String.format(Locale.US, "%.4f° N, %.4f° E", result.latitude, result.longitude)})"
                    Toast.makeText(context, "Live location recognized in ${result.districtName}!", Toast.LENGTH_SHORT).show()
                } else {
                    // Cloud emulator reported overseas data center (e.g. California)
                    locationStatusMessage = "Cloud emulator GPS detected. Select from 38 Tamil Nadu districts or pick on map."
                    showTamilNaduDistrictsDialog = true
                    Toast.makeText(context, "Cloud emulator GPS. Choose your Tamil Nadu district or select on Map.", Toast.LENGTH_LONG).show()
                }
            } else {
                showTamilNaduDistrictsDialog = true
                Toast.makeText(context, "Could not acquire GPS. Select from 38 Tamil Nadu districts or pick on map.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            startLocationDetection()
        } else {
            Toast.makeText(context, "Location permission is required to detect real location", Toast.LENGTH_LONG).show()
        }
    }

    fun requestRealLocation() {
        if (LocationUtils.hasLocationPermission(context)) {
            startLocationDetection()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
    
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

    if (showTamilNaduDistrictsDialog) {
        AlertDialog(
            onDismissRequest = { showTamilNaduDistrictsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🇮🇳", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Tamil Nadu 38 Districts", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = SecondaryBlue)
                        Text("Select your district or pick exact location on map", fontSize = 11.sp, color = TextGrey)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 440.dp)
                ) {
                    // Search bar
                    OutlinedTextField(
                        value = districtSearchQuery,
                        onValueChange = { districtSearchQuery = it },
                        placeholder = { Text("Search 38 TN districts (e.g. Madurai, Salem)...", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(18.dp), tint = SecondaryBlue) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Region Filter Tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        TNRegion.entries.forEach { reg ->
                            val isSel = selectedRegionFilter == reg
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedRegionFilter = reg },
                                label = { Text(reg.title, fontSize = 10.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SecondaryBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val filteredDistricts = TamilNaduData.ALL_38_DISTRICTS.filter { dist ->
                        val matchesSearch = dist.name.contains(districtSearchQuery, ignoreCase = true) ||
                                dist.zoneName.contains(districtSearchQuery, ignoreCase = true) ||
                                dist.popularWards.any { it.contains(districtSearchQuery, ignoreCase = true) }
                        val matchesRegion = selectedRegionFilter == TNRegion.ALL || dist.region == selectedRegionFilter
                        matchesSearch && matchesRegion
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        filteredDistricts.forEach { dist ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Place, null, tint = PrimaryOrange, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column {
                                                Text(dist.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SecondaryBlue)
                                                Text("${dist.zoneName} • ${dist.region.title}", fontSize = 10.sp, color = TextGrey)
                                            }
                                        }
                                        // Pick on map button for this district
                                        OutlinedButton(
                                            onClick = {
                                                selectedTNDistrict = dist
                                                detectedLatitude = dist.lat
                                                detectedLongitude = dist.lng
                                                showTamilNaduDistrictsDialog = false
                                                showMapPicker = true
                                            },
                                            modifier = Modifier.height(30.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp),
                                            shape = RoundedCornerShape(6.dp),
                                            border = BorderStroke(1.dp, Color(0xFF4F46E5))
                                        ) {
                                            Icon(Icons.Default.Place, null, modifier = Modifier.size(13.dp), tint = Color(0xFF4F46E5))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text("Map Pin", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4F46E5))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Popular Wards / Localities:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))

                                    // Ward chips
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 4.dp)
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        dist.popularWards.forEach { ward ->
                                            SuggestionChip(
                                                onClick = {
                                                    selectedTNDistrict = dist
                                                    location = "$ward, ${dist.name}, Tamil Nadu"
                                                    detectedLatitude = dist.lat + (Math.random() - 0.5) * 0.015
                                                    detectedLongitude = dist.lng + (Math.random() - 0.5) * 0.015
                                                    locationStatusMessage = "Selected: $ward (${dist.name})"
                                                    showTamilNaduDistrictsDialog = false
                                                },
                                                label = { Text(ward, fontSize = 10.sp) },
                                                colors = SuggestionChipDefaults.suggestionChipColors(
                                                    containerColor = Color.White
                                                ),
                                                border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Direct Map Button
                    Button(
                        onClick = {
                            showTamilNaduDistrictsDialog = false
                            showMapPicker = true
                        },
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                    ) {
                        Icon(Icons.Default.Place, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Open Full Map to Select Pin Manually", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showTamilNaduDistrictsDialog = false }) {
                    Text("Close", color = TextGrey)
                }
            }
        )
    }

    if (showMapPicker) {
        MapLocationPickerDialog(
            initialLat = detectedLatitude,
            initialLng = detectedLongitude,
            onDismiss = { showMapPicker = false },
            onLocationConfirmed = { lat, lng, addr ->
                detectedLatitude = lat
                detectedLongitude = lng
                location = addr
                locationStatusMessage = "Verified Tamil Nadu GPS: ${String.format(Locale.US, "%.5f° N, %.5f° E", lat, lng)}"
                showMapPicker = false
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

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Location *", fontWeight = FontWeight.Bold, color = SecondaryBlue, fontSize = 14.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = { showMapPicker = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF4F46E5)),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Default.Place, null, modifier = Modifier.size(15.dp), tint = Color(0xFF4F46E5))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Pick on Map", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    TextButton(
                        onClick = { showTamilNaduDistrictsDialog = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = SecondaryBlue),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text("38 Districts", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    TextButton(
                        onClick = { requestRealLocation() },
                        enabled = !isDetectingLocation,
                        colors = ButtonDefaults.textButtonColors(contentColor = PrimaryOrange),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        if (isDetectingLocation) {
                            CircularProgressIndicator(modifier = Modifier.size(13.dp), strokeWidth = 2.dp, color = PrimaryOrange)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Detecting...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.LocationOn, contentDescription = "Live GPS", modifier = Modifier.size(15.dp), tint = PrimaryOrange)
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Live GPS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            OutlinedTextField(
                value = location,
                onValueChange = { 
                    location = it 
                },
                placeholder = { Text("Enter address, pick on map, or auto-detect GPS", color = Color.LightGray) },
                trailingIcon = { 
                    Row {
                        IconButton(onClick = { showMapPicker = true }) {
                            Icon(Icons.Default.Place, contentDescription = "Pick on Map", tint = Color(0xFF4F46E5))
                        }
                        IconButton(
                            onClick = { requestRealLocation() },
                            enabled = !isDetectingLocation
                        ) {
                            if (isDetectingLocation) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = PrimaryOrange)
                            } else {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = "Detect Real Location",
                                    tint = if (detectedLatitude != null) Color(0xFF16A34A) else PrimaryOrange
                                )
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            if (detectedLatitude != null && detectedLongitude != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFDCFCE7),
                    border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Verified Live Location (${selectedTNDistrict.name} District)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534)
                            )
                            Text(
                                text = String.format(Locale.US, "%.5f° N, %.5f° E", detectedLatitude, detectedLongitude),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D)
                            )
                        }
                        OutlinedButton(
                            onClick = { showMapPicker = true },
                            modifier = Modifier.height(30.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, Color(0xFF16A34A))
                        ) {
                            Icon(Icons.Default.Place, null, modifier = Modifier.size(13.dp), tint = Color(0xFF16A34A))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Adjust on Map", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                        }
                    }
                }
            } else if (locationStatusMessage != null) {
                Text(
                    text = locationStatusMessage ?: "",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

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
                        title = title, 
                        description = description, 
                        category = selectedCategory, 
                        department = "Public Works", 
                        location = location.ifBlank { "Live GPS Pin" }, 
                        priority = selectedPriority, 
                        isAnonymous = isAnonymous, 
                        imageUrl = selectedImageUri?.toString(),
                        latitude = detectedLatitude ?: (selectedTNDistrict.lat + (Math.random() - 0.5) * 0.005),
                        longitude = detectedLongitude ?: (selectedTNDistrict.lng + (Math.random() - 0.5) * 0.005)
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