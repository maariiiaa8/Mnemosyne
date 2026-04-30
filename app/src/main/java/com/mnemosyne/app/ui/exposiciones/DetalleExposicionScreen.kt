package com.mnemosyne.app.ui.exposiciones

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.mnemosyne.app.data.model.ItemCarrito
import com.mnemosyne.app.data.model.TipoEntrada
import com.mnemosyne.app.ui.carrito.CarritoViewModel
import com.mnemosyne.app.ui.theme.*
import com.mnemosyne.app.utils.FirebaseResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleExposicionScreen(
    exposicion: Exposicion,
    onVolver: () -> Unit,
    onIrCarrito: () -> Unit,
    carritoViewModel: CarritoViewModel = viewModel()
) {
    val operacion by carritoViewModel.operacion.observeAsState()
    var mensajeExito by remember { mutableStateOf<String?>(null) }
    var mostrarEntradas by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        Log.d("DEBUG", "esPublica=${exposicion.esPublica}, entradas=${exposicion.tiposEntrada.size}")
    }
    LaunchedEffect(operacion) {
        if (operacion is FirebaseResult.Success) {
            mensajeExito = "¡Añadido al carrito!"
        }
    }

    // ── Bottom Sheet de entradas ──────────────────────────
    if (mostrarEntradas) {
        ModalBottomSheet(
            onDismissRequest = { mostrarEntradas = false },
            containerColor = Superficie,
            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "— ENTRADAS —",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp,
                    color = TextoSuave,
                    fontFamily = CinzelFamily,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                exposicion.tiposEntrada.forEach { tipo ->
                    TarjetaTipoEntrada(
                        tipo = tipo,
                        onAñadir = {
                            carritoViewModel.añadirItem(
                                ItemCarrito(
                                    exposicionId      = exposicion.id,
                                    exposicionTitulo  = exposicion.titulo,
                                    museoNombre       = exposicion.museoNombre,
                                    tipoEntradaNombre = tipo.nombre,
                                    precio            = tipo.precio,
                                    cantidad          = 1
                                )
                            )
                            mostrarEntradas = false
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = exposicion.titulo.uppercase(),
                        fontSize = 13.sp,
                        letterSpacing = 2.sp,
                        color = Crema
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Crema
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Burdeos)
            )
        },
        containerColor = Superficie,
        bottomBar = {
            if (!exposicion.esPublica && exposicion.tiposEntrada.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Superficie)
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = { mostrarEntradas = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(2.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Burdeos,
                            contentColor = Crema
                        )
                    ) {
                        Text(
                            text = "VER ENTRADAS",
                            fontSize = 12.sp,
                            letterSpacing = 3.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // ── Info principal ────────────────────────────
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Crema),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = exposicion.museoNombre.uppercase(),
                            fontSize = 10.sp,
                            letterSpacing = 2.sp,
                            color = Dorado,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${exposicion.fechaInicio}  —  ${exposicion.fechaFin}",
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                color = TextoSuave
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = exposicion.titulo,
                            fontSize = 20.sp,
                            fontFamily = CinzelFamily,
                            fontWeight = FontWeight.Medium,
                            color = BurdeosOscuro
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = exposicion.descripcion,
                            fontSize = 14.sp,
                            color = TextoSuave,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = DoradoSuave)
                    }
                }
            }

            // ── Mensaje éxito + botón carrito ─────────────
            if (mensajeExito != null) {
                item {
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
    }
}

@Composable
fun TarjetaTipoEntrada(tipo: TipoEntrada, onAñadir: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = Crema),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tipo.nombre,
                    fontSize = 15.sp,
                    fontFamily = CinzelFamily,
                    fontWeight = FontWeight.Medium,
                    color = BurdeosOscuro
                )
                Text(
                    text = tipo.descripcion,
                    fontSize = 12.sp,
                    color = TextoSuave
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (tipo.precio == 0.0) "Gratuito"
                    else "%.2f €".format(tipo.precio),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Burdeos
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = onAñadir,
                shape = RoundedCornerShape(2.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Burdeos,
                    contentColor = Crema
                )
            ) {
                Text(
                    text = "+ AÑADIR",
                    fontSize = 11.sp,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}