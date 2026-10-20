package com.example.main.ui.pantailak

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
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
                    if (data != null) {
                        data.toMutableMap().apply { put("id", doc.id) }
                    } else null
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (productList.isEmpty()) {
                Text(
                    text = "Ez dago produkturik",
                    modifier = Modifier.padding(top = 16.dp),
                    color = Color.Gray
                )
            } else {
                productList.forEach { product ->
                    val docId = product["id"] as? String ?: ""
                    val izena = product["izena"] as? String ?: "Izena gabe"
                    val prezioa = (product["prezioa"] as? Number)?.toDouble() ?: 0.0
                    val stock = (product["stock"] as? Number)?.toInt() ?: 0

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
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1.5f)
        )

        Text(
            text = "$price €",
            fontSize = 14.sp,
            color = Color.DarkGray,
            modifier = Modifier.width(80.dp)
        )

        Text(
            text = "Stock: $stock",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.width(90.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { /* TODO: Navegación a editar */ },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar $name",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Gray
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar $name",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Red
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CRUDPantailaPreview() {
    CRUDPantaila()
}
