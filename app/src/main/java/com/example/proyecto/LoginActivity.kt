// LoginActivity.kt
package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

class LoginActivity : AppCompatActivity() {

    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var loginAdapter: LoginAdapter
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edtUsername = findViewById(R.id.username)
        edtPassword = findViewById(R.id.password)
        btnLogin = findViewById(R.id.loginButton)
        loginAdapter = LoginAdapter(this)

        btnLogin.setOnClickListener {
            val nombre_usuario = edtUsername.text.toString().trim()
            val contraseña = edtPassword.text.toString().trim()

            Log.d(TAG, "Botón de login presionado. Usuario: $nombre_usuario")

            if (nombre_usuario.isNotEmpty() && contraseña.isNotEmpty()) {
                // Llamada al método login del adapter
                loginAdapter.login(
                    nombre_usuario,
                    contraseña,
                    onSuccess = { message, user ->
                        Log.d(TAG, "Login exitoso para el usuario: ${user.username} con ID: ${user.id}")
                        // Inicio de sesión exitoso, redirige a BuildingActivity
                        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, AreaEdificiosActivity::class.java)
                        intent.putExtra("nombre_usuario", user.username) // Pasa el nombre del usuario como extra
                        intent.putExtra("id_usuario", user.id) // Pasa el ID del usuario como extra
                        startActivity(intent)
                        finish() // Finaliza la actividad actual para que el usuario no pueda volver con el botón de atrás
                    },
                    onError = { message ->
                        Log.d(TAG, "Error en el login para el usuario: $nombre_usuario. Mensaje: $message")
                        // Muestra el error si el inicio de sesión falla
                        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Log.d(TAG, "Campos de usuario o contraseña vacíos")
                Toast.makeText(this, "Por favor, ingresa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
