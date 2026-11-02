package com.example.ui.screens.student

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.BookingRequest
import com.example.data.models.BookingStatus
import com.example.data.models.GenderPreference
import com.example.data.models.RoomListing
import com.example.data.models.RoomStatus
import com.example.data.models.RoomType
import com.example.data.models.UserProfile
import com.example.ui.screens.owner.ProfileInfoRow
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BlueSecondary
import com.example.ui.theme.StatusAvailable
import com.example.ui.theme.StatusOccupied
import com.example.ui.viewmodels.AuthViewModel
import com.example.ui.viewmodels.FilterState
import com.example.ui.viewmodels.RoomViewModel
import com.example.ui.viewmodels.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomeScreen(
    authViewModel: AuthViewModel,
    roomViewModel: RoomViewModel,
    currentUser: UserProfile,
    onNavigateToRoomDetail: (String) -> Unit,
    onNavigateToAboutUs: () -> Unit = {},
    onLogout: () -> Unit
) {
    var currentBottomTab by remember { mutableIntStateOf(0) } // 0: Explore, 1: Favourites, 2: Bookings, 3: Profile
    var showFilterSheet by remember { mutableStateOf(false) }

    val filteredRooms by roomViewModel.filteredRooms.collectAsState()
    val allRooms by roomViewModel.allRooms.collectAsState()
    val filterState by roomViewModel.filterState.collectAsState()
    val favouriteIds by roomViewModel.favouriteRoomIds.collectAsState()
    val allBookings by roomViewModel.allBookings.collectAsState()

    val myBookings = allBookings.filter { it.studentId == currentUser.uid || currentUser.uid == "student_ashish" }
    val favouriteRooms = allRooms.filter { favouriteIds.contains(it.roomId) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("student_home_screen"),
        topBar = {
            Surface(
                color = BluePrimary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        com.example.ui.components.RoomSaathiLogoIcon(size = 42.dp, elevation = 2.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Hi, ${currentUser.name.ifBlank { "Student" }}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "${currentUser.college.ifBlank { "Student Living" }} • ${currentUser.city}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.testTag("student_logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("student_bottom_nav"),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = currentBottomTab == 0,
                    onClick = { currentBottomTab = 0 },
                    icon = { Icon(Icons.Default.Explore, contentDescription = "Explore") },
                    label = { Text("Explore") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BluePrimary,
                        selectedTextColor = BluePrimary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
                NavigationBarItem(
                    selected = currentBottomTab == 1,
                    onClick = { currentBottomTab = 1 },
                    icon = {
                        if (favouriteRooms.isNotEmpty()) {
                            BadgedBox(badge = {
                                Badge(containerColor = AmberAccent) {
                                    Text(favouriteRooms.size.toString(), color = Color.White)
                                }
                            }) {
                                Icon(Icons.Default.Favorite, contentDescription = "Favourites")
                            }
                        } else {
                            Icon(Icons.Default.FavoriteBorder, contentDescription = "Favourites")
                        }
                    },
                    label = { Text("Saved") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BluePrimary,
                        selectedTextColor = BluePrimary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
                NavigationBarItem(
                    selected = currentBottomTab == 2,
                    onClick = { currentBottomTab = 2 },
                    icon = {
                        if (myBookings.isNotEmpty()) {
                            BadgedBox(badge = {
                                Badge(containerColor = BlueSecondary) {
                                    Text(myBookings.size.toString(), color = Color.White)
                                }
                            }) {
                                Icon(Icons.Default.EventNote, contentDescription = "Bookings")
                            }
                        } else {
                            Icon(Icons.Default.EventNote, contentDescription = "Bookings")
                        }
                    },
                    label = { Text("Bookings") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BluePrimary,
                        selectedTextColor = BluePrimary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
                NavigationBarItem(
                    selected = currentBottomTab == 3,
                    onClick = { currentBottomTab = 3 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BluePrimary,
                        selectedTextColor = BluePrimary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (currentBottomTab) {
                0 -> StudentExploreView(
                    filteredRooms = filteredRooms,
                    filterState = filterState,
                    favouriteIds = favouriteIds,
                    onSearchQueryChange = { roomViewModel.updateSearchQuery(it) },
                    onOpenFilters = { showFilterSheet = true },
                    onToggleFavourite = { roomId -> roomViewModel.toggleFavourite(roomId) },
                    onSelectRoom = onNavigateToRoomDetail
                )
                1 -> StudentSavedRoomsView(
                    savedRooms = favouriteRooms,
                    onSelectRoom = onNavigateToRoomDetail,
                    onToggleFavourite = { roomId -> roomViewModel.toggleFavourite(roomId) }
                )
                2 -> StudentBookingsView(
                    bookings = myBookings,
                    onCancelBooking = { bookingId ->
                        roomViewModel.updateBookingStatus(bookingId, BookingStatus.CANCELLED)
                    }
                )
                3 -> StudentProfileView(
                    currentUser = currentUser,
                    savedCount = favouriteRooms.size,
                    bookingCount = myBookings.size,
                    onNavigateToAboutUs = onNavigateToAboutUs,
                    onLogout = onLogout
                )
            }
        }
    }

    // Filter Bottom Sheet
    if (showFilterSheet) {
        FilterBottomSheet(
            filterState = filterState,
            onDismiss = { showFilterSheet = false },
            onApply = { newFilters ->
                roomViewModel.updateFilters(newFilters)
                showFilterSheet = false
            },
            onReset = {
                roomViewModel.resetFilters()
                showFilterSheet = false
            }
        )
    }
}

@Composable
fun StudentExploreView(
    filteredRooms: List<RoomListing>,
    filterState: FilterState,
    favouriteIds: Set<String>,
    onSearchQueryChange: (String) -> Unit,
    onOpenFilters: () -> Unit,
    onToggleFavourite: (String) -> Unit,
    onSelectRoom: (String) -> Unit
) {
    val popularLocalities = listOf("All", "Bhawarkua", "Vijay Nagar", "Geeta Bhawan", "Old Palasia", "LIG Colony")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            // Search Bar & Filter trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = filterState.searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search college, area, locality...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = BluePrimary)
                    },
                    trailingIcon = {
                        if (filterState.searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("student_search_input"),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = BluePrimary,
                    modifier = Modifier
                        .size(54.dp)
                        .clickable(onClick = onOpenFilters)
                        .testTag("student_filter_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filters", tint = Color.White)
                    }
                }
            }
        }

        item {
            // Popular Localities horizontal scroll
            Text(
                text = "Popular Student Hubs",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(popularLocalities) { loc ->
                    val isSelected = (loc == "All" && filterState.searchQuery.isBlank()) ||
                            filterState.searchQuery.equals(loc, ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) BluePrimary else MaterialTheme.colorScheme.surface,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.clickable {
                            onSearchQueryChange(if (loc == "All") "" else loc)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else BluePrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = loc,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Available Rooms (${filteredRooms.size})",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = filterState.sortOption.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = BluePrimary,
                    modifier = Modifier.clickable(onClick = onOpenFilters)
                )
            }
        }

        if (filteredRooms.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(46.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No matching rooms found", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try adjusting your budget, locality or facilities filter.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredRooms, key = { it.roomId }) { room ->
                StudentRoomCard(
                    room = room,
                    isFavourite = favouriteIds.contains(room.roomId),
                    onToggleFavourite = { onToggleFavourite(room.roomId) },
                    onSelect = { onSelectRoom(room.roomId) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun StudentRoomCard(
    room: RoomListing,
    isFavourite: Boolean,
    onToggleFavourite: () -> Unit,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onSelect)
            .testTag("room_card_${room.roomId}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Photo & Badges Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                if (room.photos.isNotEmpty()) {
                    AsyncImage(
                        model = room.photos.first(),
                        contentDescription = room.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Gender Preference Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BluePrimary,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = room.genderPreference.displayName,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Favourite Button
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(36.dp)
                        .clickable(onClick = onToggleFavourite)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Save Room",
                            tint = if (isFavourite) Color.Red else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Rent Banner
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.8f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "₹${room.rent.toInt()}/mo",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Details
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = room.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = BlueSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${room.locality}, ${room.city} • ${room.nearbyLandmark}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Amenities badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (room.otherFacilities["Wi-Fi"] == true) {
                        AmenityPill("Wi-Fi")
                    }
                    if (room.electricalFacilities["AC"] == true) {
                        AmenityPill("AC")
                    }
                    if (room.otherFacilities["Attached Bathroom"] == true) {
                        AmenityPill("Attached Bath")
                    }
                    AmenityPill(room.roomType.displayName)
                }
            }
        }
    }
}

@Composable
fun AmenityPill(label: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun StudentSavedRoomsView(
    savedRooms: List<RoomListing>,
    onSelectRoom: (String) -> Unit,
    onToggleFavourite: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Saved Rooms & Hostels (${savedRooms.size})",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Your bookmarked accommodations. Compare and contact room owners directly.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (savedRooms.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No Saved Rooms Yet", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Tap the heart icon on any listing to save it here for later.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(savedRooms, key = { it.roomId }) { room ->
                StudentRoomCard(
                    room = room,
                    isFavourite = true,
                    onToggleFavourite = { onToggleFavourite(room.roomId) },
                    onSelect = { onSelectRoom(room.roomId) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun StudentBookingsView(
    bookings: List<BookingRequest>,
    onCancelBooking: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "My Booking Inquiries (${bookings.size})",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Track your room visit requests and rental confirmations.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (bookings.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.EventNote, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No Booking Requests Yet", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Browse rooms and tap 'Send Booking Request' to get in touch with owners.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(bookings, key = { it.bookingId }) { booking ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = booking.roomTitle,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Owner: ${booking.ownerName} (${booking.ownerPhone})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when (booking.status) {
                                    BookingStatus.PENDING -> AmberAccent.copy(alpha = 0.15f)
                                    BookingStatus.ACCEPTED -> StatusAvailable.copy(alpha = 0.15f)
                                    BookingStatus.REJECTED -> StatusOccupied.copy(alpha = 0.15f)
                                    BookingStatus.COMPLETED -> BluePrimary.copy(alpha = 0.15f)
                                    BookingStatus.CANCELLED -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ) {
                                Text(
                                    text = booking.status.displayName,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = when (booking.status) {
                                        BookingStatus.PENDING -> AmberAccent
                                        BookingStatus.ACCEPTED -> StatusAvailable
                                        BookingStatus.REJECTED -> StatusOccupied
                                        BookingStatus.COMPLETED -> BluePrimary
                                        BookingStatus.CANCELLED -> MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        if (booking.message.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Your note: \"${booking.message}\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (booking.status == BookingStatus.PENDING) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = { onCancelBooking(booking.bookingId) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Cancel Booking Request")
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun StudentProfileView(
    currentUser: UserProfile,
    savedCount: Int,
    bookingCount: Int,
    onNavigateToAboutUs: () -> Unit = {},
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        com.example.ui.components.RoomSaathiLogoIcon(
            size = 84.dp,
            elevation = 6.dp,
            showBorder = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        com.example.ui.components.RoomSaathiBrandText(
            fontSize = 22.sp,
            roomColor = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = currentUser.name.ifBlank { "Student User" },
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = currentUser.phone,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                ProfileInfoRow("Account Role", "Verified Student")
                ProfileInfoRow("College / Institute", currentUser.college.ifBlank { "SGSITS Indore" })
                ProfileInfoRow("Preferred City", currentUser.city)
                ProfileInfoRow("Saved Rooms", "$savedCount Accommodations")
                ProfileInfoRow("Booking Inquiries", "$bookingCount Requests")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onNavigateToAboutUs,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("About RoomSaathi & Official Brand", fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log Out of Student Account", fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    filterState: FilterState,
    onDismiss: () -> Unit,
    onApply: (FilterState) -> Unit,
    onReset: () -> Unit
) {
    var localFilters by remember { mutableStateOf(filterState) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter & Sort Rooms",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                TextButton(onClick = onReset) {
                    Text("Reset All", color = MaterialTheme.colorScheme.error)
                }
            }

            // Sort Options
            Text("Sort By", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SortOption.values().forEach { sort ->
                    FilterChip(
                        selected = localFilters.sortOption == sort,
                        onClick = { localFilters = localFilters.copy(sortOption = sort) },
                        label = { Text(sort.displayName) }
                    )
                }
            }

            // Gender Preference
            Text("Gender Preference", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = localFilters.selectedGenderPreference == null,
                    onClick = { localFilters = localFilters.copy(selectedGenderPreference = null) },
                    label = { Text("Any") }
                )
                GenderPreference.values().forEach { pref ->
                    FilterChip(
                        selected = localFilters.selectedGenderPreference == pref,
                        onClick = { localFilters = localFilters.copy(selectedGenderPreference = pref) },
                        label = { Text(pref.displayName) }
                    )
                }
            }

            // Room Type
            Text("Room Type", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = localFilters.selectedRoomType == null,
                    onClick = { localFilters = localFilters.copy(selectedRoomType = null) },
                    label = { Text("All Types") }
                )
                RoomType.values().forEach { type ->
                    FilterChip(
                        selected = localFilters.selectedRoomType == type,
                        onClick = { localFilters = localFilters.copy(selectedRoomType = type) },
                        label = { Text(type.displayName) }
                    )
                }
            }

            // Facilities Toggles
            Text("Must Have Facilities", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = localFilters.mustHaveWifi,
                    onClick = { localFilters = localFilters.copy(mustHaveWifi = !localFilters.mustHaveWifi) },
                    label = { Text("Wi-Fi") }
                )
                FilterChip(
                    selected = localFilters.mustHaveAc,
                    onClick = { localFilters = localFilters.copy(mustHaveAc = !localFilters.mustHaveAc) },
                    label = { Text("AC") }
                )
                FilterChip(
                    selected = localFilters.mustHaveAttachedBathroom,
                    onClick = { localFilters = localFilters.copy(mustHaveAttachedBathroom = !localFilters.mustHaveAttachedBathroom) },
                    label = { Text("Attached Bath") }
                )
                FilterChip(
                    selected = localFilters.mustHaveParking,
                    onClick = { localFilters = localFilters.copy(mustHaveParking = !localFilters.mustHaveParking) },
                    label = { Text("Parking") }
                )
                FilterChip(
                    selected = localFilters.mustBeFurnished,
                    onClick = { localFilters = localFilters.copy(mustBeFurnished = !localFilters.mustBeFurnished) },
                    label = { Text("Furnished (Bed & Almirah)") }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { onApply(localFilters) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Apply Filters", fontWeight = FontWeight.Bold)
            }
        }
    }
}
