package com.example.listview.ApiService

import com.example.listview.Modelos.LoginRequest
import com.example.listview.Modelos.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    // Método POST para el login
    @POST("/api/auth/login")  // Usa la ruta de tu backend
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
}
