package com.example.club_deportivo_comf4

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BuscadorAdapter(
    private var lista: MutableList<Usuario>,
    private val onClickEditar: (Usuario) -> Unit,
    private val onClickEliminar: (Usuario) -> Unit
) : RecyclerView.Adapter<BuscadorAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombrePrincipal: TextView = view.findViewById(R.id.nombrePrincipal)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val tvFechaNacimiento: TextView = view.findViewById(R.id.tvFechaInscripcion)
        val tvDNI: TextView = view.findViewById(R.id.tvDNI)
        val tvEmail: TextView = view.findViewById(R.id.tvEmail)
        val tvTelefono: TextView = view.findViewById(R.id.tvTelefono)
        val btnEditar: ImageButton = view.findViewById(R.id.btnEditar)
        val btnBorrar: ImageButton = view.findViewById(R.id.btnBorrar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.tarjeta_usuario, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]

        holder.nombrePrincipal.text = "${item.nombre} ${item.apellido}"
        holder.tvDNI.text = item.dni
        holder.tvEmail.text = item.email
        holder.tvTelefono.text = item.telefono
        holder.tvFechaNacimiento.text = item.fechaNacimiento

        val estado = if (item.tipoUsuario == 1) "Socio" else "No Socio"
        holder.tvEstado.text = estado

        holder.btnEditar.setOnClickListener { onClickEditar(item) }
        holder.btnBorrar.setOnClickListener { onClickEliminar(item) }
    }

    override fun getItemCount(): Int = lista.size

   /* fun actualizarLista(nuevaLista: MutableList<Usuario>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }*/
}
