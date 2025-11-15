package com.example.club_deportivo_comf4

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SociosVencidosAdapter(
    private val lista: List<SocioVencido>
) : RecyclerView.Adapter<SociosVencidosAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvDni: TextView = itemView.findViewById(R.id.tvDni)
        val tvFechaAlta: TextView = itemView.findViewById(R.id.tvFechaAlta)
        val tvVencimiento: TextView = itemView.findViewById(R.id.tvVencimiento)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_socio_vencido, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val socio = lista[position]
        holder.tvNombre.text = socio.nombre
        holder.tvDni.text = socio.dni
        holder.tvFechaAlta.text = socio.fechaAlta
        holder.tvVencimiento.text = socio.vencimiento
    }

    override fun getItemCount(): Int = lista.size
}

