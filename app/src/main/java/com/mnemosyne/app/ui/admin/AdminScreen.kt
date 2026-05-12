package com.mnemosyne.app.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mnemosyne.app.ui.theme.*

@Composable
fun AdminScreen(
    onVolver: () -> Unit,
    onGestionarExposiciones: () -> Unit,
    onGestionarNoticias: () -> Unit,
    onGestionarObras: () -> Unit,
    onGestionarStock: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val museos by viewModel.museos.observeAsState(emptyMap())
    var museoSeleccionado by remember { mutableStateOf("") }

    LaunchedEffect(museos) {
        if (museos.isNotEmpty() && museoSeleccionado.isEmpty()) {
            museoSeleccionado = museos.keys.first()
            viewModel.seleccionarMuseo(museoSeleccionado)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Superficie)
            .statusBarsPadding()
    ) {
        // Cabecera
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Burdeos)
                .padding(horizontal = 4.dp, vertical = 12.dp)
        ) {
            IconButton(
                onClick = onVolver,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Crema
                )
            }
            Column(modifier = Modifier.align(Alignment.Center)) {
                Text(
                    text = "PANEL DE ADMINISTRACIÓN",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = Crema
                )
            }
        }

        // Selector de museo
        if (museos.isNotEmpty()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = "MUSEO",
                    fontSize = 10.sp,
                    letterSpacing = 3.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoSuave
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    museos.forEach { (id, nombre) ->
                        FilterChip(
                            selected = museoSeleccionado == id,
                            onClick = {
                                museoSeleccionado = id
                                viewModel.seleccionarMuseo(id)
                            },
                            label = {
                                Text(nombre, fontSize = 11.sp, letterSpacing = 1.sp)
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Burdeos,
                                selectedLabelColor = Crema,
                                containerColor = Crema,
                                labelColor = BurdeosOscuro
                            ),
                            shape = RoundedCornerShape(2.dp)
                        )
                    }
                }
            }
            HorizontalDivider(color = DoradoSuave)
        }

        // Secciones
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "GESTIONAR",
                fontSize = 10.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Bold,
                color = TextoSuave,
                modifier = Modifier.padding(top = 4.dp)
            )

            AdminMenuCard(
                icono = Icons.Default.Museum,
                titulo = "Exposiciones",
                subtitulo = "Crear, editar y eliminar exposiciones",
                onClick = onGestionarExposiciones
            )
            AdminMenuCard(
                icono = Icons.Default.Newspaper,
                titulo = "Noticias",
                subtitulo = "Gestionar noticias del museo",
                onClick = onGestionarNoticias
            )
            AdminMenuCard(
                icono = Icons.Default.Brush,
                titulo = "Obras",
                subtitulo = "Gestionar el catálogo de obras",
                onClick = onGestionarObras
            )
            AdminMenuCard(
                icono = Icons.Default.ShoppingBag,
                titulo = "Tienda",
                subtitulo = "Gestionar productos y stock",
                onClick = onGestionarStock
            )
        }
    }
}

@Composable
private fun AdminMenuCard(
    icono: ImageVector,
    titulo: String,
    subtitulo: String,
    onClick: () -> Unit
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
                tint = Burdeos,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = BurdeosOscuro
                )
                Text(
                    text = subtitulo,
                    fontSize = 12.sp,
                    color = TextoSuave
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextoSuave
            )
        }
    }
}