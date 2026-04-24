package com.mnemosyne.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mnemosyne.app.data.model.Exposicion
import com.mnemosyne.app.ui.auth.AuthViewModel
import com.mnemosyne.app.ui.auth.LoginScreen
import com.mnemosyne.app.ui.auth.RegistroScreen
import com.mnemosyne.app.ui.auth.SplashScreen
import com.mnemosyne.app.ui.carrito.CarritoScreen
import com.mnemosyne.app.ui.exposiciones.DetalleExposicionScreen
import com.mnemosyne.app.ui.exposiciones.ExposicionesScreen
import com.mnemosyne.app.ui.home.HomeScreen
import com.mnemosyne.app.ui.theme.MnemosyneTheme
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MnemosyneTheme {
                val navController = rememberNavController()

                // Exposición seleccionada compartida entre pantallas
                var exposicionSeleccionada = remember { androidx.compose.runtime.mutableStateOf<Exposicion?>(null) }

                NavHost(
                    navController = navController,
                    startDestination = "splash"
                ) {
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
                            onIrARegistro = {
                                navController.navigate("registro")
                            }
                        )
                    }

                    composable("registro") {
                        RegistroScreen(
                            onRegistroSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onIrALogin = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable("home") {
                        val authViewModel: AuthViewModel = viewModel()
                        HomeScreen(
                            onCerrarSesion = {
                                authViewModel.cerrarSesion()
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onIrAExposiciones = {
                                navController.navigate("exposiciones")
                            },
                            onIrACarrito = {
                                navController.navigate("carrito")
                            }
                        )
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
                        CarritoScreen(
                            onPagar = {
                                // Pago — lo implementamos después
                            }
                        )
                    }
                }
            }
        }
    }
}