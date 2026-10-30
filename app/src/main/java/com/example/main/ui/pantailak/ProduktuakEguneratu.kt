package com.example.main.ui.pantailak

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.apparrese.R
import com.example.main.ui.theme.ArreseGorria
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SubdirectoryArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.example.main.ui.data.Mota
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProduktuakEguneratu(productId: String? = null, onAtzeraEgin: () -> Unit) {
    var menuAbierto by remember { mutableStateOf(false) }
    var izena by remember { mutableStateOf("") }
    var deskribapena by remember { mutableStateOf("") }
    var stocka by remember { mutableStateOf("") }
    var prezioa by remember { mutableStateOf("") }
    var argazkia by remember { mutableStateOf("") }
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    // Firebase-tik jasotako motak gordetzeko zerrenda
    var motak by remember { mutableStateOf<List<Mota>>(emptyList()) }

    // Aukeratutako mota gordetzeko
    var mota by remember { mutableStateOf<Mota?>(null) }

// Dropdown menua irekita dagoen ala ez
    var motaMenuIrekita by remember { mutableStateOf(false) }


// Motak kargatu eta, ID-a badago, produktuaren datuak ere kargatu
    LaunchedEffect(productId, motak) {
        motakKargatuEgunertuPantailan { emaitza ->
            motak = emaitza

            // Produktua editatzen ari bagara, bere datuak bilatu Firestore-n
            if (!productId.isNullOrEmpty()) {
                db.collection("produktuak").document(productId).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            izena = document.getString("izena") ?: ""
                            deskribapena = document.getString("deskribapena") ?: ""
                            stocka = document.get("stock")?.toString() ?: ""
                            prezioa = document.get("prezioa")?.toString() ?: ""
                            argazkia = document.getString("argazkia") ?: ""

                            val motaId = document.getLong("mota")?.toInt()
                            if (motaId != null) {
                                mota = motak.find { it.id == motaId }
                            }
                        }
                    }
            }
        }
    }


    Scaffold(
        // BARRA SUPERIOR
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
                        IconButton(
                            onClick = { menuAbierto = true }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Abrir menu",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = menuAbierto,
                            onDismissRequest = {
                                menuAbierto = false
                            }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text("Opcion provisional")
                                },
                                onClick = {
                                    menuAbierto = false
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ArreseGorria
                )
            )
        },

    ) { paddingValues ->
        // CONTENIDO PRINCIPAL
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 30.dp
                ),
            // horizontalAlignment = Alignment.CenterHorizontally

        ) {

            item {
                Text(
                    // Titulua aldatzen da editatzen edo gehitzen ari den arabera
                    text = if (productId.isNullOrEmpty()) "Produktua gehitu" else "Produktua eguneratu",
                    fontSize = 30.sp,
                    fontFamily = FontFamily.Serif

                )
                HorizontalDivider(
                    modifier = Modifier
                        .width(430.dp)
                        .padding(vertical = 8.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = izena,
                    onValueChange = { izena = it },
                    singleLine = true,
                    label = {
                        Text("Izena")
                    },

                    //enabled = !kargatzen,
                    modifier = Modifier.padding(top = 20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = ArreseGorria,
                        focusedBorderColor = ArreseGorria
                    )
                )
            }

            item {
                OutlinedTextField(
                    value = deskribapena,
                    onValueChange = { deskribapena = it },
                    singleLine = true,
                    label = {
                        Text("Deskribapena")
                    },

                    //enabled = !kargatzen,
                    modifier = Modifier.padding(top = 20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = ArreseGorria,
                        focusedBorderColor = ArreseGorria
                    )
                )
            }

            item {
                OutlinedTextField(
                    value = stocka,
                    onValueChange = { stocka = it },
                    singleLine = true,
                    label = {
                        Text("Stock-a")
                    },

                    //enabled = !kargatzen,
                    modifier = Modifier.padding(top = 20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = ArreseGorria,
                        focusedBorderColor = ArreseGorria
                    )
                )
            }

            item {
                OutlinedTextField(
                    value = prezioa,
                    onValueChange = { prezioa = it },
                    singleLine = true,
                    label = {
                        Text("Prezioa")
                    },

                    //enabled = !kargatzen,
                    modifier = Modifier.padding(top = 20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = ArreseGorria,
                        focusedBorderColor = ArreseGorria
                    )
                )
            }

            item {
                Box{
                    OutlinedTextField(
                        value = mota?.izena ?: "",
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        label = {
                            Text("Mota")
                        },

                        // Eskuineko aldean gezia ikonoa jartzeko
                        trailingIcon = {
                            IconButton(
                                // Gezia sakatzerakoan menua ireki edo itxi
                                onClick = {
                                    motaMenuIrekita = !motaMenuIrekita
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = "Aukeratu mota"
                                )
                            }
                        },
                        modifier = Modifier.padding(top = 20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = ArreseGorria,
                            focusedBorderColor = ArreseGorria
                        )
                    )

                    // Aukeren menua
                    DropdownMenu(
                        // Menua irekita dagoen ala ez adierazi
                        expanded = motaMenuIrekita,

                        // Menutik kanpo sakatzean menua itxi
                        onDismissRequest = {
                            motaMenuIrekita = false
                        }
                    ) {
                        // Firebase-tik jasotako mota guztiak aztertu
                        motak.forEach { aukera ->

                            // Aukera bakoitzeko menu-elementu bat sortu
                            DropdownMenuItem(
                                // Aukeraren izena erakutsi
                                text = {
                                    Text(aukera.izena)
                                },
                                onClick = {
                                    // Aukeratutako mota gorde
                                    mota = aukera
                                    motaMenuIrekita = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = argazkia,
                    onValueChange = { argazkia = it },
                    singleLine = true,
                    label = {
                        Text("Argazkia")
                    },

                    //enabled = !kargatzen,
                    modifier = Modifier.padding(top = 20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = ArreseGorria,
                        focusedBorderColor = ArreseGorria
                    )
                )
            }

            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            if (izena.isBlank()) {
                                Toast.makeText(context, "Sartu produktuaren izena", Toast.LENGTH_SHORT).show()
                            } else if (mota == null) {
                                Toast.makeText(context, "Aukeratu produktu mota", Toast.LENGTH_SHORT).show()
                            } else {
                                val produktuaMap = hashMapOf(
                                    "izena" to izena.trim(),
                                    "deskribapena" to deskribapena,
                                    "stock" to (stocka.toIntOrNull() ?: 0),
                                    "prezioa" to (prezioa.toDoubleOrNull() ?: 0.0),
                                    "mota" to mota!!.id,
                                    "argazkia" to argazkia
                                )

                                if (!productId.isNullOrEmpty()) {
                                    // ID-a badago, dauden datuak eguneratzen ditugu
                                    db.collection("produktuak").document(productId)
                                        .set(produktuaMap)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Produktua ondo eguneratu da", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Errorea eguneratzean", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    // ID-rik ez badago, produktu berria sortzen dugu
                                    db.collection("produktuak").add(produktuaMap)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Produktua ondo gorde da", Toast.LENGTH_SHORT).show()
                                            onAtzeraEgin()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Errorea gordetzean", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                        },
                        modifier = Modifier
                            .width(330.dp)
                            .padding(top = 50.dp),

                        shape = RoundedCornerShape(2.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ArreseGorria,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (productId.isNullOrEmpty()) "Gorde" else "Eguneratu",
                            fontSize = 20.sp
                        )
                    }

                }
                IconButton(
                    onClick = onAtzeraEgin
                    //modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SubdirectoryArrowLeft,
                        contentDescription = "Editatu",
                        modifier =
                            Modifier.size(40.dp)
                                .padding(top = 10.dp),

                        tint = Color.Red
                    )
                }

            }
        }


    }
}

fun motakKargatuEgunertuPantailan(callback: (List<Mota>) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("product_type").get()
        .addOnSuccessListener { emaitza ->
            val datuak = mutableListOf<Mota>()

            for (dokumentua in emaitza) {
                val izena = dokumentua.getString("izena")

                if (izena != null) {
                    // Dokumentuaren IDa zenbaki bihurtu
                    val id = dokumentua.id.toIntOrNull()

                    if (id != null) {
                        datuak.add(Mota(id, izena))
                    }
                }
            }

            callback(datuak)
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Errorea motak kargatzean", e)
            callback(emptyList())
        }
}