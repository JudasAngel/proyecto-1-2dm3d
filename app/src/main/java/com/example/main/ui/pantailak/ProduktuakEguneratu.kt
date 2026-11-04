package com.example.main.ui.pantailak

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.main.ui.data.Balidazioa
import com.example.main.ui.data.Kategoria
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProduktuakEguneratu(
    modifier: Modifier = Modifier,
    productId: String?,
    kategoriak: List<Kategoria>,
    onAtzeraEgin: () -> Unit = {}
) {
    val context = LocalContext.current
    val db = remember { FirebaseFirestore.getInstance() }
    val editatzen = !productId.isNullOrEmpty()

    // Formularioko eremuak
    var izena by remember { mutableStateOf("") }
    var deskribapena by remember { mutableStateOf("") }
    var stocka by remember { mutableStateOf("") }
    var prezioa by remember { mutableStateOf("") }
    var argazkia by remember { mutableStateOf("") }
    // Mota-objektua ez dugu gordetzen, ID-a bakarrik. Izena kategoriak-etik kalkulatzen da.
    var motaId by remember { mutableStateOf<Int?>(null) }

    // Pantailaren fluxua
    var kargatzen by remember { mutableStateOf(editatzen) }
    var gordetzen by remember { mutableStateOf(false) }
    var saiatuDa by remember { mutableStateOf(false) }
    var motaMenuIrekita by remember { mutableStateOf(false) }

    val kategoriaAukeratua = kategoriak.find { it.id == motaId?.toString() }

    // Balidazioa: eremu bakoitzaren egoera
    val izenaBaliozkoa = izena.isNotBlank()
    val stockBaliozkoa = Balidazioa.stockBaliozkoa(stocka)
    val prezioBaliozkoa = Balidazioa.prezioaBaliozkoa(prezioa)
    val motaBaliozkoa = motaId != null
    val denaBaliozkoa = izenaBaliozkoa && stockBaliozkoa && prezioBaliozkoa && motaBaliozkoa

    // Errorea erakutsi bakarrik lehen saiakeraren ondoren
    val izenaOkerra = saiatuDa && !izenaBaliozkoa
    val stockOkerra = saiatuDa && !stockBaliozkoa
    val prezioOkerra = saiatuDa && !prezioBaliozkoa
    val motaOkerra = saiatuDa && !motaBaliozkoa

    // Editatzean: produktuaren datuak behin bakarrik kargatu
    LaunchedEffect(productId) {
        val id = productId
        if (id.isNullOrEmpty()) {
            kargatzen = false
            return@LaunchedEffect
        }

        kargatzen = true
        db.collection("produktuak").document(id).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    izena = doc.getString("izena") ?: ""
                    deskribapena = doc.getString("deskribapena") ?: ""
                    stocka = doc.get("stock")?.toString() ?: ""
                    prezioa = doc.get("prezioa")?.toString() ?: ""
                    argazkia = doc.getString("argazkia") ?: ""
                    motaId = (doc.get("mota") as? Number)?.toInt()
                } else {
                    Toast.makeText(context, "Ez da produktua aurkitu", Toast.LENGTH_SHORT).show()
                }
                kargatzen = false
            }
            .addOnFailureListener { e ->
                Log.e("EGUNERATU", "Errorea produktua kargatzean", e)
                Toast.makeText(context, "Errorea produktua kargatzean", Toast.LENGTH_SHORT).show()
                kargatzen = false
            }
    }

    fun akatsa(mezua: String, e: Exception) {
        gordetzen = false
        Log.e("EGUNERATU", mezua, e)
        Toast.makeText(context, mezua, Toast.LENGTH_LONG).show()
    }

    fun gorde() {
        saiatuDa = true
        val mota = motaId
        if (!denaBaliozkoa || mota == null || gordetzen) return

        gordetzen = true
        val datuak = hashMapOf<String, Any>(
            "izena" to izena.trim(),
            "deskribapena" to deskribapena.trim(),
            "stock" to stocka.trim().toInt(),
            "prezioa" to prezioa.trim().replace(",", ".").toDouble(),
            "mota" to mota,
            "argazkia" to argazkia.trim()
        )

        val id = productId
        if (id.isNullOrEmpty()) {
            // Produktu berria: lehenik izen eta mota berdina duen bat dagoen begiratu
            db.collection("produktuak")
                .whereEqualTo("izena", izena.trim())
                .whereEqualTo("mota", mota)
                .get()
                .addOnSuccessListener { emaitza ->
                    if (!emaitza.isEmpty) {
                        gordetzen = false
                        Toast.makeText(context, "Produktu hau dagoeneko badago", Toast.LENGTH_LONG).show()
                    } else {
                        db.collection("produktuak").add(datuak)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Produktua ondo gorde da", Toast.LENGTH_SHORT).show()
                                onAtzeraEgin()
                            }
                            .addOnFailureListener { e -> akatsa("Errorea gordetzean", e) }
                    }
                }
                .addOnFailureListener { e -> akatsa("Errorea egiaztatzean", e) }
        } else {
            // Editatzen ari gara: dokumentua eguneratu
            db.collection("produktuak").document(id)
                .set(datuak)
                .addOnSuccessListener {
                    Toast.makeText(context, "Produktua ondo eguneratu da", Toast.LENGTH_SHORT).show()
                    onAtzeraEgin()
                }
                .addOnFailureListener { e -> akatsa("Errorea eguneratzean", e) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (editatzen) "Produktua eguneratu" else "Produktua gehitu",
            style = MaterialTheme.typography.headlineMedium,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
        )
        HorizontalDivider()

        if (kargatzen) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            EremuaTestua(
                balioa = izena,
                onAldatu = { izena = it },
                etiketa = "Izena",
                errorea = izenaOkerra,
                errorMezua = "Sartu produktuaren izena"
            )

            EremuaTestua(
                balioa = deskribapena,
                onAldatu = { deskribapena = it },
                etiketa = "Deskribapena",
                errorea = false,
                errorMezua = "",
                singleLine = false
            )

            EremuaTestua(
                balioa = stocka,
                onAldatu = { stocka = it },
                etiketa = "Stock-a",
                errorea = stockOkerra,
                errorMezua = "Stock-ak zenbaki osoa izan behar du (0 edo handiagoa)",
                teklatua = KeyboardType.Number
            )

            EremuaTestua(
                balioa = prezioa,
                onAldatu = { prezioa = it },
                etiketa = "Prezioa",
                errorea = prezioOkerra,
                errorMezua = "Prezioak zenbaki positiboa izan behar du (adib. 2,50)",
                teklatua = KeyboardType.Decimal
            )

            // Mota: dropdown-a (Material3-ren osagai egokia)
            ExposedDropdownMenuBox(
                expanded = motaMenuIrekita,
                onExpandedChange = { motaMenuIrekita = it }
            ) {
                OutlinedTextField(
                    value = kategoriaAukeratua?.izena ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Mota") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = motaMenuIrekita)
                    },
                    isError = motaOkerra,
                    supportingText = { if (motaOkerra) Text("Aukeratu produktu mota") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = motaMenuIrekita,
                    onDismissRequest = { motaMenuIrekita = false }
                ) {
                    kategoriak.forEach { kat ->
                        DropdownMenuItem(
                            text = { Text(kat.izena) },
                            onClick = {
                                motaId = kat.id.toIntOrNull()
                                motaMenuIrekita = false
                            }
                        )
                    }
                }
            }

            EremuaTestua(
                balioa = argazkia,
                onAldatu = { argazkia = it },
                etiketa = "Argazkia",
                errorea = false,
                errorMezua = ""
            )
            Text(
                text = "Irudiaren izena drawable-arena da, luzapenik gabe eta minuskulaz (adib. ogia)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onAtzeraEgin,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text("Itzuli", fontSize = 18.sp)
                }

                Button(
                    onClick = { gorde() },
                    enabled = !gordetzen,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text(
                        text = when {
                            gordetzen -> "Gordetzen..."
                            editatzen -> "Eguneratu"
                            else -> "Gorde"
                        },
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

// Eremu bakarreko testu-eremua, errore mezuarekin. Kolorea theme-tik dator.
@Composable
private fun EremuaTestua(
    balioa: String,
    onAldatu: (String) -> Unit,
    etiketa: String,
    errorea: Boolean,
    errorMezua: String,
    teklatua: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = balioa,
        onValueChange = onAldatu,
        label = { Text(etiketa) },
        isError = errorea,
        supportingText = { if (errorea) Text(errorMezua) },
        keyboardOptions = KeyboardOptions(keyboardType = teklatua),
        singleLine = singleLine,
        modifier = Modifier.fillMaxWidth()
    )
}