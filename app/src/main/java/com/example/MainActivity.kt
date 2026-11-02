package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.navigation.RoomSaathiNavHost
import com.example.ui.theme.RoomSaathiTheme
import com.example.ui.viewmodels.AuthViewModel
import com.example.ui.viewmodels.RoomViewModel

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val roomViewModel: RoomViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoomSaathiTheme {
                RoomSaathiNavHost(
                    authViewModel = authViewModel,
                    roomViewModel = roomViewModel
                )
            }
        }
    }
}
