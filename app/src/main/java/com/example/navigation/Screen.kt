package com.example.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object RoleSelection : Screen("role_selection")
    object Auth : Screen("auth/{role}") {
        fun createRoute(role: String) = "auth/$role"
    }
    object OwnerMain : Screen("owner_main")
    object StudentMain : Screen("student_main")
    object RoomDetails : Screen("room_details/{roomId}") {
        fun createRoute(roomId: String) = "room_details/$roomId"
    }
    object AddEditRoom : Screen("add_edit_room?roomId={roomId}") {
        fun createRoute(roomId: String? = null) = if (roomId != null) "add_edit_room?roomId=$roomId" else "add_edit_room"
    }
    object AdminDashboard : Screen("admin_dashboard")
    object AboutUs : Screen("about_us")
}
