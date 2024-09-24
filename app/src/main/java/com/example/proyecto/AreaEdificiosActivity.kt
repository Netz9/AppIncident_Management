package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.DataBase.DatabaseHelper

class AreaEdificiosActivity : AppCompatActivity() {

    private lateinit var linearLayoutButtons: LinearLayout
    private lateinit var dbHelper: DatabaseHelper
    private var idUsuario: Int = -1
    private val TAG = "AreaEdificiosActivity"

    data class Edificio(val idEdificio: Int, val nombreEdificio: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_area_edificios)

        // Inicializar el LinearLayout que contiene los botones
        linearLayoutButtons = findViewById(R.id.linearLayoutButtons)

        // Inicializar la base de datos
        dbHelper = DatabaseHelper.getInstance(this)

        // Obtener el ID del usuario y el nombre desde el Intent
        idUsuario = intent.getIntExtra("id_usuario", -1)
        val nombreUsuario = intent.getStringExtra("nombre_usuario")

        // Verificar si el ID de usuario fue pasado correctamente
        if (idUsuario == -1) {
            Toast.makeText(this, "Error al obtener el ID del usuario.", Toast.LENGTH_LONG).show()
            Log.e(TAG, "ID de usuario no recibido correctamente.")
            finish() // Cierra la actividad si el ID no es válido
            return
        }

        Log.d(TAG, "ID del usuario recibido: $idUsuario")

        // Mostrar el nombre del usuario en el TextView de bienvenida
        val txtUsuario = findViewById<TextView>(R.id.txtUsuario)
        txtUsuario.text = "Bienvenido, $nombreUsuario"

        // Cargar la lista de edificios desde la base de datos
        val edificios = getEdificios()

        // Verificar si se encontraron edificios
        if (edificios.isEmpty()) {
            Toast.makeText(this, "No se encontraron edificios.", Toast.LENGTH_SHORT).show()
        } else {
            // Crear botones dinámicamente para cada edificio
            for (edificio in edificios) {
                val button = Button(this).apply {
                    text = edificio.nombreEdificio
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 16, 0, 16)
                    }
                    setOnClickListener {
                        // Acción al hacer clic en el botón: ir a la pantalla TipoMantenimientoActivity
                        val intent = Intent(this@AreaEdificiosActivity, TipoMantenimientoActivity::class.java).apply {
                            putExtra("id_edificio", edificio.idEdificio)
                            putExtra("nombre_edificio", edificio.nombreEdificio)
                            putExtra("id_usuario", idUsuario)
                            putExtra("nombre_usuario", nombreUsuario)
                        }
                        startActivity(intent)
                    }
                }
                // Agregar el botón al LinearLayout
                linearLayoutButtons.addView(button)
            }
        }
    }

    // Método para obtener la lista de edificios desde la base de datos
    private fun getEdificios(): List<Edificio> {
        val edificiosList = mutableListOf<Edificio>()
        val db = dbHelper.readableDatabase

        // Consulta para obtener los edificios de la base de datos
        val cursor = db.rawQuery("SELECT id_edificio, nombre_edificio FROM edificios", null)

        // Iterar sobre los resultados de la consulta
        if (cursor.moveToFirst()) {
            do {
                val idEdificio = cursor.getInt(cursor.getColumnIndexOrThrow("id_edificio"))
                val nombreEdificio = cursor.getString(cursor.getColumnIndexOrThrow("nombre_edificio"))
                edificiosList.add(Edificio(idEdificio, nombreEdificio))
            } while (cursor.moveToNext())
        }

        cursor.close() // Cerrar el cursor para liberar recursos
        return edificiosList
    }
}
