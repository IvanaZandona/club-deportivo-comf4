package com.example.club_deportivo_comf4

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.TextView
import android.widget.FrameLayout

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val btnCerrarSesion: TextView = findViewById(R.id.btnCerrarSesion)

        btnCerrarSesion.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // supuestamente poniendole un id de boton a los Frame (como hice) andarian como boton A CHEQUEAR
        /* val btnBuscar = findViewById<FrameLayout>(R.id.btnBuscarSocio)
        btnBuscar.setOnClickListener {
            // acción del botón
        }*/
        val btnRegistrarSocio = findViewById<FrameLayout>(R.id.btnRegistrar) //
        btnRegistrarSocio.setOnClickListener {
            val intent = Intent(this, RegistrarActivity::class.java)
            startActivity(intent)
        }
        val btnListados = findViewById<FrameLayout>(R.id.btnListados) //
        btnListados.setOnClickListener {
            val intent = Intent(this, ListadosActivity::class.java)
            startActivity(intent)
        }

    }
}