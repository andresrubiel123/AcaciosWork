package com.acacioswork.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.acacioswork.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    var usuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }

    val loginState by viewModel.loginState.collectAsState()

    /** Transición infinita para el color verde neón del título de login. @author RADJ */
    val infiniteTransition = rememberInfiniteTransition(label = "neonPulse")
    val animatedColor by infiniteTransition.animateColor(
        initialValue = NeonGreenDim,
        targetValue = NeonGreen,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "neonColor"
    )

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Card de Login
            Card(
                colors = CardDefaults.cardColors(containerColor = BgCard),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "AcaciosWork",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = animatedColor,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Acceso al sistema administrativo",
                        fontSize = 14.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    // Campo de Usuario
                    OutlinedTextField(
                        value = usuario,
                        onValueChange = {
                            usuario = it
                            viewModel.resetState()
                        },
                        label = { Text("Usuario", color = TextMuted) },
                        placeholder = { Text("Ingresa tu usuario", color = TextMuted) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = BgDark,
                            containerColor = BgDark,
                            focusedLabelColor = Primary,
                            unfocusedLabelColor = TextMuted,
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo de Contraseña
                    OutlinedTextField(
                        value = clave,
                        onValueChange = {
                            clave = it
                            viewModel.resetState()
                        },
                        label = { Text("Contraseña", color = TextMuted) },
                        placeholder = { Text("••••••••", color = TextMuted) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = BgDark,
                            containerColor = BgDark,
                            focusedLabelColor = Primary,
                            unfocusedLabelColor = TextMuted,
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Mensaje de Error
                    if (loginState is LoginState.Error) {
                        Text(
                            text = (loginState as LoginState.Error).message,
                            color = AlertRed,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(AlertRed.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Botón de Login
                    Button(
                        onClick = { viewModel.login(context, usuario, clave) },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape = RoundedCornerShape(8.dp),
                        enabled = loginState !is LoginState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (loginState is LoginState.Loading) {
                            CircularProgressIndicator(
                                color = TextLight,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = "Iniciar Sesión",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextLight
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "© 2026 AcaciosWork - Gestión de Inventario Inteligente",
                fontSize = 12.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Función auxiliar para simular mutableStateFlowOf ya que Kotlin usa mutableStateOf en Compose
@Composable
fun <T> mutableStateFlowOf(value: T): MutableState<T> = remember { mutableStateOf(value) }
