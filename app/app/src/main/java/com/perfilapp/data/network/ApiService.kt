package com.perfilapp.data.network

import com.perfilapp.data.model.LoginRequest
import com.perfilapp.data.model.LoginResponse
import com.perfilapp.data.model.MensajeResponse
import com.perfilapp.data.model.PerfilResponse
import com.perfilapp.data.model.RegistroRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

// Define las rutas del backend que usa la app
interface ApiService {

    @POST("api/auth/registro")
    suspend fun registrar(@Body datos: RegistroRequest): Response<MensajeResponse>

    @POST("api/auth/login")
    suspend fun login(@Body datos: LoginRequest): Response<LoginResponse>

    @GET("api/auth/perfil")
    suspend fun perfil(@Header("Authorization") token: String): Response<PerfilResponse>
}