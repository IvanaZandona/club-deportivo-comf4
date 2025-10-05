package com.example.club_deportivo_comf4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistrarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar)

        // Ajuste de padding para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- Botón atrás para ir a MenuActivity ---
        val botonAtras = findViewById<ImageButton>(R.id.boton_flecha_atras)
        botonAtras.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish() // Opcional: cierra esta Activity para no volver con el back
        }

        // Botón para abrir RegistroSocioActivity
        val btnAgregarSocio = findViewById<Button>(R.id.btnAgregarSocio)
        btnAgregarSocio.setOnClickListener {
            val intent = Intent(this, RegistroSocioActivity::class.java)
            startActivity(intent)
        }

        // Botón para abrir RegistroNoSocioActivity
        val btnAgregarNoSocio = findViewById<Button>(R.id.btnAgregarNoSocio)
        btnAgregarNoSocio.setOnClickListener {
            val intent = Intent(this, RegistroNoSocioActivity::class.java)
            startActivity(intent)
        }
    }
}
