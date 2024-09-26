package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout

class TipoMantenimientoActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var btnIncidencia: Button
    private lateinit var btnMantenimiento: Button
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    // Declara idUsuario y nombreUsuario como propiedades de la clase
    private var idUsuario: Int = -1
    private var nombreUsuario: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tipo_mantenimiento)


        // Referenciar los botones en el layout
        btnIncidencia = findViewById(R.id.btnIncidencia)
        btnMantenimiento = findViewById(R.id.btnMantenimiento)

        // Inicializar DrawerLayout y NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)

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

        nombreUsuario = intent.getStringExtra("nombre_usuario") ?: "Usuario"

        val headerView = navigationView.getHeaderView(0)
        val txtHeaderName = headerView.findViewById<TextView>(R.id.txtHeaderName)
        val imageViewAvatar = headerView.findViewById<ImageView>(R.id.imageViewAvatar)
        txtHeaderName.text = nombreUsuario  // Mostrar el nombre del usuario en el encabezado
        // Obtener datos pasados por el intent
        idUsuario = intent.getIntExtra("id_usuario", -1)
        nombreUsuario = intent.getStringExtra("nombre_usuario")

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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_edificios -> {
                // Navegar de regreso a AreaEdificiosActivity con el idUsuario y nombreUsuario
                val intent = Intent(this, AreaEdificiosActivity::class.java)
                // Ahora agrega el id_usuario y nombre_usuario al nuevo Intent
                intent.putExtra("id_usuario", idUsuario)
                intent.putExtra("nombre_usuario", nombreUsuario)

                startActivity(intent)
            }
            R.id.nav_cerrar_sesion -> {
                val intent = Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
        }

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
}
