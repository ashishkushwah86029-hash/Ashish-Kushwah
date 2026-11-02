package com.example.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.UserProfile
import com.example.data.models.UserRole
import com.example.data.repository.RoomSaathiRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface AuthUiState {
    object Idle : AuthUiState
    object SendingOtp : AuthUiState
    object OtpSent : AuthUiState
    object VerifyingOtp : AuthUiState
    data class Authenticated(val user: UserProfile) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _selectedRole = MutableStateFlow(UserRole.STUDENT)
    val selectedRole: StateFlow<UserRole> = _selectedRole.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _otpCode = MutableStateFlow("")
    val otpCode: StateFlow<String> = _otpCode.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _resendCountdown = MutableStateFlow(30)
    val resendCountdown: StateFlow<Int> = _resendCountdown.asStateFlow()

    fun selectRole(role: UserRole) {
        _selectedRole.value = role
    }

    fun onPhoneChange(phone: String) {
        // Only keep digits and limit to 10 digits
        val digits = phone.filter { it.isDigit() }.take(10)
        _phoneNumber.value = digits
    }

    fun onOtpChange(otp: String) {
        val digits = otp.filter { it.isDigit() }.take(6)
        _otpCode.value = digits
    }

    fun onNameChange(name: String) {
        _userName.value = name
    }

    fun sendOtp() {
        val number = _phoneNumber.value
        if (number.length != 10) {
            _uiState.value = AuthUiState.Error("Please enter a valid 10-digit Indian mobile number")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.SendingOtp
            delay(1200) // Realistic network roundtrip
            _uiState.value = AuthUiState.OtpSent
            startResendTimer()
        }
    }

    private fun startResendTimer() {
        viewModelScope.launch {
            _resendCountdown.value = 30
            while (_resendCountdown.value > 0) {
                delay(1000)
                _resendCountdown.value -= 1
            }
        }
    }

    fun verifyOtp(onSuccess: (UserProfile) -> Unit) {
        val otp = _otpCode.value
        if (otp.length != 6) {
            _uiState.value = AuthUiState.Error("Please enter the complete 6-digit OTP")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.VerifyingOtp
            delay(1200)

            // For phone OTP validation:
            // Standard test verification or real code:
            val user = UserProfile(
                uid = "usr_${UUID.randomUUID().toString().take(8)}",
                name = _userName.value.ifBlank {
                    if (_selectedRole.value == UserRole.OWNER) "Room Owner" else "Student User"
                },
                phone = "+91 " + _phoneNumber.value,
                role = _selectedRole.value,
                city = "Indore",
                college = if (_selectedRole.value == UserRole.STUDENT) "SGSITS Indore" else "",
                isVerified = true,
                createdAt = System.currentTimeMillis()
            )

            RoomSaathiRepository.setCurrentUser(user)
            _uiState.value = AuthUiState.Authenticated(user)
            onSuccess(user)
        }
    }

    fun quickRoleDemoLogin(role: UserRole, onSuccess: (UserProfile) -> Unit) {
        val demoUser = when (role) {
            UserRole.OWNER -> UserProfile(
                uid = "owner_ramesh",
                name = "Ramesh Sharma",
                phone = "+91 98260 12345",
                role = UserRole.OWNER,
                city = "Indore",
                isVerified = true
            )
            UserRole.STUDENT -> UserProfile(
                uid = "student_ashish",
                name = "Ashish Kushwah",
                phone = "+91 86029 12345",
                role = UserRole.STUDENT,
                city = "Indore",
                college = "SGSITS Indore",
                isVerified = true
            )
            UserRole.ADMIN -> UserProfile(
                uid = "admin_super",
                name = "RoomSaathi Admin",
                phone = "+91 99999 00000",
                role = UserRole.ADMIN,
                city = "Indore",
                isVerified = true
            )
        }
        RoomSaathiRepository.setCurrentUser(demoUser)
        _selectedRole.value = role
        _uiState.value = AuthUiState.Authenticated(demoUser)
        onSuccess(demoUser)
    }

    fun logout() {
        RoomSaathiRepository.setCurrentUser(null)
        _uiState.value = AuthUiState.Idle
        _phoneNumber.value = ""
        _otpCode.value = ""
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }
}
