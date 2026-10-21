
package com.example.arresees.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.example.main.ui.theme.ArreseBeltza
import com.example.main.ui.theme.ArreseGorria
import com.example.main.ui.theme.ArreseZuria

import androidx.compose.material3.lightColorScheme
private val ArreseesColorScheme = darkColorScheme(
    primary = ArreseGorria,
    onPrimary = ArreseZuria,

    secondary = ArreseZuria,
    onSecondary = ArreseBeltza,

    background = ArreseBeltza,
    onBackground = ArreseZuria,

    surface = ArreseBeltza,
    onSurface = ArreseZuria,

    error = ArreseGorria
)


private val ArreseesColorSchemeL = lightColorScheme(
    primary = ArreseGorria,
    onPrimary = ArreseZuria,

    secondary = ArreseZuria,
    onSecondary = ArreseBeltza,

    background = ArreseZuria,
    onBackground = ArreseBeltza,

    surface = ArreseZuria,
    onSurface = ArreseBeltza,

    error = ArreseGorria
)

@Composable
fun ArreseesTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ArreseesColorSchemeL,
        typography = TipografiaArresees,
        content = content
    )
}
