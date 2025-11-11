package com.example.club_deportivo_comf4

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class PagosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagos)

        // Boton Pago Mensual
        val btnPagoMensual = findViewById<LinearLayout>(R.id.btnPagoMensual)
        btnPagoMensual.setOnClickListener {
            val intent = Intent(this, PagoMensualActivity::class.java)
            startActivity(intent)
        }

        // Boton "Volver" (el rectangular de abajo)
        val btnVolver = findViewById<LinearLayout>(R.id.btnVolver)
        btnVolver.setOnClickListener {
            finish() // vuelve al menu anterior sin crear otra instancia
        }

        // Icono superior volver (mismo comportamiento)
        val iconoVolver = findViewById<android.widget.ImageView>(R.id.iconoVolver)
        iconoVolver.setOnClickListener {
            finish()
        }
    }
}
