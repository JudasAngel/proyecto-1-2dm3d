package com.example.main.ui.pantailak

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.apparrese.R
import com.example.main.ui.theme.ArreseGorria

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArreseTopBar(
    altuera: Dp = 90.dp,
    logoAltuera: Dp = 58.dp,
    ekintzak: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        modifier = Modifier.height(altuera),
        title = {
            Image(
                painter = painterResource(R.drawable.logoarrese2),
                contentDescription = "Arrese logoa",
                modifier = Modifier.height(logoAltuera)
            )
        },
        actions = ekintzak,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = ArreseGorria)
    )
}