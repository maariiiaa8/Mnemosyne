package com.mnemosyne.app.ui.exposiciones

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun ExposicionesScreen(
    onExposicionClick: (Exposicion) -> Unit,
    viewModel: ExposicionViewModel = viewModel()
) {
    val exposicionesState by viewModel.exposiciones.observeAsState()
    val museos            by viewModel.museos.observeAsState(emptyList())
    val museoFiltro       by viewModel.museoFiltro.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Superficie)
    ) {
        // ── Cabecera ─────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Burdeos)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "EXPOSICIONES",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                color = Crema
            )
        }

        // ── Filtro por museo ──────────────────────────────
        if (museos.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CremaOscura)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FiltroChip(
                        texto = "Todos",
                        seleccionado = museoFiltro == null,
                        onClick = { viewModel.filtrarPorMuseo(null) }
                    )
                }
                items(museos) { museo ->
                    FiltroChip(
                        texto = museo,
                        seleccionado = museoFiltro == museo,
                        onClick = { viewModel.filtrarPorMuseo(museo) }
                    )
                }
            }
        }

        // ── Lista de exposiciones ─────────────────────────
        when (val state = exposicionesState) {
            is FirebaseResult.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Burdeos)
                }
            }
            is FirebaseResult.Success -> {
                val filtradas = viewModel.exposicionesFiltradas()
                if (filtradas.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No hay exposiciones disponibles",
                            color = TextoSuave,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(filtradas) { exposicion ->
                            TarjetaExposicionDetalle(
                                exposicion = exposicion,
                                onClick = { onExposicionClick(exposicion) }
                            )
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
fun FiltroChip(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(2.dp),
        color = if (seleccionado) Burdeos else Crema,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = texto,
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            color = if (seleccionado) Crema else TextoOscuro,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun TarjetaExposicionDetalle(exposicion: Exposicion, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = Crema),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Museo y badge público/privado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exposicion.museoNombre.uppercase(),
                    fontSize = 10.sp,
                    letterSpacing = 2.sp,
                    color = Dorado,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    color = if (exposicion.esPublica) Dorado.copy(alpha = 0.2f)
                    else Burdeos.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = if (exposicion.esPublica) "ENTRADA LIBRE" else "CON ENTRADA",
                        fontSize = 9.sp,
                        letterSpacing = 1.sp,
                        color = if (exposicion.esPublica) TextoOscuro else Burdeos,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = exposicion.titulo,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = BurdeosOscuro
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = exposicion.descripcion,
                fontSize = 13.sp,
                color = TextoSuave,
                lineHeight = 18.sp
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
                if (!exposicion.esPublica) {
                    Text(
                        text = "Ver entradas →",
                        fontSize = 11.sp,
                        color = Burdeos,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}