package com.mnemosyne.app.ui.tienda

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mnemosyne.app.data.model.Stock
import com.mnemosyne.app.ui.theme.*
import com.mnemosyne.app.utils.FirebaseResult

@Composable
fun TiendaScreen(
    onProductoClick: (Stock) -> Unit,
    viewModel: StockViewModel = viewModel()
) {
    val productosState by viewModel.productos.observeAsState()

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
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "TIENDA",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                color = Crema
            )
        }

        when (val state = productosState) {
            is FirebaseResult.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Burdeos)
                }
            }
            is FirebaseResult.Success -> {
                if (state.data.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No hay productos disponibles",
                            color = TextoSuave,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.data) { producto ->
                            TarjetaProducto(
                                producto = producto,
                                onClick = { onProductoClick(producto) }
                            )
                        }
                    }
                }
            }
            is FirebaseResult.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.mensaje, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.cargarProductos() },
                            colors = ButtonDefaults.buttonColors(containerColor = Burdeos)
                        ) {
                            Text("Reintentar", color = Crema)
                        }
                    }
                }
            }
            null -> {}
        }
    }
}

@Composable
fun TarjetaProducto(producto: Stock, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = Crema),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            if (producto.imagenUrl.isNotEmpty()) {
                AsyncImage(
                    model = producto.imagenUrl,
                    contentDescription = producto.nombreProducto,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(DoradoSuave),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✦", color = Dorado, fontSize = 24.sp)
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = producto.nombreProducto,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = BurdeosOscuro,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "%.2f €".format(producto.precio),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Burdeos
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (producto.cantidad > 0) "En stock" else "Agotado",
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    color = if (producto.cantidad > 0) Dorado else TextoSuave
                )
            }
        }
    }
}