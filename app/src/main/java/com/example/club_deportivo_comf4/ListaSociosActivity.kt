package com.example.club_deportivo_comf4

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListaSociosActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var adapter: SociosAdapter
    private val listaSocios = mutableListOf<Socio>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_socios)

        // Inicializar DBHelper
        dbHelper = DBHelper(this)

        // Configurar RecyclerView
        configurarRecyclerView()

        // Cargar datos
        cargarSocios()

        // Configurar botones
        configurarBotones()
    }

    private fun configurarRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.rvList)


        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        // Inicializar adapter
        adapter = SociosAdapter(listaSocios, dbHelper) { socio ->

            Log.d("ListaSocios", "Click en socio: ${socio.nombre} ${socio.apellido}")

        }

        recyclerView.adapter = adapter
    }

    private fun cargarSocios() {
        // Obtener fecha actual para el cálculo de estados
        val hoy = dbHelper.fechaActual()

        // Obtener todos los socios desde la base de datos
        val sociosDesdeDB = dbHelper.obtenerTodosLosSocios(hoy)

        // Log para debug
        Log.d("ListaSocios", "Socios encontrados: ${sociosDesdeDB.size}")

        // Actualizar la lista
        listaSocios.clear()
        listaSocios.addAll(sociosDesdeDB)
        adapter.notifyDataSetChanged()

        // Mostrar mensaje si no hay datos
        if (sociosDesdeDB.isEmpty()) {
            Log.d("ListaSocios", "No hay socios registrados en la base de datos")
        }
    }

    private fun configurarBotones() {
        // Botón Volver
        val btnVolver = findViewById<Button>(R.id.btnVolver)
        btnVolver.setOnClickListener {
            // finish() es mejor si vienes del menú, así vuelves al menú
            finish()
        }

        // Botón Pagos
        val btnPagos = findViewById<Button>(R.id.btnPagos)
        btnPagos.setOnClickListener {
            val intent = Intent(this, PagosActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()

        cargarSocios()
    }
}