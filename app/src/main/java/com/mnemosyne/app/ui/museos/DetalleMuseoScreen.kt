package com.mnemosyne.app.ui.museos

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mnemosyne.app.data.model.Exposicion
import com.mnemosyne.app.data.model.Museo
import com.mnemosyne.app.data.model.Obra
import com.mnemosyne.app.ui.theme.*
import com.mnemosyne.app.utils.FirebaseResult

@Composable
fun DetalleMuseoScreen(
    museo: Museo,
    onBack: () -> Unit,
    onExposicionClick: (Exposicion) -> Unit,
    viewModel: DetalleMuseoViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val obrasState by viewModel.obras.observeAsState()
    val exposicionesState by viewModel.exposiciones.observeAsState()

    // Cargamos los datos la primera vez que entra a la pantalla
    LaunchedEffect(museo.id) {
        viewModel.cargarDatos(museo.id)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Superficie)
            .statusBarsPadding()
    ) {
        // ── Cabecera ─────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Burdeos)
                .padding(horizontal = 4.dp, vertical = 12.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Crema
                )
            }
            Text(
                text = museo.ciudad.uppercase(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                color = Crema,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // ── Contenido scrollable ──────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .navigationBarsPadding()
        ) {
            // Imagen principal del museo
            if (museo.imagenUrl.isNotEmpty()) {
                AsyncImage(
                    model = museo.imagenUrl,
                    contentDescription = museo.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(DoradoSuave)
                )
            }

            Column(modifier = Modifier.padding(20.dp)) {

                // Nombre del museo
                Text(
                    text = museo.nombre,
                    fontSize = 24.sp,
                    fontFamily = CinzelFamily,
                    fontWeight = FontWeight.Medium,
                    color = BurdeosOscuro,
                    lineHeight = 30.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Ciudad
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = Dorado,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = museo.ciudad,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        color = Dorado,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = DoradoSuave)
                Spacer(modifier = Modifier.height(10.dp))

                // Descripción
                if (museo.descripcion.isNotEmpty()) {
                    Text(
                        text = "SOBRE EL MUSEO",
                        fontSize = 10.sp,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoSuave
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = museo.descripcion,
                        fontSize = 14.sp,
                        color = BurdeosOscuro,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }

            // ── Carrusel de obras ─────────────────────────
            when (val state = obrasState) {
                is FirebaseResult.Success -> {
                    if (state.data.isNotEmpty()) {
                        SeccionCarruselObras(obras = state.data)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                is FirebaseResult.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Burdeos, modifier = Modifier.size(24.dp))
                    }
                }
                else -> {}
            }

            // ── Exposiciones del museo ────────────────────
            when (val state = exposicionesState) {
                is FirebaseResult.Success -> {
                    if (state.data.isNotEmpty()) {
                        SeccionExposicionesMuseo(
                            exposiciones = state.data,
                            onExposicionClick = onExposicionClick
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                is FirebaseResult.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Burdeos, modifier = Modifier.size(24.dp))
                    }
                }
                else -> {}
            }

            // ── Botón web oficial ─────────────────────────
            if (museo.web.isNotEmpty()) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    HorizontalDivider(color = DoradoSuave)
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            val url = if (museo.web.startsWith("http")) museo.web else "https://${museo.web}"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Burdeos)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Language,
                            contentDescription = null,
                            tint = Crema,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "VISITAR WEB OFICIAL",
                            fontSize = 11.sp,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = CinzelFamily,
                            color = Crema
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

// ── Sección carrusel de obras ─────────────────────────────
@Composable
fun SeccionCarruselObras(obras: List<Obra>) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "OBRAS DESTACADAS",
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold,
                color = TextoSuave
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(obras) { obra ->
                TarjetaObra(obra = obra)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(
            color = DoradoSuave,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}

@Composable
fun TarjetaObra(obra: Obra) {
    Card(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = Crema),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            // Imagen de la obra
            if (obra.imagenUrl.isNotEmpty()) {
                AsyncImage(
                    model = obra.imagenUrl,
                    contentDescription = obra.nombre,
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
                    Text(
                        text = "—",
                        color = Dorado,
                        fontSize = 20.sp,
                        fontFamily = CinzelFamily
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = obra.nombre,
                    fontSize = 13.sp,
                    fontFamily = CinzelFamily,
                    fontWeight = FontWeight.Medium,
                    color = BurdeosOscuro,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp
                )
                if (obra.autor.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = obra.autor,
                        fontSize = 11.sp,
                        color = TextoSuave,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (obra.siglo.isNotEmpty()) {
                    Text(
                        text = obra.siglo,
                        fontSize = 10.sp,
                        color = Dorado,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

// ── Sección exposiciones del museo ────────────────────────
@Composable
fun SeccionExposicionesMuseo(
    exposiciones: List<Exposicion>,
    onExposicionClick: (Exposicion) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "EXPOSICIONES",
            fontSize = 10.sp,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Bold,
            color = TextoSuave
        )

        Spacer(modifier = Modifier.height(12.dp))

        exposiciones.forEach { exposicion ->
            TarjetaExposicionMuseo(
                exposicion = exposicion,
                onClick = { onExposicionClick(exposicion) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun TarjetaExposicionMuseo(exposicion: Exposicion, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = Crema),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exposicion.titulo,
                    fontSize = 15.sp,
                    fontFamily = CinzelFamily,
                    fontWeight = FontWeight.Medium,
                    color = BurdeosOscuro,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${exposicion.fechaInicio}  —  ${exposicion.fechaFin}",
                    fontSize = 11.sp,
                    color = TextoSuave,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Surface(
                shape = RoundedCornerShape(2.dp),
                color = if (exposicion.esPublica) Dorado.copy(alpha = 0.2f)
                else Burdeos.copy(alpha = 0.1f)
            ) {
                Text(
                    text = if (exposicion.esPublica) "LIBRE" else "PAGO",
                    fontSize = 9.sp,
                    letterSpacing = 1.sp,
                    color = if (exposicion.esPublica) TextoOscuro else Burdeos,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}