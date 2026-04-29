package com.mnemosyne.app.ui.home

import androidx.compose.foundation.background
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
import com.mnemosyne.app.ui.theme.*
import com.mnemosyne.app.utils.FirebaseResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCerrarSesion: () -> Unit,
    onIrAExposiciones: () -> Unit,
    onIrANoticia: () -> Unit,
    onIrACarrito: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val exposicionesState by viewModel.exposiciones.observeAsState()
    val noticiasState     by viewModel.noticias.observeAsState()
    val drawerState       = rememberDrawerState(DrawerValue.Closed)
    val scope             = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Crema
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "MNEMOSYNE",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    color = Burdeos,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )

                Text(
                    text = "✦",
                    color = Dorado,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )

                HorizontalDivider(
                    color = DoradoSuave,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                NavigationDrawerItem(
                    label = {
                        Text(
                            "Inicio",
                            letterSpacing = 2.sp,
                            fontSize = 13.sp
                        )
                    },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = CremaOscura,
                        selectedTextColor = BurdeosOscuro
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    label = {
                        Text("Exposiciones", letterSpacing = 2.sp, fontSize = 13.sp)
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onIrAExposiciones()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedTextColor = TextoOscuro
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    label = {
                        Text(
                            "Noticias",
                            letterSpacing = 2.sp,
                            fontSize = 13.sp
                        )
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onIrANoticia()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedTextColor = TextoOscuro
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                NavigationDrawerItem(
                    label = {
                        Text("Mi carrito", letterSpacing = 2.sp, fontSize = 13.sp)
                    },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onIrACarrito()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedTextColor = TextoOscuro
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                HorizontalDivider(
                    color = DoradoSuave,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                NavigationDrawerItem(
                    label = {
                        Text(
                            "Cerrar sesión",
                            letterSpacing = 2.sp,
                            fontSize = 13.sp,
                            color = Burdeos
                        )
                    },
                    selected = false,
                    onClick = { onCerrarSesion() },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    ) {
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
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // ── Exposiciones destacadas ──────────────────
                item {
                    SeccionTitulo("EXPOSICIONES DESTACADAS")
                }

                when (val state = exposicionesState) {
                    is FirebaseResult.Loading -> item {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Burdeos)
                        }
                    }
                    is FirebaseResult.Success -> items(state.data) { exposicion ->
                        TarjetaExposicion(exposicion)
                    }
                    is FirebaseResult.Error -> item {
                        Text(state.mensaje, color = MaterialTheme.colorScheme.error)
                    }
                    null -> {}
                }

                // ── Noticias destacadas ──────────────────────
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    SeccionTitulo("ÚLTIMAS NOTICIAS")
                }

                when (val state = noticiasState) {
                    is FirebaseResult.Loading -> item {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Burdeos)
                        }
                    }
                    is FirebaseResult.Success -> items(state.data) { noticia ->
                        TarjetaNoticia(noticia)
                    }
                    is FirebaseResult.Error -> item {
                        Text(state.mensaje, color = MaterialTheme.colorScheme.error)
                    }
                    null -> {}
                }
            }
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
        colors = CardDefaults.cardColors(containerColor = Crema),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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