package com.example.citizencomplaintapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citizencomplaintapp.data.api.MockApiService
import com.example.citizencomplaintapp.data.model.*
import com.example.citizencomplaintapp.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class MainViewModel : ViewModel() {
    // In a real app, use Dependency Injection (Hilt/Koin)
    private val repository = AppRepository(MockApiService())

    private val _complaints = MutableStateFlow<List<Complaint>>(emptyList())
    val complaints: StateFlow<List<Complaint>> = _complaints.asStateFlow()

    private val _departmentStats = MutableStateFlow<Map<String, Float>>(emptyMap())
    val departmentStats = _departmentStats.asStateFlow()

    private val _officers = MutableStateFlow<List<Officer>>(emptyList())
    val officers: StateFlow<List<Officer>> = _officers.asStateFlow()

    init {
        loadDummyData()
        fetchOfficerData()
        updateStats()
    }

    private fun fetchOfficerData() {
        viewModelScope.launch {
            try {
                _officers.value = repository.fetchOfficers()
            } catch (e: Exception) {
                // Handle error (e.g., show a toast or snackbar)
            }
        }
    }

    private fun loadDummyData() {
        _complaints.value = listOf(
            Complaint(
                complaintId = "CMP2026001234",
                title = "Large pothole near City Market",
                description = "There is a large pothole near the City Market causing accidents.",
                categoryName = "Roads & Potholes",
                department = "Public Works",
                location = "MG Road, Near City Market",
                status = ComplaintStatus.WORK_STARTED,
                priority = Priority.HIGH,
                createdAt = Date(System.currentTimeMillis() - 86400000 * 5),
                userId = 1,
                supportCount = 24,
                isAiClassified = true,
                officerName = "Priya Singh",
                latitude = 12.9716,
                longitude = 77.5946,
                deadline = Date(System.currentTimeMillis() + 86400000 * 2),
                imageUrl = "https://images.unsplash.com/photo-1594818379496-da1e345b0ded", // Dummy pothole image
                statusHistory = listOf(
                    StatusHistoryItem(ComplaintStatus.SUBMITTED, Date(System.currentTimeMillis() - 400000000), "Complaint registered."),
                    StatusHistoryItem(ComplaintStatus.ASSIGNED, Date(System.currentTimeMillis() - 300000000), "Assigned to Priya Singh."),
                    StatusHistoryItem(ComplaintStatus.OFFICER_TRAVELING, Date(System.currentTimeMillis() - 200000000), "Officer is moving to site."),
                    StatusHistoryItem(ComplaintStatus.WORK_STARTED, Date(System.currentTimeMillis() - 100000000), "Repair work in progress.")
                )
            ),
            Complaint(
                complaintId = "CMP2026001237",
                title = "Gas Leakage Alert",
                description = "Strong smell of gas near apartment complex.",
                categoryName = "Public Safety",
                department = "Emergency Services",
                location = "Whitefield, Zone 2",
                status = ComplaintStatus.ASSIGNED,
                priority = Priority.EMERGENCY,
                createdAt = Date(),
                userId = 2,
                isEscalated = true,
                escalationLevel = "Supervisor",
                deadline = Date(System.currentTimeMillis() - 3600000) // Overdue
            ),
            Complaint(
                complaintId = "CMP2026001235",
                title = "Streetlight not working",
                description = "Sector 5 park area is dark.",
                categoryName = "Streetlights",
                department = "Power Department",
                location = "Sector 5 Park",
                status = ComplaintStatus.RESOLVED,
                priority = Priority.MEDIUM,
                createdAt = Date(),
                userId = 1,
                latitude = 12.9720,
                longitude = 77.5950
            )
        )
    }

    fun updateStats() {
        _departmentStats.value = mapOf(
            "Water Dept" to 95f,
            "Public Works" to 87f,
            "Power Dept" to 82f,
            "Sanitation" to 75f
        )
    }

    fun upvoteComplaint(id: String) {
        _complaints.value = _complaints.value.map {
            if (it.complaintId == id) it.copy(supportCount = it.supportCount + 1) else it
        }
    }

    fun analyzeComplaintText(text: String): Pair<String, Priority> {
        return when {
            text.lowercase().contains("gas") || text.lowercase().contains("accident") -> "Public Safety" to Priority.EMERGENCY
            text.lowercase().contains("leak") -> "Water Supply" to Priority.CRITICAL
            text.lowercase().contains("pothole") -> "Roads & Potholes" to Priority.HIGH
            text.lowercase().contains("light") -> "Streetlights" to Priority.MEDIUM
            else -> "General" to Priority.LOW
        }
    }

    fun checkDuplicate(title: String): Complaint? {
        return _complaints.value.find { 
            it.title.lowercase().contains(title.lowercase()) || title.lowercase().contains(it.title.lowercase())
        }
    }

    fun updateComplaintStatus(id: String, newStatus: ComplaintStatus, comment: String = "") {
        _complaints.value = _complaints.value.map {
            if (it.complaintId == id) {
                val newHistory = it.statusHistory + StatusHistoryItem(newStatus, Date(), comment)
                it.copy(status = newStatus, statusHistory = newHistory)
            } else it
        }
    }

    fun assignOfficer(complaintId: String, officerName: String) {
        _complaints.value = _complaints.value.map {
            if (it.complaintId == complaintId) {
                val newHistory = it.statusHistory + StatusHistoryItem(ComplaintStatus.ASSIGNED, Date(), "Assigned to $officerName")
                it.copy(status = ComplaintStatus.ASSIGNED, officerName = officerName, statusHistory = newHistory)
            } else it
        }
    }

    fun submitComplaint(title: String, description: String, category: String, department: String, location: String, priority: Priority, isAnonymous: Boolean = false, imageUrl: String? = null) {
        val newComplaint = Complaint(
            complaintId = "CMP${Date().time / 100000}",
            title = title,
            description = description,
            categoryName = category,
            department = department,
            location = location,
            status = ComplaintStatus.SUBMITTED,
            priority = priority,
            createdAt = Date(),
            userId = 1,
            isAnonymous = isAnonymous,
            imageUrl = imageUrl,
            statusHistory = listOf(StatusHistoryItem(ComplaintStatus.SUBMITTED, Date(), "Complaint submitted successfully."))
        )
        _complaints.value = listOf(newComplaint) + _complaints.value
    }
}