package com.example.club_deportivo_comf4

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class PagoMensualActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_mensual)

        dbHelper = DBHelper(this)

        val inputDNI = findViewById<EditText>(R.id.inputDNI)
        val inputMonto = findViewById<EditText>(R.id.inputMonto)
        val rbEfectivo = findViewById<RadioButton>(R.id.rbEfectivo)
        val rbTarjeta = findViewById<RadioButton>(R.id.rbTarjeta)
        val spinnerCuotas = findViewById<Spinner>(R.id.spinnerCuotas)
        val btnPagar = findViewById<LinearLayout>(R.id.btnPagar)

        val cuotas = arrayOf("1", "3", "6")
        spinnerCuotas.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cuotas)
        spinnerCuotas.isEnabled = false


        findViewById<ImageView>(R.id.iconoVolver).setOnClickListener { finish() }

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupMetodo)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            spinnerCuotas.isEnabled = checkedId == R.id.rbTarjeta
        }

        inputDNI.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val dni = inputDNI.text.toString().trim()
                if (dni.isNotEmpty()) {
                    val socio = dbHelper.obtenerSocioPorDNI(dni)
                    if (socio == null) {
                        Toast.makeText(this, "No se encontró ningún socio con ese DNI", Toast.LENGTH_SHORT).show()
                        inputDNI.error = "Socio no encontrado"
                    } else {
                        Toast.makeText(this, "Socio encontrado: ${socio.nombre} ${socio.apellido} - Fecha Alta: ${socio.fechaInscripcion}", Toast.LENGTH_LONG).show()
                        inputDNI.error = null
                    }
                }
            }
        }

        btnPagar.setOnClickListener {
            registrarPago()
        }
    }

    private fun registrarPago() {
        val inputDNI = findViewById<EditText>(R.id.inputDNI)
        val inputMonto = findViewById<EditText>(R.id.inputMonto)
        val rbEfectivo = findViewById<RadioButton>(R.id.rbEfectivo)
        val rbTarjeta = findViewById<RadioButton>(R.id.rbTarjeta)
        val spinnerCuotas = findViewById<Spinner>(R.id.spinnerCuotas)

        val dni = inputDNI.text.toString().trim()
        val montoTexto = inputMonto.text.toString().trim()
        val metodoPago = when {
            rbEfectivo.isChecked -> "EFECTIVO"
            rbTarjeta.isChecked -> "TARJETA"
            else -> ""
        }
        val cuotasSeleccionadas = if (rbTarjeta.isChecked)
            spinnerCuotas.selectedItem?.toString() ?: "1"
        else
            "1"

        // Validaciones básicas
        if (dni.isEmpty() || montoTexto.isEmpty() || metodoPago.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val monto = montoTexto.toDoubleOrNull()
        if (monto == null || monto <= 0) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show()
            inputMonto.error = "Monto no válido"
            return
        }

        // Obtener usuarioId
        val usuarioId = dbHelper.obtenerUsuarioIdPorDNI(dni)
        if (usuarioId == null) {
            Toast.makeText(this, "No se pudo obtener el ID del usuario", Toast.LENGTH_SHORT).show()
            return
        }

        // Verificar que sea socio
        if (!dbHelper.esSocio(usuarioId)) {
            Toast.makeText(this, "El usuario con DNI $dni no es un socio registrado", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener fecha de alta del socio
        val fechaAlta = dbHelper.obtenerFechaAltaSocio(usuarioId)
        if (fechaAlta == null) {
            Toast.makeText(this, "No se pudo obtener la fecha de alta del socio", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener datos del socio para el comprobante
        val socio = dbHelper.obtenerSocioPorDNI(dni)
        if (socio == null) {
            Toast.makeText(this, "No se pudieron obtener los datos del socio", Toast.LENGTH_SHORT).show()
            return
        }

        // Registrar pago usando la fecha de alta del socio
        val exito = dbHelper.registrarPagoMensual(dni, monto, metodoPago, cuotasSeleccionadas, fechaAlta)
        if (exito) {
            Toast.makeText(this, "Pago registrado correctamente", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, ComprobantePagoMensualActivity::class.java).apply {
                putExtra("dni", dni)
                putExtra("nombreUsuario", "${socio.nombre} ${socio.apellido}")
                putExtra("metodoPago", metodoPago)
                putExtra("cuotas", cuotasSeleccionadas)
                putExtra("monto", monto)
                putExtra("fechaPago", fechaAlta) // Usar la fecha de alta del socio
                intent.putExtra("fechaAlta", socio.fechaInscripcion)

            }
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Error al registrar el pago", Toast.LENGTH_SHORT).show()
        }
    }
}