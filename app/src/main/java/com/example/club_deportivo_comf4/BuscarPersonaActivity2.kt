package com.example.club_deportivo_comf4

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BuscarPersonaActivity2 : AppCompatActivity() {

    private lateinit var inputDNI: EditText
    private lateinit var contenedorTarjeta: LinearLayout

    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_buscar_persona2)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = DBHelper(this)
        inputDNI = findViewById(R.id.edtBuscarDni)
        contenedorTarjeta = findViewById(R.id.cardUsuario)

        findViewById<Button>(R.id.btnVolver).setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
        }

        findViewById<ImageButton>(R.id.boton_flecha_atras).setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }

        val btnBuscar = findViewById<ImageView>(R.id.btnBuscar)
        btnBuscar.setOnClickListener {
            buscarUsuario()
        }
    }

    private fun buscarUsuario() {
        val dniIngresado = inputDNI.text.toString().trim()

        if (dniIngresado.isEmpty()) {
            inputDNI.error = "Ingresá un DNI"
            return
        }

        val idUsuario = db.obtenerUsuarioIdPorDNI(dniIngresado)

        if (idUsuario != null) {
            val usuario = db.obtenerUsuarioPorId(idUsuario)

            if (usuario != null) {
                mostrarTarjeta(usuario)
            } else {
                Toast.makeText(this, "Error al cargar datos del usuario", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(this, "No existe un usuario con ese DNI", Toast.LENGTH_SHORT).show()
            inputDNI.error = "No encontrado"
        }
    }

    private fun mostrarTarjeta(usuario: Usuario) {

        contenedorTarjeta.removeAllViews()

        val vista = layoutInflater.inflate(R.layout.tarjeta_usuario, contenedorTarjeta, false)

        vista.findViewById<TextView>(R.id.nombrePrincipal).text =
            "${usuario.nombre} ${usuario.apellido}"

        vista.findViewById<TextView>(R.id.tvDNI).text = usuario.dni
        vista.findViewById<TextView>(R.id.tvEmail).text = usuario.email
        vista.findViewById<TextView>(R.id.tvTelefono).text = usuario.telefono

        val estado = if (usuario.tipoUsuario == 1) "Socio" else "No Socio"
        vista.findViewById<TextView>(R.id.tvEstado).text = estado

        // la tabla tiene fecha_nacimiento → se usa eso
        vista.findViewById<TextView>(R.id.tvFechaInscripcion).text = usuario.fechaNacimiento

        vista.findViewById<ImageButton>(R.id.btnEditar).setOnClickListener {
            Toast.makeText(this, "Editar usuario ${usuario.nombre}", Toast.LENGTH_SHORT).show()
        }

        // ✅ ESTABA AFUERA → AHORA ESTÁ BIEN UBICADO
        contenedorTarjeta.addView(vista)
    }
}
