package com.perfilapp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.perfilapp.R
import com.perfilapp.databinding.ActivityProfileBinding
import com.perfilapp.ui.auth.LoginActivity
import java.io.File

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    // Archivo temporal donde la cámara deja la foto
    private val archivoTemporal by lazy { File(filesDir, "temp_camara.jpg") }

    // Abrir la cámara
    private val tomarFoto = registerForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        if (exito) viewModel.guardarFotoDeCamara(archivoTemporal)
    }

    // Abrir la galería
    private val elegirDeGaleria = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) viewModel.guardarFotoDeGaleria(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mostrar los datos del usuario
        viewModel.usuario.observe(this) { usuario ->
            binding.tvNombre.text = "${usuario.nombre} ${usuario.apellido}"
            binding.tvCorreo.text = usuario.correo
        }

        // Mostrar la foto (o el ícono por defecto si no hay)
        viewModel.foto.observe(this) { bitmap ->
            if (bitmap != null) {
                binding.ivFoto.setPadding(0, 0, 0, 0)
                binding.ivFoto.setImageBitmap(bitmap)
            } else {
                val margen = (32 * resources.displayMetrics.density).toInt()
                binding.ivFoto.setPadding(margen, margen, margen, margen)
                binding.ivFoto.setImageResource(R.drawable.ic_persona)
            }
        }

        viewModel.mensaje.observe(this) { mensaje ->
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
        }

        // Si el token ya no sirve, vuelve al login
        viewModel.sesionExpirada.observe(this) { expirada ->
            if (expirada) irAlLogin()
        }

        binding.btnTomarFoto.setOnClickListener {
            val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", archivoTemporal)
            tomarFoto.launch(uri)
        }

        binding.btnGaleria.setOnClickListener {
            elegirDeGaleria.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.btnCerrarSesion.setOnClickListener {
            viewModel.cerrarSesion()
            irAlLogin()
        }

        viewModel.cargarPerfil()
        viewModel.cargarFoto()
    }

    private fun irAlLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}