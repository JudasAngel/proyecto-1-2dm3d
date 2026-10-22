package com.example.login.ui.pantailak

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.apparrese.R
import com.example.main.ui.theme.ArreseGorria
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

@Composable
fun SaioHasieraPantaila() {

    var email by remember { mutableStateOf("") }
    var pasahitza by remember { mutableStateOf("") }
    var kargatzen by remember { mutableStateOf(false) }
    var saioaHasita by remember { mutableStateOf(false) }

    val context = LocalContext.current

    fun erakutsiToast(mezua: String) {
        Toast.makeText(
            context,
            mezua,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun firebaseErrorea(
        errorea: Exception?,
        erregistroa: Boolean
    ): String {
        return when (errorea) {

            is FirebaseAuthUserCollisionException ->
                "Email hau dagoeneko erregistratuta dago."

            is FirebaseAuthWeakPasswordException ->
                "Pasahitzak gutxienez 6 karaktere izan behar ditu."

            is FirebaseAuthInvalidUserException ->
                "Ez dago email honekin erregistratutako konturik."

            is FirebaseAuthInvalidCredentialsException ->
                if (erregistroa) {
                    "Ezin izan da kontua sortu. Egiaztatu emaila."
                } else {
                    "Emaila edo pasahitza ez dira zuzenak."
                }

            is FirebaseNetworkException ->
                "Konexio-arazo bat dago. Egiaztatu Interneteko konexioa."

            else ->
                if (erregistroa) {
                    "Ezin izan da kontua sortu. Saiatu berriro."
                } else {
                    "Ezin izan da saioa hasi. Saiatu berriro."
                }
        }
    }

    Scaffold { paddingValues ->

        if (saioaHasita) {

            // Pantaila erakutsi saioa behar bezala hastean
            DetalleProductoScreen()

        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                // Logo Arrese
                Image(
                    painter = painterResource(R.drawable.arrese),
                    contentDescription = "Arrese logoa",
                    modifier = Modifier.size(250.dp)
                )

                // Emaila
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    singleLine = true,
                    label = {
                        Text("Emaila")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    enabled = !kargatzen,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = ArreseGorria,
                        focusedBorderColor = ArreseGorria
                    )
                )

                // Pasahitza
                OutlinedTextField(
                    value = pasahitza,
                    onValueChange = { pasahitza = it },
                    singleLine = true,
                    label = {
                        Text("Pasahitza")
                    },
                    visualTransformation =
                        PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    enabled = !kargatzen,
                    modifier = Modifier.padding(top = 20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = ArreseGorria,
                        focusedBorderColor = ArreseGorria
                    )
                )

                // Botoiak
                Row(
                    modifier = Modifier.padding(top = 40.dp),
                    horizontalArrangement = Arrangement.spacedBy(26.dp)
                ) {

                    // Sartu erregistratua zaudenenan
                    Button(
                        onClick = {
                            if (email.isBlank() || pasahitza.isBlank()) {
                                erakutsiToast(
                                    "Bete emaila eta pasahitza."
                                )
                            } else {

                                kargatzen = true

                                FirebaseAuth.getInstance()
                                    .signInWithEmailAndPassword(
                                        email.trim(),
                                        pasahitza
                                    )
                                    .addOnCompleteListener { task ->

                                        kargatzen = false
                                        if (task.isSuccessful) {
                                            erakutsiToast(
                                                "Ongi etorri "+ email + "!"
                                            )

                                            // saioaHasi pantaila zabaldu
                                            saioaHasita = true

                                        } else {

                                            erakutsiToast(
                                                firebaseErrorea(
                                                    task.exception,
                                                    false
                                                )
                                            )
                                        }
                                    }
                            }
                        },
                        enabled = !kargatzen,
                        modifier = Modifier.width(130.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ArreseGorria,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Sartu")
                    }

                    // Erregistratu: ez duzunean kontua
                    Button(
                        onClick = {

                            if (email.isBlank() || pasahitza.isBlank()) {

                                erakutsiToast(
                                    "Bete emaila eta pasahitza."
                                )

                            } else if (pasahitza.length < 6) {

                                erakutsiToast(
                                    "Pasahitzak gutxienez 6 karaktere izan behar ditu."
                                )

                            } else {

                                kargatzen = true

                                FirebaseAuth.getInstance()
                                    .createUserWithEmailAndPassword(
                                        email.trim(),
                                        pasahitza
                                    )
                                    .addOnCompleteListener { task ->

                                        kargatzen = false

                                        if (task.isSuccessful) {
                                            erakutsiToast(
                                                "Erregistroa behar bezala egin da!"
                                            )

                                            saioaHasita = true

                                        } else {
                                            erakutsiToast(
                                                firebaseErrorea(
                                                    task.exception,
                                                    true
                                                )
                                            )
                                        }
                                    }
                            }
                        },
                        enabled = !kargatzen,
                        modifier = Modifier.width(130.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ArreseGorria,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Erregistratu")
                    }
                }

                Spacer(
                    modifier = Modifier.padding(top = 16.dp)
                )

                // Indicador de carga
                if (kargatzen) {
                    CircularProgressIndicator(
                        color = ArreseGorria
                    )
                }
            }
        }
    }
}