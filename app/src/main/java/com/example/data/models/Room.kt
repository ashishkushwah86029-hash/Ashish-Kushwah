package com.example.data.models

enum class RoomType(val displayName: String) {
    SINGLE("Single Room"),
    DOUBLE_SHARING("Double Sharing"),
    TRIPLE_SHARING("Triple Sharing"),
    ONE_BHK("1 BHK Flat"),
    TWO_BHK("2 BHK Flat"),
    HOSTEL_BED("Hostel Bed")
}

enum class GenderPreference(val displayName: String) {
    ALL("All (Boys & Girls)"),
    BOYS("Boys Only"),
    GIRLS("Girls Only")
}

enum class RoomStatus(val displayName: String) {
    AVAILABLE("Available"),
    OCCUPIED("Occupied"),
    HIDDEN("Hidden")
}

data class RoomListing(
    val roomId: String = "",
    val ownerId: String = "",
    val ownerName: String = "",
    val ownerPhone: String = "",
    val title: String = "",
    val description: String = "",
    val address: String = "",
    val city: String = "Indore",
    val locality: String = "",
    val nearbyLandmark: String = "",
    val rent: Double = 0.0,
    val deposit: Double = 0.0,
    val roomType: RoomType = RoomType.SINGLE,
    val allowedOccupants: Int = 1,
    val availability: RoomStatus = RoomStatus.AVAILABLE,
    val genderPreference: GenderPreference = GenderPreference.ALL,
    val photos: List<String> = emptyList(),
    val furniture: Map<String, Boolean> = defaultFurnitureMap(),
    val electricalFacilities: Map<String, Boolean> = defaultElectricalMap(),
    val otherFacilities: Map<String, Boolean> = defaultOtherFacilitiesMap(),
    val additionalFacilityNotes: String = "",
    val latitude: Double = 22.7196,
    val longitude: Double = 75.8577,
    val isReported: Boolean = false,
    val reportReason: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

fun defaultFurnitureMap(): Map<String, Boolean> = mapOf(
    "Table" to false,
    "Chair" to false,
    "Almirah" to false,
    "Bed" to false,
    "Mattress" to false,
    "Study Table" to false,
    "Bookshelf" to false,
    "Sofa" to false
)

fun defaultElectricalMap(): Map<String, Boolean> = mapOf(
    "Fan" to true,
    "AC" to false,
    "Cooler" to false,
    "Light" to true,
    "Study Lamp" to false,
    "Geyser" to false
)

fun defaultOtherFacilitiesMap(): Map<String, Boolean> = mapOf(
    "Wi-Fi" to false,
    "Attached Bathroom" to false,
    "Balcony" to false,
    "Kitchen" to false,
    "Water Supply" to true,
    "Washing Machine" to false,
    "Parking" to false,
    "Power Backup" to false,
    "Drinking Water" to true
)
