package com.example.citizencomplaintapp.data.repository

import com.example.citizencomplaintapp.data.api.ApiService
import com.example.citizencomplaintapp.data.model.Officer
import com.example.citizencomplaintapp.data.model.Complaint

class AppRepository(private val apiService: ApiService) {
    
    suspend fun fetchOfficers(): List<Officer> {
        // In a real app, you might check a local database first (Room)
        // then fetch from network if needed.
        return apiService.getOfficers()
    }

    suspend fun fetchComplaints(): List<Complaint> {
        return apiService.getComplaints()
    }
}