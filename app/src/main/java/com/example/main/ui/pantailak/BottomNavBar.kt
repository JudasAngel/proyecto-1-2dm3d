package com.example.main.ui.pantailak

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.main.ui.data.produktuZerrenda
import com.example.main.ui.theme.ArreseGorria

private data class ItemNav(
    val ruta: String?,
    val icono: ImageVector,
    val descripcion: String
)

@Composable
fun BottomNavBar(
    navController: NavHostController,
    onSalir: () -> Unit
) {

    val navBackStackEntry by
    navController.currentBackStackEntryAsState()

    val rutaActual = navBackStackEntry?.destination?.route

    val items = listOf(
        ItemNav(
            ruta = "home",
            icono = Icons.Default.Home,
            descripcion = "Home"
        ),
        ItemNav(
            ruta = "carrito",
            icono = Icons.Default.ShoppingCart,
            descripcion = "Carrito"
        ),
        ItemNav(
            ruta = "crud",
            icono = Icons.Default.Build,
            descripcion = "CRUD"
        ),
        ItemNav(
            ruta = null,
            icono = Icons.Default.Logout,
            descripcion = "Salir"
        )
    )

    NavigationBar(
        containerColor = ArreseGorria
    ) {

        items.forEach { item ->

            NavigationBarItem(
                selected = item.ruta != null &&
                        rutaActual == item.ruta,

                onClick = {

                    if (item.ruta == null) {

                        // El botón Salir no navega a otra pantalla:
                        // ejecuta la acción de cierre de sesión.
                        onSalir()

                    } else if (rutaActual != item.ruta) {

                        navController.navigate(item.ruta) {
                            popUpTo(
                                navController.graph.findStartDestination().id
                            ) {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },

                icon = {
                    if (item.ruta == "carrito" && produktuZerrenda.isNotEmpty()) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = Color.White,
                                    contentColor = ArreseGorria
                                ) {
                                    Text(
                                        text = "${produktuZerrenda.size}",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = item.icono,
                                contentDescription = item.descripcion
                            )
                        }
                    } else {
                        Icon(
                            imageVector = item.icono,
                            contentDescription = item.descripcion
                        )
                    }
                },

                label = {
                    Text(text = item.descripcion)
                },

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color.White,
                    indicatorColor = ArreseGorria
                )
            )
        }
    }
}