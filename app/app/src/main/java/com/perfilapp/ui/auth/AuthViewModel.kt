package com.perfilapp.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.perfilapp.data.local.SessionManager
import com.perfilapp.data.model.Resultado
import com.perfilapp.data.model.Usuario
import com.perfilapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

// Lógica de las pantallas de login y registro (capa ViewModel de MVVM)
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = AuthRepository()
    private val sesion = SessionManager(application)

    private val _cargando = MutableLiveData<Boolean>()
    val cargando: LiveData<Boolean> = _cargando

    private val _loginResultado = MutableLiveData<Resultado<Usuario>>()
    val loginResultado: LiveData<Resultado<Usuario>> = _loginResultado

    private val _registroResultado = MutableLiveData<Resultado<String>>()
    val registroResultado: LiveData<Resultado<String>> = _registroResultado

    fun login(correo: String, password: String) {
        viewModelScope.launch {
            _cargando.value = true
            when (val r = repositorio.login(correo, password)) {
                is Resultado.Exito -> {
                    sesion.guardarSesion(r.datos.token, r.datos.usuario)
                    _loginResultado.value = Resultado.Exito(r.datos.usuario)
                }
                is Resultado.Error -> _loginResultado.value = Resultado.Error(r.mensaje)
            }
            _cargando.value = false
        }
    }

    fun registrar(nombre: String, apellido: String, correo: String, password: String) {
        viewModelScope.launch {
            _cargando.value = true
            when (val r = repositorio.registrar(nombre, apellido, correo, password)) {
                is Resultado.Exito -> _registroResultado.value = Resultado.Exito(r.datos.mensaje)
                is Resultado.Error -> _registroResultado.value = Resultado.Error(r.mensaje)
            }
            _cargando.value = false
        }
    }
}