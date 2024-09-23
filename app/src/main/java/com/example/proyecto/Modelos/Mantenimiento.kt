package com.example.listview.Modelos

data class Mantenimiento(
    val id_mantenimiento: Int,
    val id_tipo_mantenimiento: Int,
    val id_edificio: Int,
    val descripcion_mantenimiento: String,
    val fecha_mantenimiento: String, // O el tipo que necesites
    val id_usuario: Int,
    val fotografias: List<Foto> // Asegúrate de que esto sea correcto
)