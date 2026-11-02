package com.example.data.repository

import com.example.data.models.BookingRequest
import com.example.data.models.BookingStatus
import com.example.data.models.GenderPreference
import com.example.data.models.NotificationItem
import com.example.data.models.RoomListing
import com.example.data.models.RoomStatus
import com.example.data.models.RoomType
import com.example.data.models.UserProfile
import com.example.data.models.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object RoomSaathiRepository {
    // Current Active User
    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    // Room Listings
    private val _rooms = MutableStateFlow<List<RoomListing>>(sampleInitialRooms())
    val rooms: StateFlow<List<RoomListing>> = _rooms.asStateFlow()

    // Bookings
    private val _bookings = MutableStateFlow<List<BookingRequest>>(sampleInitialBookings())
    val bookings: StateFlow<List<BookingRequest>> = _bookings.asStateFlow()

    // Favourites (Set of Room IDs for current student)
    private val _favouriteRoomIds = MutableStateFlow<Set<String>>(setOf("room_1", "room_3"))
    val favouriteRoomIds: StateFlow<Set<String>> = _favouriteRoomIds.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow<List<NotificationItem>>(sampleInitialNotifications())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    fun setCurrentUser(user: UserProfile?) {
        _currentUser.value = user
    }

    fun updateUserProfile(name: String, phone: String, city: String, college: String) {
        val curr = _currentUser.value ?: return
        _currentUser.value = curr.copy(
            name = name,
            phone = phone,
            city = city,
            college = college
        )
    }

    fun toggleFavourite(roomId: String) {
        val currentSet = _favouriteRoomIds.value.toMutableSet()
        if (currentSet.contains(roomId)) {
            currentSet.remove(roomId)
        } else {
            currentSet.add(roomId)
        }
        _favouriteRoomIds.value = currentSet
    }

    fun isFavourite(roomId: String): Boolean {
        return _favouriteRoomIds.value.contains(roomId)
    }

    fun addRoom(room: RoomListing) {
        val list = _rooms.value.toMutableList()
        list.add(0, room)
        _rooms.value = list
    }

    fun updateRoom(updatedRoom: RoomListing) {
        val list = _rooms.value.map {
            if (it.roomId == updatedRoom.roomId) updatedRoom else it
        }
        _rooms.value = list
    }

    fun deleteRoom(roomId: String) {
        _rooms.value = _rooms.value.filter { it.roomId != roomId }
    }

    fun setRoomAvailability(roomId: String, newStatus: RoomStatus) {
        _rooms.value = _rooms.value.map {
            if (it.roomId == roomId) it.copy(availability = newStatus) else it
        }
    }

    fun createBookingRequest(
        room: RoomListing,
        student: UserProfile,
        message: String
    ): Boolean {
        // Prevent multiple conflicting active bookings from same student for same room
        val hasExistingActive = _bookings.value.any {
            it.roomId == room.roomId && it.studentId == student.uid &&
                    (it.status == BookingStatus.PENDING || it.status == BookingStatus.ACCEPTED)
        }
        if (hasExistingActive) return false

        val newBooking = BookingRequest(
            bookingId = "bk_${UUID.randomUUID().toString().take(8)}",
            roomId = room.roomId,
            roomTitle = room.title,
            roomCity = room.city,
            roomRent = room.rent,
            ownerId = room.ownerId,
            ownerName = room.ownerName,
            ownerPhone = room.ownerPhone,
            studentId = student.uid,
            studentName = student.name.ifBlank { "Student" },
            studentPhone = student.phone,
            studentCollege = student.college,
            message = message,
            status = BookingStatus.PENDING,
            createdAt = System.currentTimeMillis()
        )
        _bookings.value = listOf(newBooking) + _bookings.value

        // Notify owner
        val ownerNotification = NotificationItem(
            notificationId = "notif_${UUID.randomUUID().toString().take(8)}",
            userId = room.ownerId,
            title = "New Booking Request Received!",
            message = "${student.name.ifBlank { "A student" }} sent a booking request for '${room.title}'.",
            type = "booking_request",
            read = false,
            createdAt = System.currentTimeMillis()
        )
        _notifications.value = listOf(ownerNotification) + _notifications.value
        return true
    }

    fun updateBookingStatus(bookingId: String, newStatus: BookingStatus) {
        val booking = _bookings.value.find { it.bookingId == bookingId } ?: return
        _bookings.value = _bookings.value.map {
            if (it.bookingId == bookingId) it.copy(status = newStatus) else it
        }

        // If accepted, notify student
        val msg = when (newStatus) {
            BookingStatus.ACCEPTED -> "Congratulations! Your booking request for '${booking.roomTitle}' was ACCEPTED by the owner."
            BookingStatus.REJECTED -> "Your booking request for '${booking.roomTitle}' was not accepted."
            BookingStatus.COMPLETED -> "Your stay at '${booking.roomTitle}' is marked as completed."
            BookingStatus.CANCELLED -> "Booking for '${booking.roomTitle}' was cancelled."
            else -> "Booking status updated to ${newStatus.displayName}."
        }
        val notif = NotificationItem(
            notificationId = "notif_${UUID.randomUUID().toString().take(8)}",
            userId = booking.studentId,
            title = "Booking Update: ${newStatus.displayName}",
            message = msg,
            type = "booking_update",
            read = false,
            createdAt = System.currentTimeMillis()
        )
        _notifications.value = listOf(notif) + _notifications.value
    }

    fun reportListing(roomId: String, reason: String) {
        _rooms.value = _rooms.value.map {
            if (it.roomId == roomId) it.copy(isReported = true, reportReason = reason) else it
        }
    }

    fun markNotificationAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.notificationId == id) it.copy(read = true) else it
        }
    }

    private fun sampleInitialRooms(): List<RoomListing> = listOf(
        RoomListing(
            roomId = "room_1",
            ownerId = "owner_ramesh",
            ownerName = "Ramesh Sharma",
            ownerPhone = "+91 98260 12345",
            title = "Deluxe AC Single Room near Coaching Hub",
            description = "Spacious, well-ventilated single room with dedicated study desk, high-speed optic fiber Wi-Fi, and 24x7 RO water. Walking distance from major coaching institutes and central library.",
            address = "Plot 42, Anand Nagar, Bhawarkua Main Road",
            city = "Indore",
            locality = "Bhawarkua",
            nearbyLandmark = "Near Holkar Science College & Bus Stand",
            rent = 6500.0,
            deposit = 6500.0,
            roomType = RoomType.SINGLE,
            allowedOccupants = 1,
            availability = RoomStatus.AVAILABLE,
            genderPreference = GenderPreference.BOYS,
            photos = listOf(
                "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1598928506311-c55ded91a20c?w=800&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=800&auto=format&fit=crop&q=80"
            ),
            furniture = mapOf(
                "Table" to true,
                "Chair" to true,
                "Almirah" to true,
                "Bed" to true,
                "Mattress" to true,
                "Study Table" to true,
                "Bookshelf" to true,
                "Sofa" to false
            ),
            electricalFacilities = mapOf(
                "Fan" to true,
                "AC" to true,
                "Cooler" to false,
                "Light" to true,
                "Study Lamp" to true,
                "Geyser" to true
            ),
            otherFacilities = mapOf(
                "Wi-Fi" to true,
                "Attached Bathroom" to true,
                "Balcony" to true,
                "Kitchen" to false,
                "Water Supply" to true,
                "Washing Machine" to true,
                "Parking" to true,
                "Power Backup" to true,
                "Drinking Water" to true
            ),
            additionalFacilityNotes = "Bi-weekly room cleaning included in rent. Gate closing time 10:30 PM."
        ),
        RoomListing(
            roomId = "room_2",
            ownerId = "owner_sunita",
            ownerName = "Sunita Verma",
            ownerPhone = "+91 94250 67890",
            title = "Safe & Peaceful Girls Hostel Sharing Room",
            description = "Homely and strictly secure girls room with biometric access, in-house warden, wholesome vegetarian home-cooked meals (optional), and peaceful study environment.",
            address = "B-14, Scheme 54, Vijay Nagar",
            city = "Indore",
            locality = "Vijay Nagar",
            nearbyLandmark = "Behind C21 Mall & Brilliant Convention Centre",
            rent = 5200.0,
            deposit = 5000.0,
            roomType = RoomType.DOUBLE_SHARING,
            allowedOccupants = 2,
            availability = RoomStatus.AVAILABLE,
            genderPreference = GenderPreference.GIRLS,
            photos = listOf(
                "https://images.unsplash.com/photo-1595526114035-0d45ed16cfbf?w=800&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1540518614846-7ede433c4ef0?w=800&auto=format&fit=crop&q=80"
            ),
            furniture = mapOf(
                "Table" to true,
                "Chair" to true,
                "Almirah" to true,
                "Bed" to true,
                "Mattress" to true,
                "Study Table" to true,
                "Bookshelf" to false,
                "Sofa" to false
            ),
            electricalFacilities = mapOf(
                "Fan" to true,
                "AC" to false,
                "Cooler" to true,
                "Light" to true,
                "Study Lamp" to true,
                "Geyser" to true
            ),
            otherFacilities = mapOf(
                "Wi-Fi" to true,
                "Attached Bathroom" to true,
                "Balcony" to false,
                "Kitchen" to true,
                "Water Supply" to true,
                "Washing Machine" to true,
                "Parking" to true,
                "Power Backup" to true,
                "Drinking Water" to true
            ),
            additionalFacilityNotes = "CCTV cameras in common areas, high-speed 100Mbps Wi-Fi."
        ),
        RoomListing(
            roomId = "room_3",
            ownerId = "owner_vikram",
            ownerName = "Vikram Patel",
            ownerPhone = "+91 97555 43210",
            title = "Independent 1 BHK Flat for Students & Groups",
            description = "Full furnished 1 BHK flat with private kitchen, balcony, refrigerator, and modular storage. Ideal for 2-3 college friends who want total privacy and independent living.",
            address = "Flat 302, Green Heights, Geeta Bhawan Square",
            city = "Indore",
            locality = "Geeta Bhawan",
            nearbyLandmark = "Close to Medical College & Railway Station",
            rent = 11000.0,
            deposit = 15000.0,
            roomType = RoomType.ONE_BHK,
            allowedOccupants = 3,
            availability = RoomStatus.AVAILABLE,
            genderPreference = GenderPreference.ALL,
            photos = listOf(
                "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1554995207-c18c203602cb?w=800&auto=format&fit=crop&q=80"
            ),
            furniture = mapOf(
                "Table" to true,
                "Chair" to true,
                "Almirah" to true,
                "Bed" to true,
                "Mattress" to true,
                "Study Table" to true,
                "Bookshelf" to true,
                "Sofa" to true
            ),
            electricalFacilities = mapOf(
                "Fan" to true,
                "AC" to true,
                "Cooler" to true,
                "Light" to true,
                "Study Lamp" to true,
                "Geyser" to true
            ),
            otherFacilities = mapOf(
                "Wi-Fi" to true,
                "Attached Bathroom" to true,
                "Balcony" to true,
                "Kitchen" to true,
                "Water Supply" to true,
                "Washing Machine" to true,
                "Parking" to true,
                "Power Backup" to true,
                "Drinking Water" to true
            ),
            additionalFacilityNotes = "No owner interference, independent electricity sub-meter."
        )
    )

    private fun sampleInitialBookings(): List<BookingRequest> = listOf(
        BookingRequest(
            bookingId = "bk_101",
            roomId = "room_1",
            roomTitle = "Deluxe AC Single Room near Coaching Hub",
            roomCity = "Indore",
            roomRent = 6500.0,
            ownerId = "owner_ramesh",
            ownerName = "Ramesh Sharma",
            ownerPhone = "+91 98260 12345",
            studentId = "student_ashish",
            studentName = "Ashish Kushwah",
            studentPhone = "+91 86029 12345",
            studentCollege = "SGSITS College of Engineering",
            message = "Hello sir, I am a 2nd year B.Tech student looking for a quiet study room for the full academic year. Can I visit tomorrow afternoon?",
            status = BookingStatus.PENDING,
            createdAt = System.currentTimeMillis() - 3600000L * 5
        )
    )

    private fun sampleInitialNotifications(): List<NotificationItem> = listOf(
        NotificationItem(
            notificationId = "notif_001",
            userId = "student_ashish",
            title = "Welcome to RoomSaathi!",
            message = "Find your perfect room with zero brokerage and verified room owners.",
            type = "welcome",
            read = false,
            createdAt = System.currentTimeMillis() - 86400000L
        )
    )
}
