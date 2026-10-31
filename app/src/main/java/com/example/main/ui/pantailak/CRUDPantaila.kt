package com.example.main.ui.pantailak

import android.app.Activity
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
//esta fucnion recive on add o onedit que es para crear y para editar
@Composable
fun CRUDPantaila(
    onAdd: () -> Unit = {},
    onEdit: (String) -> Unit = {}
) {
    //lisat donde se guradan los porsuctos
    var productList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    //es una fucnion oque da informacion sirve apra poder usar toast
    val context = LocalContext.current
    //si conexto es un activity lo almacenamos sino ponemos null
    val activity = context as? Activity

    // Thread que carga los productos
    val kargatuDatuak = {
        //si la caivity no es igual a null enonces hempecamso el hilo apra cargar los datos
        if (activity != null) {
            Thread {
                val db = FirebaseFirestore.getInstance()
                db.collection("produktuak").get()
                    .addOnSuccessListener { result ->
                        //obitne los docuemnto o lo hencontrado en reuslt almacena los datos de produktuak
                        //doc tine cada bloqeu eentondes dentro de data mete los datso de cada doc y luego
                        //ha etse mismo le da la optrtunida de metrele un id uqe es el mismo que el de doc.
                        val list = result.documents.mapNotNull { doc ->
                            val data = doc.data
                            data?.toMutableMap()?.apply { put("id", doc.id) }
                        }
                        
                        // Interfazea eguneratzeko runOnUiThread
                        activity.runOnUiThread {
                            productList = list
                        }
                    }
            }.start()
        }
    }
//al inciar la apntalla llamamo al al fucnion de carga de datos
    LaunchedEffect(Unit) {
        kargatuDatuak()
    }

    Scaffold(
        floatingActionButton = {
            //camamos a la funcion de crear
            FloatingActionButton(onClick = onAdd) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Gehitu"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            //compueba si la lista esta vacia muetar el mensaje de kargatzen sino
            if (productList.isEmpty()) {
                Text(
                    text = "Produktuak Kargatzen...",
                    modifier = Modifier.padding(top = 16.dp),
                    color = Color.Gray
                )
                //carga los productos de la lista con un forech y usa basriavels para meter los datos en estas
            } else {
                productList.forEach { product ->
                    val docId = product["id"] as? String ?: ""
                    var izena = product["izena"] as? String ?: "Izenik gabe"
                    val prezioa = (product["prezioa"] as? Number)?.toDouble() ?: 0.0
                    val stock = (product["stock"] as? Number)?.toInt() ?: 0
                    //si ziena esta vacio pone izenabarik
                    if(izena.trim() == "") {
                        izena = "Izenik gabe"
                    }
                    //lammamos a una fucnion en la que le pasamos parametro y loq ue deve pasarl al ulsar los diferentes bootnes
                    ProductRow(
                        name = izena,
                        price = prezioa,
                        stock = stock,
                        //malamos la fucnion eidt y le apsom id
                        onEdit = { onEdit(docId) },
                        //si no hay id o ativyty borramos el elmento
                        onDelete = {
                            if (docId.isNotEmpty() && activity != null) {
                                // Thread para borrar
                                Thread {
                                    FirebaseFirestore.getInstance()
                                        .collection("produktuak").document(docId)
                                        .delete()
                                        .addOnSuccessListener {
                                            activity.runOnUiThread {
                                                Toast.makeText(context, "Produktua ezabatuta", Toast.LENGTH_SHORT).show()
                                                kargatuDatuak()
                                            }
                                        }
                                }.start()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
//la funcion para cargar lso porducos y en etsa se cargan los datos y pasmos los paramtros necesariso
@Composable
fun ProductRow(
    name: String,
    price: Double,
    stock: Int,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
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
                onClick = onEdit,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editatu",
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
                    contentDescription = "Ezabatu",
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
fun ProductRowPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        ProductRow(
            name = "Gurinezko opila",
            price = 2.40,
            stock = 15,
            onDelete = {}
        )
    }
}
