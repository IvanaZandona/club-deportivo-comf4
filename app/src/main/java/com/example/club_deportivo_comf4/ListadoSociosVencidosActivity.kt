package com.example.club_deportivo_comf4

import android.os.Bundle
import android.widget.ImageView // <-- Se importa ImageViewimport androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.widget.Button // Se mantiene para el otro botón
import androidx.appcompat.app.AppCompatActivity

class ListadoSociosVencidosActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_socios_vencidos)

        db = DBHelper(this)
        recycler = findViewById(R.id.recyclerSociosVencidos)

        recycler.layoutManager = LinearLayoutManager(this)

        val lista = db.obtenerSociosVencidos()
        recycler.adapter = SociosVencidosAdapter(lista)

        // --- INICIO DE LA CORRECIÓN ---
        // Se busca un 'ImageView', no un 'Button'.
        findViewById<ImageView>(R.id.iconoVolver).setOnClickListener {
            val intent = Intent(this, ListadosActivity::class.java)
            startActivity(intent)
            finish() // Opcional: cierra esta pantalla para que no se apile
        }
        // --- FIN DE LA CORRECIÓN ---

        // REDIRECCION A PAGOS (Este botón ya estaba bien)
        findViewById<Button>(R.id.btnPagos).setOnClickListener {
            val intent = Intent(this, PagosActivity::class.java)
            startActivity(intent)
        }
    }
}
