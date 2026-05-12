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
import com.mnemosyne.app.data.model.Noticia
import com.mnemosyne.app.ui.theme.*
import com.mnemosyne.app.utils.FirebaseResult

@Composable
fun AdminNoticiasScreen(
    onVolver: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val noticiasState by viewModel.noticias.observeAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }
    var noticiaEditar by remember { mutableStateOf<Noticia?>(null) }
    var mostrarConfirmarEliminar by remember { mutableStateOf<Noticia?>(null) }

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
                text = "NOTICIAS", fontSize = 13.sp, fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp, color = Crema, modifier = Modifier.align(Alignment.Center)
            )
            IconButton(
                onClick = { noticiaEditar = null; mostrarDialogo = true },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) { Icon(Icons.Default.Add, contentDescription = "Nueva", tint = Crema) }
        }

        when (val state = noticiasState) {
            is FirebaseResult.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Burdeos)
            }
            is FirebaseResult.Success -> {
                if (state.data.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay noticias", color = TextoSuave)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(state.data) { noticia ->
                            AdminItemCard(
                                titulo = noticia.titulo,
                                subtitulo = noticia.fecha,
                                badge = if (noticia.destacada) "DESTACADA" else null,
                                onEditar = { noticiaEditar = noticia; mostrarDialogo = true },
                                onEliminar = { mostrarConfirmarEliminar = noticia }
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
        NoticiaDialog(
            noticia = noticiaEditar,
            museoNombre = viewModel.museos.value?.get(viewModel.museoSeleccionado) ?: "",
            onDismiss = { mostrarDialogo = false },
            onGuardar = { noticia ->
                if (noticiaEditar == null) viewModel.crearNoticia(noticia)
                else viewModel.actualizarNoticia(noticia)
                mostrarDialogo = false
            }
        )
    }

    mostrarConfirmarEliminar?.let { noticia ->
        ConfirmarEliminarDialog(
            nombre = noticia.titulo,
            onConfirmar = { viewModel.eliminarNoticia(noticia.id); mostrarConfirmarEliminar = null },
            onDismiss = { mostrarConfirmarEliminar = null }
        )
    }
}

@Composable
private fun NoticiaDialog(
    noticia: Noticia?,
    museoNombre: String,
    onDismiss: () -> Unit,
    onGuardar: (Noticia) -> Unit
) {
    var titulo by remember { mutableStateOf(noticia?.titulo ?: "") }
    var contenido by remember { mutableStateOf(noticia?.contenido ?: "") }
    var fecha by remember { mutableStateOf(noticia?.fecha ?: "") }
    var imagenUrl by remember { mutableStateOf(noticia?.imagenUrl ?: "") }
    var destacada by remember { mutableStateOf(noticia?.destacada ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Crema,
        title = {
            Text(
                if (noticia == null) "Nueva noticia" else "Editar noticia",
                fontFamily = CinzelFamily, color = BurdeosOscuro
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminTextField("Título", titulo) { titulo = it }
                AdminTextField("Contenido", contenido, maxLines = 4) { contenido = it }
                AdminTextField("Fecha (dd/MM/yyyy)", fecha) { fecha = it }
                AdminTextField("URL imagen", imagenUrl) { imagenUrl = it }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = destacada, onCheckedChange = { destacada = it },
                        colors = CheckboxDefaults.colors(checkedColor = Burdeos))
                    Text("Destacada", fontSize = 13.sp, color = TextoOscuro)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onGuardar(
                        (noticia ?: Noticia()).copy(
                            titulo = titulo, contenido = contenido,
                            fecha = fecha, imagenUrl = imagenUrl,
                            destacada = destacada, museoNombre = museoNombre
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