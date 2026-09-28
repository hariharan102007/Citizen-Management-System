package com.example.citizencomplaintapp.data.model

data class Feedback(
    val feedbackId: Int,
    val complaintId: String,
    val rating: Int,
    val comments: String
)