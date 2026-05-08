package com.mnemosyne.app.ui.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mnemosyne.app.data.model.Pedido
import com.mnemosyne.app.ui.theme.*
import com.mnemosyne.app.utils.FirebaseResult
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisEntradasScreen(
    onVolver: () -> Unit,
    onIrAExposiciones: () -> Unit,
    soloMerch: Boolean = false,
    viewModel: MisEntradasViewModel = viewModel()
) {
    LaunchedEffect(soloMerch) {
        viewModel.cargarPedidos(soloMerch)
    }

    val pedidosState by viewModel.pedidos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (soloMerch) "MIS COMPRAS" else "MIS ENTRADAS",
                        fontSize = 13.sp,
                        letterSpacing = 2.sp,
                        color = Crema
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Crema)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Burdeos)
            )
        },
        containerColor = Superficie
    ) { innerPadding ->
        when (val state = pedidosState) {
            is FirebaseResult.Loading -> {
                Box(
                    Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Burdeos)
                }
            }

            is FirebaseResult.Success -> {
                if (state.data.isEmpty()) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = if (soloMerch) Icons.Default.ShoppingBag
                                else Icons.Default.ConfirmationNumber,
                                contentDescription = null,
                                tint = DoradoSuave,
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                text = if (soloMerch) "Aún no tienes compras"
                                else "Aún no tienes entradas",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = CinzelFamily,
                                color = BurdeosOscuro,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = if (soloMerch) "Cuando compres productos aparecerán aquí."
                                else "Cuando compres entradas aparecerán aquí.",
                                fontSize = 13.sp,
                                color = TextoSuave,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onIrAExposiciones,
                                shape = RoundedCornerShape(2.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Burdeos,
                                    contentColor = Crema
                                )
                            ) {
                                Text(
                                    text = if (soloMerch) "VER TIENDA" else "VER EXPOSICIONES",
                                    fontSize = 11.sp,
                                    letterSpacing = 3.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(state.data) { pedido ->
                            TarjetaPedido(pedido = pedido, soloMerch = soloMerch)
                        }
                    }
                }
            }

            is FirebaseResult.Error -> {
                Box(
                    Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.mensaje, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun TarjetaPedido(pedido: Pedido, soloMerch: Boolean = false) {
    val fecha = remember(pedido.fecha) {
        SimpleDateFormat("dd MMM yyyy · HH:mm", Locale("es")).format(Date(pedido.fecha))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = Crema),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Cabecera: fecha y total ────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = fecha,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    color = TextoSuave
                )
                Text(
                    text = "%.2f €".format(pedido.total),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Burdeos
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = DoradoSuave)
            Spacer(modifier = Modifier.height(8.dp))

            // ── Items del pedido ──────────────────────────
            pedido.items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        if (soloMerch) {
                            // Vista merch: sin museo, con "Producto tienda"
                            Text(
                                text = "TIENDA",
                                fontSize = 9.sp,
                                letterSpacing = 2.sp,
                                color = Dorado,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = item.exposicionTitulo,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = BurdeosOscuro
                            )
                            Text(
                                text = "× ${item.cantidad}",
                                fontSize = 12.sp,
                                color = TextoSuave
                            )
                        } else {
                            // Vista entradas: con museo y tipo de entrada
                            Text(
                                text = item.museoNombre.uppercase(),
                                fontSize = 9.sp,
                                letterSpacing = 2.sp,
                                color = Dorado,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = item.exposicionTitulo,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = BurdeosOscuro
                            )
                            Text(
                                text = "${item.tipoEntradaNombre} × ${item.cantidad}",
                                fontSize = 12.sp,
                                color = TextoSuave
                            )
                        }
                    }
                    Text(
                        text = "%.2f €".format(item.precio * item.cantidad),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BurdeosOscuro
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}