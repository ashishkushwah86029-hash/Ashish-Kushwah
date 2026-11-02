package com.example.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.data.models.BookingRequest
import com.example.data.models.BookingStatus
import com.example.data.models.GenderPreference
import com.example.data.models.RoomListing
import com.example.data.models.RoomStatus
import com.example.data.models.RoomType
import com.example.data.models.UserProfile
import com.example.data.repository.RoomSaathiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class SortOption(val displayName: String) {
    RECOMMENDED("Recommended"),
    RENT_LOW_TO_HIGH("Lowest Rent First"),
    RENT_HIGH_TO_LOW("Highest Rent First"),
    RECENTLY_ADDED("Recently Added")
}

data class FilterState(
    val searchQuery: String = "",
    val selectedCity: String = "All Cities",
    val selectedRoomType: RoomType? = null,
    val selectedGenderPreference: GenderPreference? = null,
    val minRent: Double = 0.0,
    val maxRent: Double = 30000.0,
    val mustHaveWifi: Boolean = false,
    val mustHaveAc: Boolean = false,
    val mustHaveAttachedBathroom: Boolean = false,
    val mustHaveParking: Boolean = false,
    val mustBeFurnished: Boolean = false,
    val onlyAvailableNow: Boolean = true,
    val sortOption: SortOption = SortOption.RECOMMENDED
)

class RoomViewModel : ViewModel() {
    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    val allRooms: StateFlow<List<RoomListing>> = RoomSaathiRepository.rooms
    val allBookings: StateFlow<List<BookingRequest>> = RoomSaathiRepository.bookings
    val favouriteRoomIds: StateFlow<Set<String>> = RoomSaathiRepository.favouriteRoomIds
    val notifications = RoomSaathiRepository.notifications

    // Filtered rooms for student exploration
    val filteredRooms = combine(allRooms, filterState) { rooms, filters ->
        rooms.filter { room ->
            // Availability filter
            if (filters.onlyAvailableNow && room.availability != RoomStatus.AVAILABLE) {
                return@filter false
            }
            // City
            if (filters.selectedCity != "All Cities" && !room.city.equals(filters.selectedCity, ignoreCase = true)) {
                return@filter false
            }
            // Search query across title, locality, landmark, address
            if (filters.searchQuery.isNotBlank()) {
                val q = filters.searchQuery.trim().lowercase()
                val match = room.title.lowercase().contains(q) ||
                        room.locality.lowercase().contains(q) ||
                        room.nearbyLandmark.lowercase().contains(q) ||
                        room.city.lowercase().contains(q) ||
                        room.address.lowercase().contains(q)
                if (!match) return@filter false
            }
            // Rent range
            if (room.rent < filters.minRent || room.rent > filters.maxRent) {
                return@filter false
            }
            // Room Type
            if (filters.selectedRoomType != null && room.roomType != filters.selectedRoomType) {
                return@filter false
            }
            // Gender preference
            if (filters.selectedGenderPreference != null &&
                room.genderPreference != GenderPreference.ALL &&
                room.genderPreference != filters.selectedGenderPreference
            ) {
                return@filter false
            }
            // Wi-Fi
            if (filters.mustHaveWifi && room.otherFacilities["Wi-Fi"] != true) {
                return@filter false
            }
            // AC
            if (filters.mustHaveAc && room.electricalFacilities["AC"] != true) {
                return@filter false
            }
            // Attached bathroom
            if (filters.mustHaveAttachedBathroom && room.otherFacilities["Attached Bathroom"] != true) {
                return@filter false
            }
            // Parking
            if (filters.mustHaveParking && room.otherFacilities["Parking"] != true) {
                return@filter false
            }
            // Furnished check
            if (filters.mustBeFurnished) {
                val hasBed = room.furniture["Bed"] == true
                val hasAlmirah = room.furniture["Almirah"] == true
                if (!hasBed && !hasAlmirah) return@filter false
            }
            true
        }.sortedWith { a, b ->
            when (filters.sortOption) {
                SortOption.RENT_LOW_TO_HIGH -> a.rent.compareTo(b.rent)
                SortOption.RENT_HIGH_TO_LOW -> b.rent.compareTo(a.rent)
                SortOption.RECENTLY_ADDED -> b.createdAt.compareTo(a.createdAt)
                SortOption.RECOMMENDED -> b.photos.size.compareTo(a.photos.size)
            }
        }
    }.stateIn(
        scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default),
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    fun updateSearchQuery(query: String) {
        _filterState.value = _filterState.value.copy(searchQuery = query)
    }

    fun updateCity(city: String) {
        _filterState.value = _filterState.value.copy(selectedCity = city)
    }

    fun updateSortOption(sort: SortOption) {
        _filterState.value = _filterState.value.copy(sortOption = sort)
    }

    fun updateFilters(newFilters: FilterState) {
        _filterState.value = newFilters
    }

    fun resetFilters() {
        _filterState.value = FilterState()
    }

    fun toggleFavourite(roomId: String) {
        RoomSaathiRepository.toggleFavourite(roomId)
    }

    fun isFavourite(roomId: String): Boolean {
        return RoomSaathiRepository.isFavourite(roomId)
    }

    fun addRoom(room: RoomListing) {
        RoomSaathiRepository.addRoom(room)
    }

    fun updateRoom(room: RoomListing) {
        RoomSaathiRepository.updateRoom(room)
    }

    fun deleteRoom(roomId: String) {
        RoomSaathiRepository.deleteRoom(roomId)
    }

    fun toggleAvailability(roomId: String, current: RoomStatus) {
        val next = if (current == RoomStatus.AVAILABLE) RoomStatus.OCCUPIED else RoomStatus.AVAILABLE
        RoomSaathiRepository.setRoomAvailability(roomId, next)
    }

    fun sendBookingRequest(room: RoomListing, student: UserProfile, message: String): Boolean {
        return RoomSaathiRepository.createBookingRequest(room, student, message)
    }

    fun updateBookingStatus(bookingId: String, status: BookingStatus) {
        RoomSaathiRepository.updateBookingStatus(bookingId, status)
    }

    fun reportListing(roomId: String, reason: String) {
        RoomSaathiRepository.reportListing(roomId, reason)
    }
}
