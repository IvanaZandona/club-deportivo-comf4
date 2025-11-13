package com.example.club_deportivo_comf4

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class RegistrarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // inputs
        val inputNombre = findViewById<EditText>(R.id.inputNombre)
        val inputApellido = findViewById<EditText>(R.id.inputApellido)
        val inputDni = findViewById<EditText>(R.id.inputDNI)
        val inputFechaNac = findViewById<EditText>(R.id.inputFecha)
        val inputTelefono = findViewById<EditText>(R.id.inputTelefono)
        val inputEmail = findViewById<EditText>(R.id.inputCorreo)
        val radioGenero = findViewById<RadioGroup>(R.id.radioGenero)

        // botones
        val btnLimpiar = findViewById<Button>(R.id.btnLimpiar)
        val btnAgregarSocio = findViewById<Button>(R.id.btnAgregarSocio)
        val btnAgregarNoSocio = findViewById<Button>(R.id.btnAgregarNoSocio)
        val btnAtras = findViewById<ImageButton>(R.id.boton_flecha_atras)

        // botón atrás
        btnAtras.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }

        // --- EditText con calendario ---
       // val inputFecha = findViewById<EditText>(R.id.inputFecha)
        inputFechaNac.setOnClickListener {
            val calendario = Calendar.getInstance()
            val año = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, añoSeleccionado, mesSeleccionado, diaSeleccionado ->
                    val fecha = String.format("%02d/%02d/%d", diaSeleccionado, mesSeleccionado + 1, añoSeleccionado)
                    inputFechaNac.setText(fecha)
                },
                año, mes, dia
            )
            datePickerDialog.show()
        }

        // botón limpiar campos
        btnLimpiar.setOnClickListener {
            inputNombre.setText("")
            inputApellido.setText("")
            inputDni.setText("")
            inputFechaNac.setText("")
            inputTelefono.setText("")
            inputEmail.setText("")
            radioGenero.clearCheck()
        }

        // instancia de la BD
        val db = DBHelper(this)

        // ---------------------------------------------------------
        //  Botón Agregar SOCIO
        // ---------------------------------------------------------
        btnAgregarSocio.setOnClickListener {

            val nombre = inputNombre.text.toString().trim()
            val apellido = inputApellido.text.toString().trim()
            val dni = inputDni.text.toString().trim()
            val fechaNac = inputFechaNac.text.toString().trim()
            val telefono = inputTelefono.text.toString().trim()
            val email = inputEmail.text.toString().trim()

            val generoSeleccionado = radioGenero.checkedRadioButtonId
            if (generoSeleccionado == -1) {
                Toast.makeText(this, "Seleccioná un género", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || fechaNac.isEmpty() || telefono.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Completá todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --- validar largo del DNI ---
            if (dni.length != 8) {
                Toast.makeText(this, "El DNI debe tener exactamente 8 dígitos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (db.existeDni(dni)) {
                Toast.makeText(this, "Ya existe un usuario con ese DNI", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Insertamos usuario tipo 1 = Socio
            val idUsuario = db.insertarUsuario(nombre, apellido, dni, fechaNac, telefono, email, 1)

            if (idUsuario > 0) {
                Toast.makeText(this, "Usuario registrado. Continuá con los datos de SOCIO", Toast.LENGTH_SHORT).show()

                // Vamos a pantalla de socio con ID del usuario
                val intent = Intent(this, RegistroSocioActivity::class.java)
                intent.putExtra("id_usuario", idUsuario)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error: fallo al registrar el usuario", Toast.LENGTH_SHORT).show()
            }
        }

        // ---------------------------------------------------------
        // Botón Agregar NO SOCIO
        // ---------------------------------------------------------
        btnAgregarNoSocio.setOnClickListener {

            val nombre = inputNombre.text.toString().trim()
            val apellido = inputApellido.text.toString().trim()
            val dni = inputDni.text.toString().trim()
            val fechaNac = inputFechaNac.text.toString().trim()
            val telefono = inputTelefono.text.toString().trim()
            val email = inputEmail.text.toString().trim()

            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || fechaNac.isEmpty() || telefono.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Completá todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (db.existeDni(dni)) {
                Toast.makeText(this, "Ya existe un usuario con ese DNI", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // insertamos usuario tipo 2 = No Socio
            val idUsuario = db.insertarUsuario(nombre, apellido, dni, fechaNac, telefono, email, 2)

            if (idUsuario > 0) {
                Toast.makeText(this, "Usuario registrado. Continuá con los datos de NO SOCIO", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, RegistroNoSocioActivity::class.java)
                intent.putExtra("id_usuario", idUsuario)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error: fallo al registrar el usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
