package com.example.listview.ApiService

import com.example.listview.Modelos.Departamento
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiServiceDepartamento {
    @GET("SicopApiMonitoreo/api/DashboardMapa/GetInformacionDepartamentos/2022")
    fun getDepartamentos(): Call<List<Departamento>>
}


