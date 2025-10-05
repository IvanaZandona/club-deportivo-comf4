package com.example.club_deportivo_comf4

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ComprobantePagoMensualActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_comprobante_pago_mensual)

        // Boton Volver (el rectangular de abajo)
        val btnVolver = findViewById<LinearLayout>(R.id.btnVolver)
        btnVolver.setOnClickListener {
            finish() // vuelve al menu anterior sin crear otra instancia
        }

        // Icono superior volver (mismo comportamiento)
        val iconoVolver = findViewById<android.widget.ImageView>(R.id.iconoVolver)
        iconoVolver.setOnClickListener {
            finish()
        }

        // TextViews
        val tvMetodo = findViewById<TextView>(R.id.metodoPagoElegido)
        val tvCuotas = findViewById<TextView>(R.id.cantCuotas)
        val tvMonto = findViewById<TextView>(R.id.montoNro)

        // Recibir datos del Intent
        val metodoPago = intent.getStringExtra("metodoPago") ?: "EFECTIVO"
        val cuotas = intent.getStringExtra("cuotas") ?: "1"
        val monto = intent.getIntExtra("monto", 0)

        // Asignar valores a los TextView
        tvMetodo.text = metodoPago
        tvCuotas.text = if (metodoPago == "TARJETA") "Cuotas: $cuotas" else ""
        tvMonto.text = "$ $monto"


    }
}