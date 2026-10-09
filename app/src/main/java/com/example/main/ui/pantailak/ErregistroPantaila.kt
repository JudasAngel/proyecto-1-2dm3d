package com.example.main.ui.pantailak

import com.example.apparrese.R
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PantallaRegistro() {

    // Variables para guardar lo que escribe el usuario
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var gmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
            onClick = {},
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