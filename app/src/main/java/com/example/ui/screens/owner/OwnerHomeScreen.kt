package com.example.ui.screens.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.models.RoomListing
import com.example.data.models.RoomStatus
import com.example.data.models.UserProfile
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BlueSecondary
import com.example.ui.theme.StatusAvailable
import com.example.ui.theme.StatusOccupied
import com.example.ui.theme.StatusPending
import com.example.ui.viewmodels.AuthViewModel
import com.example.ui.viewmodels.RoomViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerHomeScreen(
    authViewModel: AuthViewModel,
    roomViewModel: RoomViewModel,
    currentUser: UserProfile,
    onNavigateToAddRoom: () -> Unit,
    onNavigateToEditRoom: (String) -> Unit,
    onNavigateToRoomDetail: (String) -> Unit,
    onNavigateToAboutUs: () -> Unit = {},
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Listings & Stats, 1: Booking Requests, 2: Profile
    val allRooms by roomViewModel.allRooms.collectAsState()
    val allBookings by roomViewModel.allBookings.collectAsState()
    val notifications by roomViewModel.notifications.collectAsState()

    // Filter to this owner
    val ownerRooms = allRooms.filter { it.ownerId == currentUser.uid || currentUser.uid == "owner_ramesh" }
    val ownerBookings = allBookings.filter { it.ownerId == currentUser.uid || currentUser.uid == "owner_ramesh" }
    val pendingBookingsCount = ownerBookings.count { it.status == BookingStatus.PENDING }

    var roomToDelete by remember { mutableStateOf<RoomListing?>(null) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("owner_home_screen"),
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
                                    text = currentUser.name.ifBlank { "Room Owner" },
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Owner",
                                    tint = AmberAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "Owner Dashboard • ${currentUser.city}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onLogout,
                            modifier = Modifier.testTag("owner_logout_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Logout",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = onNavigateToAddRoom,
                    containerColor = BluePrimary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("owner_fab_add_room")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Room", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("owner_bottom_nav"),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Hotel, contentDescription = "My Rooms") },
                    label = { Text("My Rooms") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BluePrimary,
                        selectedTextColor = BluePrimary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        if (pendingBookingsCount > 0) {
                            BadgedBox(badge = {
                                Badge(containerColor = AmberAccent) {
                                    Text(pendingBookingsCount.toString(), color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }) {
                                Icon(Icons.Default.EventNote, contentDescription = "Requests")
                            }
                        } else {
                            Icon(Icons.Default.EventNote, contentDescription = "Requests")
                        }
                    },
                    label = { Text("Requests") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BluePrimary,
                        selectedTextColor = BluePrimary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
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
            when (selectedTab) {
                0 -> OwnerListingsView(
                    ownerRooms = ownerRooms,
                    pendingBookings = pendingBookingsCount,
                    onNavigateToAddRoom = onNavigateToAddRoom,
                    onNavigateToEditRoom = onNavigateToEditRoom,
                    onNavigateToRoomDetail = onNavigateToRoomDetail,
                    onToggleAvailability = { room ->
                        roomViewModel.toggleAvailability(room.roomId, room.availability)
                    },
                    onRequestDelete = { room -> roomToDelete = room }
                )
                1 -> OwnerBookingsView(
                    bookings = ownerBookings,
                    onAccept = { bookingId -> roomViewModel.updateBookingStatus(bookingId, BookingStatus.ACCEPTED) },
                    onReject = { bookingId -> roomViewModel.updateBookingStatus(bookingId, BookingStatus.REJECTED) },
                    onComplete = { bookingId -> roomViewModel.updateBookingStatus(bookingId, BookingStatus.COMPLETED) }
                )
                2 -> OwnerProfileView(
                    currentUser = currentUser,
                    totalRooms = ownerRooms.size,
                    onNavigateToAboutUs = onNavigateToAboutUs,
                    onLogout = onLogout
                )
            }
        }
    }

    // Delete confirmation dialog
    if (roomToDelete != null) {
        val room = roomToDelete!!
        AlertDialog(
            onDismissRequest = { roomToDelete = null },
            title = { Text("Delete Room Listing?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to permanently delete '${room.title}'? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        roomViewModel.deleteRoom(room.roomId)
                        roomToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { roomToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun OwnerListingsView(
    ownerRooms: List<RoomListing>,
    pendingBookings: Int,
    onNavigateToAddRoom: () -> Unit,
    onNavigateToEditRoom: (String) -> Unit,
    onNavigateToRoomDetail: (String) -> Unit,
    onToggleAvailability: (RoomListing) -> Unit,
    onRequestDelete: (RoomListing) -> Unit
) {
    val totalRooms = ownerRooms.size
    val availableCount = ownerRooms.count { it.availability == RoomStatus.AVAILABLE }
    val occupiedCount = ownerRooms.count { it.availability == RoomStatus.OCCUPIED }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            // Dashboard Metric Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Properties",
                    value = totalRooms.toString(),
                    color = BluePrimary,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Available",
                    value = availableCount.toString(),
                    color = StatusAvailable,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Occupied",
                    value = occupiedCount.toString(),
                    color = StatusOccupied,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Requests",
                    value = pendingBookings.toString(),
                    color = AmberAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            // Add Room Hero Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .clickable(onClick = onNavigateToAddRoom),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = BluePrimary,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Add New Room Listing",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Upload photos, set rent, facilities & address",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "My Room Listings (${ownerRooms.size})",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        if (ownerRooms.isEmpty()) {
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
                        Icon(
                            imageVector = Icons.Default.MeetingRoom,
                            contentDescription = null,
                            tint = BluePrimary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No listings yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap 'Add Room' to publish your first property for student tenants.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(ownerRooms, key = { it.roomId }) { room ->
                OwnerRoomCard(
                    room = room,
                    onNavigateToEdit = { onNavigateToEditRoom(room.roomId) },
                    onNavigateToDetail = { onNavigateToRoomDetail(room.roomId) },
                    onToggleAvailability = { onToggleAvailability(room) },
                    onDelete = { onRequestDelete(room) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
fun OwnerRoomCard(
    room: RoomListing,
    onNavigateToEdit: () -> Unit,
    onNavigateToDetail: () -> Unit,
    onToggleAvailability: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onNavigateToDetail),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Photo & Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                if (room.photos.isNotEmpty()) {
                    AsyncImage(
                        model = room.photos.first(),
                        contentDescription = room.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Apartment, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(40.dp))
                    }
                }

                // Availability Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (room.availability == RoomStatus.AVAILABLE) StatusAvailable else StatusOccupied,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = room.availability.displayName,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Rent Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "₹${room.rent.toInt()}/mo",
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Room info
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = room.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${room.locality}, ${room.city}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onToggleAvailability,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (room.availability == RoomStatus.AVAILABLE) "Mark Occupied" else "Mark Available",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Row {
                        IconButton(onClick = onNavigateToEdit) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = BluePrimary)
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OwnerBookingsView(
    bookings: List<BookingRequest>,
    onAccept: (String) -> Unit,
    onReject: (String) -> Unit,
    onComplete: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Student Booking Inquiries (${bookings.size})",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Review incoming student booking requests. Accept or reject them here.",
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
                        Text("No Booking Requests", fontWeight = FontWeight.Bold)
                        Text(
                            text = "When students send a booking request for your rooms, it will appear here.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(bookings, key = { it.bookingId }) { booking ->
                BookingRequestCard(
                    booking = booking,
                    onAccept = { onAccept(booking.bookingId) },
                    onReject = { onReject(booking.bookingId) },
                    onComplete = { onComplete(booking.bookingId) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun BookingRequestCard(
    booking: BookingRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onComplete: () -> Unit
) {
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
                        text = booking.studentName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${booking.studentCollege.ifBlank { "College Student" }} • ${booking.studentPhone}",
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

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Room: ${booking.roomTitle}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    if (booking.message.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"${booking.message}\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (booking.status == BookingStatus.PENDING) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = StatusAvailable),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Accept", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reject", fontWeight = FontWeight.Bold)
                    }
                }
            } else if (booking.status == BookingStatus.ACCEPTED) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Mark Tenant Stay Completed")
                }
            }
        }
    }
}

@Composable
fun OwnerProfileView(
    currentUser: UserProfile,
    totalRooms: Int,
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
            text = currentUser.name.ifBlank { "Room Owner" },
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
                ProfileInfoRow("Account Role", "Verified Room Owner")
                ProfileInfoRow("City", currentUser.city)
                ProfileInfoRow("Active Properties", "$totalRooms Listed")
                ProfileInfoRow("Verification Status", "Verified by Phone OTP")
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
            Text("Log Out of Owner Account", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
    }
}
