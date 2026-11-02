package com.example.data.models

enum class UserRole {
    OWNER,
    STUDENT,
    ADMIN;

    companion object {
        fun fromString(value: String?): UserRole {
            return when (value?.uppercase()) {
                "OWNER" -> OWNER
                "ADMIN" -> ADMIN
                else -> STUDENT
            }
        }
    }
}

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val phone: String = "",
    val role: UserRole = UserRole.STUDENT,
    val profileImage: String = "",
    val city: String = "Indore",
    val college: String = "",
    val isVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
