package com.example.proyecto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.listview.Modelos.Edificio
import android.widget.Button

class EdificioAdapter(private val listaEdificios: List<Edificio>, private val listener: (Edificio) -> Unit) :
    RecyclerView.Adapter<EdificioAdapter.EdificioViewHolder>() {

    class EdificioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: Button = view.findViewById(R.id.nombre_edificio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EdificioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_edificio, parent, false)
        return EdificioViewHolder(view)
    }

    override fun onBindViewHolder(holder: EdificioViewHolder, position: Int) {
        val edificio = listaEdificios[position]
        holder.nombre.text = edificio.nombre_edificio
        holder.nombre.setOnClickListener {
            listener(edificio) // Aquí se llama a la función de clic
        }
    }

    override fun getItemCount() = listaEdificios.size
}

