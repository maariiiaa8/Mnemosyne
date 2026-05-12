package com.mnemosyne.app.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mnemosyne.app.data.model.Exposicion
import com.mnemosyne.app.ui.theme.*
import com.mnemosyne.app.utils.FirebaseResult

@Composable
fun AdminExposicionesScreen(
    onVolver: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val exposicionesState by viewModel.exposiciones.observeAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }
    var exposicionEditar by remember { mutableStateOf<Exposicion?>(null) }
    var mostrarConfirmarEliminar by remember { mutableStateOf<Exposicion?>(null) }

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
                .padding(horizontal = 4.dp, vertical = 12.dp)
        ) {
            IconButton(onClick = onVolver, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Crema)
            }
            Text(
                text = "EXPOSICIONES",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = Crema,
                modifier = Modifier.align(Alignment.Center)
            )
            IconButton(
                onClick = { exposicionEditar = null; mostrarDialogo = true },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva", tint = Crema)
            }
        }

        when (val state = exposicionesState) {
            is FirebaseResult.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Burdeos)
            }
            is FirebaseResult.Success -> {
                if (state.data.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay exposiciones", color = TextoSuave)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(state.data) { exposicion ->
                            AdminItemCard(
                                titulo = exposicion.titulo,
                                subtitulo = "${exposicion.fechaInicio} – ${exposicion.fechaFin}",
                                badge = if (exposicion.destacada) "DESTACADA" else null,
                                onEditar = { exposicionEditar = exposicion; mostrarDialogo = true },
                                onEliminar = { mostrarConfirmarEliminar = exposicion }
                            )
                        }
                    }
                }
            }
            is FirebaseResult.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.mensaje, color = MaterialTheme.colorScheme.error)
            }
            null -> {}
        }
    }

    if (mostrarDialogo) {
        ExposicionDialog(
            exposicion = exposicionEditar,
            museoNombre = viewModel.museos.value?.get(viewModel.museoSeleccionado) ?: "",
            onDismiss = { mostrarDialogo = false },
            onGuardar = { expo ->
                if (exposicionEditar == null) viewModel.crearExposicion(expo)
                else viewModel.actualizarExposicion(expo)
                mostrarDialogo = false
            }
        )
    }

    mostrarConfirmarEliminar?.let { expo ->
        ConfirmarEliminarDialog(
            nombre = expo.titulo,
            onConfirmar = { viewModel.eliminarExposicion(expo.id); mostrarConfirmarEliminar = null },
            onDismiss = { mostrarConfirmarEliminar = null }
        )
    }
}

@Composable
private fun ExposicionDialog(
    exposicion: Exposicion?,
    museoNombre: String,
    onDismiss: () -> Unit,
    onGuardar: (Exposicion) -> Unit
) {
    var titulo by remember { mutableStateOf(exposicion?.titulo ?: "") }
    var descripcion by remember { mutableStateOf(exposicion?.descripcion ?: "") }
    var fechaInicio by remember { mutableStateOf(exposicion?.fechaInicio ?: "") }
    var fechaFin by remember { mutableStateOf(exposicion?.fechaFin ?: "") }
    var imagenUrl by remember { mutableStateOf(exposicion?.imagenUrl ?: "") }
    var destacada by remember { mutableStateOf(exposicion?.destacada ?: false) }
    var esPublica by remember { mutableStateOf(exposicion?.esPublica ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Crema,
        title = {
            Text(
                text = if (exposicion == null) "Nueva exposición" else "Editar exposición",
                fontFamily = CinzelFamily,
                color = BurdeosOscuro
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminTextField("Título", titulo) { titulo = it }
                AdminTextField("Descripción", descripcion, maxLines = 3) { descripcion = it }
                AdminTextField("Fecha inicio (dd/MM/yyyy)", fechaInicio) { fechaInicio = it }
                AdminTextField("Fecha fin (dd/MM/yyyy)", fechaFin) { fechaFin = it }
                AdminTextField("URL imagen", imagenUrl) { imagenUrl = it }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = destacada, onCheckedChange = { destacada = it },
                        colors = CheckboxDefaults.colors(checkedColor = Burdeos))
                    Text("Destacada", fontSize = 13.sp, color = TextoOscuro)
                    Spacer(modifier = Modifier.width(16.dp))
                    Checkbox(checked = esPublica, onCheckedChange = { esPublica = it },
                        colors = CheckboxDefaults.colors(checkedColor = Burdeos))
                    Text("Pública", fontSize = 13.sp, color = TextoOscuro)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onGuardar(
                        (exposicion ?: Exposicion()).copy(
                            titulo = titulo,
                            descripcion = descripcion,
                            fechaInicio = fechaInicio,
                            fechaFin = fechaFin,
                            imagenUrl = imagenUrl,
                            destacada = destacada,
                            esPublica = esPublica,
                            museoNombre = museoNombre,
                            tiposEntrada = exposicion?.tiposEntrada ?: emptyList()
                        )
                    )
                },
                enabled = titulo.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Burdeos)
            ) { Text("Guardar", color = Crema) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextoSuave) }
        }
    )
}