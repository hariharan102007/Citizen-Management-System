package com.example.citizencomplaintapp.data.model

data class Officer(
    val officerId: String,
    val name: String,
    val department: String,
    val designation: String,
    val email: String,
    val phoneNumber: String,
    val assignedComplaints: Int,
    val resolvedComplaints: Int,
    val rating: Float,
    val status: OfficerStatus = OfficerStatus.ACTIVE,
    val profileImage: String? = null
)

enum class OfficerStatus {
    ACTIVE,
    ON_LEAVE,
    SUSPENDED
}