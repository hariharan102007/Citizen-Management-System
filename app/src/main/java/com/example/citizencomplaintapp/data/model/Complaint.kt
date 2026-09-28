package com.example.citizencomplaintapp.data.model

import java.util.Date

data class Complaint(
    val complaintId: String,
    val title: String,
    val description: String,
    val categoryName: String,
    val department: String,
    val location: String,
    val status: ComplaintStatus,
    val priority: Priority,
    val createdAt: Date,
    val userId: Int,
    val supportCount: Int = 0,
    val isAiClassified: Boolean = false,
    val officerName: String? = null,
    val remarks: List<Remark> = emptyList(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val statusHistory: List<StatusHistoryItem> = emptyList(),
    val imageUrl: String? = null,
    val isAnonymous: Boolean = false,
    val upvotes: Int = 0,
    val deadline: Date? = null,
    val isEscalated: Boolean = false,
    val escalationLevel: String? = null
)

data class StatusHistoryItem(
    val status: ComplaintStatus,
    val timestamp: Date,
    val description: String
)

data class Remark(
    val author: String,
    val message: String,
    val timeAgo: String,
    val isOfficial: Boolean = true
)

enum class ComplaintStatus {
    SUBMITTED,
    ASSIGNED,
    OFFICER_TRAVELING,
    WORK_STARTED,
    IN_PROGRESS,
    RESOLVED,
    CLOSED
}

enum class Priority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL,
    EMERGENCY
}