package com.mnemosyne.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mnemosyne.app.ui.theme.*
import com.mnemosyne.app.utils.FirebaseResult

@Composable
fun RegistroScreen(
    onRegistroSuccess: () -> Unit,
    onIrALogin: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var nombre          by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorLocal      by remember { mutableStateOf<String?>(null) }

    val registroState by viewModel.registroState.observeAsState()

    LaunchedEffect(registroState) {
        if (registroState is FirebaseResult.Success) onRegistroSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Superficie)
    ) {
        // Barras decorativas
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            GreekBorderBar()
            GreekBorderBar()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Cabecera
            Text(
                text = "MNEMOSYNE",
                fontSize = 22.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 6.sp,
                color = Burdeos
            )
            Text(
                text = "✦",
                fontSize = 16.sp,
                color = Dorado,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = "NUEVA CUENTA",
                fontSize = 11.sp,
                letterSpacing = 3.sp,
                color = TextoSuave,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Tarjeta
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
                    Text(
                        "— ✦ —",
                        color = Dorado,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = "REGISTRO",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 5.sp,
                        color = BurdeosOscuro,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Nombre
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre", fontSize = 13.sp) },
                        singleLine = true,
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

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico", fontSize = 13.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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

                    // Contraseña
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña", fontSize = 13.sp) },
                        singleLine = true,
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                    )

                    // Confirmar contraseña
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar contraseña", fontSize = 13.sp) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = confirmPassword.isNotEmpty() && password != confirmPassword,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Dorado,
                            unfocusedBorderColor = DoradoSuave,
                            focusedLabelColor = Burdeos,
                            cursorColor = Burdeos,
                            errorBorderColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Error
                    val errorMensaje = errorLocal
                        ?: (registroState as? FirebaseResult.Error)?.mensaje

                    if (errorMensaje != null) {
                        Text(
                            text = errorMensaje,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            errorLocal = when {
                                nombre.isBlank()            -> "Introduce tu nombre"
                                email.isBlank()             -> "Introduce tu correo"
                                password.length < 6         -> "Mínimo 6 caracteres"
                                password != confirmPassword -> "Las contraseñas no coinciden"
                                else -> null
                            }
                            if (errorLocal == null) {
                                viewModel.registro(nombre.trim(), email.trim(), password.trim())
                            }
                        },
                        enabled = registroState !is FirebaseResult.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(2.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Burdeos,
                            contentColor = Crema
                        )
                    ) {
                        if (registroState is FirebaseResult.Loading) {
                            CircularProgressIndicator(
                                color = Crema,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "CREAR CUENTA",
                                letterSpacing = 3.sp,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            TextButton(
                onClick = onIrALogin,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "¿Ya tienes cuenta?  Inicia sesión",
                    color = TextoSuave,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}