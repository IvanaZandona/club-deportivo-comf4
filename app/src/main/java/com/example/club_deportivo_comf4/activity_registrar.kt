package com.example.club_deportivo_comf4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class activity_registrar : AppCompatActivity() {
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

        // Botón para abrir activity_registroSocio
        val btnAgregarSocio = findViewById<Button>(R.id.btnAgregarSocio)
        btnAgregarSocio.setOnClickListener {
            val intent = Intent(this, activity_registroSocio::class.java)
            startActivity(intent)
        }



        // Botón para abrir activity_registroNoSocio
        val btnAgregarNoSocio = findViewById<Button>(R.id.btnAgregarNoSocio)
        btnAgregarNoSocio.setOnClickListener {
            val intent = Intent(this, activity_registroNoSocio::class.java)
            startActivity(intent)
        }
    }
}
