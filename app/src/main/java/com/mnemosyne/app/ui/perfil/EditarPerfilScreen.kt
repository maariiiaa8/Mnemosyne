package com.mnemosyne.app.ui.perfil

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mnemosyne.app.ui.theme.*
import com.mnemosyne.app.utils.FirebaseResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfilScreen(
    onVolver: () -> Unit,
    viewModel: PerfilViewModel = viewModel()
) {
    val perfilState by viewModel.perfil.observeAsState()
    val actualizacion by viewModel.actualizacion.observeAsState()

    val usuario = (perfilState as? FirebaseResult.Success)?.data
    var nombre by remember(usuario) { mutableStateOf(usuario?.nombre ?: "") }
    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    var cargando by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> fotoUri = uri }

    LaunchedEffect(actualizacion) {
        if (actualizacion is FirebaseResult.Success) {
            cargando = false
            onVolver()
        } else if (actualizacion is FirebaseResult.Error) {
            cargando = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "EDITAR PERFIL",
                        fontSize = 13.sp,
                        letterSpacing = 2.sp,
                        color = Crema
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Crema
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Burdeos)
            )
        },
        containerColor = Superficie,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Superficie)
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        cargando = true
                        viewModel.guardarCambios(nombre, fotoUri)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(2.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Burdeos,
                        contentColor = Crema
                    ),
                    enabled = !cargando && nombre.isNotBlank()
                ) {
                    if (cargando) {
                        CircularProgressIndicator(
                            color = Crema,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "GUARDAR CAMBIOS",
                            fontSize = 12.sp,
                            letterSpacing = 3.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // ── Avatar editable ───────────────────────────
            Box(contentAlignment = Alignment.BottomEnd) {
                when {
                    fotoUri != null -> {
                        AsyncImage(
                            model = fotoUri,
                            contentDescription = "Nueva foto",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .clickable { launcher.launch("image/*") },
                            contentScale = ContentScale.Crop
                        )
                    }
                    usuario?.fotoPerfil?.isNotBlank() == true -> {
                        AsyncImage(
                            model = usuario.fotoPerfil,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .clickable { launcher.launch("image/*") },
                            contentScale = ContentScale.Crop
                        )
                    }
                    else -> {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Burdeos)
                                .clickable { launcher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = nombre
                                    .split(" ")
                                    .take(2)
                                    .filter { it.isNotEmpty() }
                                    .joinToString("") { it.first().uppercase() },
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Crema,
                                fontFamily = CinzelFamily
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Dorado)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Cambiar foto",
                        tint = Crema,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = "Toca la foto para cambiarla",
                fontSize = 11.sp,
                color = TextoSuave,
                letterSpacing = 1.sp
            )

            // ── Campo nombre ──────────────────────────────
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = {
                    Text("Nombre completo", color = TextoSuave, fontSize = 13.sp)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(2.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Burdeos,
                    unfocusedBorderColor = DoradoSuave,
                    focusedTextColor = TextoOscuro,
                    unfocusedTextColor = TextoOscuro,
                    cursorColor = Burdeos
                ),
                singleLine = true
            )

            if (actualizacion is FirebaseResult.Error) {
                Text(
                    text = (actualizacion as FirebaseResult.Error).mensaje,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp
                )
            }
        }
    }
}