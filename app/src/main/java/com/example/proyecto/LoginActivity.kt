package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.LoginAdapter

class LoginActivity : AppCompatActivity() {

    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var loginAdapter: LoginAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edtUsername = findViewById(R.id.username)
        edtPassword = findViewById(R.id.password)
        btnLogin = findViewById(R.id.loginButton)
        loginAdapter = LoginAdapter(this)

        btnLogin.setOnClickListener {
            val nombre_usuario = edtUsername.text.toString()
            val contraseña = edtPassword.text.toString()

            if (nombre_usuario.isNotEmpty() && contraseña.isNotEmpty()) {
                // Llamada al método login del adapter
                loginAdapter.login(nombre_usuario, contraseña,
                    onSuccess = { token ->
                        // Inicio de sesión exitoso, redirige a BuildingActivity
                        val intent = Intent(this@LoginActivity, BuildingActivity::class.java)
                        intent.putExtra("nombre_usuario", nombre_usuario)  // Pasa el nombre del usuario como extra
                        startActivity(intent)
                        finish() // Finaliza la actividad actual para que el usuario no pueda volver con el botón de atrás
                    },
                    onError = { message ->
                        // Muestra el error si el inicio de sesión falla
                        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(this, "Por favor, ingresa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
