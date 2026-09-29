package com.example.citizencomplaintapp.ui.screens.citizen

import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.citizencomplaintapp.data.model.TNDistrict
import com.example.citizencomplaintapp.data.model.TamilNaduData
import com.example.citizencomplaintapp.ui.theme.PrimaryOrange
import com.example.citizencomplaintapp.ui.theme.SecondaryBlue
import kotlinx.coroutines.launch
import java.util.Locale

import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.example.citizencomplaintapp.data.model.TNRegion
import com.example.citizencomplaintapp.ui.utils.LocationUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapLocationPickerDialog(
    initialLat: Double?,
    initialLng: Double?,
    onDismiss: () -> Unit,
    onLocationConfirmed: (lat: Double, lng: Double, address: String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Find initial district or default to Chennai
    var selectedDistrict by remember {
        mutableStateOf(
            if (initialLat != null && initialLng != null) {
                TamilNaduData.findNearestDistrict(initialLat, initialLng)
            } else {
                TamilNaduData.ALL_38_DISTRICTS[0] // Chennai
            }
        )
    }

    var pickedLat by remember { mutableStateOf(initialLat ?: selectedDistrict.lat) }
    var pickedLng by remember { mutableStateOf(initialLng ?: selectedDistrict.lng) }
    var pickedAddressName by remember { mutableStateOf("${selectedDistrict.popularWards.firstOrNull() ?: selectedDistrict.name}, ${selectedDistrict.name}, Tamil Nadu") }

    var showDistrictSearch by remember { mutableStateOf(false) }
    var districtSearchQuery by remember { mutableStateOf("") }
    var selectedRegionFilter by remember { mutableStateOf(TNRegion.ALL) }
    var isDetectingLiveLocation by remember { mutableStateOf(false) }

    // Map camera
    val zoomAnim = remember { Animatable(1.5f) }
    val panXAnim = remember { Animatable(0f) }
    val panYAnim = remember { Animatable(0f) }

    fun centerOnDistrict(district: TNDistrict) {
        selectedDistrict = district
        pickedLat = district.lat
        pickedLng = district.lng
        pickedAddressName = "${district.popularWards.firstOrNull() ?: district.name}, ${district.name}, Tamil Nadu"
        coroutineScope.launch {
            launch { zoomAnim.animateTo(1.6f, tween(400)) }
            launch { panXAnim.animateTo(0f, tween(400)) }
            launch { panYAnim.animateTo(0f, tween(400)) }
        }
    }

    fun detectAndCenterLiveLocation() {
        isDetectingLiveLocation = true
        LocationUtils.getRealLocation(context) { result ->
            isDetectingLiveLocation = false
            if (result != null) {
                val nearestDist = TamilNaduData.findNearestDistrict(result.latitude, result.longitude)
                selectedDistrict = nearestDist
                pickedLat = result.latitude
                pickedLng = result.longitude
                pickedAddressName = result.address
                coroutineScope.launch {
                    val geoScale = 45000f * 2.2f
                    val targetPanX = -((result.longitude - nearestDist.lng) * geoScale).toFloat()
                    val targetPanY = ((result.latitude - nearestDist.lat) * geoScale).toFloat()
                    launch { zoomAnim.animateTo(2.2f, tween(450)) }
                    launch { panXAnim.animateTo(targetPanX, tween(450)) }
                    launch { panYAnim.animateTo(targetPanY, tween(450)) }
                }
                Toast.makeText(context, "Live location recognized in ${nearestDist.name}!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Could not detect GPS. Choose from 38 Tamil Nadu districts.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A))) {
            // ================= 1. INTERACTIVE PICKER CANVAS =================
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(selectedDistrict) {
                        detectTransformGestures { _, pan, zoomChange, _ ->
                            val newZoom = (zoomAnim.value * zoomChange).coerceIn(0.8f, 4.0f)
                            coroutineScope.launch {
                                zoomAnim.snapTo(newZoom)
                                panXAnim.snapTo(panXAnim.value + pan.x)
                                panYAnim.snapTo(panYAnim.value + pan.y)
                            }
                        }
                    }
                    .pointerInput(selectedDistrict, zoomAnim.value, panXAnim.value, panYAnim.value) {
                        detectTapGestures { tapOffset ->
                            val w = size.width
                            val h = size.height
                            val cX = w / 2f + panXAnim.value
                            val cY = h / 2f + panYAnim.value
                            val geoScale = 45000f * zoomAnim.value

                            // Convert tap pixel to GPS lat/lng
                            val newLng = selectedDistrict.lng + (tapOffset.x - cX) / geoScale
                            val newLat = selectedDistrict.lat - (tapOffset.y - cY) / geoScale

                            pickedLat = newLat
                            pickedLng = newLng

                            // Find nearest ward or format
                            val ward = selectedDistrict.popularWards.firstOrNull() ?: "Zone"
                            pickedAddressName = "$ward Area, ${selectedDistrict.name}, Tamil Nadu"
                        }
                    }
            ) {
                val canvasW = size.width
                val canvasH = size.height
                val centerScreenX = canvasW / 2f + panXAnim.value
                val centerScreenY = canvasH / 2f + panYAnim.value
                val currentZoom = zoomAnim.value
                val geoScale = 45000f * currentZoom

                fun project(lat: Double, lng: Double): Offset {
                    val px = centerScreenX + (lng - selectedDistrict.lng).toFloat() * geoScale
                    val py = centerScreenY - (lat - selectedDistrict.lat).toFloat() * geoScale
                    return Offset(px, py)
                }

                // Background Landmass
                drawRect(color = Color(0xFFF8FAFC))

                // Grid of Blocks
                for (dx in -4..4) {
                    for (dy in -4..4) {
                        val bTopLeft = Offset(centerScreenX + dx * 90f * currentZoom, centerScreenY + dy * 70f * currentZoom)
                        drawRoundRect(
                            color = Color(0xFFF1F5F9),
                            topLeft = bTopLeft,
                            size = Size(75f * currentZoom, 55f * currentZoom),
                            cornerRadius = CornerRadius(8f, 8f)
                        )
                        drawRoundRect(
                            color = Color(0xFFE2E8F0),
                            topLeft = bTopLeft,
                            size = Size(75f * currentZoom, 55f * currentZoom),
                            cornerRadius = CornerRadius(8f, 8f),
                            style = Stroke(width = 1f)
                        )
                    }
                }

                // If coastal district (Chennai, Cuddalore, Nagapattinam, Thoothukudi, Kanyakumari)
                val isEastCoast = selectedDistrict.lng > 79.5
                if (isEastCoast) {
                    val oceanX = centerScreenX + 160f * currentZoom
                    drawRect(
                        color = Color(0xFFBAE6FD),
                        topLeft = Offset(oceanX, 0f),
                        size = Size(canvasW - oceanX, canvasH)
                    )
                    drawRect(
                        color = Color(0xFFFEF3C7),
                        topLeft = Offset(oceanX - 16f * currentZoom, 0f),
                        size = Size(16f * currentZoom, canvasH)
                    )
                }

                // Major Highways
                val hColor = Color(0xFFFEF08A)
                val hCasing = Color(0xFFEAB308)
                val rColor = Color.White
                val rCasing = Color(0xFFCBD5E1)

                // East-West Central Highway
                drawLine(color = hCasing, start = Offset(0f, centerScreenY), end = Offset(canvasW, centerScreenY), strokeWidth = 14f * currentZoom)
                drawLine(color = hColor, start = Offset(0f, centerScreenY), end = Offset(canvasW, centerScreenY), strokeWidth = 10f * currentZoom)

                // North-South Arterial
                drawLine(color = hCasing, start = Offset(centerScreenX, 0f), end = Offset(centerScreenX, canvasH), strokeWidth = 14f * currentZoom)
                drawLine(color = hColor, start = Offset(centerScreenX, 0f), end = Offset(centerScreenX, canvasH), strokeWidth = 10f * currentZoom)

                // Secondary roads
                for (i in -3..3) {
                    if (i != 0) {
                        drawLine(color = rCasing, start = Offset(0f, centerScreenY + i * 70f * currentZoom), end = Offset(canvasW, centerScreenY + i * 70f * currentZoom), strokeWidth = 6f * currentZoom)
                        drawLine(color = rColor, start = Offset(0f, centerScreenY + i * 70f * currentZoom), end = Offset(canvasW, centerScreenY + i * 70f * currentZoom), strokeWidth = 4f * currentZoom)

                        drawLine(color = rCasing, start = Offset(centerScreenX + i * 90f * currentZoom, 0f), end = Offset(centerScreenX + i * 90f * currentZoom, canvasH), strokeWidth = 6f * currentZoom)
                        drawLine(color = rColor, start = Offset(centerScreenX + i * 90f * currentZoom, 0f), end = Offset(centerScreenX + i * 90f * currentZoom, canvasH), strokeWidth = 4f * currentZoom)
                    }
                }

                // Ward Labels
                val labelPaint = android.graphics.Paint().apply {
                    isAntiAlias = true
                    textSize = (11f * currentZoom).coerceIn(10f, 18f) * density
                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                    color = android.graphics.Color.rgb(51, 65, 85)
                }

                selectedDistrict.popularWards.take(4).forEachIndexed { idx, ward ->
                    val wx = centerScreenX + (idx % 2 * 140f - 70f) * currentZoom
                    val wy = centerScreenY + (idx / 2 * 110f - 55f) * currentZoom
                    drawContext.canvas.nativeCanvas.drawText(ward.uppercase(), wx, wy, labelPaint)
                }

                // ================= DRAW MANUAL PICKED PIN =================
                val pinCenter = project(pickedLat, pickedLng)

                // Expanding radar ripple
                drawCircle(color = PrimaryOrange.copy(alpha = 0.25f), radius = 24f * currentZoom, center = pinCenter)
                drawCircle(color = PrimaryOrange, radius = 24f * currentZoom, center = pinCenter, style = Stroke(width = 2f))

                // Ground Shadow
                drawOval(
                    color = Color.Black.copy(alpha = 0.3f),
                    topLeft = Offset(pinCenter.x - 12f * currentZoom, pinCenter.y + 2f * currentZoom),
                    size = Size(24f * currentZoom, 8f * currentZoom)
                )

                // Large Distinct Pin
                val headRadius = 15f * currentZoom
                val headCenterY = pinCenter.y - 26f * currentZoom
                val pinPath = Path().apply {
                    moveTo(pinCenter.x, pinCenter.y)
                    cubicTo(pinCenter.x + 8f * currentZoom, pinCenter.y - 8f * currentZoom, pinCenter.x + headRadius, headCenterY + 6f * currentZoom, pinCenter.x + headRadius, headCenterY)
                    arcTo(androidx.compose.ui.geometry.Rect(pinCenter.x - headRadius, headCenterY - headRadius, pinCenter.x + headRadius, headCenterY + headRadius), 0f, -180f, false)
                    cubicTo(pinCenter.x - headRadius, headCenterY + 6f * currentZoom, pinCenter.x - 8f * currentZoom, pinCenter.y - 8f * currentZoom, pinCenter.x, pinCenter.y)
                    close()
                }

                drawPath(pinPath, color = PrimaryOrange)
                drawPath(pinPath, color = Color.White, style = Stroke(width = 3f * currentZoom))

                drawCircle(color = Color.White, radius = 6f * currentZoom, center = Offset(pinCenter.x, headCenterY))
                drawCircle(color = PrimaryOrange, radius = 3.5f * currentZoom, center = Offset(pinCenter.x, headCenterY))
            }

            // ================= 2. TOP TOOLBAR: DISTRICT PICKER & SEARCH =================
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🇮🇳", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Tamil Nadu Map Pin Picker",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = SecondaryBlue
                                    )
                                    Text(
                                        text = "Tap anywhere on the map to set exact pin",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Select District Search & Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { showDistrictSearch = !showDistrictSearch },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(36.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(16.dp), tint = SecondaryBlue)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${selectedDistrict.name} (38 Districts)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SecondaryBlue
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ArrowDropDown, null, tint = SecondaryBlue)
                            }
                        }

                        // Horizontal quick district chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Chennai", "Coimbatore", "Madurai", "Tiruchirappalli", "Salem", "Tirunelveli", "Erode", "Vellore").forEach { dName ->
                                val isSel = selectedDistrict.name == dName
                                FilterChip(
                                    selected = isSel,
                                    onClick = { centerOnDistrict(TamilNaduData.findDistrict(dName)) },
                                    label = { Text(dName, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SecondaryBlue,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                // District Dropdown List Modal
                if (showDistrictSearch) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 340.dp)
                            .padding(top = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            OutlinedTextField(
                                value = districtSearchQuery,
                                onValueChange = { districtSearchQuery = it },
                                placeholder = { Text("Search any of 38 Tamil Nadu districts...", fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(18.dp)) },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))

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
                            Spacer(modifier = Modifier.height(6.dp))

                            val filteredList = TamilNaduData.ALL_38_DISTRICTS.filter { dist ->
                                val matchesSearch = dist.name.contains(districtSearchQuery, ignoreCase = true) ||
                                        dist.zoneName.contains(districtSearchQuery, ignoreCase = true)
                                val matchesRegion = selectedRegionFilter == TNRegion.ALL || dist.region == selectedRegionFilter
                                matchesSearch && matchesRegion
                            }
                            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                items(filteredList) { dist ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                centerOnDistrict(dist)
                                                showDistrictSearch = false
                                                districtSearchQuery = ""
                                            }
                                            .padding(vertical = 8.dp, horizontal = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Place, null, tint = PrimaryOrange, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(dist.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SecondaryBlue)
                                            Text("${dist.zoneName} • ${dist.region.title}", fontSize = 10.sp, color = Color.Gray)
                                        }
                                        Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                                    }
                                    HorizontalDivider(color = Color(0xFFF1F5F9))
                                }
                            }
                        }
                    }
                }
            }

            // ================= 3. RIGHT SIDE CONTROLS (LIVE GPS, ZOOM & RESET) =================
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SmallFloatingActionButton(
                    onClick = { detectAndCenterLiveLocation() },
                    containerColor = Color.White,
                    contentColor = PrimaryOrange,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
                ) {
                    if (isDetectingLiveLocation) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = PrimaryOrange)
                    } else {
                        Icon(Icons.Default.LocationOn, contentDescription = "My Live GPS", tint = PrimaryOrange)
                    }
                }

                SmallFloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            zoomAnim.animateTo((zoomAnim.value * 1.35f).coerceAtMost(4.0f), tween(250))
                        }
                    },
                    containerColor = Color.White,
                    contentColor = SecondaryBlue,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Zoom In")
                }

                SmallFloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            zoomAnim.animateTo((zoomAnim.value / 1.35f).coerceAtLeast(0.8f), tween(250))
                        }
                    },
                    containerColor = Color.White,
                    contentColor = SecondaryBlue,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Zoom Out")
                }

                SmallFloatingActionButton(
                    onClick = { centerOnDistrict(selectedDistrict) },
                    containerColor = Color.White,
                    contentColor = SecondaryBlue,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Center on District")
                }
            }

            // ================= 4. BOTTOM BAR: CONFIRM SELECTED PIN =================
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(14.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFFFF7ED), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.LocationOn, null, tint = PrimaryOrange, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Selected Location",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = pickedAddressName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "GPS: ${String.format(Locale.US, "%.5f° N, %.5f° E", pickedLat, pickedLng)} (${selectedDistrict.zoneName})",
                                fontSize = 11.sp,
                                color = Color(0xFF15803D),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            onLocationConfirmed(pickedLat, pickedLng, pickedAddressName)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                    ) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Confirm This Location",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
