package com.perfilapp.data.local

import android.content.Context
import com.perfilapp.data.model.Usuario

// Guarda en el teléfono la sesión del usuario y la ruta de su foto de perfil
class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("perfilapp_sesion", Context.MODE_PRIVATE)

    fun guardarSesion(token: String, usuario: Usuario) {
        prefs.edit()
            .putString("token", token)
            .putInt("id", usuario.id)
            .putString("nombre", usuario.nombre)
            .putString("apellido", usuario.apellido)
            .putString("correo", usuario.correo)
            .apply()
    }

    fun obtenerToken(): String? = prefs.getString("token", null)

    fun obtenerUsuario(): Usuario? {
        val id = prefs.getInt("id", -1)
        if (id == -1) return null
        return Usuario(
            id = id,
            nombre = prefs.getString("nombre", "") ?: "",
            apellido = prefs.getString("apellido", "") ?: "",
            correo = prefs.getString("correo", "") ?: ""
        )
    }

    fun haySesion(): Boolean = obtenerToken() != null

    // La foto se guarda solo en el teléfono, asociada al id del usuario
    fun guardarRutaFoto(idUsuario: Int, ruta: String) {
        prefs.edit().putString("foto_$idUsuario", ruta).apply()
    }

    fun obtenerRutaFoto(idUsuario: Int): String? = prefs.getString("foto_$idUsuario", null)

    // Cierra sesión pero conserva la foto local del usuario
    fun cerrarSesion() {
        prefs.edit()
            .remove("token")
            .remove("id")
            .remove("nombre")
            .remove("apellido")
            .remove("correo")
            .apply()
    }
}