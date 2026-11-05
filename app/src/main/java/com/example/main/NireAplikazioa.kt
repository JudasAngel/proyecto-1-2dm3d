package com.example.apparrese

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.main.ui.data.Kategoria
import com.example.main.ui.data.SaskiViewModel
import com.example.main.ui.pantailak.ArreseTopBar
import com.example.main.ui.pantailak.KategoriaMenua
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun NireAplikazioa() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val saskiVM: SaskiViewModel = viewModel()

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
    }
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

    var kategoriak by remember { mutableStateOf<List<Kategoria>>(emptyList()) }
    var kategoriaAukeratua by remember { mutableStateOf<Kategoria?>(null) }

    DisposableEffect(Unit) {
        val listener = FirebaseFirestore.getInstance()
            .collection("product_type")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    kategoriak = snapshot.documents
                        .mapNotNull { doc ->
                            doc.getString("izena")?.let { Kategoria(doc.id, it) }
                        }
                        .sortedBy { it.id.toIntOrNull() ?: Int.MAX_VALUE }
                }
            }
        onDispose { listener.remove() }
    }

    val hasierakoRuta = "login"

    val rutasConBarra = setOf("home", "carrito", "crud", "producto/{id}")
    Scaffold(
        topBar = {
            if (rutaActual != "login") {
                ArreseTopBar(
                    ekintzak = {
                        if (rutaActual == "home") {
                            KategoriaMenua(
                                kategoriak = kategoriak,
                                aukeratua = kategoriaAukeratua,
                                onAukeratu = { kategoriaAukeratua = it }
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (rutaActual in rutasConBarra) {
                BottomNavBar(
                    navController = navController,
                    userType = userType,
                    saskiKopurua = saskiVM.ids.size,
                    onSalir = {
                        userType = null
                        saskiVM.hustu()
                        kategoriaAukeratua = null
                        MusikaKudeatzailea.gelditu()
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (rutaActual == "crud") {
                FloatingActionButton(onClick = { navController.navigate("eguneratu") }) {
                    Icon(Icons.Default.Add, contentDescription = "Gehitu")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = hasierakoRuta,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                SaioHasieraPantaila(
                    modifier = Modifier.fillMaxSize(),
                    onLoginExitoso = {
                        MusikaKudeatzailea.hasi(context)
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onErregistratu = {
                        navController.navigate("erregistro")
                    }
                )
            }

            composable("erregistro") {
                ErregistroPantaila(
                    modifier = Modifier.fillMaxSize(),
                    onBack = { navController.popBackStack() }
                )
            }

            composable("home") {
                ProduktuakPantaila(
                    saskiVM = saskiVM,
                    modifier = Modifier.fillMaxSize(),
                    kategoriak = kategoriak,
                    kategoriaAukeratua = kategoriaAukeratua,
                    onProduktuaClick = { id -> navController.navigate("producto/$id") }
                )
            }

            composable(
                route = "producto/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                DetalleProductoScreen(
                    saskiVM = saskiVM,
                    modifier = Modifier.fillMaxSize(),
                    productoId = id,
                    onVolver = { navController.popBackStack() }
                )
            }

            composable("carrito") {
                SaskiPantaila(
                    saskiVM = saskiVM,
                    modifier = Modifier.fillMaxSize()
                )
            }

            composable("crud") {
                if (userType == 2L) {
                    CRUDPantaila(
                        modifier = Modifier.fillMaxSize(),
                        onEdit = { docId -> navController.navigate("eguneratu/$docId") }
                    )
                } else {
                    LaunchedEffect(Unit) {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                }
            }

            composable("eguneratu") {
                ProduktuakEguneratu(
                    modifier = Modifier.fillMaxSize(),
                    productId = null,
                    kategoriak = kategoriak,
                    onAtzeraEgin = { navController.popBackStack() }
                )
            }

            composable(
                route = "eguneratu/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                ProduktuakEguneratu(
                    modifier = Modifier.fillMaxSize(),
                    productId = backStackEntry.arguments?.getString("productId"),
                    kategoriak = kategoriak,
                    onAtzeraEgin = { navController.popBackStack() }
                )
            }
        }
    }
}


