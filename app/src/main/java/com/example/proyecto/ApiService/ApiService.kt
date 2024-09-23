package com.example.listview.ApiService

import com.example.listview.Modelos.LoginRequest
import com.example.listview.Modelos.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path
import com.example.listview.Modelos.Edificio
import com.example.listview.Modelos.Mantenimiento
import com.example.listview.Modelos.Foto // Asegúrate de importar tu modelo de Foto

interface ApiService {
    // Método POST para el login
    @POST("/api/auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("/api/edificios")
    fun obtenerEdificios(): Call<List<Edificio>>

    @GET("/api/mantenimiento/{id_edificio}")
    fun obtenerMantenimientosPorEdificio(@Path("id_edificio") idEdificio: Int): Call<List<Mantenimiento>>

}
