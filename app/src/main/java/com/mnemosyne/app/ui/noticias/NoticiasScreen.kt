package com.mnemosyne.app.ui.noticias

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
import com.mnemosyne.app.data.model.Noticia
import com.mnemosyne.app.ui.theme.*
import com.mnemosyne.app.utils.FirebaseResult

@Composable
fun NoticiasScreen(
    onNoticiaClick: (Noticia) -> Unit,
    viewModel: NoticiasViewModel = viewModel()
) {
    val noticiasState by viewModel.noticias.observeAsState()
    val museos by viewModel.museos.observeAsState(emptyList())
    val museoFiltro by viewModel.museoFiltro.observeAsState()

    // Igual que en ExposicionesScreen: recalcula cuando cambia estado o filtro
    val filtradas = remember(noticiasState, museoFiltro) {
        viewModel.noticiasFiltradas()
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
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "NOTICIAS",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                color = Crema
            )
        }

        // Filtro por museo
        if (museos.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CremaOscura)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FiltroChipNoticia(
                        texto = "Todos",
                        seleccionado = museoFiltro == null,
                        onClick = { viewModel.filtrarPorMuseo(null) }
                    )
                }
                items(museos) { museo ->
                    FiltroChipNoticia(
                        texto = museo,
                        seleccionado = museoFiltro == museo,
                        onClick = { viewModel.filtrarPorMuseo(museo) }
                    )
                }
            }
        }

        // Lista
        when (val state = noticiasState) {
            is FirebaseResult.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Burdeos)
                }
            }
            is FirebaseResult.Success -> {
                if (filtradas.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No hay noticias disponibles",
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
                        items(filtradas) { noticia ->
                            TarjetaNoticiaDetalle(
                                noticia = noticia,
                                onClick = { onNoticiaClick(noticia) }
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
                            onClick = { viewModel.cargarNoticias() },
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
fun FiltroChipNoticia(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
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
fun TarjetaNoticiaDetalle(noticia: Noticia, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = Crema),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = noticia.museoNombre.uppercase(),
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                color = Dorado,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = noticia.titulo,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = BurdeosOscuro
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = noticia.fecha,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = TextoSuave
            )
        }
    }
}