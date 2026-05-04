package com.mnemosyne.app.ui.carrito

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mnemosyne.app.data.model.ItemCarrito
import com.mnemosyne.app.ui.compra.CompraViewModel
import com.mnemosyne.app.ui.theme.*
import com.mnemosyne.app.utils.FirebaseResult
import com.stripe.android.paymentsheet.PaymentSheetContract
import com.stripe.android.paymentsheet.PaymentSheetResult

@Composable
fun CarritoScreen(
    onPagar: () -> Unit,
    onPagoCompletado: () -> Unit,
    viewModel: CarritoViewModel = viewModel(),
    compraViewModel: CompraViewModel = viewModel()
) {
    val itemsState by viewModel.items.observeAsState()
    val clientSecret by compraViewModel.clientSecret.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = PaymentSheetContract()
    ) { result ->
        when (result) {
            is PaymentSheetResult.Completed -> onPagoCompletado()
            is PaymentSheetResult.Failed -> compraViewModel.onError(result.error.message ?: "Error")
            is PaymentSheetResult.Canceled -> {}
        }
    }

    LaunchedEffect(clientSecret) {
        clientSecret?.let { secret ->
            launcher.launch(
                PaymentSheetContract.Args.createPaymentIntentArgs(secret)
            )
        }
    }

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
                text = "MI CARRITO",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                color = Crema
            )
        }

        when (val state = itemsState) {
            is FirebaseResult.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Burdeos)
                }
            }

            is FirebaseResult.Success -> {
                if (state.data.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "✦", fontSize = 32.sp, color = DoradoSuave)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Tu carrito está vacío",
                                fontSize = 16.sp,
                                color = TextoSuave,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Añade entradas desde las exposiciones",
                                fontSize = 12.sp,
                                color = TextoSuave.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                } else {
                    val total = viewModel.calcularTotal(state.data)

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(state.data) { item ->
                            TarjetaItemCarrito(
                                item = item,
                                onIncrementar = {
                                    viewModel.actualizarCantidad(item.id, item.cantidad + 1)
                                },
                                onDecrementar = {
                                    viewModel.actualizarCantidad(item.id, item.cantidad - 1)
                                },
                                onEliminar = {
                                    viewModel.eliminarItem(item.id)
                                }
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding(),
                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Crema),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "TOTAL",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 3.sp,
                                    color = TextoSuave
                                )
                                Text(
                                    text = "%.2f €".format(total),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BurdeosOscuro
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = onPagar,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(2.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Burdeos,
                                    contentColor = Crema
                                )
                            ) {
                                Text(
                                    text = "PROCEDER AL PAGO",
                                    letterSpacing = 3.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            TextButton(
                                onClick = { viewModel.vaciarCarrito() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Vaciar carrito",
                                    color = TextoSuave,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
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
fun TarjetaItemCarrito(
    item: ItemCarrito,
    onIncrementar: () -> Unit,
    onDecrementar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = Crema),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.museoNombre.uppercase(),
                        fontSize = 10.sp,
                        letterSpacing = 2.sp,
                        color = Dorado,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.exposicionTitulo,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = BurdeosOscuro
                    )
                    Text(
                        text = item.tipoEntradaNombre,
                        fontSize = 12.sp,
                        color = TextoSuave
                    )
                }
                IconButton(onClick = onEliminar) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = TextoSuave
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "%.2f € × %d = %.2f €".format(
                        item.precio, item.cantidad, item.precio * item.cantidad
                    ),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Burdeos
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDecrementar, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Remove, contentDescription = "Restar", tint = Burdeos)
                    }
                    Text(
                        text = item.cantidad.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BurdeosOscuro,
                        modifier = Modifier.widthIn(min = 24.dp),
                        textAlign = TextAlign.Center
                    )
                    IconButton(onClick = onIncrementar, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "Sumar", tint = Burdeos)
                    }
                }
            }
        }
    }
}