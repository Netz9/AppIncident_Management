package com.example.proyecto

import android.content.Context
import android.widget.Toast
import com.example.listview.ApiService.ApiClient
import com.example.listview.ApiService.ApiService
import com.example.listview.Modelos.LoginRequest
import com.example.listview.Modelos.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginAdapter(private val context: Context) {

    fun login(username: String, password: String, onSuccess: (String?) -> Unit, onError: (String) -> Unit) {
        val apiService = ApiClient.getClient().create(ApiService::class.java)
        val loginRequest = LoginRequest(username, password)

        val call = apiService.login(loginRequest)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val token = loginResponse?.token
                    onSuccess(token)
                } else {
                    onError("Usuario o contraseña incorrectos")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onError("Error: ${t.message}")
            }
        })
    }
}
