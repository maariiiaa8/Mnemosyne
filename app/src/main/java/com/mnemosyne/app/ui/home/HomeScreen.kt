package com.mnemosyne.app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import com.mnemosyne.app.data.model.Noticia
import com.mnemosyne.app.ui.CuriosidadDelDia.CuriosidadDelDiaBoton
import com.mnemosyne.app.ui.theme.*
import com.mnemosyne.app.utils.FirebaseResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCerrarSesion: () -> Unit,
    onIrAExposiciones: () -> Unit,
    onIrANoticia: () -> Unit,
    onIrACarrito: () -> Unit,
    onIrAMuseos: () -> Unit,
    onIrAPerfil: () -> Unit,
    onAbrirMenu: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val exposicionesState by viewModel.exposiciones.observeAsState()
    val noticiasState by viewModel.noticias.observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "INICIO",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light,
                        letterSpacing = 4.sp,
                        color = Crema
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onAbrirMenu) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menú",
                            tint = Crema
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Burdeos
                )
            )
        },
        containerColor = Superficie
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {

                item {
                    SeccionTitulo("EXPOSICIONES DESTACADAS")
                }

                when (val state = exposicionesState) {

                    is FirebaseResult.Loading -> item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Burdeos)
                        }
                    }

                    is FirebaseResult.Success -> {
                        items(state.data) { exposicion ->
                            TarjetaExposicion(exposicion)
                        }
                    }

                    is FirebaseResult.Error -> item {
                        Text(
                            text = state.mensaje,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    null -> {}
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    SeccionTitulo("ÚLTIMAS NOTICIAS")
                }

                when (val state = noticiasState) {

                    is FirebaseResult.Loading -> item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Burdeos)
                        }
                    }

                    is FirebaseResult.Success -> {
                        items(state.data) { noticia ->
                            TarjetaNoticia(noticia)
                        }
                    }

                    is FirebaseResult.Error -> item {
                        Text(
                            text = state.mensaje,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    null -> {}
                }
            }

            CuriosidadDelDiaBoton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .navigationBarsPadding()
            )
        }
    }
}

@Composable
fun SeccionTitulo(texto: String) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = DoradoSuave
        )

        Text(
            text = "  $texto  ",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = TextoSuave
        )

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = DoradoSuave
        )
    }
}

@Composable
fun TarjetaExposicion(exposicion: Exposicion) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Crema
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = exposicion.museoNombre.uppercase(),
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                color = Dorado,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = exposicion.titulo,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = BurdeosOscuro
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${exposicion.fechaInicio}  —  ${exposicion.fechaFin}",
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color = TextoSuave
            )
        }
    }
}

@Composable
fun TarjetaNoticia(noticia: Noticia) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Crema
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

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