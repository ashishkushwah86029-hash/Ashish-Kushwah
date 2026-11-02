package com.example

import com.example.data.models.BookingStatus
import com.example.data.models.RoomListing
import com.example.data.models.RoomStatus
import com.example.data.models.UserProfile
import com.example.data.models.UserRole
import com.example.data.repository.RoomSaathiRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RoomSaathiBusinessLogicTest {

    @Test
    fun testRoleAssignmentAndGuard() {
        val owner = UserProfile(uid = "owner_test", name = "Test Owner", role = UserRole.OWNER)
        RoomSaathiRepository.setCurrentUser(owner)
        assertEquals(UserRole.OWNER, RoomSaathiRepository.currentUser.value?.role)

        val student = UserProfile(uid = "student_test", name = "Test Student", role = UserRole.STUDENT)
        RoomSaathiRepository.setCurrentUser(student)
        assertEquals(UserRole.STUDENT, RoomSaathiRepository.currentUser.value?.role)
    }

    @Test
    fun testFavouriteToggle() {
        val testRoomId = "test_room_99"
        assertFalse(RoomSaathiRepository.isFavourite(testRoomId))

        RoomSaathiRepository.toggleFavourite(testRoomId)
        assertTrue(RoomSaathiRepository.isFavourite(testRoomId))

        RoomSaathiRepository.toggleFavourite(testRoomId)
        assertFalse(RoomSaathiRepository.isFavourite(testRoomId))
    }

    @Test
    fun testRoomCreationAndAvailability() {
        val newRoom = RoomListing(
            roomId = "room_custom_101",
            title = "Test Single AC Room",
            rent = 7000.0,
            city = "Indore",
            locality = "Bhawarkua",
            availability = RoomStatus.AVAILABLE
        )
        RoomSaathiRepository.addRoom(newRoom)
        assertTrue(RoomSaathiRepository.rooms.value.any { it.roomId == "room_custom_101" })

        RoomSaathiRepository.setRoomAvailability("room_custom_101", RoomStatus.OCCUPIED)
        val updated = RoomSaathiRepository.rooms.value.find { it.roomId == "room_custom_101" }
        assertEquals(RoomStatus.OCCUPIED, updated?.availability)
    }

    @Test
    fun testBookingCreationAndStatusCycle() {
        val room = RoomSaathiRepository.rooms.value.first()
        val student = UserProfile(uid = "student_101", name = "Rohan", phone = "+91 99999 11111", role = UserRole.STUDENT)

        val success = RoomSaathiRepository.createBookingRequest(room, student, "Can I visit tomorrow?")
        assertTrue(success)

        val latestBooking = RoomSaathiRepository.bookings.value.first()
        assertEquals(BookingStatus.PENDING, latestBooking.status)

        RoomSaathiRepository.updateBookingStatus(latestBooking.bookingId, BookingStatus.ACCEPTED)
        val acceptedBooking = RoomSaathiRepository.bookings.value.find { it.bookingId == latestBooking.bookingId }
        assertEquals(BookingStatus.ACCEPTED, acceptedBooking?.status)
    }
}
