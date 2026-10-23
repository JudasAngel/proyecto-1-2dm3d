package com.example.main.ui.data

//Erabiltazile klass bat sortu dugu erabiltaleen datuekin.
 data class Erabiltzailea(
    val nan: String,
    val izena: String,
    val abizena: String,
    val email: String,
    val pass: String,
    val tipo: Int = 2
)