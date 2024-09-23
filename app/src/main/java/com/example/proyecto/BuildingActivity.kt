package com.example.proyecto

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listview.ApiService.ApiClient
import com.example.listview.ApiService.ApiService
import com.example.listview.Modelos.Edificio
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.appcompat.widget.Toolbar


class BuildingActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var apiService: ApiService
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EdificioAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_building)

        // Configura la Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        toolbar.post {
            val toolbarTitle = toolbar.getChildAt(0) as? TextView
            toolbarTitle?.let {
                it.textSize = 20f // Ajusta el tamaño del texto aquí (en "sp")
                it.setTypeface(null, Typeface.BOLD) // Cambiar a negrita
            }
        }

        toolbar.title = "AREA DE MANTENIMIENTO"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Cambia las IDs para que coincidan con el XML
        drawerLayout = findViewById(R.id.drawer_layout) // Cambiado aquí
        navigationView = findViewById(R.id.nav_view) // Cambiado aquí
        recyclerView = findViewById(R.id.recyclerView)

        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.drawer_open, R.string.drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Inicializa el ApiService
        apiService = ApiClient.getClient().create(ApiService::class.java)

        // Llama al método para obtener los edificios
        fetchBuildings()
    }
    // Coloca los métodos aquí:
    private fun fetchBuildings() {
        apiService.obtenerEdificios().enqueue(object : Callback<List<Edificio>> {
            override fun onResponse(call: Call<List<Edificio>>, response: Response<List<Edificio>>) {
                if (response.isSuccessful) {
                    val buildings = response.body().orEmpty()
                    Log.d("BuildingActivity", "Received buildings: $buildings")
                    if (buildings.isNotEmpty()) {
                        adapter = EdificioAdapter(buildings) { building ->
                            val intent = Intent(this@BuildingActivity, EdificioDetallesActivity::class.java).apply {
                                putExtra("id_edificio", building.id_edificio)
                                putExtra("nombre_edificio", building.nombre_edificio)
                            }
                            startActivity(intent)
                        }
                        recyclerView.layoutManager = LinearLayoutManager(this@BuildingActivity)
                        recyclerView.adapter = adapter
                    } else {
                        showToast("No buildings available")
                    }
                } else {
                    showToast("Failed to fetch buildings")
                }
            }

            override fun onFailure(call: Call<List<Edificio>>, t: Throwable) {
                Log.e("BuildingActivity", "Error fetching buildings", t)
                showToast("Error: ${t.message}")
            }
        })
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Manejar la apertura/cierre del Navigation Drawer
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // Manejar la selección de ítems en el NavigationView
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_edificios -> {
                // Acción para el botón "Inicio"
                Toast.makeText(this, "Inicio seleccionado", Toast.LENGTH_SHORT).show()
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
