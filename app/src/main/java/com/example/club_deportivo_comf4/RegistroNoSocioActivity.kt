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

class RegistroNoSocioActivity : AppCompatActivity() {

    private var noSocioRegistrado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_no_socio)

        // Ajuste de padding para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //inputs
        val botonAtras = findViewById<ImageButton>(R.id.boton_flecha_atras)
        val inputFecha = findViewById<EditText>(R.id.inputFecha)
        val btnAgregarNoSocio = findViewById<Button>(R.id.btnAgregarNoSocio)
        val btnPagos = findViewById<Button>(R.id.btnIrAPagos)
        val btnLimpiar = findViewById<Button>(R.id.btnLimpiar)

        btnPagos.isEnabled = false // deshabilitado hasta registrar

        // --- Botón atrás ---
        botonAtras.setOnClickListener {
            val intent = Intent(this, RegistrarActivity::class.java)
            startActivity(intent)
            finish() // opcional: cierra esta actividad para que no quede en el back stack
        }

        // --- EditText con calendario ---
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

        // --- Obtener ID del usuario ---
        val db = DBHelper(this)
        val idUsuario = intent.getLongExtra("id_usuario", -1)

        if (idUsuario == -1L) {
            Toast.makeText(this, "Error: no se encontró el usuario", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // --- Botón registrar No Socio ---
        btnAgregarNoSocio.setOnClickListener {
            val fecha = inputFecha.text.toString().trim()

            if (fecha.isEmpty()) {
                Toast.makeText(this, "Ingresá la fecha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val idNoSocio = db.insertarNoSocio(idUsuario, fecha)

            if (idNoSocio > 0) {
                mostrarAlertaPersonalizada("No Socio registrado correctamente (ID: $idNoSocio)")
                noSocioRegistrado = true
                btnAgregarNoSocio.isEnabled = false
                btnPagos.isEnabled = true
            } else {
                Toast.makeText(this, "Error al registrar no socio", Toast.LENGTH_SHORT).show()
            }

        }

        // --- botón limpiar ---
        btnLimpiar.setOnClickListener {
            inputFecha.setText("")
        }

        // --- botón ir a pago diario ---
        btnPagos.setOnClickListener {
            if (!noSocioRegistrado) {
                Toast.makeText(this, "Primero registrá al no socio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, PagoDiarioActivity::class.java)
            intent.putExtra("id_usuario", idUsuario)
            startActivity(intent)
        }

    }

    // Función para mostrar alerta personalizada con bordes redondeados
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
