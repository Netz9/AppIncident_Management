package com.example.listview

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.listview.ApiService.ApiClient
import com.example.listview.ApiService.ApiService
import com.example.listview.Modelos.LoginRequest
import com.example.listview.Modelos.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginAdapter(private val context: Context) {

    private val TAG = "LoginAdapter" // Define el TAG para los logs

    fun login(username: String, password: String) {
        val apiService = ApiClient.getClient().create(ApiService::class.java)
        val loginRequest = LoginRequest(username, password)

        val call = apiService.login(loginRequest)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val token = loginResponse?.token
                    Toast.makeText(context, "Login exitoso: $token", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
