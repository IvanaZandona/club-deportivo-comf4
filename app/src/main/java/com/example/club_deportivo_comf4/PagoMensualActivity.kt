package com.example.club_deportivo_comf4

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PagoMensualActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_mensual)

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

        val rbEfectivo = findViewById<RadioButton>(R.id.rbEfectivo)
        val rbTarjeta = findViewById<RadioButton>(R.id.rbTarjeta)
        val spinnerCuotas = findViewById<Spinner>(R.id.spinnerCuotas)

        // Cargar opciones del spinner
        val cuotas = arrayOf("1", "3", "6")
        spinnerCuotas.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cuotas)

        // cambio de selección
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupMetodo)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            spinnerCuotas.isEnabled = checkedId == R.id.rbTarjeta
        }

    }
}