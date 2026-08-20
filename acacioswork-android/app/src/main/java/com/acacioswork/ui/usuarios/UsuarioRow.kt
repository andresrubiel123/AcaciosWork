package com.acacioswork.ui.usuarios

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.model.Usuario
import com.acacioswork.ui.theme.*

/**
 * Fila individual de usuario en la tabla del sistema.
 * Muestra badge de rol (Administrador / Auxiliar) y badge de estado (Activo / Inactivo).
 * Paridad 100% con la versión web (badge-warn, badge-success, badge-danger).
 * @author RADJ / Antigravity
 */
@Composable
fun UsuarioRow(
    usuario: Usuario,
    isLast: Boolean,
    onDelete: (Usuario) -> Unit
) {
    val context = LocalContext.current
    val isActivo = usuario.activo == 1
    val isAdmin = usuario.idRol == 1L

    Card(
        colors = CardDefaults.cardColors(containerColor = BgCard),
        shape = if (isLast) RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
        else RoundedCornerShape(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nombre completo (paridad web: font-weight:500)
                Column(modifier = Modifier.weight(1.5f)) {
                    Text(
                        text = "${usuario.nombre} ${usuario.apellido}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextLight,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = usuario.email,
                        fontSize = 10.sp,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Login de usuario (paridad web: u.usuario)
                Text(
                    text = usuario.usuario,
                    fontSize = 12.sp,
                    color = TextLight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1.2f)
                )

                // Badge Rol (paridad web: badge-warn = Administrador, badge-success = Auxiliar)
                Box(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isAdmin) AccentOrange.copy(alpha = 0.15f)
                                else AccentGreen.copy(alpha = 0.15f)
                            )
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isAdmin) "Admin" else "Auxiliar",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAdmin) AccentOrange else AccentGreen
                        )
                    }
                }

                // Badge Estado (paridad web: badge-success / badge-danger)
                Box(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isActivo) AccentGreen.copy(alpha = 0.15f)
                                else AlertRed.copy(alpha = 0.15f)
                            )
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isActivo) "Activo" else "Inactivo",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isActivo) AccentGreen else AlertRed
                        )
                    }
                }

                // Acciones: Editar y Borrar (paridad web: btn-sm + btn-del)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.width(52.dp)
                ) {
                    IconButton(
                        onClick = {
                            Toast.makeText(
                                context,
                                "Edición disponible en el panel web.",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = { onDelete(usuario) },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = AlertRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Separador entre filas
            if (!isLast) {
                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
            }
        }
    }
}
