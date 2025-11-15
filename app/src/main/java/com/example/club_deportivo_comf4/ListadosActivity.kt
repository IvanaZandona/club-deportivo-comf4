package com.example.club_deportivo_comf4

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.content.Intent
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListadosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listados)




        // Botones
        val btnSociosVencidos = findViewById<LinearLayout>(R.id.btnSociosVencidos)
        val btnListadoSocios = findViewById<LinearLayout>(R.id.btnListadoSocios)
        val btnNoSocios = findViewById<LinearLayout>(R.id.btnListadoNoSocios)
        val btnVolver = findViewById<Button>(R.id.btnVolver)


        // Navegación
        btnSociosVencidos.setOnClickListener {
            val intent = Intent(this, ListadoSociosVencidosActivity::class.java)
            startActivity(intent)
        }

        btnListadoSocios.setOnClickListener {
            val intent = Intent(this, ListaSociosActivity::class.java)
            startActivity(intent)
        }

        btnNoSocios.setOnClickListener {
            val intent = Intent(this, ListadoNoSociosActivity::class.java)
            startActivity(intent)
        }


        btnVolver.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
    }
}

