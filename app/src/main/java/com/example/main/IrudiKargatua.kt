package com.example.main

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.activity.compose.LocalActivity
import androidx.compose.ui.res.painterResource
import java.util.Locale
import com.example.apparrese.R


// Método de clase 1: subclase de Thread
class IrudiHaria(
    private val argazkia: String,
    private val activity: Activity,
    private val onOk: (Bitmap) -> Unit,
    private val onErr: () -> Unit            // NUEVO: salida para el error
) : Thread() {

    override fun run() {
        Log.d("IRUDIA", "Hasi: $argazkia")
        try {

            // 1. Nombre (texto de Firestore) -> número de recurso del drawable
            val id = activity.resources.getIdentifier(
                argazkia.lowercase(Locale.ROOT), "drawable", activity.packageName
            )

            // 2. Número de recurso -> Bitmap (la parte pesada)
            val bmp = if (id != 0) BitmapFactory.decodeResource(activity.resources, id) else null

            Log.d("IRUDIA", "Amaituta: $argazkia (id=$id)")

            // 3. Devolver el resultado al hilo principal
            if (bmp != null) {
                activity.runOnUiThread { onOk(bmp) }
            }else{
                activity.runOnUiThread { onErr() }     // no existe o no se pudo decodificar

            }
        }catch (e: Exception){
            Log.e("IRUDIA", "Errorea: $argazkia", e)
            activity.runOnUiThread { onErr() }
        }
    }
}

//esto carga la imgen ose la estructura que sev a aver
//le apsmos el nombre de la base de dtaso corespondiente argazkia
@Composable
fun IrudiKargatua(
    argazkia: String?,
    izena: String,
    modifier: Modifier = Modifier
) {
    val activity = LocalActivity.current
    var bitmap by remember(argazkia) { mutableStateOf<Bitmap?>(null) }
    var errorea by remember(argazkia) { mutableStateOf(false) }    // NUEVO

    DisposableEffect(argazkia) {
        if (!argazkia.isNullOrBlank() && activity != null) {
            IrudiHaria(argazkia, activity,
                { bitmap = it },
                onErr = { errorea = true }
            ).start()
        }
        onDispose { }
    }

    Box(modifier = modifier) {
        val bmp = bitmap
        when {
             bmp != null ->
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = izena,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
            argazkia.isNullOrBlank() || activity == null || errorea ->
                Image(
                    painter = painterResource(R.drawable.predeterminada),
                    contentDescription = izena,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()


                )

             else -> CircularProgressIndicator()
            }
        }
    }
