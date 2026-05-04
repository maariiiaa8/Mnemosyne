package com.mnemosyne.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mnemosyne.app.data.model.Exposicion
import com.mnemosyne.app.data.model.Museo
import com.mnemosyne.app.ui.auth.AuthViewModel
import com.mnemosyne.app.ui.auth.LoginScreen
import com.mnemosyne.app.ui.auth.RegistroScreen
import com.mnemosyne.app.ui.auth.SplashScreen
import com.mnemosyne.app.ui.carrito.CarritoScreen
import com.mnemosyne.app.ui.carrito.CarritoViewModel
import com.mnemosyne.app.ui.compra.CompraViewModel
import com.mnemosyne.app.ui.exposiciones.DetalleExposicionScreen
import com.mnemosyne.app.ui.exposiciones.ExposicionesScreen
import com.mnemosyne.app.ui.home.HomeScreen
import com.mnemosyne.app.ui.museos.DetalleMuseoScreen
import com.mnemosyne.app.ui.museos.MuseosScreen
import com.mnemosyne.app.ui.perfil.EditarPerfilScreen
import com.mnemosyne.app.ui.perfil.MisEntradasScreen
import com.mnemosyne.app.ui.perfil.PerfilScreen
import com.mnemosyne.app.ui.theme.*
import com.mnemosyne.app.utils.FirebaseResult
import com.stripe.android.PaymentConfiguration
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        PaymentConfiguration.init(
            applicationContext,
            "pk_test_51TSIu6FTCAamIlnbAX5xBLrMT5shD1Vfzsi9oTkSlfC2eqwz54PpwhyFEP1p721G8w3bs4lTo20FnGTFAuO6qy4x00092aJPSP"
        )
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MnemosyneTheme {
                val navController = rememberNavController()
                val exposicionSeleccionada = remember { mutableStateOf<Exposicion?>(null) }
                val museoSeleccionado = remember { mutableStateOf<Museo?>(null) }
                val authViewModel: AuthViewModel = viewModel()
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val rutasSinDrawer = listOf("splash", "login", "registro")
                val backStackEntry by navController.currentBackStackEntryAsState()
                val rutaActual = backStackEntry?.destination?.route ?: ""
                val mostrarDrawer = rutaActual !in rutasSinDrawer

                if (mostrarDrawer) {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet(drawerContainerColor = Crema) {
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
                                    label = { Text("Inicio", letterSpacing = 2.sp, fontSize = 13.sp) },
                                    selected = rutaActual == "home",
                                    onClick = {
                                        scope.launch { drawerState.close() }
                                        navController.navigate("home") { popUpTo("home") { inclusive = true } }
                                    },
                                    colors = NavigationDrawerItemDefaults.colors(
                                        selectedContainerColor = CremaOscura,
                                        selectedTextColor = BurdeosOscuro,
                                        unselectedTextColor = TextoOscuro
                                    ),
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )

                                NavigationDrawerItem(
                                    label = { Text("Exposiciones", letterSpacing = 2.sp, fontSize = 13.sp) },
                                    selected = rutaActual == "exposiciones",
                                    onClick = {
                                        scope.launch { drawerState.close() }
                                        navController.navigate("exposiciones")
                                    },
                                    colors = NavigationDrawerItemDefaults.colors(
                                        selectedContainerColor = CremaOscura,
                                        selectedTextColor = BurdeosOscuro,
                                        unselectedTextColor = TextoOscuro
                                    ),
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )

                                NavigationDrawerItem(
                                    label = { Text("Museos", letterSpacing = 2.sp, fontSize = 13.sp) },
                                    selected = rutaActual == "museos",
                                    onClick = {
                                        scope.launch { drawerState.close() }
                                        navController.navigate("museos")
                                    },
                                    colors = NavigationDrawerItemDefaults.colors(
                                        selectedContainerColor = CremaOscura,
                                        selectedTextColor = BurdeosOscuro,
                                        unselectedTextColor = TextoOscuro
                                    ),
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                NavigationDrawerItem(
                                    label = { Text("Mi carrito", letterSpacing = 2.sp, fontSize = 13.sp) },
                                    selected = rutaActual == "carrito",
                                    onClick = {
                                        scope.launch { drawerState.close() }
                                        navController.navigate("carrito")
                                    },
                                    colors = NavigationDrawerItemDefaults.colors(
                                        selectedContainerColor = CremaOscura,
                                        selectedTextColor = BurdeosOscuro,
                                        unselectedTextColor = TextoOscuro
                                    ),
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )

                                HorizontalDivider(
                                    color = DoradoSuave,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                NavigationDrawerItem(
                                    label = { Text("Mi perfil", letterSpacing = 2.sp, fontSize = 13.sp) },
                                    selected = rutaActual == "perfil",
                                    onClick = {
                                        scope.launch { drawerState.close() }
                                        navController.navigate("perfil")
                                    },
                                    colors = NavigationDrawerItemDefaults.colors(
                                        selectedContainerColor = CremaOscura,
                                        selectedTextColor = BurdeosOscuro,
                                        unselectedTextColor = TextoOscuro
                                    ),
                                    modifier = Modifier.padding(horizontal = 12.dp)
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
                                    onClick = {
                                        authViewModel.cerrarSesion()
                                        navController.navigate("login") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    },
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    ) {
                        AppNavHost(
                            navController = navController,
                            exposicionSeleccionada = exposicionSeleccionada,
                            museoSeleccionado = museoSeleccionado,
                            authViewModel = authViewModel,
                            onAbrirMenu = { scope.launch { drawerState.open() } }
                        )
                    }
                } else {
                    AppNavHost(
                        navController = navController,
                        exposicionSeleccionada = exposicionSeleccionada,
                        museoSeleccionado = museoSeleccionado,
                        authViewModel = authViewModel,
                        onAbrirMenu = {}
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavHost(
    navController: androidx.navigation.NavHostController,
    exposicionSeleccionada: androidx.compose.runtime.MutableState<Exposicion?>,
    museoSeleccionado: androidx.compose.runtime.MutableState<Museo?>,
    authViewModel: AuthViewModel,
    onAbrirMenu: () -> Unit
) {
    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onIrARegistro = { navController.navigate("registro") }
            )
        }

        composable("registro") {
            RegistroScreen(
                onRegistroSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onIrALogin = { navController.popBackStack() }
            )
        }

        composable("home") {
            HomeScreen(
                onCerrarSesion = {
                    authViewModel.cerrarSesion()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onIrAExposiciones = { navController.navigate("exposiciones") },
                onIrACarrito = { navController.navigate("carrito") },
                onIrANoticia = { navController.navigate("noticias") },
                onIrAMuseos = { navController.navigate("museos") },
                onIrAPerfil = { navController.navigate("perfil") },
                onAbrirMenu = onAbrirMenu
            )
        }

        composable("museos") {
            MuseosScreen(
                onMuseoClick = { museo ->
                    museoSeleccionado.value = museo
                    navController.navigate("detalle_museo")
                }
            )
        }

        composable("detalle_museo") {
            museoSeleccionado.value?.let { museo ->
                DetalleMuseoScreen(
                    museo = museo,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable("exposiciones") {
            ExposicionesScreen(
                onExposicionClick = { exposicion ->
                    exposicionSeleccionada.value = exposicion
                    navController.navigate("detalle_exposicion")
                }
            )
        }

        composable("detalle_exposicion") {
            exposicionSeleccionada.value?.let { exposicion ->
                DetalleExposicionScreen(
                    exposicion = exposicion,
                    onVolver = { navController.popBackStack() },
                    onIrCarrito = { navController.navigate("carrito") }
                )
            }
        }

        composable("carrito") {
            val carritoViewModel: CarritoViewModel = viewModel()
            val compraViewModel: CompraViewModel = viewModel()
            val itemsState by carritoViewModel.items.observeAsState()
            val items = (itemsState as? FirebaseResult.Success)?.data ?: emptyList()
            val total = carritoViewModel.calcularTotal(items)

            CarritoScreen(
                onPagar = { compraViewModel.iniciarPago(total, items) },
                compraViewModel = compraViewModel,
                onPagoCompletado = {
                    compraViewModel.guardarPedidoTrasCompra(total)
                    navController.navigate("mis_entradas") {
                        popUpTo("carrito") { inclusive = true }
                    }
                },
                viewModel = carritoViewModel
            )
        }

        composable("perfil") {
            PerfilScreen(
                onEditarPerfil = { navController.navigate("editar_perfil") },
                onMisEntradas = { navController.navigate("mis_entradas") },
                onMisCompras = { },
                onMisFavoritos = { },
                onCambiarPassword = { },
                onCerrarSesion = {
                    authViewModel.cerrarSesion()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("mis_entradas") {
            MisEntradasScreen(
                onVolver = { navController.popBackStack() },
                onIrAExposiciones = { navController.navigate("exposiciones") }
            )
        }

        composable("editar_perfil") {
            EditarPerfilScreen(
                onVolver = { navController.popBackStack() }
            )
        }
    }
}