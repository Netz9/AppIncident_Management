package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class TipoMantenimientoActivity : AppCompatActivity() {

    private lateinit var btnIncidencia: Button
    private lateinit var btnMantenimiento: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tipo_mantenimiento)

        // Referenciar los botones en el layout
        btnIncidencia = findViewById(R.id.btnIncidencia)
        btnMantenimiento = findViewById(R.id.btnMantenimiento)

        // Obtener datos pasados por el intent
        val idEdificio = intent.getIntExtra("id_edificio", -1)
        val nombreEdificio = intent.getStringExtra("nombre_edificio")
        val idUsuario = intent.getIntExtra("id_usuario", -1)
        val nombreUsuario = intent.getStringExtra("nombre_usuario")

        // Manejar el clic en el botón de Incidencia
        btnIncidencia.setOnClickListener {
            // Iniciar la actividad de Incidencia
            val intent = Intent(this, IncidenciaActivity::class.java).apply {
                putExtra("id_edificio", idEdificio)
                putExtra("nombre_edificio", nombreEdificio)
                putExtra("id_usuario", idUsuario)
                putExtra("nombre_usuario", nombreUsuario)
            }
            startActivity(intent)
        }

        // Manejar el clic en el botón de Mantenimiento
        btnMantenimiento.setOnClickListener {
            // Iniciar la actividad de Mantenimiento
            val intent = Intent(this, MantenimientoActivity::class.java).apply {
                putExtra("id_edificio", idEdificio)
                putExtra("nombre_edificio", nombreEdificio)
                putExtra("id_usuario", idUsuario)
                putExtra("nombre_usuario", nombreUsuario)
            }
            startActivity(intent)
        }
    }
}
