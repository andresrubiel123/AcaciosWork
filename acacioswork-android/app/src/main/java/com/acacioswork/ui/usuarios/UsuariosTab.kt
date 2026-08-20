package com.acacioswork.ui.usuarios

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.acacioswork.model.Usuario
import com.acacioswork.network.RetrofitClient
import com.acacioswork.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Pantalla de Usuarios del Sistema con paridad al 100% con la versión Web.
 * Lista de cuentas con badge de rol, badge de estado, buscador y acciones.
 * @author RADJ / Antigravity
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosTab() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var usuarios by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf<Usuario?>(null) }
    var showNewUserDialog by remember { mutableStateOf(false) }

    fun loadUsuarios() {
        coroutineScope.launch {
            isLoading = true
            try {
                val res = RetrofitClient.apiService.getUsuarios()
                if (res.success) usuarios = res.data ?: emptyList()
            } catch (e: Exception) {
                Toast.makeText(context, "Error al cargar usuarios: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { loadUsuarios() }

    // Filtrado en tiempo real (paridad web: filterTable)
    val filteredUsuarios = remember(usuarios, searchQuery) {
        if (searchQuery.isBlank()) usuarios
        else usuarios.filter { u ->
            u.nombre.contains(searchQuery, ignoreCase = true) ||
                    u.apellido.contains(searchQuery, ignoreCase = true) ||
                    u.usuario.contains(searchQuery, ignoreCase = true) ||
                    u.email.contains(searchQuery, ignoreCase = true) ||
                    u.numeroDocumento.contains(searchQuery, ignoreCase = true)
        }
    }

    // Dialog de confirmación de eliminación
    showDeleteDialog?.let { u ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            containerColor = BgCard,
            title = { Text("¿Eliminar usuario?", color = TextLight, fontWeight = FontWeight.Bold) },
            text = { Text("Se eliminará a ${u.nombre} ${u.apellido} del sistema. Esta acción no se puede deshacer.", color = TextMuted, fontSize = 13.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = null
                        coroutineScope.launch {
                            try {
                                RetrofitClient.apiService.deleteUsuario(u.numeroDocumento)
                                Toast.makeText(context, "Usuario eliminado con éxito.", Toast.LENGTH_SHORT).show()
                                loadUsuarios()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error al eliminar: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed)
                ) { Text("Eliminar", color = TextLight) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar", color = TextMuted)
                }
            }
        )
    }

    // Dialog de nuevo usuario (redirección informativa — paridad web: modal)
    if (showNewUserDialog) {
        AlertDialog(
            onDismissRequest = { showNewUserDialog = false },
            containerColor = BgCard,
            title = { Text("Nuevo Usuario", color = TextLight, fontWeight = FontWeight.Bold) },
            text = { Text("La creación de usuarios está disponible desde el panel web o el escritorio de administración.", color = TextMuted, fontSize = 13.sp) },
            confirmButton = {
                Button(onClick = { showNewUserDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                    Text("Entendido", color = TextLight)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Cabecera con botón "+ Nuevo Usuario" (paridad web section-header)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ManageAccounts, contentDescription = null, tint = Primary, modifier = Modifier.size(22.dp))
                        }
                        Column {
                            Text("Usuarios del Sistema", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextLight)
                            Text("Administración de accesos y roles", fontSize = 12.sp, color = TextMuted)
                        }
                    }
                    Button(
                        onClick = { showNewUserDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = TextLight, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Nuevo", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextLight)
                    }
                }
            }

            // Buscador (paridad web: usr-search-input)
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("🔍 Buscar en la tabla...", color = TextMuted, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = BgCard,
                        focusedContainerColor = BgCard,
                        unfocusedContainerColor = BgCard,
                        focusedTextColor = TextLight,
                        unfocusedTextColor = TextLight,
                        cursorColor = Primary
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Contenido principal
            when {
                isLoading -> item {
                    Box(modifier = Modifier.fillMaxWidth().height(250.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            CircularProgressIndicator(color = Primary)
                            Text("Cargando usuarios...", color = TextMuted, fontSize = 13.sp)
                        }
                    }
                }

                filteredUsuarios.isEmpty() -> item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("👤", fontSize = 40.sp)
                            Text(
                                if (searchQuery.isBlank()) "No se encontraron usuarios." else "Sin resultados para \"$searchQuery\"",
                                color = TextMuted, fontSize = 13.sp
                            )
                        }
                    }
                }

                else -> {
                    // Header de tabla (paridad web thead)
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = BgCard),
                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("NOMBRE", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                                Text("USUARIO", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
                                Text("ROL", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                Text("ESTADO", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                Spacer(modifier = Modifier.width(52.dp))
                            }
                        }
                    }

                    // Filas de usuarios
                    itemsIndexed(filteredUsuarios) { index, usuario ->
                        UsuarioRow(
                            usuario = usuario,
                            isLast = index == filteredUsuarios.lastIndex,
                            onDelete = { showDeleteDialog = it }
                        )
                    }
                }
            }
        }
    }
}
