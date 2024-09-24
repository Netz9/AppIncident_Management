// LoginAdapter.kt
package com.example.proyecto

import android.content.Context
import android.util.Log
import com.example.proyecto.DataBase.DatabaseHelper
import org.mindrot.jbcrypt.BCrypt

class LoginAdapter(private val context: Context) {

    private val dbHelper = DatabaseHelper.getInstance(context)
    private val TAG = "LoginAdapter"

    data class User(val id: Int, val username: String)

    fun login(
        username: String,
        password: String,
        onSuccess: (message: String, user: User) -> Unit,
        onError: (message: String) -> Unit
    ) {
        val db = dbHelper.readableDatabase

        Log.d(TAG, "Intentando iniciar sesión para el usuario: $username")

        // Consulta segura usando parámetros para evitar inyección SQL
        val cursor = db.rawQuery(
            "SELECT id_usuario, contraseña FROM usuario WHERE nombre_usuario = ?",
            arrayOf(username)
        )

        if (cursor.moveToFirst()) {
            val idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario"))
            val hashedPassword = cursor.getString(cursor.getColumnIndexOrThrow("contraseña"))

            Log.d(TAG, "ID de usuario obtenido: $idUsuario")
            Log.d(TAG, "Contraseña hasheada obtenida: $hashedPassword")

            // Verifica la contraseña con bcrypt
            if (BCrypt.checkpw(password, hashedPassword)) {
                Log.d(TAG, "Contraseña correcta para el usuario: $username")
                val user = User(id = idUsuario, username = username)
                onSuccess("Login exitoso", user)
            } else {
                Log.d(TAG, "Contraseña incorrecta para el usuario: $username")
                onError("Usuario o contraseña incorrectos")
            }
        } else {
            Log.d(TAG, "Usuario no encontrado: $username")
            onError("Usuario no encontrado")
        }

        cursor.close()
        // No cierras la base de datos aquí
    }
}
