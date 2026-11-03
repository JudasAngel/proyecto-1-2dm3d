package com.example.main.ui.pantailak

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.main.ui.data.Produktua
import com.example.main.ui.theme.ArreseGorria
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

@Composable
fun CRUDPantaila(
    modifier: Modifier = Modifier,
    onEdit: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val db = remember { FirebaseFirestore.getInstance() }

    var produktuak by remember { mutableStateOf<List<Produktua>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var errorea by remember { mutableStateOf(false) }

    // Produktuen zerrenda entzule baten bidez: datuak aldatzen direnean automatikoki eguneratzen da
    DisposableEffect(db) {
        val listener = db.collection("produktuak")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("CRUD", "Errorea produktuak kargatzean", error)
                    errorea = true
                    cargando = false
                } else if (snapshot != null) {
                    produktuak = snapshot.documents.map { doc ->
                        Produktua(
                            id = doc.id,
                            izena = doc.getString("izena")?.takeIf { it.isNotBlank() } ?: "Izenik gabe",
                            prezioa = (doc.get("prezioa") as? Number)?.toDouble() ?: 0.0,
                            stocka = (doc.get("stock") as? Number)?.toInt() ?: 0,
                            argazkia = doc.getString("argazkia"),
                            mota = (doc.get("mota") as? Number)?.toInt() ?: 0
                        )
                    }
                    cargando = false
                    errorea = false
                }
            }

        onDispose { listener.remove() }
    }

    LazyColumn(
        modifier = modifier,
        // Behean espazio nahikoa utzi FAB-ak azken elementua ez tapatzeko
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp)
    ) {
        item {
            Text(
                text = "Produktuen kudeaketa",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }

        when {
            cargando -> item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ArreseGorria)
                }
            }

            errorea -> item {
                Text(
                    text = "Errorea produktuak kargatzean",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }

            produktuak.isEmpty() -> item {
                Text(
                    text = "Ez dago produkturik",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }

            else -> items(produktuak, key = { it.id }) { produktua ->
                ProductRow(
                    name = produktua.izena,
                    price = produktua.prezioa,
                    stock = produktua.stocka,
                    onEdit = { onEdit(produktua.id) },
                    onDelete = { ezabatu(db, context, produktua.id) }
                )
            }
        }
    }
}

// Produktua Firestore-tik ezabatzen du. Zerrenda entzuleak automatikoki eguneratzen du.
private fun ezabatu(db: FirebaseFirestore, context: Context, id: String) {
    db.collection("produktuak").document(id)
        .delete()
        .addOnSuccessListener {
            Toast.makeText(context, "Produktua ezabatuta", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Errorea ezabatzean", Toast.LENGTH_SHORT).show()
        }
}

// Produktu baten errenglona: izena, prezioa, stocka eta botoiak.
@Composable
fun ProductRow(
    name: String,
    price: Double,
    stock: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = String.format(Locale.ROOT, "%.2f €", price),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Stock: $stock",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editatu",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Ezabatu",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        HorizontalDivider()
    }
}

@Preview(showBackground = true)
@Composable
fun ProductRowPreview() {
    ProductRow(
        name = "Gurinezko opila",
        price = 2.40,
        stock = 15,
        onEdit = {},
        onDelete = {}
    )
}