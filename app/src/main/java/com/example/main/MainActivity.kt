package com.example.apparrese

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.arresees.ui.theme.ArreseesTheme
import com.example.login.ui.pantailak.SaioHasieraPantaila
import com.example.main.ui.pantailak.CRUDPantaila
import com.example.main.ui.pantailak.ProduktuakGehitu
import com.example.main.ui.pantailak.ProduktuakPantaila

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.main.ui.pantailak.SaskiPantaila

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ArreseesTheme {
                NireAplikazioa()

                var unekoPantaila by remember { mutableStateOf("produktuak") }

                when (unekoPantaila) {
                    "saskia" -> SaskiPantaila(
                        onHasieraClick = { unekoPantaila = "produktuak" },
                        onProduktuakClick = { unekoPantaila = "produktuak" }
                    )
                    else -> ProduktuakPantaila(
                        onSaskiClick = { unekoPantaila = "saskia" }
                    )
                }
            }
        }
    }
}