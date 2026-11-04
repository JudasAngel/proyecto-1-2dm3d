package com.example.main.ui.pantailak

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.main.ui.data.Kategoria
import com.example.main.ui.theme.ArreseGorria

@Composable
fun KategoriaMenua(
    kategoriak: List<Kategoria>,
    aukeratua: Kategoria?,
    onAukeratu: (Kategoria?) -> Unit
) {
    var irekita by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { irekita = true }) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "Kategoriak ireki",
                tint = Color.White
            )
        }

        DropdownMenu(
            expanded = irekita,
            onDismissRequest = { irekita = false },
            containerColor = ArreseGorria
        ) {
            DropdownMenuItem(
                text = { Text("Produktu guztiak", color = Color.White) },
                onClick = {
                    onAukeratu(null)
                    irekita = false
                }
            )
            kategoriak.forEach { kategoria ->
                DropdownMenuItem(
                    text = { Text(kategoria.izena, color = Color.White) },
                    onClick = {
                        onAukeratu(kategoria)
                        irekita = false
                    }
                )
            }
        }
    }
}