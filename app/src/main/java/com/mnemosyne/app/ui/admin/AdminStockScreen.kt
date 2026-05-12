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
import com.mnemosyne.app.data.model.Stock
import com.mnemosyne.app.ui.theme.*
import com.mnemosyne.app.utils.FirebaseResult

@Composable
fun AdminStockScreen(
    onVolver: () -> Unit,
    viewModel: AdminViewModel = viewModel()
) {
    val stockState by viewModel.stock.observeAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }
    var productoEditar by remember { mutableStateOf<Stock?>(null) }
    var mostrarConfirmarEliminar by remember { mutableStateOf<Stock?>(null) }

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
                text = "TIENDA", fontSize = 13.sp, fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp, color = Crema, modifier = Modifier.align(Alignment.Center)
            )
            IconButton(
                onClick = { productoEditar = null; mostrarDialogo = true },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) { Icon(Icons.Default.Add, contentDescription = "Nuevo", tint = Crema) }
        }

        when (val state = stockState) {
            is FirebaseResult.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Burdeos)
            }
            is FirebaseResult.Success -> {
                if (state.data.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay productos", color = TextoSuave)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(state.data) { producto ->
                            AdminItemCard(
                                titulo = producto.nombreProducto,
                                subtitulo = "%.2f € · Stock: ${producto.cantidad}".format(producto.precio),
                                badge = if (producto.cantidad == 0) "AGOTADO" else null,
                                onEditar = { productoEditar = producto; mostrarDialogo = true },
                                onEliminar = { mostrarConfirmarEliminar = producto }
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
        ProductoDialog(
            producto = productoEditar,
            onDismiss = { mostrarDialogo = false },
            onGuardar = { producto ->
                if (productoEditar == null) viewModel.crearProducto(producto)
                else viewModel.actualizarProducto(producto)
                mostrarDialogo = false
            }
        )
    }

    mostrarConfirmarEliminar?.let { producto ->
        ConfirmarEliminarDialog(
            nombre = producto.nombreProducto,
            onConfirmar = { viewModel.eliminarProducto(producto.id); mostrarConfirmarEliminar = null },
            onDismiss = { mostrarConfirmarEliminar = null }
        )
    }
}

@Composable
private fun ProductoDialog(
    producto: Stock?,
    onDismiss: () -> Unit,
    onGuardar: (Stock) -> Unit
) {
    var nombre by remember { mutableStateOf(producto?.nombreProducto ?: "") }
    var precio by remember { mutableStateOf(producto?.precio?.toString() ?: "") }
    var cantidad by remember { mutableStateOf(producto?.cantidad?.toString() ?: "") }
    var imagenUrl by remember { mutableStateOf(producto?.imagenUrl ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Crema,
        title = {
            Text(
                if (producto == null) "Nuevo producto" else "Editar producto",
                fontFamily = CinzelFamily, color = BurdeosOscuro
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminTextField("Nombre", nombre) { nombre = it }
                AdminTextField("Precio (€)", precio) { precio = it }
                AdminTextField("Cantidad en stock", cantidad) { cantidad = it }
                AdminTextField("URL imagen", imagenUrl) { imagenUrl = it }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onGuardar(
                        (producto ?: Stock()).copy(
                            nombreProducto = nombre,
                            precio = precio.toDoubleOrNull() ?: 0.0,
                            cantidad = cantidad.toIntOrNull() ?: 0,
                            imagenUrl = imagenUrl
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