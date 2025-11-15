package com.example.club_deportivo_comf4

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class PagoDiarioActivity : AppCompatActivity() {

    private val dbHelper: DBHelper by lazy { DBHelper(this) }

    private lateinit var inputDNI: EditText
    private lateinit var inputMonto: EditText
    private lateinit var spinnerActividad: Spinner
    private lateinit var rbEfectivo: RadioButton
    private lateinit var rbTarjeta: RadioButton
    private lateinit var spinnerCuotas: Spinner
    private lateinit var btnPagar: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pago_diario)

        inicializarVistas()
        configurarSpinners()
        configurarListeners()
    }

    private fun inicializarVistas() {
        inputDNI = findViewById(R.id.inputDNI)
        inputMonto = findViewById(R.id.inputMonto)
        spinnerActividad = findViewById(R.id.spinnerActividad)
        rbEfectivo = findViewById(R.id.rbEfectivo)
        rbTarjeta = findViewById(R.id.rbTarjeta)
        spinnerCuotas = findViewById(R.id.spinnerCuotas)
        btnPagar = findViewById(R.id.btnPagar)
    }

    private fun configurarSpinners() {
        val actividades = dbHelper.obtenerActividades()
        spinnerActividad.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, actividades)

        val cuotas = arrayOf("1", "3", "6")
        spinnerCuotas.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cuotas)
        spinnerCuotas.visibility = View.GONE
    }

    private fun configurarListeners() {
        findViewById<ImageView>(R.id.iconoVolver).setOnClickListener { finish() }

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupMetodo)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            spinnerCuotas.visibility = if (checkedId == R.id.rbTarjeta) View.VISIBLE else View.GONE
        }

        inputDNI.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) buscarNoSocio()
        }

        btnPagar.setOnClickListener { procesarPagoDiario() }
    }

    private fun buscarNoSocio() {
        val dni = inputDNI.text.toString().trim()
        if (dni.isNotEmpty()) {
            val noSocio = dbHelper.obtenerNoSocioPorDNI(dni)
            if (noSocio == null) {
                Toast.makeText(this, "No se encontró un no socio con ese DNI", Toast.LENGTH_SHORT).show()
                inputDNI.error = "No socio no encontrado"
            } else {
                Toast.makeText(this, "No socio: ${noSocio.nombre} ${noSocio.apellido}", Toast.LENGTH_LONG).show()
                inputDNI.error = null
            }
        }
    }

    private fun procesarPagoDiario() {
        val dni = inputDNI.text.toString().trim()
        val montoTexto = inputMonto.text.toString().trim()
        val actividadSeleccionada = spinnerActividad.selectedItem?.toString() ?: ""
        val metodoPago = when {
            rbEfectivo.isChecked -> "EFECTIVO"
            rbTarjeta.isChecked -> "TARJETA"
            else -> ""
        }
        val cuotasSeleccionadas = if (metodoPago == "TARJETA") spinnerCuotas.selectedItem.toString() else "1"

        if (dni.isEmpty() || montoTexto.isEmpty() || metodoPago.isEmpty() || actividadSeleccionada.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_LONG).show()
            return
        }

        val monto = montoTexto.toDoubleOrNull()
        if (monto == null || monto <= 0) {
            Toast.makeText(this, "Ingrese un monto válido", Toast.LENGTH_LONG).show()
            inputMonto.error = "Monto no válido"
            return
        }

        val noSocio = dbHelper.obtenerNoSocioPorDNI(dni)
        if (noSocio == null) {
            Toast.makeText(this, "No se encontró un no socio registrado con ese DNI", Toast.LENGTH_SHORT).show()
            inputDNI.error = "No socio no encontrado"
            return
        }

        val fechaDePago = dbHelper.fechaHoyString()

        val idTransaccion = dbHelper.registrarPagoNoSocio(
            dni = dni,
            monto = monto,
            metodoPago = metodoPago,
            cuotas = cuotasSeleccionadas.toInt(),
            nombreActividad = actividadSeleccionada,
            fechaDePago = fechaDePago
        )

        if (idTransaccion > -1L) {
            Toast.makeText(this, "Pago registrado correctamente", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ComprobantePagoDiarioActivity::class.java).apply {
                putExtra("idTransaccion", idTransaccion)
                putExtra("dni", dni)
                putExtra("nombreUsuario", "${noSocio.nombre} ${noSocio.apellido}")
                putExtra("actividad", actividadSeleccionada)
                putExtra("metodoPago", metodoPago)
                putExtra("cuotas", cuotasSeleccionadas)
                putExtra("monto", monto)
                putExtra("fechaPago", fechaDePago)
            }
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Error al registrar el pago", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}
