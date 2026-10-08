package com.example.login.ui.pantailak

import androidx.compose.foundation.Image
import com.example.main.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.main.ui.theme.ArreseGorria

@Composable
fun SaioHasieraPantaila() {

    Scaffold { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Arrese logoa
            Image(
                painter = painterResource(R.drawable.arreselogo),
                contentDescription = "Arrese logoa",
                modifier = Modifier
                    .size(250.dp)
            )

            ErabPass()

            Botoiak()
        }
    }
}


@Composable
fun ErabPass() {
    var email by remember { mutableStateOf("") }
    var pasahitza by remember { mutableStateOf("") }

    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        singleLine = true,
        label = { Text("Emaila") },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = ArreseGorria,
            focusedBorderColor = ArreseGorria
        )

    )

    OutlinedTextField(
        value = pasahitza,
        onValueChange = { pasahitza = it },
        singleLine = true,
        label = { Text("Pasahitza") },
        visualTransformation = PasswordVisualTransformation(), //pasahitza sartzerakoan, ez ikustea
        modifier = Modifier
            .padding(top = 20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = ArreseGorria,
            focusedBorderColor = ArreseGorria
        )
    )
}

@Composable
fun Botoiak(){
    Row(
        modifier = Modifier
            .padding(top = 70.dp),
        horizontalArrangement = Arrangement.spacedBy(26.dp)
    ) {

        Button(
            onClick = {},
            modifier = Modifier.width(130.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Sartu")
        }

        Button(
            onClick = {},
            modifier = Modifier.width(130.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Erregistratu")
        }


    }
}
