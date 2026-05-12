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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mnemosyne.app.data.model.Exposicion
import com.mnemosyne.app.data.model.ItemCarrito
import com.mnemosyne.app.data.model.Museo
import com.mnemosyne.app.data.model.Noticia
import com.mnemosyne.app.data.model.Stock
import com.mnemosyne.app.ui.admin.AdminExposicionesScreen
import com.mnemosyne.app.ui.admin.AdminNoticiasScreen
import com.mnemosyne.app.ui.admin.AdminObrasScreen
import com.mnemosyne.app.ui.admin.AdminScreen
import com.mnemosyne.app.ui.admin.AdminStockScreen
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
import com.mnemosyne.app.ui.noticias.DetallesNoticiasScreen
import com.mnemosyne.app.ui.noticias.NoticiasScreen
import com.mnemosyne.app.ui.perfil.EditarPerfilScreen
import com.mnemosyne.app.ui.perfil.MisEntradasScreen
import com.mnemosyne.app.ui.perfil.PerfilScreen
import com.mnemosyne.app.ui.theme.*
import com.mnemosyne.app.ui.tienda.DetalleTiendaScreen
import com.mnemosyne.app.ui.tienda.StockViewModel
import com.mnemosyne.app.ui.tienda.TiendaScreen
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
                val noticiaSeleccionada = remember { mutableStateOf<Noticia?>(null) }
                val productoSeleccionado = remember { mutableStateOf<Stock?>(null) }
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
                                    label = { Text("Tienda", letterSpacing = 2.sp, fontSize = 13.sp) },
                                    selected = rutaActual == "tienda",
                                    onClick = {
                                        scope.launch { drawerState.close() }
                                        navController.navigate("tienda")
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

                                NavigationDrawerItem(
                                    label = { Text("Noticias", letterSpacing = 2.sp, fontSize = 13.sp) },
                                    selected = rutaActual == "noticias",
                                    onClick = {
                                        scope.launch { drawerState.close() }
                                        navController.navigate("noticias")
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
                            noticiaSeleccionada = noticiaSeleccionada,
                            productoSeleccionado = productoSeleccionado,
                            authViewModel = authViewModel,
                            onAbrirMenu = { scope.launch { drawerState.open() } }
                        )
                    }
                } else {
                    AppNavHost(
                        navController = navController,
                        exposicionSeleccionada = exposicionSeleccionada,
                        museoSeleccionado = museoSeleccionado,
                        noticiaSeleccionada = noticiaSeleccionada,
                        productoSeleccionado = productoSeleccionado,
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
    navController: NavHostController,
    exposicionSeleccionada: MutableState<Exposicion?>,
    museoSeleccionado: MutableState<Museo?>,
    noticiaSeleccionada: MutableState<Noticia?>,
    productoSeleccionado: MutableState<Stock?>,
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
                onIrACarrito      = { navController.navigate("carrito") },
                onIrANoticia      = { navController.navigate("noticias") },
                onIrAMuseos       = { navController.navigate("museos") },
                onIrAPerfil       = { navController.navigate("perfil") },
                onAbrirMenu       = onAbrirMenu
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
                    onBack = { navController.popBackStack() },
                    onExposicionClick = { exposicion ->
                        exposicionSeleccionada.value = exposicion
                        navController.navigate("detalle_exposicion")
                    }
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
                    onVolver   = { navController.popBackStack() },
                    onIrCarrito = { navController.navigate("carrito") }
                )
            }
        }

        composable("noticias") {
            NoticiasScreen(
                onNoticiaClick = { noticia ->
                    noticiaSeleccionada.value = noticia
                    navController.navigate("detalle_noticia")
                }
            )
        }

        composable("detalle_noticia") {
            noticiaSeleccionada.value?.let { noticia ->
                DetallesNoticiasScreen(
                    noticia  = noticia,
                    onVolver = { navController.popBackStack() }
                )
            }
        }

        composable("tienda") {
            TiendaScreen(
                onProductoClick = { producto ->
                    productoSeleccionado.value = producto
                    navController.navigate("detalle_tienda")
                }
            )
        }

        composable("detalle_tienda") {
            productoSeleccionado.value?.let { producto ->
                val carritoViewModel: CarritoViewModel = viewModel()
                DetalleTiendaScreen(
                    producto         = producto,
                    onVolver         = { navController.popBackStack() },
                    onIrCarrito      = { navController.navigate("carrito") },
                    carritoViewModel = carritoViewModel,
                    onAnadirAlCarrito = { stock ->
                        carritoViewModel.añadirItem(
                            ItemCarrito(
                                exposicionId      = stock.id,
                                exposicionTitulo  = stock.nombreProducto,
                                museoId           = stock.museoId,
                                productoId        = stock.id,
                                museoNombre       = "",
                                tipoEntradaNombre = "Producto tienda",
                                precio            = stock.precio,
                                cantidad          = 1,
                                categoria         = "merch"
                            )
                        )
                    }
                )
            }
        }

        composable("carrito") {
            val carritoViewModel: CarritoViewModel = viewModel()
            val compraViewModel: CompraViewModel = viewModel()
            val stockViewModel: StockViewModel = viewModel()
            val itemsState by carritoViewModel.items.observeAsState()
            val items = (itemsState as? FirebaseResult.Success)?.data ?: emptyList()
            val total = carritoViewModel.calcularTotal(items)

            CarritoScreen(
                onPagar         = { compraViewModel.iniciarPago(total, items) },
                compraViewModel = compraViewModel,
                stockViewModel  = stockViewModel,
                onPagoCompletado = {
                    stockViewModel.reducirStockTrasCompra(items)
                    compraViewModel.guardarPedidoTrasCompra(total)
                    navController.navigate("perfil") {
                        popUpTo("carrito") { inclusive = true }
                    }
                },
                viewModel = carritoViewModel
            )
        }

        composable("perfil") {
            PerfilScreen(
                onEditarPerfil    = { navController.navigate("editar_perfil") },
                onMisEntradas     = { navController.navigate("mis_entradas") },
                onMisCompras      = { navController.navigate("mis_compras") },
                onCambiarPassword = { },
                onPanelAdmin = {
                    navController.navigate("admin")
                },
                onCerrarSesion    = {
                    authViewModel.cerrarSesion()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("mis_entradas") {
            MisEntradasScreen(
                onVolver          = { navController.popBackStack() },
                onIrAExposiciones = { navController.navigate("exposiciones") },
                soloMerch         = false
            )
        }

        composable("mis_compras") {
            MisEntradasScreen(
                onVolver          = { navController.popBackStack() },
                onIrAExposiciones = { navController.navigate("tienda") },
                soloMerch         = true
            )
        }

        composable("editar_perfil") {
            EditarPerfilScreen(
                onVolver = { navController.popBackStack() }
            )
        }

        composable("admin") {
            AdminScreen(
                onVolver = { navController.popBackStack() },
                onGestionarExposiciones = { navController.navigate("admin_exposiciones") },
                onGestionarNoticias = { navController.navigate("admin_noticias") },
                onGestionarObras = { navController.navigate("admin_obras") },
                onGestionarStock = { navController.navigate("admin_stock") }
            )
        }
        composable("admin_exposiciones") {
            AdminExposicionesScreen(onVolver = { navController.popBackStack() })
        }
        composable("admin_noticias") {
            AdminNoticiasScreen(onVolver = { navController.popBackStack() })
        }
        composable("admin_obras") {
            AdminObrasScreen(onVolver = { navController.popBackStack() })
        }
        composable("admin_stock") {
            AdminStockScreen(onVolver = { navController.popBackStack() })
        }
    }
}