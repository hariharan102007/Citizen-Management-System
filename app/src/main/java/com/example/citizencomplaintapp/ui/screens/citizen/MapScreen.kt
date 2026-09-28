package com.example.citizencomplaintapp.ui.screens.citizen

import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.citizencomplaintapp.data.model.Priority
import com.example.citizencomplaintapp.ui.theme.PrimaryOrange
import com.example.citizencomplaintapp.ui.theme.SecondaryBlue
import com.example.citizencomplaintapp.ui.viewmodel.MainViewModel

private const val MAPTILER_KEY = "O81Uc5VcDanRAuIi218c"

@Composable
fun MapScreen(viewModel: MainViewModel) {
    val complaints by viewModel.complaints.collectAsState()

    val firstWithLoc = remember(complaints) {
        complaints.firstOrNull { it.latitude != null && it.longitude != null }
    }
    val centerLat = firstWithLoc?.latitude ?: 12.9716
    val centerLng = firstWithLoc?.longitude ?: 77.5946

    val markersJs = remember(complaints) {
        complaints.filter { it.latitude != null && it.longitude != null }.joinToString("\n") { complaint ->
            val colorHex = when (complaint.priority) {
                Priority.EMERGENCY, Priority.CRITICAL -> "#E53935"
                Priority.HIGH -> "#FF9800"
                Priority.MEDIUM, Priority.LOW -> "#2E7D32"
            }
            val titleEscaped = complaint.title.replace("'", "\\'").replace("\"", "\\\"")
            val priorityName = complaint.priority.name
            val categoryEscaped = complaint.categoryName.replace("'", "\\'").replace("\"", "\\\"")

            """
            L.circleMarker([${complaint.latitude}, ${complaint.longitude}], {
                color: '#ffffff',
                weight: 2,
                fillColor: '$colorHex',
                fillOpacity: 0.9,
                radius: 12
            }).addTo(map).bindPopup(
                '<div style="font-family:sans-serif; padding:4px;">' +
                '<b style="font-size:13px; color:#1f2937;">$titleEscaped</b><br>' +
                '<span style="font-size:11px; color:#6b7280;">Category: $categoryEscaped</span><br>' +
                '<span style="font-size:11px; font-weight:bold; color:$colorHex;">Priority: $priorityName</span>' +
                '</div>'
            );
            """.trimIndent()
        }
    }

    val htmlContent = remember(markersJs, centerLat, centerLng) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.css" />
            <script src="https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.js"></script>
            <style>
                body, html { margin: 0; padding: 0; height: 100%; width: 100%; overflow: hidden; background-color: #e5e7eb; }
                #map { width: 100%; height: 100%; position: absolute; top: 0; bottom: 0; }
                .leaflet-control-attribution { display: none !important; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                const map = L.map('map', {
                    zoomControl: false,
                    attributionControl: false
                }).setView([$centerLat, $centerLng], 12);

                const maptilerTiles = L.tileLayer('https://api.maptiler.com/maps/base-v4/256/{z}/{x}/{y}.png?key=$MAPTILER_KEY', {
                    maxZoom: 19
                });

                const osmTiles = L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 19
                });

                maptilerTiles.addTo(map);
                maptilerTiles.on('tileerror', function() {
                    osmTiles.addTo(map);
                });

                L.control.zoom({ position: 'topright' }).addTo(map);

                $markersJs
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        cacheMode = WebSettings.LOAD_DEFAULT
                    }
                    loadDataWithBaseURL("https://api.maptiler.com", htmlContent, "text/html", "UTF-8", null)
                }
            },
            update = { webView ->
                webView.loadDataWithBaseURL("https://api.maptiler.com", htmlContent, "text/html", "UTF-8", null)
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay UI - Header
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, null, tint = SecondaryBlue, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Live Map: ${complaints.size} Active Cases",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        // Overlay UI - Legend
        Card(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .padding(bottom = 20.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                LegendItem(Color(0xFFE53935), "Emergency / Critical")
                LegendItem(PrimaryOrange, "High Priority")
                LegendItem(Color(0xFF2E7D32), "Resolved / Medium / Low")
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 10.sp, color = Color.Black)
    }
}