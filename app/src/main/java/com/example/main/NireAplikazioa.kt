package com.example.apparrese

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.main.ui.pantailak.SaskiPantaila
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NireAplikazioa() {
    LaunchedEffect(Unit) {
        Log.d("NAVEGACION", "NireAplikazioa se está ejecutando")
    }
    val navController = rememberNavController()

    val navBackStackEntry by
    navController.currentBackStackEntryAsState()

    val rutaActual = navBackStackEntry?.destination?.route
    LaunchedEffect(rutaActual) {
        Log.d("NAVEGACION", "Ruta actual: $rutaActual")
    }    // La barra solo aparece en estas pantallas.
    val rutasConBarra = setOf(
        "home",
        "carrito",
        "crud"
    )

    Scaffold(


        bottomBar = {
            if (rutaActual == "home" ||
                rutaActual == "carrito" ||
                rutaActual == "crud"
            ) {
                BottomNavBar(
                    navController = navController,
                    onSalir = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo("login") {
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
            composable("login") {
                SaioHasieraPantaila(
                    onLoginExitoso = {
                        Log.d("NAVEGACION", "Login exitoso: navegando a HOME")

                        navController.navigate("home") {
                            popUpTo("login") {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }

                        Log.d(
                            "NAVEGACION",
                            "Navegación a HOME solicitada"
                        )
                    }
                )
            }

            composable("registro") {
                ErregistroPantaila()
            }

            composable("home") {
                ProduktuakPantaila(
                    onSaskiClick = {
                        navController.navigate("carrito") {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable("carrito") {
                SaskiPantaila()
            }

            composable("crud") {
                CRUDPantaila()
            }
        }
    }
}