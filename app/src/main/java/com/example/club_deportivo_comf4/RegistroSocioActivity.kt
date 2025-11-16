package com.example.club_deportivo_comf4

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar
import android.widget.Toast
import android.widget.RadioGroup
class RegistroSocioActivity : AppCompatActivity() {

    private var socioRegistrado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_socio)

        // Ajuste de padding para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //inputs
        val inputFecha = findViewById<EditText>(R.id.inputFecha)
        val radioApto = findViewById<RadioGroup>(R.id.radioAptoFisico)
        val radioFicha = findViewById<RadioGroup>(R.id.radioFichaMedica)
        val btnAgregarSocio = findViewById<Button>(R.id.btnAgregarSocio)
        val btnPagos = findViewById<Button>(R.id.btnAgregarNoSocio)
        val btnLimpiar = findViewById<Button>(R.id.btnLimpiar)
        val btnAtras = findViewById<ImageButton>(R.id.boton_flecha_atras)

        // deshabilitar el botón “Ir a pagos” hasta guardar socio
        btnPagos.isEnabled = false

        //  Botón atrás
        btnAtras.setOnClickListener {
            val intent = Intent(this, RegistrarActivity::class.java)
            startActivity(intent)
            finish() // opcional: cierra esta actividad
        }


        inputFecha.setOnClickListener {
            val calendario = Calendar.getInstance()
            val año = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, añoSeleccionado, mesSeleccionado, diaSeleccionado ->
                    val fecha = String.format("%02d/%02d/%d", diaSeleccionado, mesSeleccionado + 1, añoSeleccionado)
                    inputFecha.setText(fecha)
                },
                año, mes, dia
            )
            datePickerDialog.show()
        }

        // Instancia de base de datos y obtención de id del usuario
        val db = DBHelper(this)
        val idUsuario = intent.getLongExtra("id_usuario", -1)

        if (idUsuario == -1L) {
            Toast.makeText(this, "Error: no se encontró el usuario", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Botón Agregar Socio con alerta personalizada -

        btnAgregarSocio.setOnClickListener {
            val fechaInscripcion = inputFecha.text.toString().trim()
            val aptoSeleccionado = radioApto.checkedRadioButtonId
            val fichaSeleccionada = radioFicha.checkedRadioButtonId

            if (fechaInscripcion.isEmpty() || aptoSeleccionado == -1 || fichaSeleccionada == -1) {
                Toast.makeText(this, "Completá todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val aptoInt = if (aptoSeleccionado == R.id.rbAptoSi) 1 else 0
            val fichaInt = if (fichaSeleccionada == R.id.rbFichaSi) 1 else 0

            val idSocio = db.insertarSocio(idUsuario, fechaInscripcion, aptoInt, fichaInt)

            if (idSocio > 0) {
                mostrarAlertaPersonalizada("Socio registrado correctamente (ID: $idSocio)")
                socioRegistrado = true
                // deshabilitar el botón guardar para evitar duplicados
                btnAgregarSocio.isEnabled = false
                // habilitar el botón ir a pagos
                btnPagos.isEnabled = true
            } else {
                Toast.makeText(this, "Error al registrar socio", Toast.LENGTH_SHORT).show()
            }

        }

        // botón limpiar campos
        btnLimpiar.setOnClickListener {
            inputFecha.setText("")
            radioApto.clearCheck()
            radioFicha.clearCheck()
        }

        // botón ir a pagos
        btnPagos.setOnClickListener {
            if (!socioRegistrado) {
                Toast.makeText(this, "Primero registrá al socio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // esta activity siempre es para socios, vamos directo al pago mensual
            val intent = Intent(this, PagoMensualActivity::class.java)
            intent.putExtra("id_usuario", idUsuario)  // usamos el mismo usuario_id
            startActivity(intent)
        }

    }

    // mostrar alerta personalizada con bordes redondeados
    private fun mostrarAlertaPersonalizada(mensaje: String) {
        val dialogView = layoutInflater.inflate(R.layout.custom_alerta, null)
        val tvMensaje = dialogView.findViewById<TextView>(R.id.tvMensaje)
        val btnAceptar = dialogView.findViewById<Button>(R.id.btnAceptar)
        tvMensaje.text = mensaje

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        alertDialog.setCancelable(false)
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.alerta)
        alertDialog.show()

        btnAceptar.setOnClickListener {
            alertDialog.dismiss()
        }
    }

}

