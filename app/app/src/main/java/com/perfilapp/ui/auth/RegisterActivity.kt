package com.perfilapp.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.perfilapp.data.model.Resultado
import com.perfilapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Barra de progreso
        viewModel.cargando.observe(this) { cargando ->
            binding.progreso.visibility = if (cargando) View.VISIBLE else View.GONE
            binding.btnRegistrar.isEnabled = !cargando
        }

        // Resultado del registro: si sale bien, regresa al login
        viewModel.registroResultado.observe(this) { resultado ->
            when (resultado) {
                is Resultado.Exito -> {
                    Toast.makeText(this, resultado.datos, Toast.LENGTH_LONG).show()
                    finish()
                }
                is Resultado.Error ->
                    Toast.makeText(this, resultado.mensaje, Toast.LENGTH_LONG).show()
            }
        }

        binding.btnRegistrar.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val apellido = binding.etApellido.text.toString().trim()
            val correo = binding.etCorreo.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmar = binding.etConfirmar.text.toString()

            when {
                nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || password.isEmpty() ->
                    mostrar("Completa todos los campos")
                !Patterns.EMAIL_ADDRESS.matcher(correo).matches() ->
                    mostrar("El correo no es válido")
                password.length < 6 ->
                    mostrar("La contraseña debe tener al menos 6 caracteres")
                password != confirmar ->
                    mostrar("Las contraseñas no coinciden")
                else -> viewModel.registrar(nombre, apellido, correo, password)
            }
        }

        // Volver al login
        binding.tvIrLogin.setOnClickListener { finish() }
    }

    private fun mostrar(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
}