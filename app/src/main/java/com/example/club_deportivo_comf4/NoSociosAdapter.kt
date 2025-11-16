package com.example.club_deportivo_comf4

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoSociosAdapter(
    private val listaNoSocios: MutableList<NoSocio>,
    private val dbHelper: DBHelper,
    private val itemClickListener: (NoSocio) -> Unit
) : RecyclerView.Adapter<NoSociosAdapter.NoSocioViewHolder>() {


    class NoSocioViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val cabecera: LinearLayout = view.findViewById(R.id.cabecera)
        val contenidoDesplegable: LinearLayout = view.findViewById(R.id.contenidoDesplegable)
        val flechaDesplegable: ImageView = view.findViewById(R.id.flechaDesplegable)

        val tvNombrePrincipal: TextView = view.findViewById(R.id.nombrePrincipal)
        val tvFechaInscripcion: TextView = view.findViewById(R.id.tvFechaInscripcion)
        val tvVencimiento: TextView = view.findViewById(R.id.tvUltimoVencimiento)
        val tvDNI: TextView = view.findViewById(R.id.tvDNI)
        val tvEmail: TextView = view.findViewById(R.id.tvEmail)
        val tvTelefono: TextView = view.findViewById(R.id.tvTelefono)

        val btnEditar: ImageButton = view.findViewById(R.id.btnEditar)
        val btnImprimirCarnet: ImageButton = view.findViewById(R.id.btnImprimirCarnet)
        val btnBorrar: ImageButton = view.findViewById(R.id.btnBorrar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoSocioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tarjeta_usuario, parent, false)
        return NoSocioViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoSocioViewHolder, position: Int) {
        val noSocio = listaNoSocios[position]
        val context = holder.itemView.context

        // Rellenar datos
        holder.tvNombrePrincipal.text = "${noSocio.nombre} ${noSocio.apellido}"
        holder.tvFechaInscripcion.text = noSocio.fechaRegistro
        holder.tvVencimiento.text = "—"
        holder.tvDNI.text = noSocio.dni
        holder.tvEmail.text = noSocio.email
        holder.tvTelefono.text = noSocio.telefono

        // --- DESPLEGABLE ---
        holder.contenidoDesplegable.visibility = View.GONE
        holder.flechaDesplegable.rotation = 0f
        holder.cabecera.setOnClickListener {
            val isVisible = holder.contenidoDesplegable.visibility == View.VISIBLE
            if (isVisible) {
                holder.contenidoDesplegable.visibility = View.GONE
                holder.flechaDesplegable.rotation = 0f
            } else {
                holder.contenidoDesplegable.visibility = View.VISIBLE
                holder.flechaDesplegable.rotation = 180f
            }
        }

        // Click en la tarjeta
        holder.itemView.setOnClickListener {
            itemClickListener(noSocio)
        }

        // Botón Borrar
        holder.btnBorrar.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Eliminar No Socio")
                .setMessage("¿Desea eliminar a ${noSocio.nombre}?")
                .setPositiveButton("Sí") { _, _ ->
                    if (dbHelper.eliminarNoSocio(noSocio.dni)) {
                        listaNoSocios.removeAt(position)
                        notifyItemRemoved(position)
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }

        // Otros botones (editar e imprimir)
        holder.btnEditar.setOnClickListener {
        }

        holder.btnImprimirCarnet.setOnClickListener {
        }
    }

    override fun getItemCount(): Int = listaNoSocios.size

    fun actualizarLista(nuevaLista: List<NoSocio>) {
        listaNoSocios.clear()
        listaNoSocios.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}
