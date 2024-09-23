package com.example.proyecto

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listview.ApiService.ApiClient
import com.example.listview.ApiService.ApiService
import com.example.listview.Modelos.Mantenimiento
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EdificioDetallesActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MantenimientoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edificio_detalles)

        // Configura el RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Llama a la función para obtener mantenimientos
        obtenerMantenimientos()

        // Resto de tu código (configuración de Toolbar y NavigationView)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_edificios -> {
                    startActivity(Intent(this, BuildingActivity::class.java))
                    drawerLayout.closeDrawers()
                    true
                }
                else -> false
            }
        }

        val nombreEdificio = intent.getStringExtra("nombre_edificio")
        supportActionBar?.title = "Edificio ${nombreEdificio ?: "Desconocido"}"
    }

    private fun obtenerMantenimientos() {
        // Obtén el id del edificio desde el Intent
        val idEdificio = intent.getIntExtra("id_edificio", -1)

        if (idEdificio != -1) {
            val apiService = ApiClient.getClient().create(ApiService::class.java)
            apiService.obtenerMantenimientosPorEdificio(idEdificio).enqueue(object : Callback<List<Mantenimiento>> {
                override fun onResponse(call: Call<List<Mantenimiento>>, response: Response<List<Mantenimiento>>) {
                    if (response.isSuccessful) {
                        val mantenimientosDesdeApi = response.body() ?: emptyList()
                        Log.d("EdificioDetallesActivity", "Mantenimientos: $mantenimientosDesdeApi")
                        Log.d("EdificioDetallesActivity", "Número de mantenimientos: ${mantenimientosDesdeApi.size}") // Agregar aquí
                        mantenimientosDesdeApi.forEach { mantenimiento ->
                            mantenimiento.fotografias.forEach { foto ->
                                Log.d("AquiWe", "Foto ID: ${foto.id}")
                            }
                        }
                        // Configura el adaptador aquí
                        adapter = MantenimientoAdapter(mantenimientosDesdeApi)
                        recyclerView.adapter = adapter
                        adapter.notifyDataSetChanged()
                    } else {
                        Log.e("EdificioDetallesActivity", "Error en la respuesta: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<List<Mantenimiento>>, t: Throwable) {
                    Log.e("EdificioDetallesActivity", "Error: ${t.message}")
                }
            })
        } else {
            Toast.makeText(this, "ID de edificio no válido", Toast.LENGTH_SHORT).show()
        }
    }


    class MantenimientoAdapter(private val mantenimientos: List<Mantenimiento>) : RecyclerView.Adapter<MantenimientoAdapter.ViewHolder>() {
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val descripcionTextView: TextView = view.findViewById(R.id.descripcionTextView)
            val fechaTextView: TextView = view.findViewById(R.id.fechaTextView)
            val imagenView: ImageView = view.findViewById(R.id.imagenView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mantenimiento, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val mantenimiento = mantenimientos[position]
            holder.descripcionTextView.text = mantenimiento.descripcion_mantenimiento
            holder.fechaTextView.text = mantenimiento.fecha_mantenimiento

            // Log para cada fotografía
            mantenimiento.fotografias.forEach { foto ->
                Log.d("MantenimientoAdapter", "Foto ID: ${foto.id}")
                Log.d("MantenimientoAdapter", "Base64: ${foto.base64}")
            }

            // Muestra solo la primera foto como ejemplo
            if (mantenimiento.fotografias.isNotEmpty()) {
                val foto = mantenimiento.fotografias[0]
                if (!foto.base64.isNullOrEmpty()) {
                    try {
                        val bitmap = Base64.decode(foto.base64, Base64.DEFAULT)
                        val decodedBitmap = BitmapFactory.decodeByteArray(bitmap, 0, bitmap.size)
                        if (decodedBitmap != null) {
                            holder.imagenView.setImageBitmap(decodedBitmap)
                        } else {
                            holder.imagenView.setImageResource(R.drawable.no_image_available) // Imagen por defecto
                        }
                    } catch (e: Exception) {
                        Log.e("MantenimientoAdapter", "Error decoding image: ${e.message}")
                        holder.imagenView.setImageResource(R.drawable.no_image_available) // Imagen por defecto
                    }
                } else {
                    holder.imagenView.setImageResource(R.drawable.no_image_available) // Imagen por defecto
                }
            } else {
                holder.imagenView.setImageResource(R.drawable.no_image_available) // Imagen por defecto
            }
        }


        override fun getItemCount() = mantenimientos.size
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
