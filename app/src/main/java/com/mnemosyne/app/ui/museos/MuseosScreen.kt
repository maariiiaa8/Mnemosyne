package com.mnemosyne.app.ui.museos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.mnemosyne.app.data.model.Museo
import com.mnemosyne.app.ui.theme.*
import com.mnemosyne.app.utils.FirebaseResult

@Composable
fun MuseosScreen(
    onMuseoClick: (Museo) -> Unit,
    viewModel: MuseosViewModel = viewModel()
) {
    val museosState by viewModel.museos.observeAsState()

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
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "MUSEOS",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                color = Crema
            )
        }

        // ── Contenido ────────────────────────────────────
        when (val state = museosState) {
            is FirebaseResult.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Burdeos)
                }
            }

            is FirebaseResult.Success -> {
                if (state.data.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No hay museos disponibles",
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
                        items(state.data) { museo ->
                            TarjetaMuseo(
                                museo = museo,
                                onClick = { onMuseoClick(museo) }
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
fun TarjetaMuseo(museo: Museo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = Crema),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = museo.ciudad.uppercase(),
                    fontSize = 10.sp,
                    letterSpacing = 2.sp,
                    color = Dorado,
                    fontWeight = FontWeight.Bold
                )
                if (museo.destacado) {
                    Surface(
                        shape = RoundedCornerShape(2.dp),
                        color = Burdeos.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "DESTACADO",
                            fontSize = 9.sp,
                            letterSpacing = 1.sp,
                            color = Burdeos,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = museo.nombre,
                fontSize = 17.sp,
                fontFamily = CinzelFamily,
                fontWeight = FontWeight.Medium,
                color = BurdeosOscuro
            )

            if (museo.descripcion.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = museo.descripcion,
                    fontSize = 13.sp,
                    color = TextoSuave,
                    lineHeight = 18.sp,
                    maxLines = 3
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            HorizontalDivider(color = DoradoSuave)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ver colección →",
                fontSize = 11.sp,
                color = Burdeos,
                fontFamily = CinzelFamily,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}