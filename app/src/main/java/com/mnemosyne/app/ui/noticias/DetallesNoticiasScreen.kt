package com.mnemosyne.app.ui.noticias

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
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Museum
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mnemosyne.app.data.model.Noticia
import com.mnemosyne.app.ui.theme.BurdeosOscuro
import com.mnemosyne.app.ui.theme.Burdeos
import com.mnemosyne.app.ui.theme.CinzelFamily
import com.mnemosyne.app.ui.theme.Crema
import com.mnemosyne.app.ui.theme.Dorado
import com.mnemosyne.app.ui.theme.DoradoSuave
import com.mnemosyne.app.ui.theme.Superficie
import com.mnemosyne.app.ui.theme.TextoSuave

@Composable
fun DetallesNoticiasScreen(
    noticia: Noticia,
    onVolver: () -> Unit
) {
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
                onClick = onVolver,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Crema
                )
            }
            Text(
                text = "NOTICIAS",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                color = Crema,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // ── Contenido scrollable ──────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .navigationBarsPadding()
        ) {
            // Imagen
            if (noticia.imagenUrl.isNotEmpty()) {
                AsyncImage(
                    model = noticia.imagenUrl,
                    contentDescription = noticia.titulo,
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

                // Badge destacada
                if (noticia.destacada) {
                    Surface(
                        shape = RoundedCornerShape(2.dp),
                        color = Burdeos.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "DESTACADA",
                            fontSize = 9.sp,
                            letterSpacing = 1.sp,
                            color = Burdeos,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Título
                Text(
                    text = noticia.titulo,
                    fontSize = 22.sp,
                    fontFamily = CinzelFamily,
                    fontWeight = FontWeight.Medium,
                    color = BurdeosOscuro,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Museo
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Museum,
                        contentDescription = null,
                        tint = Dorado,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = noticia.museoNombre.uppercase(),
                        fontSize = 10.sp,
                        letterSpacing = 2.sp,
                        color = Dorado,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Fecha
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = TextoSuave,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = noticia.fecha,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        color = TextoSuave
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = DoradoSuave)
                Spacer(modifier = Modifier.height(20.dp))

                // Contenido
                if (noticia.contenido.isNotEmpty()) {
                    Text(
                        text = noticia.contenido,
                        fontSize = 14.sp,
                        color = BurdeosOscuro,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}