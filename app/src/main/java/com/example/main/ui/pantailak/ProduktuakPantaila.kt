package com.example.main.ui.pantailak

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.apparrese.R
import com.example.main.ui.theme.ArreseGorria
import com.example.main.ui.data.Produktua
import com.example.main.ui.data.produktuZerrenda
import androidx.compose.foundation.clickable
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProduktuakPantaila(
    onSaskiClick: () -> Unit = {}
) {
    var menuAbierto by remember { mutableStateOf(false) }

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
                    top = 16.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            item {
                Text(
                    text = "Produktuak",
                    fontSize = 30.sp,
                    fontFamily = FontFamily.Serif,
                    color = MaterialTheme.colorScheme.onSurface

                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            if (errorea != null) {
                item {
                    Text(text = "Error: $errorea",
                        color = MaterialTheme.colorScheme.onSurface
                    )

                }
            } else if (produktuak.isEmpty()) {
                item {
                    Text("Kargatzen ...")
                }
            } else {

                items(produktuak) { produktua ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {

                        android.util.Log.d("IMG", "Firebase: ${produktua.argazkia}")

                        val imageResId = produktua.argazkia?.let {
                            context.resources.getIdentifier(
                                it.lowercase(),
                                "drawable",
                                context.packageName
                            )
                        } ?: 0


                        android.util.Log.d("IMG", "ID Imagen: $imageResId")

                        Image(
                            painter = painterResource(
                                id = if (imageResId != 0)
                                    imageResId
                                else
                                    R.drawable.predeterminada
                            ),
                            contentDescription = produktua.izena,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        )


                        Text(
                            modifier = Modifier.padding( top= 10.dp),
                            text = motak[produktua.mota] ?: "Sin categoría",
                            fontSize = 22.sp,
                            fontFamily = FontFamily.Serif,
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth().padding( top= 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = produktua.izena,
                                fontSize = 22.sp,
                                fontFamily = FontFamily.Serif,
                                color = MaterialTheme.colorScheme.onSurface

                            )

                            Text(
                                text = "${produktua.prezioa} €",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface

                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier
                                .width(500.dp)
                                .padding(vertical = 10.dp),
                            color = MaterialTheme.colorScheme.onSurface


                        )
                        Text(
                            text = "Sartu karritora ➝",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    produktuZerrenda.add(produktua.id)
                                    Toast.makeText(context, "${produktua.izena} karritora gehitu da", Toast.LENGTH_SHORT).show()
                                },
                            textAlign = TextAlign.End
                        )


                    }
                }
            }
        }
    }
}

fun ProduktuakKargatu(
    resultado: (List<Produktua>) -> Unit,
    errorea: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    db.collection("produktuak")
        .get()
        .addOnSuccessListener { documentos ->
            android.util.Log.d(
                "FIRESTORE",
                "Documentos encontrados: ${documentos.size()}"
            )
            documentos.documents.forEach { documento ->
                android.util.Log.d(
                    "FIRESTORE",
                    "ID: ${documento.id}, datos: ${documento.data}"
                )
            }

            val lista = documentos.documents.mapNotNull { documento ->
                try {
                    documento.toObject(Produktua::class.java)?.copy(id = documento.id)
                } catch (e: Exception) {
                    android.util.Log.e(
                        "FIRESTORE",
                        "Error en documento ${documento.id}",
                        e
                    )
                    null
                }
            }

            android.util.Log.d(
                "FIRESTORE",
                "Productos convertidos: ${lista.size}"
            )
            resultado(lista)
        }
        .addOnFailureListener { e ->
            errorea(e.message ?: "Errorea produktuak kargatzean")
        }
}

fun KargatuMotak(
    resultado: (Map<Int, String>) -> Unit
) {
    FirebaseFirestore.getInstance()
        .collection("product_type")
        .get()
        .addOnSuccessListener { docs ->

            val motak = docs.documents.associate { doc ->
                doc.id.toInt() to (doc.getString("izena") ?: "")
            }

            resultado(motak)
        }
}