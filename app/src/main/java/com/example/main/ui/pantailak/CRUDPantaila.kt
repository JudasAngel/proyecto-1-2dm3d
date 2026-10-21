package com.example.main.ui.pantailak

import android.widget.Toast
import java.util.Locale
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun CRUDPantaila() {
    val db = FirebaseFirestore.getInstance()
    var productList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    val context = LocalContext.current

    val fetchProducts = {
        db.collection("produktuak")
            .get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { doc ->
                    val data = doc.data
                    data?.toMutableMap()?.apply { put("id", doc.id) }
                }
                productList = list
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Errorea produktuak kargatzerakoan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    LaunchedEffect(Unit) {
        fetchProducts()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top right add button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                // TODO: navegación para añadir producto
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir producto",
                    modifier = Modifier.size(28.dp),
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Product list with vertical separation
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            if (productList.isEmpty()) {
                Text(
                    text = "Produktuak Kargatzen...",
                    modifier = Modifier.padding(top = 16.dp),
                    color = Color.Gray
                )
            } else {
                productList.forEach { product ->
                    val docId = product["id"] as? String ?: ""
                    var izena = product["izena"] as? String ?: "Izenik gabe"
                    val prezioa = (product["prezioa"] as? Number)?.toDouble() ?: 0.0
                    val stock = (product["stock"] as? Number)?.toInt() ?: 0

                    if(izena.trim() == "") {
                        izena = "Izenik gabe"
                    }

                    ProductRow(
                        name = izena,
                        price = prezioa,
                        stock = stock,
                        onDelete = {
                            if (docId.isNotEmpty()) {
                                db.collection("produktuak").document(docId)
                                    .delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Produktua ezabatuta", Toast.LENGTH_SHORT).show()
                                        fetchProducts()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Errorea ezabatzean: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun ProductRow(name: String, price: Double, stock: Int, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = String.format(Locale.ROOT, "%.2f €", price),
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = "Stock: $stock",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { /* TODO: Navegación a editar */ },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar $name",
                    modifier = Modifier.size(26.dp),
                    tint = Color.Gray
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar $name",
                    modifier = Modifier.size(26.dp),
                    tint = Color.Red
                )
            }
        }
    }
    HorizontalDivider(Modifier.padding(horizontal = 8.dp), 1.dp, Color.Gray)
}

@Preview(showBackground = true)
@Composable
fun CRUDPantailaPreview() {
    // Note: CRUDPantaila requires Firebase, test ProductRowPreview instead
}

@Preview(showBackground = true)
@Composable
fun ProductRowPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        // Variation 1: Subtitle layout (Price & stock under name)
        ProductRow(
            name = "Gurinezko opila",
            price = 2.40,
            stock = 15,
            onDelete = {}
        )
    }
}
