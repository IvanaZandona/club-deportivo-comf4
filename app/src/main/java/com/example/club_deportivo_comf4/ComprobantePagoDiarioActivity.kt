package com.example.club_deportivo_comf4

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class ComprobantePagoDiarioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_comprobante_pago_diario)

        // --- TextViews del layout ---
        val tvNombreUsuario = findViewById<TextView>(R.id.nombreUsuario)
        val tvDniUsuario = findViewById<TextView>(R.id.dniUsuario)
        val tvActividad = findViewById<TextView>(R.id.actividad)
        val tvMetodo = findViewById<TextView>(R.id.metodoPagoElegido)
        val tvCuotas = findViewById<TextView>(R.id.cantCuotas)
        val tvMonto = findViewById<TextView>(R.id.montoNro)
        val tvFechaEmision = findViewById<TextView>(R.id.emisionPago)
        //val tvVencimiento = findViewById<TextView>(R.id.vencimientoCuota)

        // --- Recibir datos del Intent ---
        val nombreUsuario = intent.getStringExtra("nombreUsuario") ?: "No disponible"
        val dni = intent.getStringExtra("dni") ?: "No disponible"
        val actividad = intent.getStringExtra("actividad") ?: "No disponible"
        val metodoPago = intent.getStringExtra("metodoPago") ?: "EFECTIVO"
        val cuotas = intent.getStringExtra("cuotas") ?: "1"
        val monto = intent.getDoubleExtra("monto", 0.0)

        val fechaPagoRecibida = intent.getStringExtra("fechaPago") ?: ""

        val fechaFormateadaParaMostrar = try {

            val formatoOriginal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formatoDeseado = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaDate = formatoOriginal.parse(fechaPagoRecibida)
            if (fechaDate != null) {
                formatoDeseado.format(fechaDate)
            } else {
                fechaPagoRecibida
            }
        } catch (e: Exception) {
            fechaPagoRecibida
        }

        // --- Asignar valores a los TextViews ---
        tvNombreUsuario.text = nombreUsuario
        tvDniUsuario.text = dni
        tvActividad.text = actividad
        tvMetodo.text = metodoPago
        tvCuotas.text = if (metodoPago == "TARJETA") "Cuotas: $cuotas" else ""
        tvMonto.text = "$ ${"%.2f".format(monto)}"

        tvFechaEmision.text = fechaFormateadaParaMostrar
        //tvVencimiento.text = fechaFormateadaParaMostrar

        // Boton Volver (el rectangular de abajo)
        val btnVolver = findViewById<LinearLayout>(R.id.btnVolver)
        /*btnVolver.setOnClickListener {
            finish()
        }*/
        btnVolver.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        // Icono superior volver (mismo comportamiento)
        val iconoVolver = findViewById<android.widget.ImageView>(R.id.iconoVolver)
        iconoVolver.setOnClickListener {
            finish()
        }
    }
}
