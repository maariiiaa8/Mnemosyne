package com.mnemosyne.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mnemosyne.app.R
import com.mnemosyne.app.ui.theme.*
import com.mnemosyne.app.utils.FirebaseResult


@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onIrARegistro: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val loginState      by viewModel.loginState.observeAsState()

    LaunchedEffect(Unit) {
        if (viewModel.hayUsuarioLogueado()) onLoginSuccess()
    }
    LaunchedEffect(loginState) {
        if (loginState is FirebaseResult.Success) onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Superficie),
        contentAlignment = Alignment.Center
    ) {
        // ── Líneas decorativas griegas arriba y abajo ──
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            GreekBorderBar()
            GreekBorderBar()
        }

        // ── Contenido principal ────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tarjeta de acceso
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(2.dp),
                colors = CardDefaults.cardColors(containerColor = Crema),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Ornamento superior
                    Text("— ✦ —", color = Dorado, fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 12.dp))

                    Text(
                        text = "inicio de sesión",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp,
                        color = BurdeosOscuro,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico", fontSize = 13.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            capitalization = KeyboardCapitalization.None
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Dorado,
                            unfocusedBorderColor = DoradoSuave,
                            focusedLabelColor = Burdeos,
                            cursorColor = Burdeos
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña", fontSize = 13.sp) },
                        singleLine = true,
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            capitalization = KeyboardCapitalization.None
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Dorado,
                            unfocusedBorderColor = DoradoSuave,
                            focusedLabelColor = Burdeos,
                            cursorColor = Burdeos
                        ),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = TextoSuave
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (loginState is FirebaseResult.Error) {
                        Text(
                            text = (loginState as FirebaseResult.Error).mensaje,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.login(email.trim(), password.trim()) },
                        enabled = loginState !is FirebaseResult.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(2.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Burdeos,
                            contentColor = Crema
                        )
                    ) {
                        if (loginState is FirebaseResult.Loading) {
                            CircularProgressIndicator(
                                color = Crema,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "ENTRAR",
                                letterSpacing = 4.sp,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            TextButton(
                onClick = onIrARegistro,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "¿No tienes cuenta?  Regístrate",
                    color = TextoSuave,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun GreekBorderBar() {
    Text(
        text = "",
        color = Dorado.copy(alpha = 0.6f),
        fontSize = 10.sp,
        letterSpacing = 2.sp,
        modifier = Modifier
            .fillMaxWidth()
            .background(BurdeosOscuro)
            .padding(vertical = 6.dp, horizontal = 16.dp)
    )
}