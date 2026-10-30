package com.example.main.ui.pantailak

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.apparrese.R
import com.example.main.ui.data.Kategoria
import com.example.main.ui.theme.ArreseGorria
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Locale
import com.example.main.ui.data.Produktua
import com.example.main.ui.data.produktuZerrenda
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProduktuakPantaila() {

    // Kategoria-menua irekita dagoen ala ez kontrolatzen du.
    var menuAbierto by remember { mutableStateOf(false) }

    // Firestore-tik lortutako kategoriak gordetzen ditu.
    var categorias by remember { mutableStateOf<List<Kategoria>>(emptyList()) }

    // null bada, produktu guztiak erakusten dira.
    var categoriaSeleccionada by remember { mutableStateOf<Kategoria?>(null) }

    // Kategorien karga- eta errore-egoerak.
    var cargandoTipos by remember { mutableStateOf(true) }

    var errorTipos by remember { mutableStateOf(false) }

    // Produktuen zerrenda eta haien karga-egoerak.
    var productos by remember { mutableStateOf<List<Produktua>>(emptyList()) }

    // Produktuen zerrenda
    var produktuak by remember { mutableStateOf<List<Produktua>>(emptyList()) }

    // Bilatzailean idatzitako testua
    var bilaketaTestua by remember { mutableStateOf("") }

    // Bilaketaren emaitzak
    var iragazitakoProduktuak by remember { mutableStateOf<List<Produktua>>(emptyList()) }

    var cargandoProductos by remember { mutableStateOf(true) }

    var errorProductos by remember {
        mutableStateOf(false)
    }

    // Firestore-ren instantzia lortzen du.
    val db = remember {
        FirebaseFirestore.getInstance()
    }

    // Bilaketa haria kudeatzen du
    LaunchedEffect(bilaketaTestua, productos) {

        // Bilaketa bigarren harian egiten du.
        val emaitzak = withContext(Dispatchers.Default) {

            // Bilaketa-testua letra xehez bihurtzen du
            val bilatutakoTestua = bilaketaTestua.lowercase(Locale.ROOT)

            // Produktuak iragazten ditu
            productos.filter { producto ->

                // Bilaketa bertan behera geratu den egiaztatzen du
                coroutineContext.ensureActive()

                // Produktuaren izenean testua bilatzen du
                producto.izena.lowercase(Locale.ROOT)
                    .contains(bilatutakoTestua)
            }
        }

        // Emaitzak pantailaren egoeran gordetzen ditu.
        iragazitakoProduktuak = emaitzak
    }

    // KATEGORIAK KARGATZEA

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
                                Kategoria(
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

    // PRODUKTUAK KARGATZEA
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

                    val precio = (doc.get("prezioa") as? Number)?.toDouble()
                        ?: doc.getString("prezioa")?.toDoubleOrNull()
                        ?: 0.0

                    val rawMota = doc.get("mota")
                    val motaId = (rawMota as? Number)?.toString()
                        ?: (rawMota as? String)

                    val nombreCategoria = categorias.find {
                        it.id == motaId
                    }?.izena

                    val tipo = categoria?.izena
                        ?: nombreCategoria
                        ?: "Kategoriarik gabe"

                    val motaNumero = (rawMota as? Number)?.toInt()
                        ?: (rawMota as? String)?.toIntOrNull()
                        ?: 0

                    Produktua(
                        id = id,
                        mota = motaNumero,
                        izena = nombre,
                        deskribapena = doc.getString("deskribapena") ?: "",
                        prezioa = precio,
                        argazkia = doc.getString("argazkia")
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

    // PANTAILAREN DISEINUA
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

                    // Produktuak bilatzeko testu-eremua
                    OutlinedTextField(
                        value = bilaketaTestua,
                        onValueChange = { testua ->
                            bilaketaTestua = testua
                        },
                        label = { Text("Produktuak bilatu") },
                        placeholder = { Text("Idatzi produktuaren izena...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        singleLine = true
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
                    items = iragazitakoProduktuak,
                    key = { it.id }
                ) { producto ->

                    val tipo = categorias.find {
                        it.id == producto.mota.toString()
                    }?.izena ?: "Kategoriarik gabe"

                    ProductuItem(
                        producto = producto,
                        tipo = tipo
                    )
                }
            }
        }
    }
}

// Produktu-txartela: irudia, kategoria, izena eta prezioa.
@Composable
fun ProductuItem(
    producto: Produktua,
    tipo: String
) {
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

            val context = LocalContext.current
            val imageResId = producto.argazkia?.let {
                context.resources.getIdentifier(
                    it.lowercase(Locale.ROOT),
                    "drawable",
                    context.packageName
                )
            } ?: 0

            android.util.Log.d(
                "IMG",
                "Producto: ${producto.izena}, " +
                        "Imagen: ${producto.argazkia}, ID: $imageResId"
            )

            Image(
                painter = painterResource(
                    id = if (imageResId != 0) {
                        imageResId
                    } else {
                        R.drawable.predeterminada
                    }
                ),
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
                    text = tipo,
                    style = MaterialTheme.typography.labelMedium,
                    color = ArreseGorria
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = producto.izena,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

                    Text(
                        text = "Sartu karritora ➝",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                produktuZerrenda.add(producto.id)
                                Toast.makeText(
                                    context,
                                    "${producto.izena} karritora gehitu da",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

// Produktuak bilatzeko funtzioa
fun bilatuProduktuak(
    produktua: List<Produktua>,
    texto: String
): List<Produktua> {

    // Testua letra xehez bihurtzen du
    val bilatutakoTestua = texto.lowercase(Locale.ROOT)

    // Testua produktuaren izenean bilatzen du
    return produktua.filter { produktua ->
        produktua.izena.lowercase(Locale.ROOT).contains(bilatutakoTestua)
    }
}
