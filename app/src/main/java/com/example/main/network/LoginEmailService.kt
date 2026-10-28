package com.example.main.network

import android.os.Build
import android.util.Log
import com.example.apparrese.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Llama a la AWS Lambda "empresaapp-bidali-emaila" por HTTPS para que envíe
 * al usuario un email de aviso de inicio de sesión.
 *
 * La URL y el token NO están en el código: vienen de local.properties
 * a través de BuildConfig (local.properties no se sube a GitHub).
 */
object LoginEmailService {

    private const val TAG = "LoginEmailService"

    // Scope propio: el envío no se cancela cuando la pantalla de login desaparece al navegar
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /** Envía el email en segundo plano sin bloquear el login. */
    fun sendLoginEmailInBackground(email: String, nombre: String) {
        scope.launch {
            val resultado = sendLoginEmail(email, nombre)
            if (resultado is Result.Error) {
                // No bloquea el login si el email falla, solo se registra
                Log.w(TAG, "No se pudo enviar el email: ${resultado.message}")
            }
        }
    }

    sealed class Result {
        object Ok : Result()
        data class Error(val code: Int, val message: String) : Result()
    }

    suspend fun sendLoginEmail(email: String, nombre: String): Result =
        withContext(Dispatchers.IO) {
            val endpoint = BuildConfig.LAMBDA_URL
            if (!endpoint.startsWith("https://")) {
                Log.w(TAG, "LAMBDA_URL no configurada: revisa local.properties")
                return@withContext Result.Error(-1, "LAMBDA_URL no configurada o no es HTTPS")
            }

            if (BuildConfig.LAMBDA_API_TOKEN.any { it.code > 127 }) {
                Log.w(TAG, "LAMBDA_API_TOKEN tiene caracteres no ASCII (ñ, tildes...): " +
                        "usa solo letras, números y guiones, igual en AWS y en local.properties")
            }

            val body = JSONObject().apply {
                put("email", email)
                put("nombre", nombre)
                put("dispositivo", "${Build.MANUFACTURER} ${Build.MODEL}")
            }.toString()

            var conn: HttpURLConnection? = null
            try {
                conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 10_000
                    readTimeout = 20_000
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    setRequestProperty("x-api-token", BuildConfig.LAMBDA_API_TOKEN)
                }

                conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }

                val code = conn.responseCode
                val stream = if (code in 200..299) conn.inputStream else conn.errorStream
                val text = stream?.bufferedReader()?.use { it.readText() }.orEmpty()

                if (code in 200..299) {
                    Log.i(TAG, "Email de inicio de sesión enviado")
                    Result.Ok
                } else {
                    // La Lambda responde {"error": ...}; AWS (p. ej. 403) responde {"Message": ...}
                    val msg = runCatching {
                        val json = JSONObject(text)
                        json.optString("error").ifBlank { json.optString("Message") }
                    }.getOrNull().takeUnless { it.isNullOrBlank() } ?: "Error HTTP $code"
                    Log.w(TAG, "La Lambda respondió $code: $msg")
                    if (code == 403) {
                        Log.w(TAG, "403 = AWS rechaza la llamada: revisa LAMBDA_URL en local.properties " +
                                "y que la Function URL tenga Auth type NONE")
                    }
                    Result.Error(code, msg)
                }
            } catch (e: IOException) {
                Log.e(TAG, "Sin conexión con la Lambda", e)
                Result.Error(-1, "Sin conexión: ${e.message}")
            } catch (e: IllegalArgumentException) {
                // p. ej. cabecera con caracteres no válidos: no debe cerrar la app
                Log.e(TAG, "Petición no válida", e)
                Result.Error(-1, "Petición no válida: ${e.message}")
            } finally {
                conn?.disconnect()
            }
        }
}
