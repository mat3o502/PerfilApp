package com.perfilapp.data.repository

import com.google.gson.Gson
import com.perfilapp.data.model.LoginRequest
import com.perfilapp.data.model.LoginResponse
import com.perfilapp.data.model.MensajeResponse
import com.perfilapp.data.model.PerfilResponse
import com.perfilapp.data.model.RegistroRequest
import com.perfilapp.data.model.Resultado
import com.perfilapp.data.network.ApiService
import com.perfilapp.data.network.RetrofitClient
import retrofit2.Response
import java.io.IOException

// Intermediario entre los ViewModels y el backend
class AuthRepository(private val api: ApiService = RetrofitClient.api) {

    suspend fun registrar(nombre: String, apellido: String, correo: String, password: String): Resultado<MensajeResponse> =
        ejecutar { api.registrar(RegistroRequest(nombre, apellido, correo, password)) }

    suspend fun login(correo: String, password: String): Resultado<LoginResponse> =
        ejecutar { api.login(LoginRequest(correo, password)) }

    suspend fun perfil(token: String): Resultado<PerfilResponse> =
        ejecutar { api.perfil("Bearer $token") }

    // Ejecuta la llamada y traduce la respuesta a Exito o Error con un mensaje claro
    private suspend fun <T> ejecutar(llamada: suspend () -> Response<T>): Resultado<T> {
        return try {
            val respuesta = llamada()
            val cuerpo = respuesta.body()
            if (respuesta.isSuccessful && cuerpo != null) {
                Resultado.Exito(cuerpo)
            } else {
                val mensaje = try {
                    Gson().fromJson(respuesta.errorBody()?.string(), MensajeResponse::class.java)?.mensaje
                } catch (e: Exception) {
                    null
                }
                Resultado.Error(mensaje ?: "Error del servidor (${respuesta.code()})")
            }
        } catch (e: IOException) {
            Resultado.Error("No se pudo conectar al servidor. Verifique la red.")
        } catch (e: Exception) {
            Resultado.Error("Ocurrió un error inesperado.")
        }
    }
}