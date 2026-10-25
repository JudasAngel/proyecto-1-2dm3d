package com.example.apparrese

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.main.ui.pantailak.BottomNavBar
import com.example.main.ui.pantailak.CRUDPantaila
import com.example.main.ui.pantailak.ProduktuakPantaila
import com.example.login.ui.pantailak.SaioHasieraPantaila
import com.example.main.ui.pantailak.ErregistroPantaila
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NireAplikazioa() {

    val navController = rememberNavController()

    val navBackStackEntry by
    navController.currentBackStackEntryAsState()

    val rutaActual = navBackStackEntry?.destination?.route

    // La barra solo aparece en estas pantallas.
    val rutasConBarra = setOf(
        "home",
        "carrito",
        "crud"
    )

    Scaffold(
        bottomBar = {
            if (rutaActual in rutasConBarra) {
                BottomNavBar(
                    navController = navController,
                    onSalir = {
                        // Cerrar sesión de Firebase.
                        FirebaseAuth.getInstance().signOut()

                        // Volver al Login y limpiar la navegación anterior.
                        navController.navigate("login") {
                            popUpTo(
                                navController.graph.findStartDestination().id
                            ) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {

            // LOGIN: primera pantalla de la aplicación.

            composable("login") {
                SaioHasieraPantaila(
                    onLoginExitoso = {
                        navController.navigate("home") {
                            popUpTo("login") {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }


            // REGISTRO: sin barra inferior.
            composable("registro") {
                ErregistroPantaila()
            }

            // HOME: muestra los productos.
            composable("home") {
                ProduktuakPantaila()
            }

            // CARRITO: pantalla provisional.
            composable("carrito") {
                Text(text = "Aquí aparecerán los productos del carrito")
            }

            // CRUD: visible para todos por ahora.
            composable("crud") {
                CRUDPantaila()
            }
        }
    }
}