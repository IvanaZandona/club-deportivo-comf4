package com.example.club_deportivo_comf4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EditarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- Botón atrás para volver a ListadosActivity ---
        val botonAtras = findViewById<ImageButton>(R.id.boton_flecha_atras)
        botonAtras.setOnClickListener {
            val intent = Intent(this, ListadosActivity::class.java)
            startActivity(intent)
            finish()
        }

        // --- Botón Guardar Edición ---
        val btnGuardar = findViewById<Button>(R.id.btnGuardarEdicion)
        btnGuardar.setOnClickListener {
            mostrarAlertaPersonalizada("Guardado correctamente")
        }
    }

    // --- Función para mostrar alerta personalizada con bordes redondeados ---
    private fun mostrarAlertaPersonalizada(mensaje: String) {
        val dialogView = layoutInflater.inflate(R.layout.custom_alerta, null)
        val tvMensaje = dialogView.findViewById<TextView>(R.id.tvMensaje)
        val btnAceptar = dialogView.findViewById<Button>(R.id.btnAceptar)

        // Texto del mensaje recibido por parámetro
        tvMensaje.text = mensaje

        // Crear y mostrar la alerta
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        alertDialog.setCancelable(false)
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.alerta)
        alertDialog.show()

        // Acción del botón
        btnAceptar.setOnClickListener {
            alertDialog.dismiss()
        }
    }
}
