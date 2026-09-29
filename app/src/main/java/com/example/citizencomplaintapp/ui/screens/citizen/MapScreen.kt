package com.example.citizencomplaintapp.ui.screens.citizen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citizencomplaintapp.data.model.Complaint
import com.example.citizencomplaintapp.data.model.Priority
import com.example.citizencomplaintapp.data.model.TNDistrict
import com.example.citizencomplaintapp.data.model.TNRegion
import com.example.citizencomplaintapp.data.model.TamilNaduData
import com.example.citizencomplaintapp.ui.theme.PrimaryOrange
import com.example.citizencomplaintapp.ui.theme.SecondaryBlue
import com.example.citizencomplaintapp.ui.utils.LocationUtils
import com.example.citizencomplaintapp.ui.utils.UserLocationResult
import com.example.citizencomplaintapp.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import kotlin.math.hypot

enum class MapThemeMode {
    STREETS, SATELLITE_DARK
}

@Composable
fun MapScreen(
    viewModel: MainViewModel,
    onNavigateToDetails: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val complaints by viewModel.complaints.collectAsState()

    var activeDistrict by remember { mutableStateOf(TamilNaduData.ALL_38_DISTRICTS[0]) } // Chennai default
    var showDistrictDropdown by remember { mutableStateOf(false) }
    var districtSearchQuery by remember { mutableStateOf("") }
    var selectedRegionFilter by remember { mutableStateOf(TNRegion.ALL) }

    var mapTheme by remember { mutableStateOf(MapThemeMode.STREETS) }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var selectedComplaint by remember { mutableStateOf<Complaint?>(null) }
    var showLegend by remember { mutableStateOf(false) }

    var userLiveLocation by remember { mutableStateOf<UserLocationResult?>(null) }
    var isLocatingUser by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Interactive camera state
    val zoomAnim = remember { Animatable(1.3f) }
    val panXAnim = remember { Animatable(0f) }
    val panYAnim = remember { Animatable(0f) }

    // Pulsing transition
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    fun focusOnLocation(lat: Double, lng: Double, targetZoom: Float = 2.0f) {
        coroutineScope.launch {
            val geoScale = 45000f
            val targetPanX = -((lng - activeDistrict.lng) * geoScale * targetZoom).toFloat()
            val targetPanY = ((lat - activeDistrict.lat) * geoScale * targetZoom).toFloat()

            launch { zoomAnim.animateTo(targetZoom, tween(600, easing = FastOutSlowInEasing)) }
            launch { panXAnim.animateTo(targetPanX, tween(600, easing = FastOutSlowInEasing)) }
            launch { panYAnim.animateTo(targetPanY, tween(600, easing = FastOutSlowInEasing)) }
        }
    }

    fun switchDistrict(district: TNDistrict) {
        activeDistrict = district
        coroutineScope.launch {
            launch { zoomAnim.animateTo(1.3f, tween(500)) }
            launch { panXAnim.animateTo(0f, tween(500)) }
            launch { panYAnim.animateTo(0f, tween(500)) }
        }
    }

    fun detectUserLocation(centerOnUser: Boolean = true) {
        isLocatingUser = true
        LocationUtils.getRealLocation(context) { result ->
            isLocatingUser = false
            if (result != null) {
                userLiveLocation = result
                if (result.isIndianLocation) {
                    val nearest = TamilNaduData.findNearestDistrict(result.latitude, result.longitude)
                    activeDistrict = nearest
                    if (centerOnUser) {
                        focusOnLocation(result.latitude, result.longitude, 2.4f)
                    }
                }
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val fine = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarse = perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fine || coarse) {
            detectUserLocation(centerOnUser = true)
        }
    }

    fun requestUserLocation() {
        if (LocationUtils.hasLocationPermission(context)) {
            detectUserLocation(centerOnUser = true)
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        if (LocationUtils.hasLocationPermission(context)) {
            detectUserLocation(centerOnUser = false)
        }
    }

    // Filter complaints for the currently selected district or nearby
    val districtComplaints = remember(complaints, activeDistrict) {
        complaints.filter { c ->
            val lat = c.latitude ?: 0.0
            val lng = c.longitude ?: 0.0
            val dist = hypot(lat - activeDistrict.lat, lng - activeDistrict.lng)
            dist < 0.25 || c.location.contains(activeDistrict.name, ignoreCase = true)
        }
    }

    val filteredComplaints = remember(districtComplaints, selectedCategoryFilter) {
        when (selectedCategoryFilter) {
            "All" -> districtComplaints
            "Emergency" -> districtComplaints.filter { it.priority == Priority.EMERGENCY || it.priority == Priority.CRITICAL }
            else -> districtComplaints.filter { it.categoryName.contains(selectedCategoryFilter, ignoreCase = true) }
        }
    }

    LaunchedEffect(filteredComplaints) {
        if (selectedComplaint == null && filteredComplaints.isNotEmpty()) {
            selectedComplaint = filteredComplaints.first()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ================= TAMIL NADU MAP CANVAS =================
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(activeDistrict) {
                    detectTransformGestures { _, pan, zoomChange, _ ->
                        val newZoom = (zoomAnim.value * zoomChange).coerceIn(0.6f, 4.5f)
                        coroutineScope.launch {
                            zoomAnim.snapTo(newZoom)
                            panXAnim.snapTo(panXAnim.value + pan.x)
                            panYAnim.snapTo(panYAnim.value + pan.y)
                        }
                    }
                }
                .pointerInput(filteredComplaints, zoomAnim.value, panXAnim.value, panYAnim.value, activeDistrict) {
                    detectTapGestures { tapOffset ->
                        val w = size.width
                        val h = size.height
                        val cX = w / 2f + panXAnim.value
                        val cY = h / 2f + panYAnim.value
                        val geoScale = 45000f * zoomAnim.value

                        var tapped: Complaint? = null
                        var minDistance = Float.MAX_VALUE

                        for (c in filteredComplaints) {
                            val pinX = cX + ((c.longitude ?: activeDistrict.lng) - activeDistrict.lng).toFloat() * geoScale
                            val pinY = cY - ((c.latitude ?: activeDistrict.lat) - activeDistrict.lat).toFloat() * geoScale - 24f * zoomAnim.value

                            val dist = hypot(tapOffset.x - pinX, tapOffset.y - pinY)
                            if (dist < 44.dp.toPx() && dist < minDistance) {
                                minDistance = dist
                                tapped = c
                            }
                        }

                        if (tapped != null) {
                            selectedComplaint = tapped
                            focusOnLocation(tapped.latitude ?: activeDistrict.lat, tapped.longitude ?: activeDistrict.lng, zoomAnim.value.coerceAtLeast(1.8f))
                        }
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
                val px = centerScreenX + (lng - activeDistrict.lng).toFloat() * geoScale
                val py = centerScreenY - (lat - activeDistrict.lat).toFloat() * geoScale
                return Offset(px, py)
            }

            fun drawRoad(p1: Offset, p2: Offset, width: Float, fill: Color, casing: Color) {
                drawLine(color = casing, start = p1, end = p2, strokeWidth = width + 3f * currentZoom)
                drawLine(color = fill, start = p1, end = p2, strokeWidth = width)
            }

            val landBgColor = if (mapTheme == MapThemeMode.STREETS) Color(0xFFF8FAFC) else Color(0xFF0F172A)
            drawRect(color = landBgColor)

            val streetFill = if (mapTheme == MapThemeMode.STREETS) Color.White else Color(0xFF1E293B)
            val roadCasing = if (mapTheme == MapThemeMode.STREETS) Color(0xFFCBD5E1) else Color(0xFF334155)
            val arterialFill = if (mapTheme == MapThemeMode.STREETS) Color(0xFFFEF08A) else Color(0xFF38BDF8)
            val arterialCasing = if (mapTheme == MapThemeMode.STREETS) Color(0xFFEAB308) else Color(0xFF0284C7)
            val parkColor = if (mapTheme == MapThemeMode.STREETS) Color(0xFFDCFCE7) else Color(0xFF064E3B)
            val waterColor = if (mapTheme == MapThemeMode.STREETS) Color(0xFFBAE6FD) else Color(0xFF1E3A8A)

            val textPaint = android.graphics.Paint().apply {
                isAntiAlias = true
                textSize = (11f * currentZoom).coerceIn(10f, 22f) * density
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                color = if (mapTheme == MapThemeMode.STREETS) android.graphics.Color.rgb(71, 85, 105) else android.graphics.Color.rgb(148, 163, 184)
            }

            val waterPaint = android.graphics.Paint(textPaint).apply {
                color = if (mapTheme == MapThemeMode.SATELLITE_DARK) android.graphics.Color.rgb(96, 165, 250) else android.graphics.Color.rgb(3, 105, 161)
            }
            val parkPaint = android.graphics.Paint(textPaint).apply {
                color = if (mapTheme == MapThemeMode.SATELLITE_DARK) android.graphics.Color.rgb(52, 211, 153) else android.graphics.Color.rgb(22, 101, 52)
            }

            // 1. Geography Features
            val isCoastal = activeDistrict.lng > 79.5 || activeDistrict.name in listOf("Chennai", "Cuddalore", "Nagapattinam", "Mayiladuthurai", "Thoothukudi", "Kanyakumari", "Tiruvallur", "Chengalpattu", "Ramanathapuram")
            if (isCoastal) {
                val coastX = centerScreenX + 170f * currentZoom
                drawRect(color = waterColor, topLeft = Offset(coastX, 0f), size = Size(canvasW - coastX, canvasH))
                drawRect(color = Color(0xFFFEF3C7), topLeft = Offset(coastX - 18f * currentZoom, 0f), size = Size(18f * currentZoom, canvasH))
                drawContext.canvas.nativeCanvas.drawText("BAY OF BENGAL", coastX + 24f * currentZoom, canvasH * 0.45f, waterPaint)
            }

            // Green Parks in District Center
            val parkCenter = Offset(centerScreenX - 80f * currentZoom, centerScreenY - 60f * currentZoom)
            drawRoundRect(color = parkColor, topLeft = parkCenter, size = Size(50f * currentZoom, 35f * currentZoom), cornerRadius = CornerRadius(8f, 8f))
            drawContext.canvas.nativeCanvas.drawText("DISTRICT PARK", parkCenter.x, parkCenter.y - 6f, parkPaint)

            // 2. District Road Network
            val arterialW = 10f * currentZoom
            val secondaryW = 5f * currentZoom

            // Main East-West District Arterial
            drawRoad(Offset(0f, centerScreenY), Offset(canvasW, centerScreenY), arterialW, arterialFill, arterialCasing)
            // North-South Arterial
            drawRoad(Offset(centerScreenX, 0f), Offset(centerScreenX, canvasH), arterialW, arterialFill, arterialCasing)

            // Secondary Roads Grid
            for (i in -3..3) {
                if (i != 0) {
                    drawRoad(Offset(0f, centerScreenY + i * 75f * currentZoom), Offset(canvasW, centerScreenY + i * 75f * currentZoom), secondaryW, streetFill, roadCasing)
                    drawRoad(Offset(centerScreenX + i * 95f * currentZoom, 0f), Offset(centerScreenX + i * 95f * currentZoom, canvasH), secondaryW, streetFill, roadCasing)
                }
            }

            // Road & Ward Name Labels
            drawContext.canvas.nativeCanvas.drawText("${activeDistrict.name.uppercase()} MAIN HIGHWAY", centerScreenX - 110f, centerScreenY - 12f, textPaint)
            drawContext.canvas.nativeCanvas.drawText("${activeDistrict.zoneName.uppercase()} CENTRAL", centerScreenX + 10f, centerScreenY + 40f, textPaint)

            activeDistrict.popularWards.take(4).forEachIndexed { idx, ward ->
                val wx = centerScreenX + (idx % 2 * 130f - 65f) * currentZoom
                val wy = centerScreenY + (idx / 2 * 110f - 50f) * currentZoom
                drawContext.canvas.nativeCanvas.drawText(ward.uppercase(), wx, wy, textPaint)
            }

            // 3. Incident Pins
            filteredComplaints.forEach { complaint ->
                val lat = complaint.latitude ?: activeDistrict.lat
                val lng = complaint.longitude ?: activeDistrict.lng
                val pinCenter = project(lat, lng)

                val isSelected = selectedComplaint?.complaintId == complaint.complaintId
                val isEmergency = complaint.priority == Priority.EMERGENCY || complaint.priority == Priority.CRITICAL

                val pinColor = when (complaint.priority) {
                    Priority.EMERGENCY, Priority.CRITICAL -> Color(0xFFEF4444)
                    Priority.HIGH -> PrimaryOrange
                    Priority.MEDIUM, Priority.LOW -> Color(0xFF10B981)
                }

                if (isEmergency) {
                    drawCircle(color = Color(0xFFEF4444).copy(alpha = pulseAlpha), radius = (20f * pulseScale) * currentZoom, center = pinCenter)
                }

                if (isSelected) {
                    drawCircle(color = SecondaryBlue.copy(alpha = 0.25f), radius = 28f * currentZoom, center = pinCenter)
                    drawCircle(color = SecondaryBlue, radius = 28f * currentZoom, center = pinCenter, style = Stroke(width = 2.5f * currentZoom))
                }

                drawOval(
                    color = Color.Black.copy(alpha = 0.28f),
                    topLeft = Offset(pinCenter.x - 12f * currentZoom, pinCenter.y + 2f * currentZoom),
                    size = Size(24f * currentZoom, 8f * currentZoom)
                )

                val pinPath = Path().apply {
                    val headRadius = 14f * currentZoom
                    val tipY = pinCenter.y
                    val headCenterY = pinCenter.y - 24f * currentZoom
                    moveTo(pinCenter.x, tipY)
                    cubicTo(pinCenter.x + 8f * currentZoom, tipY - 8f * currentZoom, pinCenter.x + headRadius, headCenterY + 6f * currentZoom, pinCenter.x + headRadius, headCenterY)
                    arcTo(androidx.compose.ui.geometry.Rect(pinCenter.x - headRadius, headCenterY - headRadius, pinCenter.x + headRadius, headCenterY + headRadius), 0f, -180f, false)
                    cubicTo(pinCenter.x - headRadius, headCenterY + 6f * currentZoom, pinCenter.x - 8f * currentZoom, tipY - 8f * currentZoom, pinCenter.x, tipY)
                    close()
                }

                drawPath(pinPath, color = pinColor)
                drawPath(pinPath, color = Color.White, style = Stroke(width = 2.5f * currentZoom))

                val headCenterY = pinCenter.y - 24f * currentZoom
                drawCircle(color = Color.White, radius = 5.5f * currentZoom, center = Offset(pinCenter.x, headCenterY))
                drawCircle(color = pinColor, radius = 3.5f * currentZoom, center = Offset(pinCenter.x, headCenterY))
            }

            // 4. User Live GPS Marker
            userLiveLocation?.let { uLoc ->
                val userCenter = project(uLoc.latitude, uLoc.longitude)

                drawCircle(color = Color(0xFF3B82F6).copy(alpha = (pulseAlpha * 0.75f).coerceIn(0f, 1f)), radius = (26f * pulseScale) * currentZoom, center = userCenter)
                drawCircle(color = Color.White, radius = 11f * currentZoom, center = userCenter)
                drawCircle(color = Color(0xFF2563EB), radius = 8f * currentZoom, center = userCenter)
                drawCircle(color = Color.White, radius = 3.5f * currentZoom, center = userCenter)

                val userLabelPaint = android.graphics.Paint().apply {
                    isAntiAlias = true
                    textSize = (9f * currentZoom).coerceIn(9f, 16f) * density
                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                    color = android.graphics.Color.WHITE
                }
                val labelText = "YOU ARE HERE"
                val textWidth = userLabelPaint.measureText(labelText)
                val pillH = (16f * currentZoom).coerceIn(16f, 26f) * density
                val pillW = textWidth + 16f * density
                val pillTop = userCenter.y - 18f * currentZoom - pillH

                drawRoundRect(color = Color(0xFF1E40AF), topLeft = Offset(userCenter.x - pillW / 2f, pillTop), size = Size(pillW, pillH), cornerRadius = CornerRadius(pillH / 2f, pillH / 2f))
                drawRoundRect(color = Color.White, topLeft = Offset(userCenter.x - pillW / 2f, pillTop), size = Size(pillW, pillH), cornerRadius = CornerRadius(pillH / 2f, pillH / 2f), style = Stroke(width = 1.5f))
                drawContext.canvas.nativeCanvas.drawText(labelText, userCenter.x - textWidth / 2f, pillTop + pillH * 0.72f, userLabelPaint)
            }
        }

        // ================= TOP OVERLAY UI: 38 DISTRICTS SELECTOR =================
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
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
                            Text("🇮🇳", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Tamil Nadu Civic Live Map",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = SecondaryBlue
                                )
                                Text(
                                    text = "${activeDistrict.name} District • ${activeDistrict.zoneName}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF15803D),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Theme Toggles
                        Row(
                            modifier = Modifier
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(10.dp))
                                .padding(2.dp)
                        ) {
                            ThemeTabButton("Streets", mapTheme == MapThemeMode.STREETS) { mapTheme = MapThemeMode.STREETS }
                            ThemeTabButton("Satellite", mapTheme == MapThemeMode.SATELLITE_DARK) { mapTheme = MapThemeMode.SATELLITE_DARK }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // District Selector Button (Opens 38 Districts List)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { showDistrictDropdown = !showDistrictDropdown },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(34.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp),
                            border = BorderStroke(1.dp, SecondaryBlue)
                        ) {
                            Icon(Icons.Default.Place, null, modifier = Modifier.size(15.dp), tint = SecondaryBlue)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "District: ${activeDistrict.name} (38 Districts)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SecondaryBlue
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowDropDown, null, tint = SecondaryBlue)
                        }
                    }

                    // Quick Popular TN District Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Chennai", "Coimbatore", "Madurai", "Tiruchirappalli", "Salem", "Tirunelveli", "Erode", "Vellore", "Thanjavur").forEach { dName ->
                            val isSel = activeDistrict.name == dName
                            FilterChip(
                                selected = isSel,
                                onClick = { switchDistrict(TamilNaduData.findDistrict(dName)) },
                                label = {
                                    Text(
                                        text = dName,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SecondaryBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Category Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("All", "Emergency", "Roads", "Streetlights", "Water", "Public Safety").forEach { filter ->
                            val isSelected = selectedCategoryFilter == filter
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedCategoryFilter = filter
                                    filteredComplaints.firstOrNull()?.let {
                                        selectedComplaint = it
                                        focusOnLocation(it.latitude ?: activeDistrict.lat, it.longitude ?: activeDistrict.lng)
                                    }
                                },
                                label = { Text(filter, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = if (filter == "Emergency") Color(0xFFEF4444) else PrimaryOrange,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    if (userLiveLocation != null) {
                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp)
                                .clickable {
                                    userLiveLocation?.let { focusOnLocation(it.latitude, it.longitude, 2.4f) }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Live GPS: ${userLiveLocation?.address}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D4ED8),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Text("Center", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryOrange)
                            }
                        }
                    }
                }
            }

            // 38 Districts Dropdown Dialog
            if (showDistrictDropdown) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .padding(top = 4.dp)
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
                                            switchDistrict(dist)
                                            showDistrictDropdown = false
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

        // ================= RIGHT SIDE CAMERA TOOLS =================
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SmallFloatingActionButton(
                onClick = { requestUserLocation() },
                containerColor = if (userLiveLocation != null) SecondaryBlue else Color.White,
                contentColor = if (userLiveLocation != null) Color.White else SecondaryBlue,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
            ) {
                if (isLocatingUser) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = PrimaryOrange)
                } else {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "My Live Location",
                        tint = if (userLiveLocation != null) Color(0xFF60A5FA) else SecondaryBlue
                    )
                }
            }

            SmallFloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        zoomAnim.animateTo((zoomAnim.value * 1.35f).coerceAtMost(4.5f), tween(300))
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
                        zoomAnim.animateTo((zoomAnim.value / 1.35f).coerceAtLeast(0.6f), tween(300))
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
                onClick = { switchDistrict(activeDistrict) },
                containerColor = Color.White,
                contentColor = SecondaryBlue,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset Camera")
            }

            SmallFloatingActionButton(
                onClick = { showLegend = !showLegend },
                containerColor = Color.White,
                contentColor = SecondaryBlue,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
            ) {
                Icon(Icons.Default.Info, contentDescription = "Toggle Legend")
            }
        }

        // ================= POPUP LEGEND =================
        AnimatedVisibility(
            visible = showLegend,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 175.dp)
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Priority Legend",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryBlue,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    LegendItemRow(Color(0xFFEF4444), "Emergency / Critical (Pulsing)")
                    LegendItemRow(PrimaryOrange, "High Priority")
                    LegendItemRow(Color(0xFF10B981), "Medium / Low / Resolved")
                }
            }
        }

        // ================= BOTTOM INCIDENTS CAROUSEL =================
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            if (filteredComplaints.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredComplaints) { complaint ->
                        val isSelected = selectedComplaint?.complaintId == complaint.complaintId
                        IncidentCard(
                            complaint = complaint,
                            isSelected = isSelected,
                            onFocusOnMap = {
                                selectedComplaint = complaint
                                focusOnLocation(complaint.latitude ?: activeDistrict.lat, complaint.longitude ?: activeDistrict.lng, 2.2f)
                            },
                            onViewDetails = {
                                onNavigateToDetails(complaint.complaintId)
                            }
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, null, tint = SecondaryBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "No cases currently reported in ${activeDistrict.name} District.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeTabButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .background(
                if (isSelected) SecondaryBlue else Color.Transparent,
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 9.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else Color(0xFF64748B)
        )
    }
}

