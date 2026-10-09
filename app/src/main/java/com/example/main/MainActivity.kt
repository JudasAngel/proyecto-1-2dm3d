package com.example.apparrese

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.main.ui.theme.LoginTheme
import com.example.login.ui.pantailak.SaioHasieraPantaila

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            LoginTheme {
                SaioHasieraPantaila()
            }
        }
    }
}