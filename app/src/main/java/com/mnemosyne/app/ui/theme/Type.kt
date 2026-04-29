package com.mnemosyne.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mnemosyne.app.R

val CinzelFamily = FontFamily(
    Font(R.font.cinzel_regular, FontWeight.Normal),
    Font(R.font.cinzel_regular, FontWeight.Bold)
)

val CormorantFamily = FontFamily(
    Font(R.font.cormorant_regular, FontWeight.Normal),
    Font(R.font.cormorant_regular, FontWeight.Medium),
    Font(R.font.cormorant_regular, FontWeight.Bold)
)
val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = CinzelFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        letterSpacing = 4.sp
    ),
    titleLarge = TextStyle(
        fontFamily = CinzelFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        letterSpacing = 3.sp
    ),
    labelLarge = TextStyle(
        fontFamily = CinzelFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        letterSpacing = 2.sp
    ),

    bodyLarge = TextStyle(
        fontFamily = CormorantFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.3.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = CormorantFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.3.sp
    ),
    bodySmall = TextStyle(
        fontFamily = CormorantFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.3.sp
    )
)