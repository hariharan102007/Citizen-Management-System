package com.example.citizencomplaintapp.data.api

import com.example.citizencomplaintapp.data.model.Officer
import com.example.citizencomplaintapp.data.model.Complaint

// This is where you would use Retrofit annotations like @GET("officers")
interface ApiService {
    suspend fun getOfficers(): List<Officer>
    suspend fun getComplaints(): List<Complaint>
}

// Mock implementation for current development
class MockApiService : ApiService {
    override suspend fun getOfficers(): List<Officer> {
        return listOf(
            Officer("OFF001", "Priya Singh", "Public Works", "Senior Engineer", "priya.s@civic.gov", "+91 9876543210", 12, 45, 4.8f),
            Officer("OFF002", "Amit Verma", "Water Department", "Assistant Engineer", "amit.v@civic.gov", "+91 9876543211", 8, 32, 4.5f),
            Officer("OFF003", "Suresh Kumar", "Power Department", "Field Officer", "suresh.k@civic.gov", "+91 9876543212", 15, 28, 4.2f)
        )
    }

    override suspend fun getComplaints(): List<Complaint> {
        return emptyList() // Simplified for this example
    }
}