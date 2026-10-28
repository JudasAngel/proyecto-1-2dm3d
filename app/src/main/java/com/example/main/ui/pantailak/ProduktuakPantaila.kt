package com.example.main.ui.pantailak

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.apparrese.R
import com.example.main.ui.theme.ArreseGorria
import com.example.objetos.ui.`class`.Productu
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Locale

// Firestore-ko kategoria baten informazioa gordetzen du.
data class Categoria(
    val id: String,
    val izena: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProduktuakPantaila() {

    // Kategoria-menua irekita dagoen ala ez kontrolatzen du.
    var menuAbierto by remember {
        mutableStateOf(false)
    }

    // Firestore-tik lortutako kategoriak gordetzen ditu.
    var categorias by remember {
        mutableStateOf<List<Categoria>>(emptyList())
    }

    // null bada, produktu guztiak erakusten dira.
    var categoriaSeleccionada by remember {
        mutableStateOf<Categoria?>(null)
    }

    // Kategorien karga- eta errore-egoerak.
    var cargandoTipos by remember {
        mutableStateOf(true)
    }

    var errorTipos by remember {
        mutableStateOf(false)
    }

    // Produktuen zerrenda eta haien karga-egoerak.
    var productos by remember {
        mutableStateOf<List<Productu>>(emptyList())
    }

    var cargandoProductos by remember {
        mutableStateOf(true)
    }

    var errorProductos by remember {
        mutableStateOf(false)
    }

    // Firestore-ren instantzia lortzen du.
    val db = remember {
        FirebaseFirestore.getInstance()
    }

    // --------------------------------------------------
    // 1. KATEGORIAK KARGATZEA
    // Kolekzioa: product_type
    // Erabilitako eremua: izena
    // --------------------------------------------------

    DisposableEffect(db) {

        val listener = db.collection("product_type")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    errorTipos = true
                    cargandoTipos = false

                    android.util.Log.e(
                        "DEBUG_FIREBASE",
                        "Errorea kategoriak kargatzean",
                        error
                    )

                } else if (snapshot != null) {

                    // Dokumentuak Categoria objektu bihurtzen ditu.
                    categorias = snapshot.documents
                        .mapNotNull { doc ->

                            val nombre = doc.getString("izena")

                            if (nombre != null) {
                                Categoria(
                                    id = doc.id,
                                    izena = nombre
                                )
                            } else {
                                null
                            }
                        }
                        // Kategoriak ID numerikoaren arabera ordenatzen ditu.
                        .sortedBy {
                            it.id.toIntOrNull() ?: Int.MAX_VALUE
                        }

                    cargandoTipos = false
                    errorTipos = false
                }
            }

        // Pantailatik irtetean entzulea kentzen du.
        onDispose {
            listener.remove()
        }
    }

    // --------------------------------------------------
    // 2. PRODUKTUAK KARGATZEA
    // Kolekzioa: produktuak
    // --------------------------------------------------

    DisposableEffect(db, categoriaSeleccionada, categorias) {

        var consulta: Query = db.collection("produktuak")

        val categoria = categoriaSeleccionada

        if (categoria != null) {

            val idMota = categoria.id.toIntOrNull()

            consulta = if (idMota != null) {
                consulta.whereEqualTo("mota", idMota)
            } else {
                consulta.whereEqualTo("mota", categoria.id)
            }
        }

        cargandoProductos = true
        errorProductos = false

        val listener = consulta.addSnapshotListener { snapshot, error ->

            if (error != null) {

                errorProductos = true
                cargandoProductos = false

                android.util.Log.e(
                    "DEBUG_FIREBASE",
                    "Errorea produktuak kargatzean",
                    error
                )

            } else if (snapshot != null) {

                productos = snapshot.documents.mapNotNull { doc ->

                    val id = doc.id

                    val nombre = doc.getString("izena")
                        ?: return@mapNotNull null

                    val precio = doc.getDouble("prezioa")
                        ?: doc.getLong("prezioa")?.toDouble()
                        ?: 0.0

                    val motaId = doc.getLong("mota")?.toString()
                        ?: doc.getString("mota")

                    val nombreCategoria = categorias.find {
                        it.id == motaId
                    }?.izena

                    val tipo = categoria?.izena
                        ?: nombreCategoria
                        ?: "Kategoriarik gabe"

                    Productu(
                        id = id,
                        tipo = tipo,
                        izena = nombre,
                        deskribapena = doc.getString("deskribapena") ?: "",
                        prezioa = precio,
                        argazkia = R.drawable.predeterminada
                    )
                }

                cargandoProductos = false
                errorProductos = false

                android.util.Log.d(
                    "DEBUG_FIREBASE",
                    "Kargatutako produktuak: ${productos.size}"
                )
            }
        }

        onDispose {
            listener.remove()
        }
    }

    // --------------------------------------------------
    // 3. PANTAILAREN DISEINUA
    // --------------------------------------------------

    var produktuak by remember {
        mutableStateOf<List<Produktua>>(emptyList())
    }
    var motak by remember {
        mutableStateOf<Map<Int, String>>(emptyMap())
    }
    var errorea by remember {
        mutableStateOf<String?>(null)
    }
    //es par cargar los datso al iciiarse la pantalla osea al construir la parte visual
    LaunchedEffect(Unit) {
        //funcion de mota para consegir el tipo de producto
        KargatuMotak { mapa ->
            motak = mapa
        }
        //carga el resultado de la lista de datos y el porsible masejae de error qu epueda dar
        ProduktuakKargatu(
            resultado = { lista ->
                produktuak = lista
            },
            errorea = { mensaje ->
                errorea = mensaje
            }
        )

    }

    val context = LocalContext.current



    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(90.dp),
                title = {
                    Image(
                        painter = painterResource(R.drawable.logoarrese2),
                        contentDescription = "Arrese logoa",
                        modifier = Modifier.height(58.dp)
                    )
                },
                actions = {
                    IconButton(
                        onClick = onSaskiClick
                    ) {
                        BadgedBox(
                            badge = {
                                if (produktuZerrenda.isNotEmpty()) {
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
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.shoppingcart),
                                contentDescription = "Karritoa",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Box {
                        IconButton(
                            onClick = {
                                menuAbierto = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Kategoriak ireki",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        // Iragazkia: fondo rojo eta testu zuria.
                        DropdownMenu(
                            expanded = menuAbierto,
                            onDismissRequest = {
                                menuAbierto = false
                            },
                            containerColor = ArreseGorria
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Produktu guztiak",
                                        color = Color.White
                                    )
                                },
                                onClick = {
                                    categoriaSeleccionada = null
                                    menuAbierto = false
                                }
                            )

                            HorizontalDivider(
                                color = Color.White.copy(alpha = 0.4f)
                            )

                            when {
                                cargandoTipos -> {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "Kategoriak kargatzen...",
                                                color = Color.White
                                            )
                                        },
                                        enabled = false,
                                        onClick = {}
                                    )
                                }

                                errorTipos -> {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "Errorea kategoriak kargatzean",
                                                color = Color.White
                                            )
                                        },
                                        enabled = false,
                                        onClick = {}
                                    )
                                }

                                categorias.isEmpty() -> {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "Ez dago kategoriarik",
                                                color = Color.White
                                            )
                                        },
                                        enabled = false,
                                        onClick = {}
                                    )
                                }

                                else -> {
                                    categorias.forEach { cat ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = cat.izena,
                                                    color = Color.White
                                                )
                                            },
                                            onClick = {
                                                categoriaSeleccionada = cat
                                                menuAbierto = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ArreseGorria
                )
            )
        },

        bottomBar = {
            NavigationBar(
                modifier = Modifier.height(64.dp),
                containerColor = ArreseGorria,
                windowInsets = WindowInsets(0.dp)
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = {},
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

                NavigationBarItem(
                    selected = true,
                    onClick = {},
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

                NavigationBarItem(
                    selected = false,
                    onClick = {},
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 20.dp,
                bottom = 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = categoriaSeleccionada?.izena
                            ?: "Gure produktuak",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (categoriaSeleccionada == null) {
                            "Aurkitu gustuko dituzun produktuak"
                        } else {
                            "${categoriaSeleccionada!!.izena} kategoriako produktuak"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    HorizontalDivider(
                        color = ArreseGorria.copy(alpha = 0.35f),
                        thickness = 1.dp
                    )
                }
            }

            if (cargandoProductos) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = ArreseGorria
                        )
                    }
                }
            } else if (errorProductos) {
                item {
                    Text(
                        text = "Ezin izan dira produktuak kargatu. Saiatu berriro.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            } else if (productos.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ez dago produkturik",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Saiatu beste kategoria bat aukeratzen.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(
                    items = productos,
                    key = { it.id }
                ) { producto ->
                    ProductuItem(producto)
                }
            }
        }
    }
}

// Produktu-txartela: irudia, kategoria, izena eta prezioa.
@Composable
fun ProductuItem(producto: Productu) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = producto.argazkia),
                contentDescription = producto.izena,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
            )

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = producto.tipo,
                    style = MaterialTheme.typography.labelMedium,
                    color = ArreseGorria
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = producto.izena,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = String.format(
                            Locale("es", "ES"),
                            "%.2f €",
                            producto.prezioa
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ArreseGorria
                    )
                }
            }
        }
    }
}