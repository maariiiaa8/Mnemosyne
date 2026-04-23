package com.mnemosyne.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MnemosyneColorScheme = lightColorScheme(
    primary          = Burdeos,
    onPrimary        = Crema,
    primaryContainer = BurdeosSuave,
    secondary        = Dorado,
    onSecondary      = TextoOscuro,
    background       = Superficie,
    onBackground     = TextoOscuro,
    surface          = Crema,
    onSurface        = TextoOscuro,
    onSurfaceVariant = TextoSuave,
    error            = Color(0xFFB00020),
    outline          = DoradoSuave
)

@Composable
fun MnemosyneTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MnemosyneColorScheme,
        typography  = Typography,
        content     = content
    )
}