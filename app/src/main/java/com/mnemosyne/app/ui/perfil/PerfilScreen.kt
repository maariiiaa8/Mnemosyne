package com.mnemosyne.app.ui.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mnemosyne.app.data.model.Usuario
import com.mnemosyne.app.ui.theme.*
import com.mnemosyne.app.utils.FirebaseResult

@Composable
fun PerfilScreen(
    onEditarPerfil: () -> Unit,
    onMisEntradas: () -> Unit,
    onMisCompras: () -> Unit,
    onMisFavoritos: () -> Unit,
    onCambiarPassword: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: PerfilViewModel = viewModel()
) {
    val perfilState by viewModel.perfil.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Superficie)
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Burdeos)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "MI PERFIL",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                color = Crema
            )
        }

        when (val state = perfilState) {
            is FirebaseResult.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Burdeos)
                }
            }
            is FirebaseResult.Success -> {
                PerfilContenido(
                    usuario = state.data,
                    onEditarPerfil = onEditarPerfil,
                    onMisEntradas = onMisEntradas,
                    onMisCompras = onMisCompras,
                    onMisFavoritos = onMisFavoritos,
                    onCambiarPassword = onCambiarPassword,
                    onCerrarSesion = onCerrarSesion
                )
            }
            is FirebaseResult.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.mensaje, color = MaterialTheme.colorScheme.error)
                }
            }
            null -> {}
        }
    }
}

@Composable
private fun PerfilContenido(
    usuario: Usuario,
    onEditarPerfil: () -> Unit,
    onMisEntradas: () -> Unit,
    onMisCompras: () -> Unit,
    onMisFavoritos: () -> Unit,
    onCambiarPassword: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Tarjeta de usuario ────────────────────────────
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(2.dp),
                colors = CardDefaults.cardColors(containerColor = Crema),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ── Avatar: foto o iniciales ──────────
                    if (usuario.fotoPerfil.isNotBlank()) {
                        AsyncImage(
                            model = usuario.fotoPerfil,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Burdeos),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = usuario.nombre
                                    .split(" ")
                                    .take(2)
                                    .joinToString("") { it.first().uppercase() },
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Crema,
                                fontFamily = CinzelFamily
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = usuario.nombre,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = CinzelFamily,
                        color = BurdeosOscuro,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = usuario.email,
                        fontSize = 13.sp,
                        color = TextoSuave,
                        textAlign = TextAlign.Center
                    )

                    if (usuario.fechaRegistro.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Miembro desde ${usuario.fechaRegistro}",
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            color = Dorado,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // ── Sección: Mi actividad ─────────────────────────
        item { SeccionTitulo("MI ACTIVIDAD") }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FilaAcceso(
                    icono = Icons.Default.ConfirmationNumber,
                    titulo = "Mis entradas",
                    subtitulo = "Exposiciones y visitas compradas",
                    onClick = onMisEntradas
                )
                FilaAcceso(
                    icono = Icons.Default.ShoppingBag,
                    titulo = "Mis compras",
                    subtitulo = "Artículos de la tienda",
                    onClick = onMisCompras
                )
                FilaAcceso(
                    icono = Icons.Default.Favorite,
                    titulo = "Mis favoritos",
                    subtitulo = "Exposiciones guardadas",
                    onClick = onMisFavoritos
                )
            }
        }

        // ── Sección: Cuenta ───────────────────────────────
        item { SeccionTitulo("CUENTA") }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FilaAcceso(
                    icono = Icons.Default.Edit,
                    titulo = "Editar perfil",
                    subtitulo = "Cambia tu nombre y foto",
                    onClick = onEditarPerfil
                )
                FilaAcceso(
                    icono = Icons.Default.Logout,
                    titulo = "Cerrar sesión",
                    subtitulo = "",
                    onClick = onCerrarSesion,
                    color = Burdeos
                )
            }
        }
    }
}

@Composable
private fun SeccionTitulo(texto: String) {
    Text(
        text = texto,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 3.sp,
        color = TextoSuave,
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
    )
}

@Composable
private fun FilaAcceso(
    icono: ImageVector,
    titulo: String,
    subtitulo: String,
    onClick: () -> Unit,
    color: androidx.compose.ui.graphics.Color = BurdeosOscuro
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = Crema),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = titulo,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = color
                )
                if (subtitulo.isNotBlank()) {
                    Text(
                        text = subtitulo,
                        fontSize = 12.sp,
                        color = TextoSuave
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextoSuave,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}