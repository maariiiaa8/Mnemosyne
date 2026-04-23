package com.mnemosyne.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mnemosyne.app.ui.auth.LoginScreen
import com.mnemosyne.app.ui.auth.RegistroScreen
import com.mnemosyne.app.ui.auth.SplashScreen
import com.mnemosyne.app.ui.home.HomeScreen
import com.mnemosyne.app.ui.theme.MnemosyneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MnemosyneTheme {
                val navController = rememberNavController()
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
                        HomeScreen(
                            onCerrarSesion = {
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}