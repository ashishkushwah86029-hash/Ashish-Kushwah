package com.example.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.models.UserProfile
import com.example.data.models.UserRole
import com.example.data.repository.RoomSaathiRepository
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.auth.PhoneAuthScreen
import com.example.ui.screens.owner.AddEditRoomScreen
import com.example.ui.screens.owner.OwnerHomeScreen
import com.example.ui.screens.roleselection.RoleSelectionScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.screens.student.RoomDetailScreen
import com.example.ui.screens.student.StudentHomeScreen
import com.example.ui.viewmodels.AuthViewModel
import com.example.ui.viewmodels.RoomViewModel

@Composable
fun RoomSaathiNavHost(
    authViewModel: AuthViewModel,
    roomViewModel: RoomViewModel,
    navController: NavHostController = rememberNavController()
) {
    val currentUser by RoomSaathiRepository.currentUser.collectAsState()
    val selectedRole by authViewModel.selectedRole.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // 1. Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToRoleSelection = {
                    navController.navigate(Screen.RoleSelection.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // 2. Role Selection Screen
        composable(Screen.RoleSelection.route) {
            RoleSelectionScreen(
                authViewModel = authViewModel,
                selectedRole = selectedRole,
                onRoleSelected = { role -> authViewModel.selectRole(role) },
                onProceed = { role ->
                    authViewModel.selectRole(role)
                    navController.navigate(Screen.Auth.createRoute(role.name.lowercase()))
                },
                onNavigateToAdmin = {
                    navController.navigate(Screen.AdminDashboard.route)
                }
            )
        }

        // 3. Phone & OTP Auth Screen
        composable(
            route = Screen.Auth.route,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val roleArg = backStackEntry.arguments?.getString("role")
            val targetRole = UserRole.fromString(roleArg)

            PhoneAuthScreen(
                authViewModel = authViewModel,
                role = targetRole,
                onBack = { navController.popBackStack() },
                onAuthenticated = { authenticatedUser ->
                    // STRICT ROLE SECURITY GUARD: Owner and Student have distinct isolated dashboards
                    if (authenticatedUser.role == UserRole.OWNER) {
                        navController.navigate(Screen.OwnerMain.route) {
                            popUpTo(Screen.RoleSelection.route) { inclusive = true }
                        }
                    } else if (authenticatedUser.role == UserRole.STUDENT) {
                        navController.navigate(Screen.StudentMain.route) {
                            popUpTo(Screen.RoleSelection.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.AdminDashboard.route) {
                            popUpTo(Screen.RoleSelection.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        // 4. Owner Dashboard Screen (Protected)
        composable(Screen.OwnerMain.route) {
            val user = currentUser ?: UserProfile(
                uid = "owner_ramesh",
                name = "Ramesh Sharma",
                phone = "+91 98260 12345",
                role = UserRole.OWNER,
                city = "Indore"
            )

            OwnerHomeScreen(
                authViewModel = authViewModel,
                roomViewModel = roomViewModel,
                currentUser = user,
                onNavigateToAddRoom = {
                    navController.navigate(Screen.AddEditRoom.createRoute(null))
                },
                onNavigateToEditRoom = { roomId ->
                    navController.navigate(Screen.AddEditRoom.createRoute(roomId))
                },
                onNavigateToRoomDetail = { roomId ->
                    navController.navigate(Screen.RoomDetails.createRoute(roomId))
                },
                onNavigateToAboutUs = {
                    navController.navigate(Screen.AboutUs.route)
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.RoleSelection.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // 5. Student Dashboard Screen (Protected)
        composable(Screen.StudentMain.route) {
            val user = currentUser ?: UserProfile(
                uid = "student_ashish",
                name = "Ashish Kushwah",
                phone = "+91 86029 12345",
                role = UserRole.STUDENT,
                city = "Indore",
                college = "SGSITS Indore"
            )

            StudentHomeScreen(
                authViewModel = authViewModel,
                roomViewModel = roomViewModel,
                currentUser = user,
                onNavigateToRoomDetail = { roomId ->
                    navController.navigate(Screen.RoomDetails.createRoute(roomId))
                },
                onNavigateToAboutUs = {
                    navController.navigate(Screen.AboutUs.route)
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.RoleSelection.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // 6. Room Details Screen
        composable(
            route = Screen.RoomDetails.route,
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId").orEmpty()
            val user = currentUser ?: UserProfile(
                uid = "student_ashish",
                name = "Ashish Kushwah",
                phone = "+91 86029 12345",
                role = UserRole.STUDENT,
                city = "Indore"
            )

            RoomDetailScreen(
                roomViewModel = roomViewModel,
                roomId = roomId,
                currentUser = user,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 7. Add / Edit Room Listing (Owner only)
        composable(
            route = Screen.AddEditRoom.route,
            arguments = listOf(
                navArgument("roomId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId")
            val user = currentUser ?: UserProfile(
                uid = "owner_ramesh",
                name = "Ramesh Sharma",
                phone = "+91 98260 12345",
                role = UserRole.OWNER,
                city = "Indore"
            )

            AddEditRoomScreen(
                roomViewModel = roomViewModel,
                currentUser = user,
                roomIdToEdit = roomId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 8. Admin Safety Console
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                roomViewModel = roomViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 9. About RoomSaathi Brand & Story Screen
        composable(Screen.AboutUs.route) {
            com.example.ui.screens.about.AboutUsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
