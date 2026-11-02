package com.example.apparrese

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.main.ui.pantailak.BottomNavBar
import com.example.main.ui.pantailak.CRUDPantaila
import com.example.main.ui.pantailak.ProduktuakPantaila
import com.example.login.ui.pantailak.SaioHasieraPantaila
import com.example.main.MusikaKudeatzailea
import com.example.main.ui.pantailak.ErregistroPantaila
import com.example.main.ui.pantailak.ProduktuakEguneratu
import com.example.main.ui.pantailak.SaskiPantaila
import com.google.firebase.auth.FirebaseAuth
import com.example.login.ui.pantailak.DetalleProductoScreen
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun NireAplikazioa() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var userType by remember { mutableStateOf<Long?>(null) }

    // Listen to Firebase Auth state changes so userType is fetched immediately upon login
    DisposableEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                FirebaseFirestore.getInstance()
                    .collection("erabiltzaileak")
                    .document(user.uid)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val rawType = document.get("id-type") ?: document.get("tipo") ?: document.get("idType")
                            userType = when (rawType) {
                                is Number -> rawType.toLong()
                                is String -> rawType.toLongOrNull()
                                else -> 1L
                            }
                            Log.d("USER_TYPE", "User type loaded: $userType (uid: ${user.uid})")
                        } else {
                            Log.w("USER_TYPE", "Document for uid ${user.uid} does not exist in erabiltzaileak")
                            userType = 1L
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("USER_TYPE", "Error fetching user document", e)
                        userType = 1L
                    }
            } else {
                userType = null
            }
        }
        auth.addAuthStateListener(listener)
        onDispose {
            auth.removeAuthStateListener(listener)
        }
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
        "crud",
        "producto/{id}"
    )
    // 2. Observador de ciclo de vida (pausa y reanuda)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> MusikaKudeatzailea.pausatu()
                Lifecycle.Event.ON_RESUME -> MusikaKudeatzailea.jarraitu()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(


        bottomBar = {
            if (rutaActual == "home" ||
                rutaActual == "carrito" ||
                rutaActual == "crud"
            ) {

                BottomNavBar(
                    navController = navController,
                    userType = userType,
                    onSalir = {
                        userType = null
                        MusikaKudeatzailea.gelditu()
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
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
                        MusikaKudeatzailea.hasi(context)
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

                    onProduktuaClick = { id ->

                        Log.d(
                            "NAVEGACION",
                            "ID recibido desde ProduktuakPantaila: $id"
                        )

                        navController.navigate("producto/$id")

                        Log.d(
                            "NAVEGACION",
                            "Navegando al producto: $id"
                        )
                    }
                )
            }
            composable(
                route = "producto/{id}",
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->

                val id = backStackEntry.arguments?.getString("id") ?: ""

                Log.d(
                    "NAVEGACION",
                    "ENTRANDO EN DETALLES. ID: $id"
                )

                DetalleProductoScreen(
                    productoId = id,
                    onVolver = {
                        navController.popBackStack()
                    }
                )
            }

            composable("carrito") {
                SaskiPantaila()
            }

            composable("crud") {
                if (userType == 2L) {
                    CRUDPantaila(
                        onAdd = {
                            // Navega a la pantalla sin pasar ningún ID (modo añadir)
                            navController.navigate("eguneratu")
                        },
                        onEdit = { docId ->
                            // Navega a la pantalla pasando el ID del producto (modo editar)
                            navController.navigate("eguneratu/$docId")
                        }
                    )
                } else {
                    LaunchedEffect(Unit) {
                        navController.navigate("home") {
                            popUpTo("home") {inclusive = true}
                        }
                    }
                }
            }

            // Ruta para AÑADIR un nuevo producto
            composable("eguneratu") {
                ProduktuakEguneratu(
                    productId = null,
                    onAtzeraEgin = { navController.popBackStack() }
                )
            }

            composable(
                route = "eguneratu/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId")

                ProduktuakEguneratu(
                    productId = productId,
                    onAtzeraEgin = { navController.popBackStack() }
                )
            }




        }
    }
}

