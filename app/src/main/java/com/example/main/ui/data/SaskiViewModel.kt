package com.example.main.ui.data

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

// Saskiaren iturri bakarra: produktuen ID-en zerrenda (ID errepikatuta = unitate bat gehiago).
class SaskiViewModel : ViewModel() {

    private val _ids = mutableStateListOf<String>()
    val ids: List<String> get() = _ids

    fun kopurua(id: String): Int = _ids.count { it == id }

    // Stock-a gainditzen ez bada bakarrik gehitzen du
    fun gehitu(id: String, stocka: Int) {
        if (kopurua(id) < stocka) _ids.add(id)
    }

    fun kendu(id: String) { _ids.remove(id) }

    fun ezabatu(id: String) { _ids.removeAll { it == id } }

    fun hustu() { _ids.clear() }
}