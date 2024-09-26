package com.example.proyecto

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.proyecto.DataBase.DatabaseHelper
import com.google.android.material.navigation.NavigationView
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MantenimientoActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var spinnerTipoMantenimiento: Spinner
    private lateinit var edtDescripcion: EditText
    private lateinit var btnAgregarFotos: Button
    private lateinit var btnGuardar: Button
    private lateinit var llPreviewImages: LinearLayout
    private lateinit var dbHelper: DatabaseHelper

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    private val REQUEST_IMAGE_CAPTURE = 1
    private val MAX_IMAGES = 5
    private val selectedImages = mutableListOf<Uri>()

    private var idEdificio: Int = -1
    private var idUsuario: Int = -1
    private var nombreUsuario: String = ""
    private var nombreEdificio: String = ""
    private var tiposMantenimientoList = mutableListOf<TipoMantenimiento>()
    private var currentPhotoPath: String = ""

    data class TipoMantenimiento(val id: Int, val nombre: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mantenimiento)

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
        nombreEdificio = intent.getStringExtra("nombre_edificio") ?: "Edificio"

        // Opcional: Configurar el encabezado del NavigationView
        val headerView = navigationView.getHeaderView(0)
        val txtHeaderName = headerView.findViewById<TextView>(R.id.txtHeaderName)
        val imageViewAvatar = headerView.findViewById<ImageView>(R.id.imageViewAvatar)
        txtHeaderName.text = nombreUsuario  // Mostrar el nombre del usuario en el encabezado

        // Inicializar vistas
        spinnerTipoMantenimiento = findViewById(R.id.spinnerTipoMantenimiento)
        edtDescripcion = findViewById(R.id.edtDescripcion)
        btnAgregarFotos = findViewById(R.id.btnAgregarFotos)
        btnGuardar = findViewById(R.id.btnGuardar)
        llPreviewImages = findViewById(R.id.llPreviewImages)

        // Aplicar color verde a los botones después de inicializarlos
        val greenColor = ContextCompat.getColor(this, R.color.green)
        btnAgregarFotos.setBackgroundColor(greenColor)
        btnGuardar.setBackgroundColor(greenColor)

        // Inicializar base de datos
        dbHelper = DatabaseHelper.getInstance(this)

        // Obtener datos del Intent
        idEdificio = intent.getIntExtra("id_edificio", -1)
        idUsuario = intent.getIntExtra("id_usuario", -1)

        if (idEdificio == -1 || idUsuario == -1) {
            Toast.makeText(this, "Error al obtener datos necesarios.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Cargar tipos de mantenimiento en el Spinner
        cargarTiposMantenimiento()

        // Solicitar permisos de cámara y almacenamiento si es necesario
        solicitarPermisos()

        // Configurar botón para agregar fotos
        btnAgregarFotos.setOnClickListener {
            if (selectedImages.size >= MAX_IMAGES) {
                Toast.makeText(this, "Máximo $MAX_IMAGES imágenes permitidas.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            dispatchTakePictureIntent()
        }

        // Configurar botón para guardar mantenimiento
        btnGuardar.setOnClickListener {
            guardarMantenimiento()
        }
    }

    private fun solicitarPermisos() {
        val permisosNecesarios = mutableListOf<String>()

        val permisoCamara = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (permisoCamara != PackageManager.PERMISSION_GRANTED) {
            permisosNecesarios.add(Manifest.permission.CAMERA)
        }

        val permisoWriteStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permisoWriteStorage != PackageManager.PERMISSION_GRANTED) {
            permisosNecesarios.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (permisosNecesarios.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permisosNecesarios.toTypedArray(), 100)
        }
    }

    private fun cargarTiposMantenimiento() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id_tipo_mantenimiento, nombre_tipo_mantenimiento FROM tipo_mantenimiento", null)

        tiposMantenimientoList.clear()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_tipo_mantenimiento"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre_tipo_mantenimiento"))
                tiposMantenimientoList.add(TipoMantenimiento(id, nombre))
            } while (cursor.moveToNext())
        }
        cursor.close()

        val nombresTipos = tiposMantenimientoList.map { it.nombre }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresTipos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoMantenimiento.adapter = adapter
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                Log.e("MantenimientoActivity", "Error creando el archivo de imagen: ${ex.message}")
                null
            }

            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "${applicationContext.packageName}.fileprovider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        } else {
            Toast.makeText(this, "No se encontró una aplicación de cámara.", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
        currentPhotoPath = image.absolutePath
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val file = File(currentPhotoPath)
            val imageUri = Uri.fromFile(file)
            if (selectedImages.size < MAX_IMAGES) {
                selectedImages.add(imageUri)
                agregarImagenAPrevisualizacion(imageUri)
                Toast.makeText(this, "Imagen agregada.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Máximo $MAX_IMAGES imágenes permitidas.", Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun agregarImagenAPrevisualizacion(imageUri: Uri) {
        val imageView = ImageView(this)
        val layoutParams = LinearLayout.LayoutParams(200, 200)
        layoutParams.setMargins(8, 8, 8, 8)
        imageView.layoutParams = layoutParams
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.setImageURI(imageUri)

        imageView.setOnClickListener {
            val index = llPreviewImages.indexOfChild(imageView)
            if (index != -1) {
                selectedImages.removeAt(index)
                llPreviewImages.removeViewAt(index)
            }
        }

        llPreviewImages.addView(imageView)
    }

    private fun guardarMantenimiento() {
        val tipoMantenimientoSeleccionado = tiposMantenimientoList[spinnerTipoMantenimiento.selectedItemPosition]
        val descripcion = edtDescripcion.text.toString().trim()
        val fechaActual = obtenerFechaActual()

        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val valoresMantenimiento = ContentValues().apply {
                put("id_tipo_mantenimiento", tipoMantenimientoSeleccionado.id)
                put("id_edificio", idEdificio)
                put("descripcion_mantenimiento", descripcion)
                put("fecha_mantenimiento", fechaActual)
                put("id_usuario", idUsuario)
            }

            val idMantenimientoInsertado = db.insert("mantenimiento", null, valoresMantenimiento)
            if (idMantenimientoInsertado == -1L) {
                Toast.makeText(this, "Error al guardar el mantenimiento.", Toast.LENGTH_SHORT).show()
                db.endTransaction()
                return
            }

            val exitoFotos = insertarFotografiasMantenimiento(db, selectedImages, fechaActual, idMantenimientoInsertado)

            if (!exitoFotos) {
                Toast.makeText(this, "Error al guardar las fotografías.", Toast.LENGTH_SHORT).show()
                db.endTransaction()
                return
            }

            db.setTransactionSuccessful()
            Toast.makeText(this, "Mantenimiento guardado exitosamente.", Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: Exception) {
            Log.e("MantenimientoActivity", "Excepción al guardar mantenimiento: ${e.message}")
            Toast.makeText(this, "Error al guardar el mantenimiento.", Toast.LENGTH_SHORT).show()
        } finally {
            db.endTransaction()
        }
    }

    private fun insertarFotografiasMantenimiento(db: SQLiteDatabase, imageUris: List<Uri>, fecha: String, idMantenimiento: Long): Boolean {
        if (imageUris.isEmpty()) return true

        try {
            val jsonFotos = JSONArray()
            for (uri in imageUris) {
                val jsonFoto = JSONObject()
                jsonFoto.put("foto", uri.toString())
                jsonFotos.put(jsonFoto)
            }

            val valores = ContentValues().apply {
                put("foto", jsonFotos.toString())
                put("fecha_fotografia", fecha)
                put("id_mantenimiento", idMantenimiento)
            }

            val resultado = db.insert("fotografia_mantenimiento", null, valores)
            if (resultado == -1L) {
                Log.e("MantenimientoActivity", "Error al insertar fotografías en mantenimiento.")
                return false
            }

            return true
        } catch (e: Exception) {
            Log.e("MantenimientoActivity", "Excepción al insertar fotografías: ${e.message}")
            return false
        }
    }

    private fun obtenerFechaActual(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_edificios -> {
                val intent = Intent(this, AreaEdificiosActivity::class.java)
                intent.putExtra("id_usuario", idUsuario)
                intent.putExtra("nombre_usuario", nombreUsuario)
                startActivity(intent)
            }
            R.id.nav_tipos -> {
                // Redirigir a TipoMantenimientoActivity cuando se seleccione el menú "Mantenimiento/Incidencia"
                val intent = Intent(this, TipoMantenimientoActivity::class.java)
                intent.putExtra("id_usuario", idUsuario)
                intent.putExtra("nombre_usuario", nombreUsuario)
                intent.putExtra("id_edificio", idEdificio)
                intent.putExtra("nombre_edificio", nombreEdificio)
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
