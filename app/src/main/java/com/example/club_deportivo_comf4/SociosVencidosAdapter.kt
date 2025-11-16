package com.example.club_deportivo_comf4

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
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
        val ivEmail: ImageView = itemView.findViewById(R.id.ivEmail)
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

        holder.ivEmail.setOnClickListener {
            val context = holder.itemView.context
            val dialogView = LayoutInflater.from(context)
                .inflate(R.layout.custom_alerta, null)

            val tvMensaje: TextView = dialogView.findViewById(R.id.tvMensaje)
            tvMensaje.text = "Mensaje enviado correctamente"

            val alertDialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .create()

            val btnAceptar: Button = dialogView.findViewById(R.id.btnAceptar)
            btnAceptar.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()
        }
    }

    override fun getItemCount(): Int = lista.size
}
