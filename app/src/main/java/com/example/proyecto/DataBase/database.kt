package com.example.proyecto.DataBase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import org.mindrot.jbcrypt.BCrypt

class DatabaseHelper private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "mantenimiento_meso_final.db"
        private const val DATABASE_VERSION = 6 // Incrementa la versión si haces cambios

        @Volatile
        private var INSTANCE: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DatabaseHelper(context.applicationContext).also { INSTANCE = it }
            }
        }

        // Definiciones de las tablas
        private const val TABLE_USUARIO = "usuario"
        private const val TABLE_EDIFICIOS = "edificios"
        private const val TABLE_FOTOGRAFIA_INCIDENCIA = "fotografia_incidencia"
        private const val TABLE_TIPO_INCIDENCIA = "tipo_incidencia"
        private const val TABLE_TIPO_MANTENIMIENTO = "tipo_mantenimiento"
        private const val TABLE_MANTENIMIENTO = "mantenimiento"
        private const val TABLE_FOTOGRAFIA_MANTENIMIENTO = "fotografia_mantenimiento"
        private const val TABLE_INCIDENCIAS = "incidencias"

        // Sentencias SQL para crear las tablas
        private const val CREATE_TABLE_USUARIO = """
            CREATE TABLE $TABLE_USUARIO (
                id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                contraseña TEXT NOT NULL,
                nombre_usuario TEXT NOT NULL UNIQUE
            );
        """

        private const val CREATE_TABLE_EDIFICIOS = """
            CREATE TABLE $TABLE_EDIFICIOS (
                id_edificio INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_edificio TEXT NOT NULL,
                descripcion TEXT,
                latitud REAL NOT NULL,
                longitud REAL NOT NULL
            );
        """

        private const val CREATE_TABLE_FOTOGRAFIA_INCIDENCIA = """
            CREATE TABLE $TABLE_FOTOGRAFIA_INCIDENCIA (
                id_fotografia INTEGER PRIMARY KEY AUTOINCREMENT,
                foto TEXT,
                fecha_incidencia TEXT NOT NULL
            );
        """

        private const val CREATE_TABLE_TIPO_INCIDENCIA = """
            CREATE TABLE $TABLE_TIPO_INCIDENCIA (
                id_incidencia INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_incidencia TEXT NOT NULL
            );
        """

        private const val CREATE_TABLE_TIPO_MANTENIMIENTO = """
            CREATE TABLE $TABLE_TIPO_MANTENIMIENTO (
                id_tipo_mantenimiento INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_tipo_mantenimiento TEXT NOT NULL
            );
        """

        private const val CREATE_TABLE_MANTENIMIENTO = """
            CREATE TABLE $TABLE_MANTENIMIENTO (
                id_mantenimiento INTEGER PRIMARY KEY AUTOINCREMENT,
                id_tipo_mantenimiento INTEGER,
                id_edificio INTEGER,
                descripcion_mantenimiento TEXT,
                fecha_mantenimiento TEXT NOT NULL,
                id_usuario INTEGER,
                FOREIGN KEY (id_tipo_mantenimiento) REFERENCES $TABLE_TIPO_MANTENIMIENTO(id_tipo_mantenimiento),
                FOREIGN KEY (id_edificio) REFERENCES $TABLE_EDIFICIOS(id_edificio),
                FOREIGN KEY (id_usuario) REFERENCES $TABLE_USUARIO(id_usuario)
            );
        """

        private const val CREATE_TABLE_FOTOGRAFIA_MANTENIMIENTO = """
            CREATE TABLE $TABLE_FOTOGRAFIA_MANTENIMIENTO (
                id_fotografia INTEGER PRIMARY KEY AUTOINCREMENT,
                foto TEXT,
                fecha_fotografia TEXT NOT NULL,
                id_mantenimiento INTEGER,
                FOREIGN KEY (id_mantenimiento) REFERENCES $TABLE_MANTENIMIENTO(id_mantenimiento)
            );
        """

        private const val CREATE_TABLE_INCIDENCIAS = """
            CREATE TABLE $TABLE_INCIDENCIAS (
                id_incidencia INTEGER PRIMARY KEY AUTOINCREMENT,
                tipo_incidencia_id_incidencia INTEGER NOT NULL,
                id_fotografia INTEGER,
                id_edificio INTEGER,
                descripcion_incidencia TEXT,
                fecha_incidencia TEXT NOT NULL,
                id_usuario INTEGER,
                FOREIGN KEY (tipo_incidencia_id_incidencia) REFERENCES $TABLE_TIPO_INCIDENCIA(id_incidencia),
                FOREIGN KEY (id_fotografia) REFERENCES $TABLE_FOTOGRAFIA_INCIDENCIA(id_fotografia),
                FOREIGN KEY (id_edificio) REFERENCES $TABLE_EDIFICIOS(id_edificio),
                FOREIGN KEY (id_usuario) REFERENCES $TABLE_USUARIO(id_usuario)
            );
        """

        // Inserción del usuario inicial
        private const val INSERT_USER = """
            INSERT INTO $TABLE_USUARIO (nombre, apellido, contraseña, nombre_usuario)
            VALUES (?, ?, ?, ?);
        """
    }

    // Data class para facilitar la gestión de datos de edificios
    data class EdificioData(
        val nombreEdificio: String,
        val descripcion: String,
        val latitud: Double,
        val longitud: Double
    )

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true) // Habilitar claves foráneas
    }

    override fun onCreate(db: SQLiteDatabase) {
        try {
            Log.d("DatabaseHelper", "Iniciando onCreate...")
            // Crear tablas
            db.execSQL(CREATE_TABLE_USUARIO)
            Log.d("DatabaseHelper", "Tabla usuario creada.")
            db.execSQL(CREATE_TABLE_EDIFICIOS)
            Log.d("DatabaseHelper", "Tabla edificios creada.")
            db.execSQL(CREATE_TABLE_TIPO_INCIDENCIA)
            Log.d("DatabaseHelper", "Tabla tipo_incidencia creada.")
            db.execSQL(CREATE_TABLE_TIPO_MANTENIMIENTO)
            db.execSQL(CREATE_TABLE_MANTENIMIENTO)
            db.execSQL(CREATE_TABLE_FOTOGRAFIA_INCIDENCIA)
            db.execSQL(CREATE_TABLE_FOTOGRAFIA_MANTENIMIENTO)
            db.execSQL(CREATE_TABLE_INCIDENCIAS)
            Log.d("DatabaseHelper", "Todas las tablas creadas.")

            // Insertar datos iniciales
            insertarUsuarioInicial(db)
            insertarEdificiosIniciales(db)
            insertarTiposIncidenciaIniciales(db)
            insertarTiposMantenimientoIniciales(db)
            Log.d("DatabaseHelper", "Base de datos creada exitosamente.")
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error en onCreate: ${e.message}")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            Log.d("DatabaseHelper", "Actualizando la base de datos de la versión $oldVersion a $newVersion...")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_INCIDENCIAS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_FOTOGRAFIA_MANTENIMIENTO")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_MANTENIMIENTO")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_FOTOGRAFIA_INCIDENCIA")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_TIPO_INCIDENCIA")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_TIPO_MANTENIMIENTO")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_EDIFICIOS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIO")
            onCreate(db)
            Log.d("DatabaseHelper", "Base de datos actualizada exitosamente.")
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error en onUpgrade: ${e.message}")
        }
    }

    private fun insertarUsuarioInicial(db: SQLiteDatabase) {
        try {
            val nombre = "Juan"
            val apellido = "Pérez"
            val nombreUsuario = "user1"
            val plainPassword = "pass" // Reemplaza con la contraseña real
            val hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt())

            val bindArgs = arrayOf(
                nombre,
                apellido,
                hashedPassword,
                nombreUsuario
            )

            db.execSQL(INSERT_USER, bindArgs)
            Log.d("DatabaseHelper", "Usuario inicial insertado.")
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al insertar usuario inicial: ${e.message}")
        }
    }

    private fun insertarEdificiosIniciales(db: SQLiteDatabase) {
        val edificiosIniciales = listOf(
            EdificioData(
                nombreEdificio = "Edificio Central",
                descripcion = "Oficinas administrativas principales.",
                latitud = 40.416775,
                longitud = -3.703790
            ),
            EdificioData(
                nombreEdificio = "Torre Este",
                descripcion = "Residencias y apartamentos.",
                latitud = 40.417775,
                longitud = -3.704790
            ),
            EdificioData(
                nombreEdificio = "Complejo Industrial",
                descripcion = "Fábricas y almacenes.",
                latitud = 40.418775,
                longitud = -3.705790
            ),
            EdificioData(
                nombreEdificio = "Centro Comercial",
                descripcion = "Tiendas y restaurantes.",
                latitud = 40.419775,
                longitud = -3.706790
            ),
            EdificioData(
                nombreEdificio = "Hospital General",
                descripcion = "Servicios de salud y emergencia.",
                latitud = 40.420775,
                longitud = -3.707790
            )
        )

        try {
            for (edificio in edificiosIniciales) {
                val valores = ContentValues().apply {
                    put("nombre_edificio", edificio.nombreEdificio)
                    put("descripcion", edificio.descripcion)
                    put("latitud", edificio.latitud)
                    put("longitud", edificio.longitud)
                }

                val idInsertado = db.insert(TABLE_EDIFICIOS, null, valores)
                if (idInsertado == -1L) {
                    Log.e("DatabaseHelper", "Error al insertar el edificio: ${edificio.nombreEdificio}")
                } else {
                    Log.d("DatabaseHelper", "Edificio insertado con ID: $idInsertado - ${edificio.nombreEdificio}")
                }
            }
            Log.d("DatabaseHelper", "Edificios iniciales insertados exitosamente.")
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al insertar edificios iniciales: ${e.message}")
        }
    }

    private fun insertarTiposIncidenciaIniciales(db: SQLiteDatabase) {
        val tiposIncidencia = listOf(
            "Eléctrica",
            "Plomería",
            "Infraestructura",
            "Mantenimiento General",
            "Seguridad"
        )

        try {
            for (tipo in tiposIncidencia) {
                val valores = ContentValues().apply {
                    put("nombre_incidencia", tipo)
                }

                val idInsertado = db.insert(TABLE_TIPO_INCIDENCIA, null, valores)
                if (idInsertado == -1L) {
                    Log.e("DatabaseHelper", "Error al insertar tipo de incidencia: $tipo")
                } else {
                    Log.d("DatabaseHelper", "Tipo de incidencia insertado con ID: $idInsertado - $tipo")
                }
            }
            Log.d("DatabaseHelper", "Tipos de incidencia iniciales insertados exitosamente.")
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al insertar tipos de incidencia: ${e.message}")
        }
    }
    private fun insertarTiposMantenimientoIniciales(db: SQLiteDatabase) {
        val tiposMantenimiento = listOf(
            "Preventivo",
            "Correctivo",
            "Emergencia",
            "Predictivo",
            "Otros"
        )

        try {
            for (tipo in tiposMantenimiento) {
                val valores = ContentValues().apply {
                    put("nombre_tipo_mantenimiento", tipo)
                }

                val idInsertado = db.insert(TABLE_TIPO_MANTENIMIENTO, null, valores)
                if (idInsertado == -1L) {
                    Log.e("DatabaseHelper", "Error al insertar tipo de mantenimiento: $tipo")
                } else {
                    Log.d("DatabaseHelper", "Tipo de mantenimiento insertado con ID: $idInsertado - $tipo")
                }
            }
            Log.d("DatabaseHelper", "Tipos de mantenimiento iniciales insertados exitosamente.")
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al insertar tipos de mantenimiento: ${e.message}")
        }
    }

}
