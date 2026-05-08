package com.mnemosyne.app.ui.tienda

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mnemosyne.app.data.model.Stock
import com.mnemosyne.app.ui.carrito.CarritoViewModel
import com.mnemosyne.app.ui.theme.*
import com.mnemosyne.app.utils.FirebaseResult

@Composable
fun DetalleTiendaScreen(
    producto: Stock,
    onVolver: () -> Unit,
    onAnadirAlCarrito: (Stock) -> Unit,
    onIrCarrito: () -> Unit,
    carritoViewModel: CarritoViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val operacion by carritoViewModel.operacion.observeAsState()
    var mensajeExito by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(operacion) {
        if (operacion is FirebaseResult.Success) {
            mensajeExito = "¡Añadido al carrito!"
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
            Text(
                text = "TIENDA",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                color = Crema,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            // Imagen
            if (producto.imagenUrl.isNotEmpty()) {
                AsyncImage(
                    model = producto.imagenUrl,
                    contentDescription = producto.nombreProducto,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(DoradoSuave),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✦", color = Dorado, fontSize = 40.sp)
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = producto.nombreProducto,
                    fontSize = 22.sp,
                    fontFamily = CinzelFamily,
                    fontWeight = FontWeight.Medium,
                    color = BurdeosOscuro,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "%.2f €".format(producto.precio),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Burdeos
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = if (producto.cantidad > 0) Dorado.copy(alpha = 0.2f)
                    else Burdeos.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = if (producto.cantidad > 0) "EN STOCK (${producto.cantidad})" else "AGOTADO",
                        fontSize = 9.sp,
                        letterSpacing = 1.sp,
                        color = if (producto.cantidad > 0) TextoOscuro else Burdeos,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = DoradoSuave)
                Spacer(modifier = Modifier.height(20.dp))

                // ── Mensaje éxito ─────────────────────────
                if (mensajeExito != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Dorado.copy(alpha = 0.2f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = mensajeExito!!,
                                color = BurdeosOscuro,
                                fontWeight = FontWeight.Medium
                            )
                            TextButton(onClick = onIrCarrito) {
                                Text(
                                    text = "Ver carrito →",
                                    color = Burdeos,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Botón añadir al carrito
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Superficie)
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            Button(
                onClick = { onAnadirAlCarrito(producto) },
                enabled = producto.cantidad > 0,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(2.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Burdeos,
                    disabledContainerColor = TextoSuave
                )
            ) {
                Text(
                    text = if (producto.cantidad > 0) "AÑADIR AL CARRITO" else "AGOTADO",
                    letterSpacing = 2.sp,
                    fontSize = 13.sp,
                    color = Crema,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }
}