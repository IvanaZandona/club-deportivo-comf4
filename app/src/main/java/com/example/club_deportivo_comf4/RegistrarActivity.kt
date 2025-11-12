package com.example.club_deportivo_comf4

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        // 👉 Inputs
        val inputNombre = findViewById<EditText>(R.id.inputNombre)
        val inputApellido = findViewById<EditText>(R.id.inputApellido)
        val inputDni = findViewById<EditText>(R.id.inputDNI)
        val inputTelefono = findViewById<EditText>(R.id.inputTelefono)
        val inputCorreo = findViewById<EditText>(R.id.inputCorreo)
        val radioGenero = findViewById<RadioGroup>(R.id.radioGenero)

        // 👉 Botones
        val btnLimpiar = findViewById<Button>(R.id.btnLimpiar)
        val btnAgregarSocio = findViewById<Button>(R.id.btnAgregarSocio)
        val btnAgregarNoSocio = findViewById<Button>(R.id.btnAgregarNoSocio)
        val btnAtras = findViewById<ImageButton>(R.id.boton_flecha_atras)

        // 👉 Botón atrás
        btnAtras.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }

        // 👉 Botón limpiar campos
        btnLimpiar.setOnClickListener {
            inputNombre.setText("")
            inputApellido.setText("")
            inputDni.setText("")
            inputTelefono.setText("")
            inputCorreo.setText("")
            radioGenero.clearCheck()
        }

        // 👉 Instancia de la BD
        val db = DBHelper(this)

        // ---------------------------------------------------------
        //  Botón Agregar SOCIO
        // ---------------------------------------------------------
        btnAgregarSocio.setOnClickListener {

            val nombre = inputNombre.text.toString().trim()
            val apellido = inputApellido.text.toString().trim()
            val dni = inputDni.text.toString().trim()
            val telefono = inputTelefono.text.toString().trim()
            val correo = inputCorreo.text.toString().trim()

            val generoSeleccionado = radioGenero.checkedRadioButtonId
            if (generoSeleccionado == -1) {
                Toast.makeText(this, "Seleccioná un género", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || telefono.isEmpty() || correo.isEmpty()) {
                Toast.makeText(this, "Completá todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 👉 Insertamos usuario tipo 1 = Socio
            val idUsuario = db.insertarUsuario(nombre, apellido, dni, telefono, correo, 1)

            if (idUsuario > 0) {
                Toast.makeText(this, "Usuario registrado. Continuá con los datos de SOCIO", Toast.LENGTH_SHORT).show()

                // 👉 Vamos a pantalla de socio con ID del usuario
                val intent = Intent(this, RegistroSocioActivity::class.java)
                intent.putExtra("id_usuario", idUsuario)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
            }
        }

        // ---------------------------------------------------------
        // Botón Agregar NO SOCIO
        // ---------------------------------------------------------
        btnAgregarNoSocio.setOnClickListener {

            val nombre = inputNombre.text.toString().trim()
            val apellido = inputApellido.text.toString().trim()
            val dni = inputDni.text.toString().trim()
            val telefono = inputTelefono.text.toString().trim()
            val correo = inputCorreo.text.toString().trim()

            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || telefono.isEmpty() || correo.isEmpty()) {
                Toast.makeText(this, "Completá todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 👉 Insertamos usuario tipo 2 = No Socio
            val idUsuario = db.insertarUsuario(nombre, apellido, dni, telefono, correo, 2)

            if (idUsuario > 0) {
                Toast.makeText(this, "Usuario registrado. Continuá con los datos de NO SOCIO", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, RegistroNoSocioActivity::class.java)
                intent.putExtra("id_usuario", idUsuario)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
