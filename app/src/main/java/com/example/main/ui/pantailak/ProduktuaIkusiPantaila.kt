package com.example.login.ui.pantailak

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.main.IrudiKargatua
import com.example.main.ui.data.Produktua
import com.example.main.ui.data.produktuZerrenda
import com.example.main.ui.theme.ArreseGorria
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

@Composable
fun DetalleProductoScreen(
    modifier: Modifier = Modifier,
    productoId: String,
    onVolver: () -> Unit = {}
) {
    val context = LocalContext.current
    val db = remember { FirebaseFirestore.getInstance() }

    var produktua by remember { mutableStateOf<Produktua?>(null) }
    var kargatzen by remember { mutableStateOf(true) }
    var errorea by remember { mutableStateOf<String?>(null) }

    // Produktua Firestore-tik kargatu: productoId aldatzen denean bakarrik
    LaunchedEffect(productoId) {
        kargatzen = true
        errorea = null

        if (productoId.isBlank()) {
            errorea = "Ez da produkturik hautatu"
            kargatzen = false
            return@LaunchedEffect
        }

        db.collection("produktuak").document(productoId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    produktua = Produktua(
                        id = doc.id,
                        izena = doc.getString("izena") ?: "",
                        deskribapena = doc.getString("deskribapena") ?: "",
                        prezioa = (doc.get("prezioa") as? Number)?.toDouble() ?: 0.0,
                        stocka = (doc.get("stock") as? Number)?.toInt() ?: 0,
                        argazkia = doc.getString("argazkia"),
                        mota = (doc.get("mota") as? Number)?.toInt() ?: 0
                    )
                } else {
                    errorea = "Ez da produktua aurkitu"
                }
                kargatzen = false
            }
            .addOnFailureListener { e ->
                Log.e("DETALLE", "Errorea produktua kargatzean", e)
                errorea = "Errorea produktua kargatzean"
                kargatzen = false
            }
    }

    val item = produktua

    // Saskian dagoen kopurua stock-a baino txikiagoa den begiratu
    val saskianKopurua = produktuZerrenda.count { it == productoId }
    val gehitzeaPosible = item != null && item.stocka > 0 && saskianKopurua < item.stocka

    val botoiTestua = when {
        item != null && item.stocka <= 0 -> "Stock agortuta"
        item != null && saskianKopurua >= item.stocka -> "Stock guztia saskian"
        else -> "Saskira gehitu"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        when {
            kargatzen -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ArreseGorria)
                }
            }

            errorea != null -> {
                Text(
                    text = errorea ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 24.dp)
                )
            }

            item != null -> {
                // Irudia: zerrendako txartelarekin kargatzeko modu bera erabiltzen du
                IrudiKargatua(
                    argazkia = item.argazkia,
                    izena = item.izena,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = item.izena,
                    fontSize = 34.sp,
                    lineHeight = 42.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = String.format(Locale.ROOT, "%.2f €", item.prezioa),
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = ArreseGorria
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Deskribapena",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = item.deskribapena.ifBlank { "Ez dago deskribapenik" },
                    fontSize = 17.sp,
                    lineHeight = 27.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Stock: ${item.stocka}",
                    fontSize = 17.sp
                )

                // Ekintza botoiak edukiaren amaieran: scroll egin ondoren ere ikusgai daude
                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onVolver,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text("Itzuli", fontSize = 18.sp)
                    }

                    Button(
                        onClick = {
                            produktuZerrenda.add(item.id)
                            Toast.makeText(
                                context,
                                "Produktua saskira gehitu da",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        enabled = gehitzeaPosible,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text(botoiTestua, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}