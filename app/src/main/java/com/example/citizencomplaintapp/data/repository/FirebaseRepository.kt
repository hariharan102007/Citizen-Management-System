package com.example.citizencomplaintapp.data.repository

import com.example.citizencomplaintapp.data.model.Complaint
import com.example.citizencomplaintapp.data.model.Officer
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val complaintsCollection = firestore.collection("complaints")
    private val officersCollection = firestore.collection("officers")

    // Real-time updates for complaints
    fun getComplaintsFlow(): Flow<List<Complaint>> = callbackFlow {
        val subscription = complaintsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val complaints = snapshot.toObjects(Complaint::class.java)
                trySend(complaints)
            }
        }
        awaitClose { subscription.remove() }
    }

    suspend fun submitComplaint(complaint: Complaint) {
        complaintsCollection.document(complaint.complaintId).set(complaint).await()
    }

    suspend fun updateComplaint(complaint: Complaint) {
        complaintsCollection.document(complaint.complaintId).set(complaint).await()
    }

    suspend fun getOfficers(): List<Officer> {
        return officersCollection.get().await().toObjects(Officer::class.java)
    }
}
