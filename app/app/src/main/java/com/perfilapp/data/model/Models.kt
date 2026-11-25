package com.perfilapp.data.model

import com.google.gson.annotations.SerializedName

// Datos que se envían al backend
data class RegistroRequest(
    val nombre: String,
    val apellido: String,
    val correo: String,
    val password: String
)

data class LoginRequest(
    val correo: String,
    val password: String
)

// Datos que responde el backend
data class Usuario(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val correo: String
)

data class LoginResponse(
    val mensaje: String,
    val token: String,
    val usuario: Usuario
)

data class MensajeResponse(
    val mensaje: String
)

data class PerfilResponse(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val correo: String,
    @SerializedName("fecha_registro") val fechaRegistro: String?
)

// Resultado de una operación: éxito con datos, o error con mensaje
sealed class Resultado<out T> {
    data class Exito<T>(val datos: T) : Resultado<T>()
    data class Error(val mensaje: String) : Resultado<Nothing>()
}