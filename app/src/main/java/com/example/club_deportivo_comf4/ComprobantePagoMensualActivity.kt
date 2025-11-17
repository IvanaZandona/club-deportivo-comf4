package com.example.club_deportivo_comf4

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class ComprobantePagoMensualActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_comprobante_pago_mensual)

        val btnVolver = findViewById<LinearLayout>(R.id.btnVolver)
        //btnVolver.setOnClickListener { finish() }
        btnVolver.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        val iconoVolver = findViewById<android.widget.ImageView>(R.id.iconoVolver)
        iconoVolver.setOnClickListener { finish() }

        val tvNombreUsuario = findViewById<TextView>(R.id.nombreUsuario)
        val tvDniUsuario = findViewById<TextView>(R.id.dniUsuario)
        val tvMetodo = findViewById<TextView>(R.id.metodoPagoElegido)
        val tvCuotas = findViewById<TextView>(R.id.cantCuotas)
        val tvMonto = findViewById<TextView>(R.id.montoNro)
        val tvFechaEmision = findViewById<TextView>(R.id.emisionPago)
        val tvVencimiento = findViewById<TextView>(R.id.vencimientoCuota)

        val nombreUsuario = intent.getStringExtra("nombreUsuario") ?: "No disponible"
        val dni = intent.getStringExtra("dni") ?: "No disponible"
        val metodoPago = intent.getStringExtra("metodoPago") ?: "EFECTIVO"
        val cuotas = intent.getStringExtra("cuotas") ?: "1"
        val monto = intent.getDoubleExtra("monto", 0.0)

        val fechaPago = intent.getStringExtra("fechaPago") ?: ""
        val fechaAlta = intent.getStringExtra("fechaAlta") ?: ""
        val fechaUltimoPago = intent.getStringExtra("fechaUltimoPago") ?: ""
        val fechaVencimiento = intent.getStringExtra("fechaVencimiento") ?: ""

        val fechaVencimientoStr = fechaVencimiento // ya viene calculada
        tvVencimiento.text = fechaVencimientoStr

        val sdfOriginal = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val sdfMostrar = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Convertir fecha de pago
        val fechaPagoStr = try {
            val date = sdfOriginal.parse(fechaPago)
            sdfMostrar.format(date!!)
        } catch (e: Exception) {
            fechaPago
        }

        /*val fechaVencimientoStr = try {
            val date = sdfOriginal.parse(fechaVencimiento)
            sdfMostrar.format(date!!)
        } catch (e: Exception) {
            fechaVencimiento
        }*/

        // Convertir fecha alta y sumar 1 mes
        /*val fechaVencimientoStr = try {
            val partes = fechaAlta.split("-")
            val anio = partes[0].toInt()
            val mes = partes[1].toInt() - 1
            val dia = partes[2].toInt()

            val calendar = Calendar.getInstance()
            calendar.set(anio, mes, dia)

            calendar.add(Calendar.MONTH, 1)

            val venc = calendar.time
            sdfMostrar.format(venc)
        } catch (e: Exception) {
            "Fecha inválida"
        }*/

        tvNombreUsuario.text = nombreUsuario
        tvDniUsuario.text = dni
        tvMetodo.text = metodoPago
        tvCuotas.text = if (metodoPago == "TARJETA") "Cuotas: $cuotas" else ""
        tvMonto.text = "$ ${"%.2f".format(monto)}"
        tvFechaEmision.text = fechaPagoStr
        tvVencimiento.text = fechaVencimientoStr
    }
}
