package com.example.main.ui.pantailak

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.main.IrudiKargatua
import com.example.main.ui.data.Kategoria
import com.example.main.ui.data.Produktua
import com.example.main.ui.data.produktuZerrenda
import com.example.main.ui.theme.ArreseGorria
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Locale
import android.app.Activity
import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun ProduktuakPantaila(
    modifier: Modifier = Modifier,
    kategoriak: List<Kategoria>,
    kategoriaAukeratua: Kategoria?,
    onProduktuaClick: (String) -> Unit = {}
) {
    val haria = remember { Executors.newSingleThreadExecutor() }
    val context = LocalContext.current
    val db = remember { FirebaseFirestore.getInstance() }

    var productos by remember { mutableStateOf<List<Produktua>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var errorea by remember { mutableStateOf(false) }
    var bilaketaTestua by remember { mutableStateOf("") }

    // Bilaketa: testua edo produktuak aldatzen direnean bakarrik berriro kalkulatzen da
    var productosFiltrados by remember { mutableStateOf<List<Produktua>>(emptyList()) }

    val activity = context as? Activity

// Bilaketa haria batean egiten da. Bilaketa aldatzen denean aurrekoa ezeztatzen da.
    DisposableEffect(bilaketaTestua, productos) {
        val bizirik = AtomicBoolean(true)
        val testua = bilaketaTestua.trim()

        val hilo = Thread {
            val emaitza = productos.filter { it.izena.contains(testua, ignoreCase = true) }
            if (bizirik.get()) {
                Handler(Looper.getMainLooper()).post {
                    if (bizirik.get()) productosFiltrados = emaitza
                }
            }
        }
        hilo.start()

        onDispose {
            bizirik.set(false)
            hilo.interrupt()
        }
    }

    // Produktuak kargatu: kategoria aldatzen denean bakarrik
    DisposableEffect(kategoriaAukeratua) {
        cargando = true
        errorea = false

        val consulta: Query = kategoriaAukeratua?.let { kat ->
            val idMota: Any = kat.id.toIntOrNull() ?: kat.id
            db.collection("produktuak").whereEqualTo("mota", idMota)
        } ?: db.collection("produktuak")

        val listener = consulta.addSnapshotListener(haria) { snapshot, error ->
            if (error != null) {
                Log.e("DEBUG_FIREBASE", "Errorea produktuak kargatzean", error)
                activity?.runOnUiThread {
                    errorea = true
                    cargando = false
                }
            } else if (snapshot != null) {
                // Dokumentuen mapaketa entzulearen haria (executor) batean egiten da
                val lista = snapshot.documents.mapNotNull { doc ->
                    val izena = doc.getString("izena") ?: return@mapNotNull null

                    val prezioa = when (val p = doc.get("prezioa")) {
                        is Number -> p.toDouble()
                        is String -> p.replace(",", ".").toDoubleOrNull() ?: 0.0
                        else -> 0.0
                    }

                    val motaId = doc.getLong("mota")?.toInt()
                        ?: doc.getString("mota")?.toIntOrNull()
                        ?: 0

                    Produktua(
                        id = doc.id,
                        mota = motaId,
                        izena = izena,
                        deskribapena = doc.getString("deskribapena") ?: "",
                        prezioa = prezioa,
                        argazkia = doc.getString("argazkia"),
                        stocka = (doc.get("stock") as? Number)?.toInt() ?: 0
                    )
                }

                activity?.runOnUiThread {
                    productos = lista
                    cargando = false
                    errorea = false
                }
            }
        }

        onDispose { listener.remove() }
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = kategoriaAukeratua?.izena ?: "Gure produktuak",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (kategoriaAukeratua == null) {
                        "Aurkitu gustuko dituzun produktuak"
                    } else {
                        "${kategoriaAukeratua.izena} kategoriako produktuak"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                HorizontalDivider(
                    color = ArreseGorria.copy(alpha = 0.35f),
                    thickness = 1.dp
                )

                OutlinedTextField(
                    value = bilaketaTestua,
                    onValueChange = { bilaketaTestua = it },
                    label = { Text("Bilatu produktua") },
                    placeholder = { Text("Idatzi produktuaren izena") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    singleLine = true
                )
            }
        }

        if (cargando) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ArreseGorria)
                }
            }
        } else if (errorea) {
            item {
                Text(
                    text = "Ezin izan dira produktuak kargatu. Saiatu berriro.",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }
        } else if (productosFiltrados.isEmpty()) {
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
                        text = "Saiatu beste kategoria bat aukeratzen edo bilaketa aldatzen.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(
                items = productosFiltrados,
                key = { it.id }
            ) { producto ->
                val tipo = kategoriak.find {
                    it.id == producto.mota.toString()
                }?.izena ?: "Kategoriarik gabe"

                ProductuItem(
                    producto = producto,
                    tipo = tipo,
                    onClick = { onProduktuaClick(producto.id) }
                )
            }
        }
    }
}

// Produktu-txartela: irudia, kategoria, izena eta prezioa.
@Composable
fun ProductuItem(
    producto: Produktua,
    tipo: String,
    onClick: () -> Unit = {}
) {
    // Saskian dagoen kopurua eta stock-a: stock-a gainditzea ekiditeko
    val saskianKopurua = produktuZerrenda.count { it == producto.id }
    val gehitzeaPosible = producto.stocka > 0 && saskianKopurua < producto.stocka

    val saskiTestua = when {
        producto.stocka <= 0 -> "Stock agortuta"
        saskianKopurua >= producto.stocka -> "Stock guztia saskian"
        else -> "Sartu karritora ➝"
    }

    Card(
        onClick = onClick,
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
        val context = LocalContext.current

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            IrudiKargatua(
                argazkia = producto.argazkia,
                izena = producto.izena,
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
                        text = saskiTestua,
                        color = if (gehitzeaPosible) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.clickable(enabled = gehitzeaPosible) {
                            // Berriz egiaztatu klik azkarrek ere stock-a gainditu ez dezaten
                            if (produktuZerrenda.count { it == producto.id } < producto.stocka) {
                                produktuZerrenda.add(producto.id)
                                Toast.makeText(
                                    context,
                                    "${producto.izena} karritora gehitu da",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}