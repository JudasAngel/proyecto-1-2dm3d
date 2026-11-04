package com.example.main.ui.pantailak

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.main.ui.data.Produktua
import com.example.main.ui.data.produktuZerrenda
import com.example.main.ui.theme.ArreseGorria
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

private const val BIDALKETA_PREZIOA = 2.50

// Saskiko lerro bat: produktua + zenbat unitate dagoen saskian
private data class SaskiLerroa(
    val produktua: Produktua,
    val kantitatea: Int
)

@Composable
fun SaskiPantaila(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val db = remember { FirebaseFirestore.getInstance() }

    // Produktu xehetasunak Firestore-tik (id -> Produktua)
    var produktuak by remember { mutableStateOf<Map<String, Produktua>>(emptyMap()) }
    var kargatzen by remember { mutableStateOf(false) }
    var errorea by remember { mutableStateOf(false) }

    // Iturri bakarra: produktuZerrenda. Kantitateak hemendik kalkulatzen dira.
    val kopurua = produktuZerrenda.groupingBy { it }.eachCount()
    val ids = kopurua.keys.toList().sorted()

    // Saskiko produktu berriak agertzen direnean bakarrik kargatu
    val activity = context as? Activity   // import android.app.Activity

    DisposableEffect(ids) {
        if (ids.isEmpty()) {
            produktuak = emptyMap()
            kargatzen = false
            return@DisposableEffect onDispose { }
        }

        kargatzen = true
        errorea = false

        val koordinatzailea = activity?.let { act ->
            kargatuSaskia(
                activity = act,
                ids = ids,
                onOk = { items ->
                    produktuak = items.associate {
                        it.id to Produktua(
                            id = it.id,
                            izena = it.izena,
                            prezioa = it.prezioa,
                            stocka = it.stocka
                        )
                    }
                    kargatzen = false
                },
                onErr = { mezua ->
                    Log.e("SASKIA", mezua)
                    errorea = true
                    kargatzen = false
                }
            )
        }

        if (koordinatzailea == null) {      // no Activity available
            errorea = true
            kargatzen = false
        }

        onDispose { koordinatzailea?.interrupt() }   // cancels coordinator and batches
    }

    // Kalkuluak: beti produktuZerrenda-tik datoz, ez dago egoera bikoizturik
    val lerroak = ids.mapNotNull { id ->
        produktuak[id]?.let { SaskiLerroa(it, kopurua[id] ?: 0) }
    }
    val azpitotala = lerroak.sumOf { it.produktua.prezioa * it.kantitatea }
    val bidalketa = if (lerroak.isNotEmpty()) BIDALKETA_PREZIOA else 0.0
    val guztira = azpitotala + bidalketa

    Column(modifier = modifier.fillMaxSize()) {

        Text(
            text = "Zure Saskia",
            fontSize = 26.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        // Zerrenda: egoera bakoitza argi bereizita
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {
                kargatzen -> CircularProgressIndicator(
                    color = ArreseGorria,
                    modifier = Modifier.align(Alignment.Center)
                )

                errorea -> Text(
                    text = "Errorea saskia kargatzean",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )

                lerroak.isEmpty() -> Text(
                    text = "Zure saskia hutsik dago",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(lerroak, key = { it.produktua.id }) { lerroa ->
                        SaskiTxartela(
                            lerroa = lerroa,
                            onGehitu = { produktuZerrenda.add(lerroa.produktua.id) },
                            onKendu = { produktuZerrenda.remove(lerroa.produktua.id) },
                            onEzabatu = { produktuZerrenda.removeAll { it == lerroa.produktua.id } }
                        )
                    }
                }
            }
        }

        // Beheko laukia: guztira eta ordainketa
        Surface(
            tonalElevation = 8.dp,
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Azpitotala:", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatPrezioa(azpitotala), fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Bidalketa:", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatPrezioa(bidalketa), fontSize = 16.sp)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Guztira:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = formatPrezioa(guztira),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArreseGorria
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        Toast.makeText(context, "Erosketa simulatua!", Toast.LENGTH_SHORT).show()
                        produktuZerrenda.clear()
                    },
                    enabled = lerroak.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ArreseGorria)
                ) {
                    Text("Ordaindu", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// Saskiko produktu baten txartela
@Composable
private fun SaskiTxartela(
    lerroa: SaskiLerroa,
    onGehitu: () -> Unit,
    onKendu: () -> Unit,
    onEzabatu: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lerroa.produktua.izena,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = String.format(Locale.ROOT, "%.2f € / alea", lerroa.produktua.prezioa),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onKendu, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.Remove, contentDescription = "Kendu")
            }

            Text(
                text = "${lerroa.kantitatea}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            // Stockak mugatzen du: ezin da stock baino gehiago gehitu
            IconButton(
                onClick = onGehitu,
                enabled = lerroa.kantitatea < lerroa.produktua.stocka,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Gehitu",
                    tint = ArreseGorria
                )
            }

            IconButton(onClick = onEzabatu, modifier = Modifier.size(40.dp)) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Ezabatu",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

private fun formatPrezioa(balioa: Double): String =
    String.format(Locale.ROOT, "%.2f €", balioa)

@Preview(showBackground = true)
@Composable
fun SaskiTxartelaPreview() {
    SaskiTxartela(
        lerroa = SaskiLerroa(
            produktua = Produktua(id = "1", izena = "Gurinezko opila", prezioa = 2.40, stocka = 15),
            kantitatea = 2
        ),
        onGehitu = {},
        onKendu = {},
        onEzabatu = {}
    )
}