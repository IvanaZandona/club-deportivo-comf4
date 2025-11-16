package com.example.club_deportivo_comf4

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListadoSociosVencidosActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_socios_vencidos)

        Log.d("SociosVencidos", "Activity creada")

        db = DBHelper(this)
        recycler = findViewById(R.id.recyclerSociosVencidos)

        // Configurar RecyclerView
        recycler.layoutManager = LinearLayoutManager(this)

        cargarSociosVencidos()

        // Botón volver
        findViewById<ImageView>(R.id.iconoVolver).setOnClickListener {
            val intent = Intent(this, ListadosActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Botón pagos
        findViewById<Button>(R.id.btnPagos).setOnClickListener {
            val intent = Intent(this, PagosActivity::class.java)
            startActivity(intent)
        }
    }

    private fun cargarSociosVencidos() {
        try {
            Log.d("SociosVencidos", "Cargando socios vencidos...")

            val lista = db.obtenerSociosVencidos()
            Log.d("SociosVencidos", "Socios vencidos obtenidos: ${lista.size}")

            // Debug: mostrar cada socio en logs
            lista.forEachIndexed { index, socio ->
                Log.d("SociosVencidos", "Socio $index: ${socio.nombre} - DNI: ${socio.dni} - Vence: ${socio.vencimiento}")
            }

            if (lista.isEmpty()) {
                // Mostrar Toast cuando no hay socios vencidos
                Toast.makeText(this, "No hay socios con pagos vencidos", Toast.LENGTH_LONG).show()
                Log.d("SociosVencidos", "No hay socios vencidos para mostrar")
            } else {
                recycler.adapter = SociosVencidosAdapter(lista)
                Log.d("SociosVencidos", "Adapter configurado con ${lista.size} elementos")
            }

        } catch (e: Exception) {
            Log.e("SociosVencidos", "Error cargando socios vencidos: ${e.message}")
            e.printStackTrace()

            // Mostrar error al usuario
            Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            db.close()
        } catch (e: Exception) {
            Log.e("SociosVencidos", "Error cerrando DB: ${e.message}")
        }
    }
}