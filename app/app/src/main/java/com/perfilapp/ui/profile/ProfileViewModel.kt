package com.perfilapp.ui.profile

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.perfilapp.data.local.SessionManager
import com.perfilapp.data.model.Resultado
import com.perfilapp.data.model.Usuario
import com.perfilapp.data.repository.AuthRepository
import com.perfilapp.utils.ImagenUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = AuthRepository()
    private val sesion = SessionManager(application)

    private val _usuario = MutableLiveData<Usuario>()
    val usuario: LiveData<Usuario> = _usuario

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje

    private val _sesionExpirada = MutableLiveData<Boolean>()
    val sesionExpirada: LiveData<Boolean> = _sesionExpirada

    private val _foto = MutableLiveData<Bitmap?>()
    val foto: LiveData<Bitmap?> = _foto

    init {
        sesion.obtenerUsuario()?.let { _usuario.value = it }
    }

    fun cargarPerfil() {
        val token = sesion.obtenerToken()
        if (token == null) {
            _sesionExpirada.value = true
            return
        }

        viewModelScope.launch {
            when (val r = repositorio.perfil(token)) {
                is Resultado.Exito ->
                    _usuario.value = Usuario(
                        r.datos.id,
                        r.datos.nombre,
                        r.datos.apellido,
                        r.datos.correo
                    )
                is Resultado.Error ->
                    if (r.mensaje.contains("Token")) {
                        sesion.cerrarSesion()
                        _sesionExpirada.value = true
                    } else {
                        _mensaje.value = r.mensaje
                    }
            }
        }
    }

    private fun archivoFoto(): File? {
        val usuario = sesion.obtenerUsuario() ?: return null
        return File(getApplication<Application>().filesDir, "perfil_${usuario.id}.jpg")
    }

    fun cargarFoto() {
        val archivo = archivoFoto() ?: return

        viewModelScope.launch {
            val bitmap = withContext(Dispatchers.IO) {
                if (archivo.exists() && archivo.length() > 0) {
                    ImagenUtils.cargarBitmap(archivo.absolutePath)
                } else {
                    null
                }
            }
            _foto.value = bitmap
        }
    }

    fun guardarFotoDeCamara(temporal: File) {
        val destino = archivoFoto() ?: return

        viewModelScope.launch {
            val ok = withContext(Dispatchers.IO) {
                try {
                    temporal.copyTo(destino, overwrite = true)
                    temporal.delete()
                    true
                } catch (e: Exception) {
                    false
                }
            }

            if (ok) cargarFoto() else _mensaje.value = "No se pudo guardar la fotografía"
        }
    }

    fun guardarFotoDeGaleria(uri: Uri) {
        val destino = archivoFoto() ?: return

        viewModelScope.launch {
            val ok = withContext(Dispatchers.IO) {
                try {
                    getApplication<Application>().contentResolver.openInputStream(uri)?.use { entrada ->
                        destino.outputStream().use { salida -> entrada.copyTo(salida) }
                    }
                    true
                } catch (e: Exception) {
                    false
                }
            }

            if (ok) cargarFoto() else _mensaje.value = "No se pudo guardar la imagen"
        }
    }

    fun cerrarSesion() {
        sesion.cerrarSesion()
    }
}