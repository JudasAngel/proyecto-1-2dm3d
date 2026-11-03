package com.example.apparrese

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.arresees.ui.theme.ArreseesTheme
import com.example.main.MusikaKudeatzailea


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
    //para que sis e sale de la palicaion la usca no se escuche en sgeundo palnao
    override fun onDestroy() {
        if (isFinishing) MusikaKudeatzailea.gelditu()
        super.onDestroy()
    }
}