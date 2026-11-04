package com.example.main.ui.data

object Balidazioa {
    fun stockBaliozkoa(stock: String): Boolean =
        stock.trim().toIntOrNull()?.let { it >= 0 } ?: false

    fun prezioaBaliozkoa(prezioa: String): Boolean =
        prezioa.trim().replace(",", ".").toDoubleOrNull()?.let { it >= 0.0 } ?: false
}

