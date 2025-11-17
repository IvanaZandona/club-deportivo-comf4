package com.example.club_deportivo_comf4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView

class CarnetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_carnet)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Datos recibidos
        val nombre = intent.getStringExtra("nombre") ?: ""
        val apellido = intent.getStringExtra("apellido") ?: ""
        val dni = intent.getStringExtra("dni") ?: ""
        val fechaNacimiento = intent.getStringExtra("fechaNacimiento") ?: ""
        //val idUsuario = intent.getIntExtra("idUsuario", -1)
        val idUsuario = intent.getLongExtra("idUsuario", -1)

        // Setear datos
        findViewById<TextView>(R.id.tvUsuarioIDSocio).text = idUsuario.toString()
        findViewById<TextView>(R.id.tvNombreSocio).text = nombre
        findViewById<TextView>(R.id.tvUsuarioIDSocio).text = idUsuario.toString()
        findViewById<TextView>(R.id.txtApellido).text = apellido
        findViewById<TextView>(R.id.txtFechaNac).text = fechaNacimiento
        findViewById<TextView>(R.id.tvDniNumero).text = dni


        val volverAtras = findViewById<Button>(R.id.volverAtras)
        volverAtras.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}

