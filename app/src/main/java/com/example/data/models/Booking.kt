package com.example.data.models

enum class BookingStatus(val displayName: String) {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed")
}

data class BookingRequest(
    val bookingId: String = "",
    val roomId: String = "",
    val roomTitle: String = "",
    val roomCity: String = "",
    val roomRent: Double = 0.0,
    val ownerId: String = "",
    val ownerName: String = "",
    val ownerPhone: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val studentPhone: String = "",
    val studentCollege: String = "",
    val message: String = "",
    val status: BookingStatus = BookingStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)

data class NotificationItem(
    val notificationId: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "general",
    val read: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
