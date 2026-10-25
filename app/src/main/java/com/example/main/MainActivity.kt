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

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ArreseesTheme {
                NireAplikazioa()

            }
        }
    }
}