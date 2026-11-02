package com.example.ui.screens.owner

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.GenderPreference
import com.example.data.models.RoomListing
import com.example.data.models.RoomStatus
import com.example.data.models.RoomType
import com.example.data.models.UserProfile
import com.example.data.models.defaultElectricalMap
import com.example.data.models.defaultFurnitureMap
import com.example.data.models.defaultOtherFacilitiesMap
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BlueSecondary
import com.example.ui.theme.StatusAvailable
import com.example.ui.viewmodels.RoomViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditRoomScreen(
    roomViewModel: RoomViewModel,
    currentUser: UserProfile,
    roomIdToEdit: String?,
    onNavigateBack: () -> Unit
) {
    val existingRoom = remember(roomIdToEdit) {
        if (roomIdToEdit != null) {
            roomViewModel.allRooms.value.find { it.roomId == roomIdToEdit }
        } else null
    }

    var title by remember { mutableStateOf(existingRoom?.title ?: "") }
    var address by remember { mutableStateOf(existingRoom?.address ?: "") }
    var city by remember { mutableStateOf(existingRoom?.city ?: "Indore") }
    var locality by remember { mutableStateOf(existingRoom?.locality ?: "") }
    var landmark by remember { mutableStateOf(existingRoom?.nearbyLandmark ?: "") }
    var rentText by remember { mutableStateOf(if (existingRoom != null) existingRoom.rent.toInt().toString() else "") }
    var depositText by remember { mutableStateOf(if (existingRoom != null) existingRoom.deposit.toInt().toString() else "") }
    var description by remember { mutableStateOf(existingRoom?.description ?: "") }
    var contactPhone by remember { mutableStateOf(existingRoom?.ownerPhone.takeIf { !it.isNullOrBlank() } ?: currentUser.phone) }
    var allowedOccupants by remember { mutableIntStateOf(existingRoom?.allowedOccupants ?: 1) }

    var selectedRoomType by remember { mutableStateOf(existingRoom?.roomType ?: RoomType.SINGLE) }
    var selectedGender by remember { mutableStateOf(existingRoom?.genderPreference ?: GenderPreference.ALL) }
    var selectedStatus by remember { mutableStateOf(existingRoom?.availability ?: RoomStatus.AVAILABLE) }

    // Photos list
    var photosList by remember {
        mutableStateOf(
            existingRoom?.photos ?: listOf(
                "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1598928506311-c55ded91a20c?w=800&auto=format&fit=crop&q=80"
            )
        )
    }

    // Facilities Maps
    val furnitureMap = remember {
        mutableStateMapOf<String, Boolean>().apply {
            putAll(existingRoom?.furniture ?: defaultFurnitureMap())
        }
    }
    val electricalMap = remember {
        mutableStateMapOf<String, Boolean>().apply {
            putAll(existingRoom?.electricalFacilities ?: defaultElectricalMap())
        }
    }
    val otherMap = remember {
        mutableStateMapOf<String, Boolean>().apply {
            putAll(existingRoom?.otherFacilities ?: defaultOtherFacilitiesMap())
        }
    }
    var additionalNotes by remember { mutableStateOf(existingRoom?.additionalFacilityNotes ?: "") }

    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Native Photo Picker launcher (Supports up to 15 photos simultaneously)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 15)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val newUris = uris.map { it.toString() }
            photosList = (photosList + newUris).take(15)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (existingRoom != null) "Edit Room Listing" else "Add New Room Listing",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BluePrimary)
            )
        }
    ) { padding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Photo Upload Section
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
                        Text(
                            text = "Room Photographs (${photosList.size}/15)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Select Photos", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Select up to 15 high-quality photographs from your phone gallery. Tap star to set primary cover photo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (photosList.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Tap to browse gallery photos", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsIndexed(photosList) { index, photoUrl ->
                                val isPrimary = index == 0
                                Box(
                                    modifier = Modifier
                                        .size(110.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .border(
                                            width = if (isPrimary) 2.dp else 1.dp,
                                            color = if (isPrimary) AmberAccent else MaterialTheme.colorScheme.outlineVariant,
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                ) {
                                    AsyncImage(
                                        model = photoUrl,
                                        contentDescription = "Room Photo $index",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )

                                    // Remove Photo Button
                                    Surface(
                                        shape = CircleShape,
                                        color = Color.Black.copy(alpha = 0.6f),
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .size(24.dp)
                                            .clickable {
                                                photosList = photosList.filterIndexed { i, _ -> i != index }
                                            }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(14.dp))
                                        }
                                    }

                                    // Set Primary Star Button
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isPrimary) AmberAccent else Color.Black.copy(alpha = 0.6f),
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(4.dp)
                                            .size(24.dp)
                                            .clickable {
                                                // Move this photo to index 0
                                                val reordered = photosList.toMutableList()
                                                val item = reordered.removeAt(index)
                                                reordered.add(0, item)
                                                photosList = reordered
                                            }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (isPrimary) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                                contentDescription = "Cover",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // General Information Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "General Details",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Listing Title *") },
                        placeholder = { Text("e.g. Spacious Single AC Room near Coaching") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = rentText,
                            onValueChange = { rentText = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Monthly Rent (₹) *") },
                            placeholder = { Text("6500") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = depositText,
                            onValueChange = { depositText = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Deposit (₹) *") },
                            placeholder = { Text("6500") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Room Type Selector Chips
                    Text("Room Type", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        RoomType.values().forEach { type ->
                            FilterChip(
                                selected = selectedRoomType == type,
                                onClick = { selectedRoomType = type },
                                label = { Text(type.displayName) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Gender Preference
                    Text("Suitable For (Gender Preference)", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        GenderPreference.values().forEach { pref ->
                            FilterChip(
                                selected = selectedGender == pref,
                                onClick = { selectedGender = pref },
                                label = { Text(pref.displayName) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Detailed Description *") },
                        placeholder = { Text("Mention rules, ventilation, food facility, gate timing, study atmosphere...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3
                    )
                }
            }

            // Location & Address Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = BluePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Address & Location",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Property Full Address *") },
                        placeholder = { Text("Plot 42, Anand Nagar Main Road") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = locality,
                            onValueChange = { locality = it },
                            label = { Text("Locality / Area *") },
                            placeholder = { Text("Bhawarkua") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("City *") },
                            placeholder = { Text("Indore") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = landmark,
                        onValueChange = { landmark = it },
                        label = { Text("Nearby Landmark / College *") },
                        placeholder = { Text("Near Holkar Science College & Bus Stand") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = contactPhone,
                        onValueChange = { contactPhone = it },
                        label = { Text("Contact Phone Number *") },
                        placeholder = { Text("+91 98260 12345") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Furniture & Facilities Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Furniture & Facilities",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Check all the amenities provided inside this room listing:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Furniture items
                    Text("Room Furniture", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = BluePrimary))
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        furnitureMap.keys.sorted().forEach { item ->
                            val isChecked = furnitureMap[item] == true
                            FilterChip(
                                selected = isChecked,
                                onClick = { furnitureMap[item] = !isChecked },
                                label = { Text(item) },
                                leadingIcon = {
                                    if (isChecked) Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Electrical Facilities
                    Text("Electrical Facilities", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = BluePrimary))
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        electricalMap.keys.sorted().forEach { item ->
                            val isChecked = electricalMap[item] == true
                            FilterChip(
                                selected = isChecked,
                                onClick = { electricalMap[item] = !isChecked },
                                label = { Text(item) },
                                leadingIcon = {
                                    if (isChecked) Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Other Facilities
                    Text("Other Facilities & Utilities", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = BluePrimary))
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        otherMap.keys.sorted().forEach { item ->
                            val isChecked = otherMap[item] == true
                            FilterChip(
                                selected = isChecked,
                                onClick = { otherMap[item] = !isChecked },
                                label = { Text(item) },
                                leadingIcon = {
                                    if (isChecked) Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = additionalNotes,
                        onValueChange = { additionalNotes = it },
                        label = { Text("Additional Facility Notes") },
                        placeholder = { Text("e.g. In-house food tiffin facility available, RO water dispenser on each floor") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Error display
            if (errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Save / Publish Button
            Button(
                onClick = {
                    if (title.isBlank() || rentText.isBlank() || address.isBlank() || locality.isBlank()) {
                        errorMessage = "Please fill in title, monthly rent, address, and locality."
                        return@Button
                    }
                    val rentVal = rentText.toDoubleOrNull() ?: 0.0
                    val depositVal = depositText.toDoubleOrNull() ?: rentVal

                    val newListing = RoomListing(
                        roomId = existingRoom?.roomId ?: "room_${UUID.randomUUID().toString().take(8)}",
                        ownerId = currentUser.uid,
                        ownerName = currentUser.name.ifBlank { "Room Owner" },
                        ownerPhone = contactPhone,
                        title = title.trim(),
                        description = description.trim(),
                        address = address.trim(),
                        city = city.trim(),
                        locality = locality.trim(),
                        nearbyLandmark = landmark.trim(),
                        rent = rentVal,
                        deposit = depositVal,
                        roomType = selectedRoomType,
                        allowedOccupants = allowedOccupants,
                        availability = selectedStatus,
                        genderPreference = selectedGender,
                        photos = photosList,
                        furniture = furnitureMap.toMap(),
                        electricalFacilities = electricalMap.toMap(),
                        otherFacilities = otherMap.toMap(),
                        additionalFacilityNotes = additionalNotes.trim(),
                        createdAt = existingRoom?.createdAt ?: System.currentTimeMillis()
                    )

                    if (existingRoom != null) {
                        roomViewModel.updateRoom(newListing)
                    } else {
                        roomViewModel.addRoom(newListing)
                    }
                    onNavigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("save_room_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                Text(
                    text = if (existingRoom != null) "Update Room Listing" else "Publish Room Listing",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
