package com.example.main.ui.data

import androidx.compose.runtime.mutableStateListOf

val produktuZerrenda = mutableStateListOf<String>()

data class Produktua(
    val id: String = "",
    val mota: Int = 0,
    val izena: String = "",
    val deskribapena: String = "",
    val prezioa: Double = 0.0,
    val argazkia: String? = null,
    val stocka: Int = 0
)