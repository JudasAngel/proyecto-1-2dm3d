package com.example.arresees.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.example.main.ui.theme.ArreseBeltza
import com.example.main.ui.theme.ArreseGorria
import com.example.main.ui.theme.ArreseZuria

private val ArreseesColorScheme = darkColorScheme(
    primary = ArreseGorria,
    onPrimary = ArreseZuria,

    secondary = ArreseZuria,
    onSecondary = ArreseBeltza,

    onBackground = ArreseZuria,

    onSurface = ArreseZuria,

    error = ArreseGorria
)

@Composable
fun ArreseesTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ArreseesColorScheme,
        typography = TipografiaArresees,
        content = content
    )
}