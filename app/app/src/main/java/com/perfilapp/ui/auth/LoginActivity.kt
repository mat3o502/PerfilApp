package com.perfilapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.perfilapp.data.local.SessionManager
import com.perfilapp.data.model.Resultado
import com.perfilapp.databinding.ActivityLoginBinding
import com.perfilapp.ui.profile.ProfileActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (SessionManager(this).haySesion()) {
            irAlPerfil()
            return
        }

        // Mostrar u ocultar la barra de progreso
        viewModel.cargando.observe(this) { cargando ->
            binding.progreso.visibility = if (cargando) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !cargando
        }

        // Resultado del login
        viewModel.loginResultado.observe(this) { resultado ->
            when (resultado) {
                is Resultado.Exito -> irAlPerfil()

                is Resultado.Error ->
                    Toast.makeText(this, resultado.mensaje, Toast.LENGTH_LONG).show()
            }
        }

        binding.btnLogin.setOnClickListener {
            val correo = binding.etCorreo.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.login(correo, password)
            }
        }

        binding.tvIrRegistro.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun irAlPerfil() {
        startActivity(Intent(this, ProfileActivity::class.java))
        finish()
    }
}