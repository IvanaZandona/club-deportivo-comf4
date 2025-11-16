package com.example.club_deportivo_comf4

import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
// Importa tu paquete R para resolver R.layout.item_socio (si lo renombraste)

class SociosAdapter(
    private val listaSocios: MutableList<Socio>,
    private val dbHelper: DBHelper,
    // Puedes inyectar un listener si quieres manejar clicks en la Activity/Fragment
    private val itemClickListener: (Socio) -> Unit
): RecyclerView.Adapter<SociosAdapter.SocioViewHolder>() {

    // ==========================================
    // 1. EL VIEWHOLDER (Añade todos los campos del item_socio.xml)
    // ==========================================
    class SocioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Textos
        val tvNombrePrincipal: TextView = view.findViewById(R.id.nombrePrincipal)

        val tvFechaInscripcion: TextView = view.findViewById(R.id.tvFechaInscripcion)
        val tvVencimiento: TextView = view.findViewById(R.id.tvUltimoVencimiento)
        val tvDNI: TextView = view.findViewById(R.id.tvDNI)
        val tvEmail: TextView = view.findViewById(R.id.tvEmail)
        val tvTelefono: TextView = view.findViewById(R.id.tvTelefono)

        // Botones de acción
        val btnEditar: ImageButton = view.findViewById(R.id.btnEditar)
        val btnImprimirCarnet: ImageButton = view.findViewById(R.id.btnImprimirCarnet)
        val btnBorrar: ImageButton = view.findViewById(R.id.btnBorrar)
    }

    // ==========================================
    // 2. CREAR VIEWHOLDER
    // ==========================================
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tarjeta_usuario, parent, false) // ⬅️ Usamos item_socio.xml
        return SocioViewHolder(view)
    }

    // ==========================================
    // 3. ENLAZAR DATOS Y LÓGICA DE BOTONES
    // ==========================================
    override fun onBindViewHolder(holder: SocioViewHolder, position: Int) {
        val socioActual = listaSocios[position]
        val context = holder.itemView.context // Acceso al contexto

        // Rellenar la tarjeta

        holder.tvNombrePrincipal.text = "${socioActual.nombre} ${socioActual.apellido}"

        holder.tvFechaInscripcion.text = socioActual.fechaInscripcion
        "14/05/25".also { holder.tvVencimiento.text = it }
        holder.tvDNI.text = socioActual.dni
        holder.tvEmail.text = socioActual.email
        holder.tvTelefono.text = socioActual.telefono

        // Lógica de color de estado (Mejora visual)
    /*    if (socioActual.estado == "Vencido") {
            holder.tvEstado.setTextColor(Color.RED)
            // holder.tvEstado.setBackgroundResource(R.drawable.bg_estado_vencido) // Si tienes un drawable rojo
        } else {
            holder.tvEstado.setTextColor(Color.parseColor("#00CC00")) // Color verde
            // holder.tvEstado.setBackgroundResource(R.drawable.bg_estado_activo) // Si tienes un drawable verde
        }*/

        // --- MANEJO DE EVENTOS ---

        // Click en la tarjeta
        holder.itemView.setOnClickListener {
            itemClickListener(socioActual)
        }

        // Botón Borrar (Long Click ya no es necesario, usamos el botón)
        holder.btnBorrar.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Eliminar Socio")
                .setMessage("¿Desea eliminar permanentemente a ${socioActual.nombre}?")
                .setPositiveButton("Sí") { _, _ ->
                    // Ejecutar la eliminación en la DB
                    if (dbHelper.eliminarSocio(socioActual.dni)) {
                        // Si se elimina de la DB, se elimina del Adapter
                        listaSocios.removeAt(position)
                        notifyItemRemoved(position)
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }

        // Otros botones (Editar, Imprimir)
        holder.btnEditar.setOnClickListener {
            // Lógica para abrir la pantalla de edición
        }

        holder.btnImprimirCarnet.setOnClickListener {
            // Lógica para imprimir (mostrar diálogo, etc.)
        }
    }

    // ==========================================
    // 4. CONTADOR Y ACTUALIZACIÓN
    // ==========================================
    override fun getItemCount(): Int {
        return listaSocios.size
    }

    fun actualizarLista(nuevaLista: List<Socio>) {
        listaSocios.clear()
        listaSocios.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}