package com.example.citizencomplaintapp.ui.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import java.util.Locale

data class UserLocationResult(
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val isIndianLocation: Boolean = true,
    val isTamilNaduLocation: Boolean = false,
    val districtName: String = "Chennai"
)

data class IndianLocationPreset(
    val name: String,
    val locality: String,
    val city: String,
    val state: String = "Tamil Nadu",
    val pincode: String,
    val latitude: Double,
    val longitude: Double
) {
    val fullAddress: String
        get() = "$locality, $city, $state $pincode"
}

object LocationUtils {

    // Boundary check for India: Lat 6.0° to 37.5° N, Lng 68.0° to 97.5° E
    fun isInsideIndia(lat: Double, lng: Double): Boolean {
        return lat in 6.0..37.5 && lng in 68.0..97.5
    }

    // Boundary check for Tamil Nadu: Lat 8.0° to 13.6° N, Lng 76.2° to 80.5° E
    fun isInsideTamilNadu(lat: Double, lng: Double): Boolean {
        return lat in 8.0..13.6 && lng in 76.2..80.5
    }

    val TN_PRESETS = listOf(
        // Chennai
        IndianLocationPreset("Anna Salai / Mount Road", "Anna Salai, Triplicane / T. Nagar", "Chennai", "Tamil Nadu", "600002", 13.0600, 80.2600),
        IndianLocationPreset("Marina Beach / Kamarajar Salai", "Kamarajar Salai, Near Light House", "Chennai", "Tamil Nadu", "600004", 13.0500, 80.2824),
        IndianLocationPreset("Velachery Main Road", "Velachery Bypass Rd, Ward 178", "Chennai", "Tamil Nadu", "600042", 12.9759, 80.2212),
        IndianLocationPreset("Guindy Industrial Estate", "Kathipara Junction Area", "Chennai", "Tamil Nadu", "600032", 13.0067, 80.2022),
        
        // Coimbatore
        IndianLocationPreset("Gandhipuram Cross Cut Road", "Cross Cut Rd, Gandhipuram Zone", "Coimbatore", "Tamil Nadu", "641012", 11.0168, 76.9558),
        IndianLocationPreset("RS Puram D.B. Road", "Diwan Bahadur Road, RS Puram", "Coimbatore", "Tamil Nadu", "641002", 11.0110, 76.9480),
        
        // Madurai
        IndianLocationPreset("Meenakshi Amman Temple Zone", "North Chitrai St, Temple Ward", "Madurai", "Tamil Nadu", "625001", 9.9252, 78.1198),
        IndianLocationPreset("Mattuthavani Bus Stand Rd", "Melur Main Rd, Mattuthavani", "Madurai", "Tamil Nadu", "625007", 9.9390, 78.1560),

        // Tiruchirappalli
        IndianLocationPreset("Thillai Nagar Main Road", "Thillai Nagar East", "Tiruchirappalli", "Tamil Nadu", "620018", 10.8250, 78.6890),
        IndianLocationPreset("Srirangam Temple Area", "North Chithirai St, Srirangam", "Tiruchirappalli", "Tamil Nadu", "620006", 10.8620, 78.6900),

        // Salem
        IndianLocationPreset("Salem New Bus Stand Area", "Five Roads Junction, Meyyanur", "Salem", "Tamil Nadu", "636004", 11.6643, 78.1460),

        // Tirunelveli
        IndianLocationPreset("Palayamkottai High Ground", "Trivandrum Rd, Palayamkottai", "Tirunelveli", "Tamil Nadu", "627002", 8.7139, 77.7567)
    )

    val INDIAN_PRESETS = TN_PRESETS

    fun hasLocationPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    fun getRealLocation(
        context: Context,
        onResult: (UserLocationResult?) -> Unit
    ) {
        if (!hasLocationPermission(context)) {
            onResult(null)
            return
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (locationManager == null) {
            onResult(null)
            return
        }

        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        )

        var bestLocation: Location? = null
        for (provider in providers) {
            try {
                if (locationManager.isProviderEnabled(provider)) {
                    val loc = locationManager.getLastKnownLocation(provider)
                    if (loc != null) {
                        if (bestLocation == null || loc.accuracy < bestLocation.accuracy) {
                            bestLocation = loc
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore disabled providers
            }
        }

        if (bestLocation != null) {
            resolveAddress(context, bestLocation, onResult)
            return
        }

        val enabledProvider = providers.firstOrNull { 
            try { locationManager.isProviderEnabled(it) } catch (e: Exception) { false } 
        } ?: LocationManager.PASSIVE_PROVIDER

        val handler = Handler(Looper.getMainLooper())
        var listener: LocationListener? = null

        val timeoutRunnable = Runnable {
            listener?.let {
                try {
                    locationManager.removeUpdates(it)
                } catch (e: Exception) {}
            }
            onResult(null)
        }

        listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                handler.removeCallbacks(timeoutRunnable)
                try {
                    locationManager.removeUpdates(this)
                } catch (e: Exception) {}
                resolveAddress(context, location, onResult)
            }

            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        try {
            handler.postDelayed(timeoutRunnable, 5000)
            locationManager.requestLocationUpdates(
                enabledProvider,
                0L,
                0f,
                listener,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            handler.removeCallbacks(timeoutRunnable)
            onResult(null)
        }
    }

    private fun resolveAddress(
        context: Context,
        location: Location,
        onResult: (UserLocationResult?) -> Unit
    ) {
        val lat = location.latitude
        val lng = location.longitude
        val inIndia = isInsideIndia(lat, lng)
        val inTN = isInsideTamilNadu(lat, lng)
        val nearestDistrict = com.example.citizencomplaintapp.data.model.TamilNaduData.findNearestDistrict(lat, lng)

        Thread {
            val addressText = try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    formatAddress(addresses[0], nearestDistrict.name)
                } else {
                    if (inTN) {
                        "${nearestDistrict.popularWards.firstOrNull() ?: "Civic Zone"}, ${nearestDistrict.name}, Tamil Nadu"
                    } else {
                        formatCoords(lat, lng)
                    }
                }
            } catch (e: Exception) {
                if (inTN) {
                    "${nearestDistrict.popularWards.firstOrNull() ?: "Civic Zone"}, ${nearestDistrict.name}, Tamil Nadu"
                } else {
                    formatCoords(lat, lng)
                }
            }

            Handler(Looper.getMainLooper()).post {
                onResult(
                    UserLocationResult(
                        latitude = lat,
                        longitude = lng,
                        address = addressText,
                        isIndianLocation = inIndia,
                        isTamilNaduLocation = inTN,
                        districtName = nearestDistrict.name
                    )
                )
            }
        }.start()
    }

    private fun formatAddress(address: Address, fallbackDistrict: String): String {
        val parts = mutableListOf<String>()
        address.thoroughfare?.let { parts.add(it) }
        address.subLocality?.let { parts.add(it) }
        val locality = address.locality ?: address.subAdminArea ?: fallbackDistrict
        parts.add(locality)
        val admin = address.adminArea ?: "Tamil Nadu"
        parts.add(admin)
        address.postalCode?.let { parts.add(it) }
        
        return if (parts.isNotEmpty()) {
            parts.distinct().joinToString(", ")
        } else {
            address.getAddressLine(0) ?: formatCoords(address.latitude, address.longitude)
        }
    }

    private fun formatCoords(lat: Double, lng: Double): String {
        return String.format(Locale.US, "GPS: %.4f° N, %.4f° E", lat, lng)
    }
}
