package com.example.citizencomplaintapp.data.model

data class User(
    val userId: Int,
    val name: String,
    val email: String,
    val mobile: String,
    val role: UserRole
)

enum class UserRole {
    CITIZEN,
    OFFICER,
    ADMIN
}