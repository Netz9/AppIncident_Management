package com.example.listview.Modelos
import com.google.gson.annotations.SerializedName

data class Foto(
    @SerializedName("id_fotografia") val id: Int,
    @SerializedName("foto") val base64: String?,
    @SerializedName("fecha_fotografia") val fecha: String,
    @SerializedName("id_mantenimiento") val idMantenimiento: Int
)
