package com.mnemosyne.app.ui.CuriosidadDelDia

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mnemosyne.app.ui.curiosidadDelDia.CuriosidadDelDiaViewModel
import com.mnemosyne.app.ui.theme.Burdeos
import com.mnemosyne.app.ui.theme.BurdeosOscuro
import com.mnemosyne.app.ui.theme.CinzelFamily
import com.mnemosyne.app.ui.theme.Crema
import com.mnemosyne.app.ui.theme.Dorado
import com.mnemosyne.app.ui.theme.DoradoSuave
import com.mnemosyne.app.ui.theme.TextoSuave
import com.mnemosyne.app.utils.FirebaseResult

@Composable
fun CuriosidadDelDiaBoton(
    modifier: Modifier = Modifier,
    viewModel: CuriosidadDelDiaViewModel = viewModel()
) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    FloatingActionButton(
        onClick = { mostrarDialogo = true },
        containerColor = Burdeos,
        contentColor = Crema,
        shape = CircleShape,
        modifier = modifier.size(52.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.AutoAwesome,
            contentDescription = "Dato curioso del día",
            tint = Crema,
            modifier = Modifier.size(22.dp)
        )
    }

    if (mostrarDialogo) {
        val curiosidadState by viewModel.curiosidad.observeAsState()

        Dialog(onDismissRequest = { mostrarDialogo = false }) {
            Card(
                shape = RoundedCornerShape(4.dp),
                colors = CardDefaults.cardColors(containerColor = Crema),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "✦",
                                color = Dorado,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DATO CURIOSO DEL DÍA",
                                fontSize = 11.sp,
                                fontFamily = CinzelFamily,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                color = Burdeos
                            )
                        }
                        IconButton(
                            onClick = { mostrarDialogo = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Cerrar",
                                tint = TextoSuave,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(DoradoSuave)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    when (val state = curiosidadState) {
                        is FirebaseResult.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(80.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Burdeos, strokeWidth = 2.dp)
                            }
                        }
                        is FirebaseResult.Success -> {
                            Text(
                                text = state.data,
                                fontSize = 14.sp,
                                color = BurdeosOscuro,
                                lineHeight = 22.sp,
                                textAlign = TextAlign.Start
                            )
                        }
                        is FirebaseResult.Error -> {
                            Text(
                                text = "No se pudo cargar el dato de hoy.",
                                fontSize = 13.sp,
                                color = TextoSuave,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        null -> {}
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}