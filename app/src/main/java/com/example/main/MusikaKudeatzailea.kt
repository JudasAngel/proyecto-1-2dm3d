package com.example.main

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.example.apparrese.R

object MusikaKudeatzailea {
    private val lock = Any()               // llave: protege player y martxan

    //encedido por decirlo asi
    private var player: MediaPlayer? = null
    private var martxan = false //para gesionar vien sisales de la palicacion
    fun hasi(context: Context) {
        synchronized(lock) {                      // mirar y cambiar, sin cortes
            if (martxan) return                   // evita dos hilos de música
            martxan = true
        }
        // NUEVO: "quiero música"
        val appContext = context.applicationContext   // no retener la Activity

        Thread {
            Log.d("MUSIKA", "Hilo: cargando audio")
            try {
                val mp = MediaPlayer.create(appContext, R.raw.musica)
                synchronized(lock) {
                    if (!martxan || mp == null) {
                        Log.d("MUSIKA", "Hilo: me pidieron parar mientras cargaba")
                        //paramos la carga
                        mp?.release()
                        martxan = false
                        Log.d("MUSIKA", "Hilo: cancelado mientras cargaba")
                        return@Thread

                    }
                    mp.isLooping = true
                    mp.setVolume(1f, 1f)
                    mp.start()
                    player = mp

                }
                Log.d("MUSIKA", "Hilo: sonando")
            } catch (e: Exception) {
                Log.e("MUSIKA", "Ezin izan da musika hasi", e)
                synchronized(lock) { martxan = false }
            }
        }.start()
    }

    //pausamo la fucniona
    fun pausatu() {
        //sincornicamos para saber si hay otra llamada dentro
        synchronized(lock) {
            if (player?.isPlaying == true) player?.pause()
        }
    }
    //seguimos es is martxan vuelev a ser true peus sige la musica
    fun jarraitu() {
        synchronized(lock) {
            if (martxan) player?.start()
        }
    }
    //este s para cerra la musca con el boton de salir
    fun gelditu() {
        synchronized(lock) {
            martxan = false
            //miramos si si palyer encendio por decirlos asi
            player?.let {
                //si palyer usana lo apramos y cerrasmo elimanmos los daos de la musica
                if (it.isPlaying) it.stop()
                it.release()
            }
            //reinicamso player a null
            player = null
        }
        Log.d("MUSIKA", "Musika geldituta")
    }
}