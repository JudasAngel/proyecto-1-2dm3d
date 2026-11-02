package com.example.main.ui.pantailak

import android.app.Activity
import android.util.Log
import com.example.main.ui.data.Produktua
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class SaskiLotea(
    private val ids: List<String>,
    private val kopuruak: Map<String, Int>,
    private val lock: Any,                         // protege las dos listas compartidas
    private val emaitza: MutableList<SaskiItem>,
    private val errorak: MutableList<String>
) : Runnable {

    override fun run() {
        Log.d("SASKI", "Lotea hasi: ${ids.size} id")
        try {
            val snap = Tasks.await(
                FirebaseFirestore.getInstance().collection("produktuak")
                    .whereIn(FieldPath.documentId(), ids).get(),
                10, TimeUnit.SECONDS
            )
            val items = snap.documents.mapNotNull { doc ->
                doc.toObject(Produktua::class.java)?.let {
                    SaskiItem(doc.id, it.izena, it.prezioa, kopuruak[doc.id] ?: 1)
                }
            }
            synchronized(lock) { emaitza.addAll(items) }     // varios hilos a la vez
            Log.d("SASKI", "Lotea amaituta: ${items.size} produktu")
        } catch (e: InterruptedException) {
            Log.d("SASKI", "Lotea ezeztatuta")
            Thread.currentThread().interrupt()
        } catch (e: Exception) {
            Log.e("SASKI", "Lotea huts egin du", e)
            synchronized(lock) { errorak.add("Ezin izan da saskia kargatu") }   // mensaje propio
        }
    }
}

fun kargatuSaskia(
    activity: Activity,
    ids: List<String>,
    onOk: (List<SaskiItem>) -> Unit,
    onErr: (String) -> Unit
): Thread {
    val kopuruak = ids.groupingBy { it }.eachCount()

    val coordinador = Thread {
        val lock = Any()
        val emaitza = mutableListOf<SaskiItem>()
        val errorak = mutableListOf<String>()

        val hariak = kopuruak.keys.toList().chunked(10)
            .map { Thread(SaskiLotea(it, kopuruak, lock, emaitza, errorak)) }

        hariak.forEach { it.start() }
        try {
            hariak.forEach { it.join() }           // espera aquí, nunca en el hilo principal
        } catch (e: InterruptedException) {
            hariak.forEach { it.interrupt() }      // cancelación: se propaga a los lotes
            Log.d("SASKI", "Koordinatzailea ezeztatuta")
            return@Thread
        }

        val (items, fallos) = synchronized(lock) { emaitza.toList() to errorak.toList() }
        if (Thread.currentThread().isInterrupted) return@Thread

        activity.runOnUiThread {
            if (fallos.isNotEmpty() && items.isEmpty()) onErr(fallos.first())
            else onOk(items.sortedBy { it.izena })     // los lotes terminan en orden aleatorio
        }
    }
    coordinador.start()
    return coordinador
}