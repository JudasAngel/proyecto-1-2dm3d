package com.example.main.ui.pantailak

import android.widget.Toast
import java.util.Locale
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.apparrese.R
import com.example.main.ui.data.Produktua
import com.example.main.ui.data.produktuZerrenda
import com.example.main.ui.theme.ArreseGorria
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore

data class SaskiItem(
    val id: String,
    val izena: String,
    val prezioa: Double,
    var kantitatea: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaskiPantaila(
    onHasieraClick: () -> Unit = {},
    onProduktuakClick: () -> Unit = {}
) {
    var menuAbierto by remember { mutableStateOf(false) }
    val context = LocalContext.current

    var saskiItems by remember { mutableStateOf<List<SaskiItem>>(emptyList()) }
    var kargatzen by remember { mutableStateOf(false) }
    var errorea by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(produktuZerrenda.toList()) {
        if (produktuZerrenda.isNotEmpty()) {
            kargatzen = true
            errorea = null
            val idCounts = produktuZerrenda.groupingBy { it }.eachCount()
            val uniqueIds = idCounts.keys.toList()

            val db = FirebaseFirestore.getInstance()
            db.collection("produktuak")
                .whereIn(FieldPath.documentId(), uniqueIds)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val list = querySnapshot.documents.mapNotNull { doc ->
                        val p = doc.toObject(Produktua::class.java)
                        if (p != null) {
                            SaskiItem(
                                id = doc.id,
                                izena = p.izena,
                                prezioa = p.prezioa,
                                kantitatea = idCounts[doc.id] ?: 1
                            )
                        } else null
                    }
                    saskiItems = list
                    kargatzen = false
                }
                .addOnFailureListener { e ->
                    errorea = e.message ?: "Errorea saskia kargatzean"
                    kargatzen = false
                }
        } else {
            saskiItems = emptyList()
            kargatzen = false
        }
    }

    val subtotal = saskiItems.sumOf { it.prezioa * it.kantitatea }
    val bidalketa = if (saskiItems.isNotEmpty()) 2.50 else 0.0
    val totala = subtotal + bidalketa

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(110.dp),
                title = {
                    Image(
                        painter = painterResource(R.drawable.logoarrese2),
                        contentDescription = "Logo Arrese",
                        modifier = Modifier.height(70.dp)
                    )
                },
                actions = {
                    Box {
                        IconButton(onClick = { menuAbierto = true }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Abrir menu",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = menuAbierto,
                            onDismissRequest = { menuAbierto = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Aukera provisionala") },
                                onClick = { menuAbierto = false }
                            )
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
                    onClick = onHasieraClick,
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
                    onClick = onProduktuakClick,
                    icon = {
                        Image(
                            painter = painterResource(R.drawable.shoppingcart),
                            contentDescription = "Saskia",
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
                    onClick = { },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = "Zure Saskia",
                fontSize = 26.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            if (kargatzen) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ArreseGorria)
                }
            } else if (errorea != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Errorea: $errorea", fontSize = 16.sp, color = Color.Red)
                }
            } else if (saskiItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Zure saskia hutsik dago", fontSize = 18.sp, color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(saskiItems, key = { it.id }) { item ->
                        SaskiCard(
                            item = item,
                            onAdd = {
                                produktuZerrenda.add(item.id)
                                saskiItems = saskiItems.map {
                                    if (it.id == item.id) it.copy(kantitatea = it.kantitatea + 1) else it
                                }
                            },
                            onRemove = {
                                val index = produktuZerrenda.indexOf(item.id)
                                if (index != -1) {
                                    produktuZerrenda.removeAt(index)
                                }
                                saskiItems = saskiItems.mapNotNull {
                                    if (it.id == item.id) {
                                        if (it.kantitatea > 1) it.copy(kantitatea = it.kantitatea - 1) else null
                                    } else it
                                }
                            },
                            onDelete = {
                                produktuZerrenda.removeAll { it == item.id }
                                saskiItems = saskiItems.filter { it.id != item.id }
                            }
                        )
                    }
                }
            }

            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = Color(0xFFFAFAFA)
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
                        Text("Azpitotala:", fontSize = 16.sp, color = Color.Gray)
                        Text(String.format(Locale.ROOT, "%.2f €", subtotal), fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Bidalketa:", fontSize = 16.sp, color = Color.Gray)
                        Text(String.format(Locale.ROOT, "%.2f €", bidalketa), fontSize = 16.sp)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Guztira:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(
                            String.format(Locale.ROOT, "%.2f €", totala),
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
                            saskiItems = emptyList()
                        },
                        enabled = saskiItems.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ArreseGorria)
                    ) {
                        Text("Ordaindu", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun SaskiCard(
    item: SaskiItem,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.izena, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = String.format(Locale.ROOT, "%.2f € / alea", item.prezioa),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Remove, contentDescription = "Kendu", tint = Color.Gray)
                }
                Text(text = "${item.kantitatea}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onAdd, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Gehitu", tint = ArreseGorria)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Ezabatu", tint = Color.Red)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SaskiPantailaPreview() {
    SaskiPantaila()
}