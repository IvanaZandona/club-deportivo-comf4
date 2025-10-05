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

class RegistroSocioActivity : AppCompatActivity() {

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

        // --- Botón atrás ---
        val botonAtras = findViewById<ImageButton>(R.id.boton_flecha_atras)
        botonAtras.setOnClickListener {
            val intent = Intent(this, RegistrarActivity::class.java)
            startActivity(intent)
            finish() // opcional: cierra esta actividad
        }

        // --- EditText con calendario ---
        val inputFecha = findViewById<EditText>(R.id.inputFecha)
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

        // --- Botón Agregar Socio con alerta personalizada ---
        val btnAgregarSocio = findViewById<Button>(R.id.btnAgregarSocio)
        btnAgregarSocio.setOnClickListener {
            val idGenerado = generarID() // reemplazá con la lógica real de registro
            mostrarAlertaPersonalizada("Socio registrado con ID: $idGenerado")
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

    // Función para generar ID aleatorio
    private fun generarID(): Int {
        return (1000..9999).random()
    }
}
