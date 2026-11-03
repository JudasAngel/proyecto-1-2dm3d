package com.example.login.ui.pantailak

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.apparrese.R
import com.example.main.ui.data.Produktua
import com.example.main.ui.data.produktuZerrenda
import com.example.main.ui.theme.ArreseGorria
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    productoId: String = "",
    onVolver: () -> Unit = {},
    onSaskiraGehitu: (Produktua) -> Unit = {}
) {
    val context = LocalContext.current

    var produktua by remember { mutableStateOf<Produktua?>(null) }
    var kargatzen by remember { mutableStateOf(true) }
    var errorea by remember { mutableStateOf<String?>(null) }

    // Produktua Firestore-tik kargatu
    LaunchedEffect(productoId) {
        if (productoId.isBlank()) {
            errorea = "Ez da produkturik hautatu"
            kargatzen = false
            return@LaunchedEffect
        }

        FirebaseFirestore.getInstance()
            .collection("produktuak")
            .document(productoId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    produktua = Produktua(
                        id = document.id,
                        izena = document.getString("izena") ?: "",
                        deskribapena = document.getString("deskribapena") ?: "",
                        prezioa = document.getDouble("prezioa") ?: 0.0,
                        argazkia = document.getString("argazkia"),
                        stocka = (document.getLong("stock") ?: 0L).toInt(),
                        mota = (document.getLong("mota") ?: 0L).toInt()
                    )
                } else {
                    errorea = "Ez da produktua aurkitu"
                }

                kargatzen = false
            }
            .addOnFailureListener { exception ->
                errorea = exception.message ?: "Errorea produktua kargatzean"
                kargatzen = false
            }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        // Goiko barra gorria eta logoa
        topBar = {
            TopAppBar(
                modifier = Modifier.height(90.dp),
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.logoarrese2),
                        contentDescription = "Arrese logoa",
                        modifier = Modifier.height(58.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ArreseGorria
                )
            )
        },

        // Beheko zatia: guztira eta botoiak
        bottomBar = {
            produktua?.let { item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Guztira: %.2f €".format(item.prezioa),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(bottom = 10.dp)
                    )

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
                            Text(
                                text = "Itzuli",
                                fontSize = 18.sp
                            )
                        }

                        Button(
                            onClick = {
                                produktua?.let { producto ->
                                    produktuZerrenda.add(producto.id)
                                    onSaskiraGehitu(producto)

                                    Toast.makeText(
                                        context,
                                        "Produktua saskira gehitu da",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            enabled = item.stocka > 0
                        ) {
                            Text(
                                text = "Saskira gehitu",
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            when {
                kargatzen -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                errorea != null -> {
                    Text(
                        text = errorea ?: "",
                        modifier = Modifier.padding(top = 30.dp),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 18.sp
                    )
                }

                produktua != null -> {
                    val item = produktua!!

                    // Produktuaren irudia
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 55.dp, bottom = 35.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val imageName = item.argazkia
                            ?.substringBeforeLast(".")
                            ?.lowercase()
                            ?.replace(" ", "_")
                            ?: ""

                        val imageId = context.resources.getIdentifier(
                            imageName,
                            "drawable",
                            context.packageName
                        )

                        Image(
                            painter = painterResource(
                                id = if (imageId != 0) {
                                    imageId
                                } else {
                                    R.drawable.predeterminada
                                }
                            ),
                            contentDescription = item.izena,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                        )
                    }

                    // Produktuaren izena
                    Text(
                        text = item.izena,
                        fontSize = 34.sp,
                        lineHeight = 42.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Prezioa
                    Text(
                        text = "%.2f €".format(item.prezioa),
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    // Deskribapena
                    Text(
                        text = "Deskribapena",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = item.deskribapena,
                        fontSize = 17.sp,
                        lineHeight = 27.sp
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    // Stocka
                    Text(
                        text = "Stock: ${item.stocka}",
                        fontSize = 17.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}