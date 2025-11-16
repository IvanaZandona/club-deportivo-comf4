package com.example.club_deportivo_comf4

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoSociosAdapter(
    private val listaNoSocios: MutableList<NoSocio>,
    private val dbHelper: DBHelper,
    private val itemClickListener: (NoSocio) -> Unit
) : RecyclerView.Adapter<NoSociosAdapter.NoSocioViewHolder>() {

    // ============================================================
    // 1. VIEWHOLDER (usa los IDs del layout tarjeta_usuario.xml)
    // ============================================================
    class NoSocioViewHolder(view: View) : RecyclerView.ViewHolder(view) {

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

    // ============================================================
    // 2. CREAR VIEWHOLDER
    // ============================================================
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoSocioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tarjeta_usuario, parent, false)

        return NoSocioViewHolder(view)
    }

    // ============================================================
    // 3. VINCULAR DATOS A LA TARJETA
    // ============================================================
    override fun onBindViewHolder(holder: NoSocioViewHolder, position: Int) {
        val noSocio = listaNoSocios[position]
        val context = holder.itemView.context

        // Datos reales
        holder.tvNombrePrincipal.text = "${noSocio.nombre} ${noSocio.apellido}"
        holder.tvFechaInscripcion.text = noSocio.fechaRegistro
        holder.tvVencimiento.text = "—" // No socios no tienen vencimiento
        holder.tvDNI.text = noSocio.dni
        holder.tvEmail.text = noSocio.email
        holder.tvTelefono.text = noSocio.telefono

        // Click item
        holder.itemView.setOnClickListener {
            itemClickListener(noSocio)
        }

        // BORRAR NO SOCIO
        holder.btnBorrar.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Eliminar No Socio")
                .setMessage("¿Desea eliminar a ${noSocio.nombre}?")
                .setPositiveButton("Sí") { _, _ ->

                    // ⚠️ DEBE EXISTIR eliminarNoSocio()
                    if (dbHelper.eliminarNoSocio(noSocio.dni)) {
                        listaNoSocios.removeAt(position)
                        notifyItemRemoved(position)
                    }

                }
                .setNegativeButton("No", null)
                .show()
        }

        // Botón Editar (opcional)
        holder.btnEditar.setOnClickListener {
            // futura lógica
        }

        holder.btnImprimirCarnet.setOnClickListener {
            // futura lógica
        }
    }

    // ============================================================
    // 4. TAMAÑO DE LA LISTA
    // ============================================================
    override fun getItemCount(): Int = listaNoSocios.size

    // ============================================================
    // 5. Actualizar lista completa
    // ============================================================
    fun actualizarLista(nuevaLista: List<NoSocio>) {
        listaNoSocios.clear()
        listaNoSocios.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}
