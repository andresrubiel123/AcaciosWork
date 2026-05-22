package com.acacioswork

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.acacioswork.network.SessionManager
import com.acacioswork.ui.dashboard.DashboardScreen
import com.acacioswork.ui.login.LoginScreen
import com.acacioswork.ui.theme.AcaciosWorkTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar SessionManager de forma bloqueante antes de renderizar para saber si hay sesión
        val sessionManager = SessionManager.getInstance(applicationContext)

        setContent {
            AcaciosWorkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    var isLoggedIn by remember { mutableStateOf(SessionManager.token != null) }

                    if (isLoggedIn) {
                        DashboardScreen(
                            onLogout = { isLoggedIn = false }
                        )
                    } else {
                        LoginScreen(
                            onLoginSuccess = { isLoggedIn = true }
                        )
                    }
                }
            }
        }
    }
}
