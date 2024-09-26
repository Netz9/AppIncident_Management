package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ImageView // Importa ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.example.proyecto.DataBase.DatabaseHelper
import com.google.android.material.navigation.NavigationView

class AreaEdificiosActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var linearLayoutButtons: LinearLayout
    private lateinit var dbHelper: DatabaseHelper
    private var idUsuario: Int = -1
    private val TAG = "AreaEdificiosActivity"
    private lateinit var drawerLayout: androidx.drawerlayout.widget.DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    data class Edificio(val idEdificio: Int, val nombreEdificio: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_area_edificios)

        // Inicializar componentes del layout
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)
        linearLayoutButtons = findViewById(R.id.linearLayoutButtons)

        // Configurar la Toolbar
        setSupportActionBar(toolbar)

        // Configurar el ActionBarDrawerToggle
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Configurar el listener para el NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        // Opcional: Configurar el encabezado del NavigationView
        val headerView = navigationView.getHeaderView(0)
        val txtHeaderName = headerView.findViewById<TextView>(R.id.txtHeaderName)
        val imageViewAvatar = headerView.findViewById<ImageView>(R.id.imageViewAvatar)

        // Obtener el ID del usuario y nombre desde el Intent
        idUsuario = intent.getIntExtra("id_usuario", -1)
        val nombreUsuario = intent.getStringExtra("nombre_usuario") ?: "Usuario"

        // Mostrar el nombre del usuario en el header
        txtHeaderName.text = nombreUsuario

        // Verificar si el ID de usuario fue pasado correctamente
        if (idUsuario == -1) {
            Toast.makeText(this, "Error al obtener el ID del usuario.", Toast.LENGTH_LONG).show()
            Log.e(TAG, "ID de usuario no recibido correctamente.")
            finish() // Cierra la actividad si el ID no es válido
            return
        }

        Log.d(TAG, "ID del usuario rec: $idUsuario")
        // Inicializar la base de datos
        dbHelper = DatabaseHelper.getInstance(this)

        // Obtener el ID del usuario desde el Intent
        idUsuario = intent.getIntExtra("id_usuario", -1)

        // Verificar si el ID de usuario fue pasado correctamente
        if (idUsuario == -1) {
            Toast.makeText(this, "Error al obtener el ID del usuario.", Toast.LENGTH_LONG).show()
            Log.e(TAG, "ID de usuario no recibido correctamente.")
            finish() // Cierra la actividad si el ID no es válido
            return
        }

        Log.d(TAG, "ID del usuario recibido: $idUsuario")

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
                        450, 300
                    ).apply {
                        setMargins(0, 16, 0, 16)
                    }
                    setBackgroundResource(R.drawable.round_button_shape) // Aplicar el fondo redondeado
                    setTextColor(ContextCompat.getColor(this@AreaEdificiosActivity, android.R.color.white)) // Texto blanco
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_edificios -> {
                // Acción para "Edificios"
                Toast.makeText(this, "Ya esta en la vista de edificios", Toast.LENGTH_SHORT).show()
                // Puedes reiniciar la actividad o realizar otra acción
                // Por ejemplo, si ya estás en esta actividad, no haces nada
            }
            R.id.nav_tipos -> {
                Toast.makeText(this, "Por favor seleccione un edificio", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_cerrar_sesion -> {
                // Acción para "Cerrar Sesión"
                // Por ejemplo, regresar a la pantalla de login
                val intent = Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
            // Maneja más items si tienes
        }

        // Cerrar el drawer después de seleccionar un item
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
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
