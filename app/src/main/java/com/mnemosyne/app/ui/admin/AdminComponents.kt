package com.mnemosyne.app.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mnemosyne.app.ui.theme.*

@Composable
fun AdminItemCard(
    titulo: String,
    subtitulo: String,
    badge: String? = null,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(containerColor = Crema),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = BurdeosOscuro
                )
                if (subtitulo.isNotBlank()) {
                    Text(text = subtitulo, fontSize = 12.sp, color = TextoSuave)
                }
                if (badge != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(2.dp),
                        color = Dorado.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 9.sp,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = BurdeosOscuro,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            IconButton(onClick = onEditar) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Burdeos)
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = TextoSuave)
            }
        }
    }
}

@Composable
fun AdminTextField(
    label: String,
    value: String,
    maxLines: Int = 1,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp, color = TextoSuave) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(2.dp),
        maxLines = maxLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Burdeos,
            unfocusedBorderColor = DoradoSuave,
            focusedTextColor = TextoOscuro,
            unfocusedTextColor = TextoOscuro,
            cursorColor = Burdeos
        )
    )
}

@Composable
fun ConfirmarEliminarDialog(
    nombre: String,
    onConfirmar: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Crema,
        title = {
            Text("Eliminar", fontFamily = CinzelFamily, color = BurdeosOscuro)
        },
        text = {
            Text(
                "¿Seguro que quieres eliminar \"$nombre\"? Esta acción no se puede deshacer.",
                color = TextoOscuro, fontSize = 14.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmar,
                colors = ButtonDefaults.buttonColors(containerColor = Burdeos)
            ) { Text("Eliminar", color = Crema) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextoSuave) }
        }
    )
}