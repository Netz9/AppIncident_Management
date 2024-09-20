package com.example.proyecto

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.listview.LoginAdapter

class LoginActivity : AppCompatActivity() {

    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var loginAdapter: LoginAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Asegúrate de que los IDs coincidan con los definidos en el XML
        edtUsername = findViewById(R.id.username)  // ID del EditText para el nombre de usuario
        edtPassword = findViewById(R.id.password)  // ID del EditText para la contraseña
        btnLogin = findViewById(R.id.loginButton)  // ID del botón de inicio de sesión
        loginAdapter = LoginAdapter(this)

        btnLogin.setOnClickListener {
            val username = edtUsername.text.toString()
            val password = edtPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                loginAdapter.login(username, password)
            } else {
                Toast.makeText(this, "Por favor, ingresa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
