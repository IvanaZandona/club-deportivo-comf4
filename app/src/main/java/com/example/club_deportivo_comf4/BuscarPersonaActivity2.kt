package com.example.club_deportivo_comf4

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BuscarPersonaActivity2 : AppCompatActivity() {

    private lateinit var inputDNI: EditText
    private lateinit var contenedorTarjeta: LinearLayout
    private lateinit var imgInicial: ImageView

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

        // Inicializamos views
        db = DBHelper(this)
        inputDNI = findViewById(R.id.edtBuscarDni)
        contenedorTarjeta = findViewById(R.id.cardUsuario)
        imgInicial = findViewById(R.id.imgInicial)

        findViewById<Button>(R.id.btnVolver).setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }

        findViewById<ImageButton>(R.id.boton_flecha_atras).setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }

        findViewById<ImageView>(R.id.btnBuscar).setOnClickListener {
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
                ocultarTarjeta()
            }
        } else {
            Toast.makeText(this, "No existe un usuario con ese DNI", Toast.LENGTH_SHORT).show()
            inputDNI.error = "No encontrado"
            ocultarTarjeta()
        }
    }

    //Tarjeta al encotrar un usuario
    private fun mostrarTarjeta(usuario: Usuario) {
        imgInicial.visibility = View.GONE
        contenedorTarjeta.removeAllViews()
        contenedorTarjeta.visibility = View.VISIBLE

        val tarjeta =
            layoutInflater.inflate(R.layout.tarjeta_usuario_buscador, contenedorTarjeta, false)

        tarjeta.findViewById<TextView>(R.id.nombrePrincipal).text =
            "${usuario.nombre} ${usuario.apellido}"
        tarjeta.findViewById<TextView>(R.id.tvDNI).text = usuario.dni
        tarjeta.findViewById<TextView>(R.id.tvEmail).text = usuario.email
        tarjeta.findViewById<TextView>(R.id.tvTelefono).text = usuario.telefono

        val estado = if (usuario.tipoUsuario == 1) "Socio" else "No Socio"
        tarjeta.findViewById<TextView>(R.id.tvEstado).text = estado

        if (usuario.tipoUsuario == 1) {
            val socio = db.obtenerSocioPorDNI(usuario.dni)?.let { socio ->
                tarjeta.findViewById<TextView>(R.id.tvFechaInscripcion).text = socio.fechaInscripcion
                tarjeta.findViewById<TextView>(R.id.tvUltimoVencimiento).text = socio.fechaVencimiento

            }
        } else if (usuario.tipoUsuario == 2) {
            val noSocio = db.obtenerNoSocioPorDNI(usuario.dni)?.let { noSocio ->
                tarjeta.findViewById<TextView>(R.id.tvFechaInscripcion).text = noSocio.fechaRegistro
            }
        }


        // Botones
        tarjeta.findViewById<ImageButton>(R.id.btnEditar).setOnClickListener {
            Toast.makeText(this, "Editar usuario ${usuario.nombre}", Toast.LENGTH_SHORT).show()
        }

        tarjeta.findViewById<ImageButton>(R.id.btnBorrar).setOnClickListener {
            Toast.makeText(this, "Debe borrar al usuario desde los listados correspondientes", Toast.LENGTH_SHORT).show()
        }

        // accede a boton de carnet solo si es socio
        val btnCarnet = tarjeta.findViewById<ImageButton>(R.id.btnImprimirCarnet)
        btnCarnet.setOnClickListener {
            if (usuario.tipoUsuario != 1) {
                Toast.makeText(this, "Solo los socios tienen carnet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val socio = db.obtenerSocioPorDNI(usuario.dni)

            val intent = Intent(this, CarnetActivity::class.java)
            intent.putExtra("nombre", usuario.nombre)
            intent.putExtra("apellido", usuario.apellido)
            intent.putExtra("dni", usuario.dni)
            intent.putExtra("fechaNacimiento", usuario.fechaNacimiento)
            intent.putExtra("idUsuario", usuario.id)

            startActivity(intent)
        }

        // Agregamos la tarjeta al contenedor
        contenedorTarjeta.addView(tarjeta)
    }

    private fun ocultarTarjeta() {
        contenedorTarjeta.removeAllViews()
        contenedorTarjeta.visibility = View.GONE
        imgInicial.visibility = View.VISIBLE
    }
}