@Composable
fun IncidentCard(
    complaint: Complaint,
    isSelected: Boolean,
    onFocusOnMap: () -> Unit,
    onViewDetails: () -> Unit
) {
    val priorityColor = when (complaint.priority) {
        Priority.EMERGENCY, Priority.CRITICAL -> Color(0xFFEF4444)
        Priority.HIGH -> PrimaryOrange
        Priority.MEDIUM, Priority.LOW -> Color(0xFF10B981)
    }

    Card(
        modifier = Modifier
            .width(280.dp)
            .clickable { onFocusOnMap() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = if (isSelected) BorderStroke(2.dp, SecondaryBlue) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = complaint.categoryName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SecondaryBlue
                )
                Surface(
                    color = priorityColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = complaint.priority.name,
                        color = priorityColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = complaint.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "📍 ${complaint.location}",
                fontSize = 11.sp,
                color = Color(0xFF64748B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onFocusOnMap,
                    modifier = Modifier.weight(1f).height(32.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, SecondaryBlue)
                ) {
                    Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(14.dp), tint = SecondaryBlue)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Focus Pin", fontSize = 11.sp, color = SecondaryBlue)
                }

                Button(
                    onClick = onViewDetails,
                    modifier = Modifier.weight(1f).height(32.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                ) {
                    Text("View Details", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun LegendItemRow(color: Color, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 11.sp, color = Color(0xFF334155))
    }
}
