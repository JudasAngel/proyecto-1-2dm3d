    package com.example.main.ui.pantailak

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
fun ErregistroPantaila() {

    val context = LocalContext.current

    // Firebase Auth eta Firestore
    val auth = Firebase.auth
    val db = FirebaseFirestore.getInstance()

    // Variables para guardar lo que escribe el usuario
    var id by remember {mutableStateOf("")}
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var gmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nan by remember { mutableStateOf("")}

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // LOGO
        Image(
            painter = painterResource(id = R.drawable.arrese),
            contentDescription = "Logo de Ampese",
            modifier = Modifier.size(275.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // IZENA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Izena:",
                fontSize = 18.sp
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                modifier = Modifier.width(250.dp),
                placeholder = {
                    Text("Escribe tu nombre")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFD32F2F),
                    focusedBorderColor = Color(0xFFD32F2F)
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ABIZENA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Abizena:",
                fontSize = 18.sp
            )

            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                modifier = Modifier.width(250.dp),
                placeholder = {
                    Text("Escribe tu apellido")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFD32F2F),
                    focusedBorderColor = Color(0xFFD32F2F)
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        //NAN
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "NAN:",
                fontSize = 18.sp
            )

            OutlinedTextField(
                value = nan,
                onValueChange = { nan = it },
                modifier = Modifier.width(250.dp),
                placeholder = {
                    Text("idatzi zure NANa")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFD32F2F),
                    focusedBorderColor = Color(0xFFD32F2F)
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // GMAIL
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Gmail:",
                fontSize = 18.sp
            )

            OutlinedTextField(
                value = gmail,
                onValueChange = { gmail = it },
                modifier = Modifier.width(250.dp),
                placeholder = {
                    Text("Escribe tu Gmail")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFD32F2F),
                    focusedBorderColor = Color(0xFFD32F2F)
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // PASAHITZA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Pasahitza:",
                fontSize = 18.sp
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.width(250.dp),
                placeholder = {
                    Text("Escribe tu contraseña")
                },
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFD32F2F),
                    focusedBorderColor = Color(0xFFD32F2F)
                )
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // BOTÓN SORTU
        Button(
            onClick = {
                if (nombre.isBlank() || apellido.isBlank() || gmail.isBlank() || password.isBlank() || nan.isBlank()) {
                    Toast.makeText(context, "Eremu guztiak beteta izan behar dira", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val nanRegex = Regex("^[0-9]{8}[A-Za-z]$")
                if (!nanRegex.matches(nan.trim())) {
                    Toast.makeText(context, "NANak 8 zenbaki eta letra 1 izan behar ditu (adib. 12345678A)", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(gmail.trim()).matches()) {
                    Toast.makeText(context, "E-maila formatu egokian izan behar da", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (password.trim().length < 6) {
                    Toast.makeText(context, "Pasahitzak gutxienez 6 karaktere izan behar ditu", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                auth.createUserWithEmailAndPassword(gmail.trim(), password.trim())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("Firebase", "createUserWithEmail:success")
                            id = auth.currentUser?.uid ?: ""
                            
                            val userData = hashMapOf(
                                "uid" to id,
                                "izena" to nombre,
                                "abizena" to apellido,
                                "email" to gmail,
                                "NAN" to nan,
                                "id-type" to 1
                            )

                            db.collection("erabiltzaileak").document(id).set(userData)
                                .addOnSuccessListener {
                                    Log.d("Firestore", "Erabiltzailea ongi gordeta ID honekin: $id")
                                    Toast.makeText(context, "Erregistroa arrakastatsua eta gordeta!", Toast.LENGTH_SHORT).show()
                                    (context as? ComponentActivity)?.finish()
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Errorea datuak gordetzen", e)
                                    Toast.makeText(context, "Errorea Firestore-n: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Log.w("Firebase", "createUserWithEmail:failure", task.exception)
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
