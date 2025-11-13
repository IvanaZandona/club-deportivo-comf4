package com.example.club_deportivo_comf4

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ComprobantePagoDiarioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_comprobante_pago_diario)

        // TextViews
        val tvActividad = findViewById<TextView>(R.id.actividad)
        val tvMetodo = findViewById<TextView>(R.id.metodoPagoElegido)
        val tvCuotas = findViewById<TextView>(R.id.cantCuotas)
        val tvMonto = findViewById<TextView>(R.id.montoNro)

        // Recibir datos del Intent
        val actividad = intent.getStringExtra("actividad")
        val metodoPago = intent.getStringExtra("metodoPago") ?: "EFECTIVO"
        val cuotas = intent.getStringExtra("cuotas") ?: "1"
        val monto = intent.getIntExtra("monto", 0)

        // Asignar valores a los TextView
        tvActividad.text = actividad
        tvMetodo.text = metodoPago
        tvCuotas.text = if (metodoPago == "TARJETA") "Cuotas: $cuotas" else ""
        tvMonto.text = "$ $monto"



    }
}