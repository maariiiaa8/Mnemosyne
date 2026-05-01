package com.mnemosyne.app.ui.museos

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mnemosyne.app.data.model.Museo
import com.mnemosyne.app.ui.theme.BurdeosOscuro
import com.mnemosyne.app.ui.theme.Burdeos
import com.mnemosyne.app.ui.theme.CinzelFamily
import com.mnemosyne.app.ui.theme.Crema
import com.mnemosyne.app.ui.theme.Dorado
import com.mnemosyne.app.ui.theme.DoradoSuave
import com.mnemosyne.app.ui.theme.Superficie
import com.mnemosyne.app.ui.theme.TextoSuave
import java.util.Locale
import java.util.Locale.getDefault

@Composable
fun DetalleMuseoScreen(
    museo: Museo,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Superficie)
            .statusBarsPadding()
    ) {
        // ── Cabecera ─────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Burdeos)
                .padding(horizontal = 4.dp, vertical = 12.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Crema
                )
            }
            Text(
                text = museo.ciudad.uppercase(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                color = Crema,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // ── Contenido scrollable (un solo verticalScroll) ─
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .navigationBarsPadding()
        ) {
            // Imagen
            if (museo.imagenUrl.isNotEmpty()) {
                AsyncImage(
                    model = museo.imagenUrl,
                    contentDescription = museo.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(DoradoSuave)
                )
            }

            Column(modifier = Modifier.padding(20.dp)) {

//                // Badge destacado
//                if (museo.destacado) {
//                    Surface(
//                        shape = RoundedCornerShape(2.dp),
//                        color = Burdeos.copy(alpha = 0.1f)
//                    ) {
//                        Text(
//                            text = "DESTACADO",
//                            fontSize = 9.sp,
//                            letterSpacing = 1.sp,
//                            color = Burdeos,
//                            fontWeight = FontWeight.Bold,
//                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
//                        )
//                    }
//                    Spacer(modifier = Modifier.height(12.dp))
//                }

                // Nombre
                Text(
                    text = museo.nombre,
                    fontSize = 24.sp,
                    fontFamily = CinzelFamily,
                    fontWeight = FontWeight.Medium,
                    color = BurdeosOscuro,
                    lineHeight = 30.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Ciudad
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = Dorado,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = museo.ciudad,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        color = Dorado,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = DoradoSuave)
                Spacer(modifier = Modifier.height(10.dp))

                // Descripción
                if (museo.descripcion.isNotEmpty()) {
                    Text(
                        text = "SOBRE EL MUSEO",
                        fontSize = 10.sp,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoSuave
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = museo.descripcion,
                        fontSize = 14.sp,
                        color = BurdeosOscuro,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                }

                // Botón web
                if (museo.web.isNotEmpty()) {
                    HorizontalDivider(color = DoradoSuave)
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            val url = if (museo.web.startsWith("http")) museo.web else "https://${museo.web}"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Burdeos)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Language,
                            contentDescription = null,
                            tint = Crema,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "VISITAR WEB OFICIAL",
                            fontSize = 11.sp,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = CinzelFamily,
                            color = Crema
                        )
                    }
                }
            }
        }
    }
}