package com.mnemosyne.app.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.mnemosyne.app.data.model.Obra
import com.mnemosyne.app.ui.theme.*
import com.mnemosyne.app.utils.FirebaseResult

@Composable
fun AdminObrasScreen(
    onVolver: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val obrasState by viewModel.obras.observeAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }
    var obraEditar by remember { mutableStateOf<Obra?>(null) }
    var mostrarConfirmarEliminar by remember { mutableStateOf<Obra?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().background(Superficie).statusBarsPadding()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().background(Burdeos)
                .padding(horizontal = 4.dp, vertical = 12.dp)
        ) {
            IconButton(onClick = onVolver, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Crema)
            }
            Text(
                text = "OBRAS", fontSize = 13.sp, fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp, color = Crema, modifier = Modifier.align(Alignment.Center)
            )
            IconButton(
                onClick = { obraEditar = null; mostrarDialogo = true },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) { Icon(Icons.Default.Add, contentDescription = "Nueva", tint = Crema) }
        }

        when (val state = obrasState) {
            is FirebaseResult.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Burdeos)
            }
            is FirebaseResult.Success -> {
                if (state.data.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay obras", color = TextoSuave)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(state.data) { obra ->
                            AdminItemCard(
                                titulo = obra.nombre,
                                subtitulo = "${obra.autor} · ${obra.siglo}",
                                badge = if (!obra.disponible) "NO DISPONIBLE" else null,
                                onEditar = { obraEditar = obra; mostrarDialogo = true },
                                onEliminar = { mostrarConfirmarEliminar = obra }
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
        ObraDialog(
            obra = obraEditar,
            onDismiss = { mostrarDialogo = false },
            onGuardar = { obra ->
                if (obraEditar == null) viewModel.crearObra(obra)
                else viewModel.actualizarObra(obra)
                mostrarDialogo = false
            }
        )
    }

    mostrarConfirmarEliminar?.let { obra ->
        ConfirmarEliminarDialog(
            nombre = obra.nombre,
            onConfirmar = { viewModel.eliminarObra(obra.id); mostrarConfirmarEliminar = null },
            onDismiss = { mostrarConfirmarEliminar = null }
        )
    }
}

@Composable
private fun ObraDialog(
    obra: Obra?,
    onDismiss: () -> Unit,
    onGuardar: (Obra) -> Unit
) {
    var nombre by remember { mutableStateOf(obra?.nombre ?: "") }
    var autor by remember { mutableStateOf(obra?.autor ?: "") }
    var siglo by remember { mutableStateOf(obra?.siglo ?: "") }
    var cultura by remember { mutableStateOf(obra?.cultura ?: "") }
    var descripcion by remember { mutableStateOf(obra?.descripcion ?: "") }
    var imagenUrl by remember { mutableStateOf(obra?.imagenUrl ?: "") }
    var disponible by remember { mutableStateOf(obra?.disponible ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Crema,
        title = {
            Text(
                if (obra == null) "Nueva obra" else "Editar obra",
                fontFamily = CinzelFamily, color = BurdeosOscuro
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminTextField("Nombre", nombre) { nombre = it }
                AdminTextField("Autor", autor) { autor = it }
                AdminTextField("Siglo", siglo) { siglo = it }
                AdminTextField("Cultura", cultura) { cultura = it }
                AdminTextField("Descripción", descripcion, maxLines = 3) { descripcion = it }
                AdminTextField("URL imagen", imagenUrl) { imagenUrl = it }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = disponible, onCheckedChange = { disponible = it },
                        colors = CheckboxDefaults.colors(checkedColor = Burdeos))
                    Text("Disponible", fontSize = 13.sp, color = TextoOscuro)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onGuardar(
                        (obra ?: Obra()).copy(
                            nombre = nombre, autor = autor, siglo = siglo,
                            cultura = cultura, descripcion = descripcion,
                            imagenUrl = imagenUrl, disponible = disponible
                        )
                    )
                },
                enabled = nombre.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Burdeos)
            ) { Text("Guardar", color = Crema) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextoSuave) }
        }
    )
}