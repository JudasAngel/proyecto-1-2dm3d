package com.example.main.ui.pantailak

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.apparrese.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ErregistroPantaila(modifier: Modifier = Modifier, onBack: () -> Unit = {}) {

    val context = LocalContext.current
    var id by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var gmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nan by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        // ENPRESAREN LOGOA
        Image(
            painter = painterResource(id = R.drawable.arrese),
            contentDescription = "Logo de Arrese",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(275.dp)
                .height(150.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),

            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // IZENA
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Izena:",
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    modifier = Modifier.width(250.dp),
                    placeholder = {
                        Text("Idatzi zure izena")
                    },

                    // Erabilitako eta erabili gabeko ertzaren kolorea.
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFD32F2F),
                        focusedBorderColor = Color(0xFFD32F2F)
                    )
                )
            }

            // ABIZENA IDAZTEKO EREMUA
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Abizena:",
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )

                // ABIZENA
                OutlinedTextField(
                    value = apellido,

                    // Abizenaren balioa eguneratzen du.
                    onValueChange = { apellido = it },

                    modifier = Modifier.width(250.dp),

                    placeholder = {
                        Text("Idatzi zure abizena")
                    },

                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFD32F2F),
                        focusedBorderColor = Color(0xFFD32F2F)
                    )
                )
            }

            // NAN-A
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "NAN:",
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = nan,
                    onValueChange = { nan = it },
                    modifier = Modifier.width(250.dp),

                    placeholder = {
                        Text("Idatzi zure NANa")
                    },

                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFD32F2F),
                        focusedBorderColor = Color(0xFFD32F2F)
                    )
                )
            }

            // GMAIL
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Gmail:",
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = gmail,
                    onValueChange = { gmail = it },
                    modifier = Modifier.width(250.dp),

                    placeholder = {
                        Text("Idatzi zure Gmail")
                    },

                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFD32F2F),
                        focusedBorderColor = Color(0xFFD32F2F)
                    )
                )
            }

            // PASAHITZA
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Pasahitza:",
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.width(250.dp),

                    placeholder = {
                        Text("Idatzi zure pasahitza")
                    },

                    // Pasahitza ezkutatzeko
                    visualTransformation = PasswordVisualTransformation(),

                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFD32F2F),
                        focusedBorderColor = Color(0xFFD32F2F)
                    )
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),

            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            Button(
                onClick = {
                    onBack()
                },

                modifier = Modifier
                    .width(150.dp)
                    .height(50.dp),

                shape = RoundedCornerShape(8.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F)
                )
            ) {
                Text(text = "ITZULI")
            }

            // SORTU BOTOIA
            Button(
                onClick = {

                    // Eremuren bat hutsik badago, errore-mezua erakusten da
                    if (nombre.isBlank() || apellido.isBlank() || gmail.isBlank() || password.isBlank() || nan.isBlank()) {
                        Toast.makeText(context, "Eremu guztiak beteta izan behar dira", Toast.LENGTH_SHORT).show()

                        return@Button
                    }

                    // NAN-AREN FORMATUA EGIAZTATU
                    val nanRegex = Regex("^[0-9]{8}[A-Za-z]$")

                    if (!nanRegex.matches(nan.trim())) {
                        Toast.makeText(context, "NANak 8 zenbaki eta letra 1 izan behar ditu (adib. 12345678A)", Toast.LENGTH_SHORT).show()

                        // NANaren formatua okerra bada, prozesua gelditzen da
                        return@Button
                    }

                    //EMAILAREN FORMATUA EGIAZTATU

                    // Emailak helbide elektroniko baten oinarrizko formatua
                    if (!android.util.Patterns.EMAIL_ADDRESS
                            .matcher(gmail.trim())
                            .matches()
                    ) {
                        Toast.makeText(context, "E-maila formatu egokian izan behar da", Toast.LENGTH_SHORT).show()

                        // Emailaren formatua okerra bada, gelditu egiten da
                        return@Button
                    }

                   // PASAHITZAREN LUZERA EGIAZTATU
                    if (password.length < 6) {
                        Toast.makeText(context, "Pasahitzak gutxienez 6 karaktere izan behar ditu", Toast.LENGTH_SHORT).show()

                        // Baldintza betetzen ez bada ez du kontua sortzen
                        return@Button
                    }

                    // FIREBASE ZERBITZUAK LORTU.
                    val auth = Firebase.auth

                    // datu-basean erabiltzailearen datuak gordetzeko.
                    val db = FirebaseFirestore.getInstance()

                    // FIREBASE AUTHENTICATION-EAN KONTUA SORTU.

                    // Emaila eta pasahitza erabiliz kontu berria sortzen saiatu
                    auth.createUserWithEmailAndPassword(
                        gmail.trim(),
                        password
                    ).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("Firebase", "createUserWithEmail:success")

                            // Firebase Authentication-en sortutako erabiltzailearen UIDa lortzen du
                            id = auth.currentUser?.uid ?: ""

                            val userData = hashMapOf(
                                "uid" to id,
                                "izena" to nombre.trim(),
                                "abizena" to apellido.trim(),
                                "email" to gmail.trim(),

                                // NANa maiuskulaz gordetzen du
                                "NAN" to nan.trim().uppercase(),

                                // Erabiltzaile motaren identifikatzailea gordetzen du
                                "id-type" to 1
                            )

                            // DATUAK FIRESTORE-N GORDE

                            // "erabiltzaileak" bilduman gordetzen ditu datuak
                            db.collection("erabiltzaileak")
                                .document(id)
                                .set(userData)
                                // Datuak behar bezala gordetzen badira exekutatzen da
                                .addOnSuccessListener {
                                    // Logcat-en datuak gorde direla adierazten du
                                    Log.d("Firestore", "Erabiltzailea ongi gordeta ID honekin: $id")

                                    // Erregistroa amaitu dela jakinarazten dio erabiltzaileari
                                    Toast.makeText(context, "Erregistroa arrakastatsua eta gordeta!", Toast.LENGTH_SHORT).show()

                                    onBack()
                                }

                                // Firestore-n datuak gordetzean errorea gertatzen bada
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Errorea datuak gordetzen", e)

                                    // Errorearen mezua erabiltzaileari erakusten dio
                                    Toast.makeText(context, "Errorea Firestore-n: ${e.message}", Toast.LENGTH_LONG).show()
                                }

                        } else {

                            // Firebase Authentication-ek kontua sortu ezin badu
                            Log.w(
                                "Firebase",
                                "createUserWithEmail:failure",
                                task.exception
                            )

                            // Kontua sortzean gertatutako errorearen mezua erakusten du
                            Toast.makeText(context, "Errorea erregistroan: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                },

                modifier = Modifier
                    .width(150.dp)
                    .height(50.dp),

                shape = RoundedCornerShape(8.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F)
                )
            ) {
                Text(
                    text = "SORTU",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}