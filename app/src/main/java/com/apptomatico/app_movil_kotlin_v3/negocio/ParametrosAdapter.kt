package com.apptomatico.app_movil_kotlin_v3.negocio

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.apptomatico.app_movil_kotlin_v3.R
import com.apptomatico.app_movil_kotlin_v3.model.ItemParametrosAccDB


class ParametrosAdapter(
    private val lista: ArrayList<ItemParametrosAccDB>,
    private val consulta: String,
    private val obtenerNombreDeCampo: (String, String) -> String
) : RecyclerView.Adapter<ParametrosAdapter.ParametroViewHolder>() {

    inner class ParametroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCampo: TextView = view.findViewById(R.id.tvCampo)
        val tvTipo: TextView = view.findViewById(R.id.tvTipo)
        val etValor: EditText = view.findViewById(R.id.etValor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParametroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_parametro_input, parent, false)
        return ParametroViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParametroViewHolder, position: Int) {
        val item = lista[position]
        holder.tvCampo.text = obtenerNombreDeCampo(consulta, item.campo!!)
        holder.tvTipo.text = item.tipo

        holder.etValor.setText(item.valor ?: "")

        holder.etValor.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                item.valor = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun getItemCount(): Int = lista.size
}
