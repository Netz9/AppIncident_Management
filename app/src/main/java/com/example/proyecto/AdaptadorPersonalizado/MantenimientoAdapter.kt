package com.example.proyecto

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.listview.Modelos.Mantenimiento
import kotlinx.coroutines.CoroutineStart
import android.util.Base64
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto.AdaptadorPersonalizado.FotoAdapter


class MantenimientoAdapter(private val mantenimientos: List<Mantenimiento>) : RecyclerView.Adapter<MantenimientoAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val descripcionTextView: TextView = view.findViewById(R.id.descripcionTextView)
        val fechaTextView: TextView = view.findViewById(R.id.fechaTextView)
        val imagenRecyclerView: RecyclerView = view.findViewById(R.id.imagenRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mantenimiento, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mantenimiento = mantenimientos[position]
        holder.descripcionTextView.text = mantenimiento.descripcion_mantenimiento
        holder.fechaTextView.text = mantenimiento.fecha_mantenimiento

        // Configuración del RecyclerView para las fotos
        if (mantenimiento.fotografias.isNotEmpty()) {
            val fotoAdapter = FotoAdapter(mantenimiento.fotografias)
            holder.imagenRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
            holder.imagenRecyclerView.adapter = FotoAdapter(mantenimiento.fotografias) // Asumiendo que tienes una lista de fotos
        } else {
            // Si no hay fotos, puedes ocultar el RecyclerView o mostrar un mensaje
            holder.imagenRecyclerView.visibility = View.GONE
        }
    }

    override fun getItemCount() = mantenimientos.size
}
