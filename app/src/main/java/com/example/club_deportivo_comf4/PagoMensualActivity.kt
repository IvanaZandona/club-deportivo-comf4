package com.example.club_deportivo_comf4

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class PagoMensualActivity : AppCompatActivity() {

    private val dbHelper: DBHelper by lazy { DBHelper(this) }

    private lateinit var inputDNI: EditText
    private lateinit var inputMonto: EditText
    private lateinit var rbEfectivo: RadioButton
    private lateinit var rbTarjeta: RadioButton
    private lateinit var spinnerCuotas: Spinner
    private lateinit var btnPagar: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_mensual)

        inicializarVistas()
        configurarSpinners()
        configurarListeners()
    }

    private fun inicializarVistas() {
        inputDNI = findViewById(R.id.inputDNI)
        inputMonto = findViewById(R.id.inputMonto)
        rbEfectivo = findViewById(R.id.rbEfectivo)
        rbTarjeta = findViewById(R.id.rbTarjeta)
        spinnerCuotas = findViewById(R.id.spinnerCuotas)
        btnPagar = findViewById(R.id.btnPagar)
    }

    private fun configurarSpinners() {
        val cuotas = arrayOf("1", "3", "6")
        spinnerCuotas.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cuotas)
        spinnerCuotas.isEnabled = false
    }

    private fun configurarListeners() {
        findViewById<ImageView>(R.id.iconoVolver).setOnClickListener { finish() }

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupMetodo)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            spinnerCuotas.isEnabled = checkedId == R.id.rbTarjeta
        }

        inputDNI.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) buscarSocio()
        }

        btnPagar.setOnClickListener { registrarPago() }
    }

    private fun buscarSocio() {
        val dni = inputDNI.text.toString().trim()
        if (dni.isNotEmpty()) {
            val socio = dbHelper.obtenerSocioPorDNI(dni)
            if (socio != null) {
                Toast.makeText(
                    this,
                    "Socio encontrado: ${socio.nombre} ${socio.apellido} - Fecha Alta: ${socio.fechaInscripcion}",
                    Toast.LENGTH_LONG
                ).show()
                inputDNI.error = null
            } else {
                Toast.makeText(this, "No se encontró ningún socio con ese DNI", Toast.LENGTH_SHORT).show()
                inputDNI.error = "Socio no encontrado"
            }
        }
    }

    private fun registrarPago() {
        val dni = inputDNI.text.toString().trim()
        val montoTexto = inputMonto.text.toString().trim()
        val metodoPago = when {
            rbEfectivo.isChecked -> "EFECTIVO"
            rbTarjeta.isChecked -> "TARJETA"
            else -> ""
        }
        val cuotasSeleccionadas = if (rbTarjeta.isChecked) spinnerCuotas.selectedItem?.toString() ?: "1" else "1"

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

        val socio = dbHelper.obtenerSocioPorDNI(dni)
        if (socio == null) {
            Toast.makeText(this, "No se encontraron datos del socio", Toast.LENGTH_SHORT).show()
            return
        }

        //  Solo llamar al mét
        val exito = dbHelper.registrarPagoMensual(dni, monto, metodoPago, cuotasSeleccionadas.toInt())

        if (exito) {
            Toast.makeText(this, "Pago registrado correctamente", Toast.LENGTH_SHORT).show()

            val fechaPagoReal = dbHelper.obtenerUltimaFechaPagoReal(socio.idUsuario) ?: dbHelper.fechaActual()

            // Para el comprobante, usar fecha_alta como fecha de pago (es lo que se usó para primer pago)
            val intent = Intent(this, ComprobantePagoMensualActivity::class.java).apply {
                putExtra("dni", dni)
                putExtra("nombreUsuario", "${socio.nombre} ${socio.apellido}")
                putExtra("metodoPago", metodoPago)
                putExtra("cuotas", cuotasSeleccionadas)
                putExtra("monto", monto)
                //putExtra("fechaPago", socio.fechaInscripcion) //  Usar fecha de inscripción
                putExtra("fechaPago", fechaPagoReal)
                putExtra("fechaAlta", socio.fechaInscripcion)
                putExtra("fechaUltimoPago", socio.fechaUltimoPago)
                putExtra("fechaVencimiento", socio.fechaVencimiento)

            }
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Error al registrar el pago", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}
