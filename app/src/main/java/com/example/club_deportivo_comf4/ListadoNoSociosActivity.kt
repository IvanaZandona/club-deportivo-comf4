package com.example.club_deportivo_comf4

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListadoNoSociosActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var adapter: NoSociosAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvSinResultados: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_no_socios)

        dbHelper = DBHelper(this)

        recyclerView = findViewById(R.id.rvListNoSocios)
        tvSinResultados = findViewById(R.id.tvSinResultados)

        configurarRecyclerView()
        configurarBotones()
    }

    private fun configurarRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        // Adapter con lista vacía al inicio
        adapter = NoSociosAdapter(mutableListOf(), dbHelper) { noSocio ->
            Log.d("ListadoNoSocios", "Click en No Socio: ${noSocio.nombre} ${noSocio.apellido}")
        }

        recyclerView.adapter = adapter
    }

    private fun cargarNoSocios() {
        val noSociosDesdeDB = dbHelper.obtenerTodosLosNoSocios()

        Log.d("ListadoNoSocios", "No socios en DB: ${noSociosDesdeDB.size}")

        // Actualizar lista del adapter
        adapter.actualizarLista(noSociosDesdeDB)

        if (noSociosDesdeDB.isEmpty()) {
            recyclerView.visibility = View.GONE
            tvSinResultados.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            tvSinResultados.visibility = View.GONE
        }
    }

    private fun configurarBotones() {
        val btnVolver = findViewById<Button>(R.id.btnVolver)
        btnVolver.setOnClickListener { finish() }

        val btnPagos = findViewById<Button>(R.id.btnPagos)
        btnPagos.setOnClickListener {
            startActivity(Intent(this, PagosActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        cargarNoSocios()
    }
}
