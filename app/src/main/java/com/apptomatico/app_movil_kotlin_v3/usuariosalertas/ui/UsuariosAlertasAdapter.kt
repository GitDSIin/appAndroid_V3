package com.apptomatico.app_movil_kotlin_v3.usuariosalertas.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.apptomatico.app_movil_kotlin_v3.R
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.apptomatico.app_movil_kotlin_v3.usuariosalertas.UsuariosAlertasFragment.Companion.listaUsuarios


class UsuariosAlertasAdapter(val context: Context
): RecyclerView.Adapter<UsuariosAlertasAdapter.UsuarioViewHolder>() {

    private var databaseHelper_Data: DatabaseHelper = DatabaseHelper.getInstance(context)

    inner class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEquipo: TextView = itemView.findViewById(R.id.tvNombreEquipo)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreUsuario)
        val switchAlerta: Switch = itemView.findViewById(R.id.switchAlerta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario_alerta, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = listaUsuarios!![position]
        holder.tvEquipo.text = usuario?.nombre ?: "Sin nombre"
        holder.tvNombre.text = usuario?.usuario ?: "Sin nombre"

        holder.switchAlerta.setOnCheckedChangeListener(null)
        holder.switchAlerta.isChecked = usuario?.alerta_movil == 1

        holder.switchAlerta.setOnCheckedChangeListener { _, isChecked ->
            val nuevaAlerta = if (isChecked) 1 else 0
            usuario?.alerta_movil = nuevaAlerta
            databaseHelper_Data.actualizarAlertaMovil(usuario.id!!, nuevaAlerta)
        }
    }

    override fun getItemCount() = listaUsuarios!!.size

    fun actualizarTodos(nuevoValor: Boolean) {
        val nuevaAlerta = if (nuevoValor) 1 else 0
        for (usuario in listaUsuarios!!) {
            if (usuario.alerta_movil != nuevaAlerta) {
                usuario.alerta_movil = nuevaAlerta
                databaseHelper_Data.actualizarAlertaMovil(usuario.id!!, nuevaAlerta)
            }
        }
        notifyDataSetChanged()
    }
}
