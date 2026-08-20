package com.acacioswork.ui.configuracion

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import com.acacioswork.ui.theme.*

/**
 * Colores personalizados para los campos de texto en configuración.
 * @author RADJ / Antigravity
 */
@Composable
fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Primary,
    unfocusedBorderColor = BgDark,
    focusedContainerColor = BgDark,
    unfocusedContainerColor = BgDark,
    focusedTextColor = TextLight,
    unfocusedTextColor = TextLight
)
