package com.example.listview.AdaptadorPersonalizado

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat

import com.example.listview.R

class ProyectosAdapter(private val context: Context, private val proyectos: List<Proyectos>) : BaseAdapter() {

    override fun getCount(): Int = proyectos.size

    override fun getItem(position: Int): Any = proyectos[position]

    override fun getItemId(position: Int): Long = position.toLong()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = convertView ?: inflater.inflate(R.layout.list_item_proyectos, parent, false)

        val proyectos = getItem(position) as Proyectos

        val tvAnioID = view.findViewById<TextView>(R.id.tvAnioID)
        val tvProyectoCodigo = view.findViewById<TextView>(R.id.tvProyectoCodigo)
        val tvProyectoDescripcion = view.findViewById<TextView>(R.id.tvProyectoDescripcion)
        val tvProyectoCodigoFinanciero = view.findViewById<TextView>(R.id.tvProyectoCodigoFinanciero)
        val tvDepartamentoNombre = view.findViewById<TextView>(R.id.tvDepartamentoNombre)
        val tvMunicipioNombre = view.findViewById<TextView>(R.id.tvMunicipioNombre)
        val tvAvanceFisico = view.findViewById<TextView>(R.id.tvAvanceFisico)
        val tvDepartamentoID = view.findViewById<TextView>(R.id.tvDepartamentoID)
        val tvCodMunicipio = view.findViewById<TextView>(R.id.tvCodMunicipio)



        val colorIndicator = view.findViewById<View>(R.id.colorIndicator)

        tvAnioID.text = "Año: "+proyectos.AnioID.toString()
        tvProyectoCodigo.text = "Codigo proyecto: "+proyectos.ProyectoCodigo
        tvProyectoDescripcion.text = "Descripción: "+proyectos.ProyectoDescripcion
        tvProyectoCodigoFinanciero.text = "Código financiero: "+proyectos.ProyectoCodigoFinanciero
        tvDepartamentoNombre.text = "Nombre departamento: "+proyectos.DepartamentoNombre
        tvMunicipioNombre.text = "Nombre municipio: "+proyectos.MunicipioNombre
        tvAvanceFisico.text = "Avance: ${(proyectos.AvanceFisico * 100).toInt()}%"
        tvDepartamentoID.text = "Departamento id: "+proyectos.DepartamentoID.toString()
        tvCodMunicipio.text = "Codigo municipio"+proyectos.CodMunicipio.toString()



        // Cambiar el color del texto y del indicador de color según el valor de PorcentajeAvance
        val avance = (proyectos.AvanceFisico * 100).toInt()

        styleTextView(context, tvAvanceFisico, avance)
        styleColorIndicator(context, colorIndicator, avance)

        return view
    }

    private fun styleTextView(context: Context, textView: TextView, avance: Int) {
        // Define el color del texto basado en el valor de avance
        val textColor = when {
            avance <= 50 -> ContextCompat.getColor(context, android.R.color.holo_red_light)
            avance <= 80 -> ContextCompat.getColor(context, android.R.color.holo_orange_light)
            else -> ContextCompat.getColor(context, android.R.color.holo_green_light)
        }

        // Aplica el color del texto
        textView.setTextColor(textColor)
    }

    private fun styleColorIndicator(context: Context, view: View, avance: Int) {
        // Define el color del indicador de color basado en el valor de avance
        val color = when {
            avance <= 50 -> ContextCompat.getColor(context, android.R.color.holo_red_light)
            avance <= 80 -> ContextCompat.getColor(context, android.R.color.holo_orange_light)
            else -> ContextCompat.getColor(context, android.R.color.holo_green_light)
        }

        // Aplica el color de fondo del indicador de color
        view.setBackgroundColor(color)
    }
}
