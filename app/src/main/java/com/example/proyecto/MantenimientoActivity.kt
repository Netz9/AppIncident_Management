package com.example.proyecto

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.proyecto.DataBase.DatabaseHelper
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.database.sqlite.SQLiteDatabase

class MantenimientoActivity : AppCompatActivity() {

    private lateinit var spinnerTipoMantenimiento: Spinner
    private lateinit var edtDescripcion: EditText
    private lateinit var btnAgregarFotos: Button
    private lateinit var btnGuardar: Button
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var llPreviewImages: LinearLayout

    private val REQUEST_IMAGE_CAPTURE = 1
    private val MAX_IMAGES = 5
    private val selectedImages = mutableListOf<Uri>()

    private var idEdificio: Int = -1
    private var idUsuario: Int = -1
    private var tiposMantenimientoList = mutableListOf<TipoMantenimiento>()

    private var currentPhotoPath: String = ""

    data class TipoMantenimiento(val id: Int, val nombre: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mantenimiento)

        // Inicializar vistas
        spinnerTipoMantenimiento = findViewById(R.id.spinnerTipoMantenimiento)
        edtDescripcion = findViewById(R.id.edtDescripcion)
        btnAgregarFotos = findViewById(R.id.btnAgregarFotos)
        btnGuardar = findViewById(R.id.btnGuardar)
        llPreviewImages = findViewById(R.id.llPreviewImages)

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
        // Asegurarse de que hay una actividad de cámara para manejar el intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Crear el archivo donde se guardará la foto
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                // Error al crear el archivo
                Log.e("MantenimientoActivity", "Error creando el archivo de imagen: ${ex.message}")
                null
            }

            // Continuar solo si el archivo fue creado correctamente
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
        // Crear un nombre de archivo único
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val image = File.createTempFile(
            imageFileName,  /* prefijo */
            ".jpg",         /* sufijo */
            storageDir      /* directorio */
        )

        // Guardar la ruta del archivo para usarla después
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

        // Opcional: agregar funcionalidad para eliminar la imagen al hacer clic
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
            // Insertar el mantenimiento en la base de datos y obtener el id_mantenimiento
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

            // Insertar las fotos en la tabla fotografia_mantenimiento
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
        if (imageUris.isEmpty()) return true // No hay fotos que guardar

        try {
            // Convertir la lista de URIs a un JSONArray de rutas
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

    // Manejo de permisos
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 100) {
            for (i in permissions.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permiso ${permissions[i]} denegado.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
