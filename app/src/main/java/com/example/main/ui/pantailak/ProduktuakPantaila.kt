
package com.example.main.ui.pantailak

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.apparrese.R
import com.example.main.ui.theme.ArreseGorria

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProduktuakPantaila() {

    var menuAbierto by remember { mutableStateOf(false) }

    Scaffold(
        // BARRA SUPERIOR
        topBar = {
            TopAppBar(
                modifier = Modifier.height(110.dp),
                title = {
                    Image(
                        painter = painterResource(R.drawable.logoarrese2),
                        contentDescription = "Logo Arrese",
                        modifier = Modifier.height(70.dp)
                    )
                },
                actions = {
                    Box {
                        IconButton(
                            onClick = { menuAbierto = true }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Abrir menu",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = menuAbierto,
                            onDismissRequest = {
                                menuAbierto = false
                            }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text("Opcion provisional")
                                },
                                onClick = {
                                    menuAbierto = false
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ArreseGorria
                )
            )
        },

        // BARRA INFERIOR
        bottomBar = {
            NavigationBar(
                modifier = Modifier.height(64.dp),
                containerColor = ArreseGorria,
                windowInsets = WindowInsets(0.dp)
            ) {
                // ICONO INICIO
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = {
                        Image(
                            painter = painterResource(R.drawable.home),
                            contentDescription = "Hasiera",
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.White,
                        indicatorColor = ArreseGorria
                    )
                )

                // ICONO PRODUCTOS
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = {
                        Image(
                            painter = painterResource(R.drawable.shoppingcart),
                            contentDescription = "Produktuak",
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.White,
                        indicatorColor = ArreseGorria
                    )
                )

                // ICONO PERFIL
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = {
                        Image(
                            painter = painterResource(R.drawable.perfil),
                            contentDescription = "Profila",
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.White,
                        indicatorColor = ArreseGorria
                    )
                )
            }
        }
    ) { paddingValues ->

        // CONTENIDO PRINCIPAL
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "PRODUKTUAK",
                color = ArreseGorria
            )
        }
    }
}
