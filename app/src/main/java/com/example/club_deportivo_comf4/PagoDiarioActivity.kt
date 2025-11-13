package com.example.club_deportivo_comf4

import android.content.Intent
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
import android.widget.Toast


class PagoDiarioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_diario)

        //inputs
        val spinnerActividad = findViewById<Spinner>(R.id.spinnerActividad)
        val rbEfectivo = findViewById<RadioButton>(R.id.rbEfectivo)
        val rbTarjeta = findViewById<RadioButton>(R.id.rbTarjeta)
        val spinnerCuotas = findViewById<Spinner>(R.id.spinnerCuotas)

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


        // Cargar actividades desde la BD
        val db = DBHelper(this)
        val actividades = db.obtenerActividades()

        if (actividades.isNotEmpty()) {
            val adapterActividades = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, actividades)
            spinnerActividad.adapter = adapterActividades
        } else {
            Toast.makeText(this, "No hay actividades cargadas en la base de datos", Toast.LENGTH_SHORT).show()
        }

        // Cargar opciones del spinner de cant cuotas (solo para tarjeta)
        val cuotas = arrayOf("1", "3", "6")
        spinnerCuotas.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cuotas)
        // cambio de selección
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupMetodo)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            spinnerCuotas.isEnabled = checkedId == R.id.rbTarjeta
        }

        // --- Botón pagar
        val btnPagar = findViewById<LinearLayout>(R.id.btnPagar)
        btnPagar.setOnClickListener {
            val intent = Intent(this, ComprobantePagoDiarioActivity::class.java)

            // enviar datos del pago (para mostrar en el comprobante)
            val actividadSeleccionada = spinnerActividad.selectedItem?.toString() ?: "Sin actividad"
            val metodoPago = if (rbEfectivo.isChecked) "EFECTIVO" else "TARJETA"
            val cuotasSeleccionadas = spinnerCuotas.selectedItem?.toString() ?: "1"
            val monto = 32_000 // dsp lo obtenemos del campo EditText

            intent.putExtra("actividad", actividadSeleccionada)
            intent.putExtra("metodoPago", metodoPago)
            intent.putExtra("cuotas", cuotasSeleccionadas)
            intent.putExtra("monto", monto)

            startActivity(intent)
        }


    }
}