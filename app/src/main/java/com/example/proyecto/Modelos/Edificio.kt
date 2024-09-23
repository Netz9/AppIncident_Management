package com.example.listview.Modelos

data class Edificio(
    val id_edificio: Int,
    val nombre_edificio: String,
    val descripcion: String,
    val latitud: Double,   // Usar Double para DECIMAL
    val longitud: Double    // Usar Double para DECIMAL
)
