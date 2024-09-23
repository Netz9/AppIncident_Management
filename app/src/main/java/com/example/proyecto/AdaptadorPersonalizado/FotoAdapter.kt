package com.example.proyecto.AdaptadorPersonalizado

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.listview.Modelos.Foto
import com.example.proyecto.R
import java.lang.Exception

class FotoAdapter(private val fotos: List<Foto>) : RecyclerView.Adapter<FotoAdapter.FotoViewHolder>() {

    class FotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagenView: ImageView = view.findViewById(R.id.imagenView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_foto, parent, false)
        return FotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: FotoViewHolder, position: Int) {
        val foto = fotos[position]
        if (!foto.base64.isNullOrEmpty()) {
            try {
                val cleanBase64 = foto.base64.trim()
                val bitmapBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                val decodedBitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.size)
                holder.imagenView.setImageBitmap(decodedBitmap)
            } catch (e: Exception) {
                // Registro de error para depuración
                e.printStackTrace()
                holder.imagenView.setImageResource(R.drawable.no_image_available)
            }
        } else {
            holder.imagenView.setImageResource(R.drawable.no_image_available)
        }
    }

    override fun getItemCount() = fotos.size
}
