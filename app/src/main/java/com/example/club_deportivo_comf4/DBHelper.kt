package com.example.club_deportivo_comf4

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.widget.Toast

class DBHelper(private val context: Context) :
    SQLiteOpenHelper(context, "ClubDeportivo.db", null, 4) {

    // para que SQLite respete las FK
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        android.util.Log.d("DBHelper", "BASE DE DATOS CREADA")
        db.execSQL(
            "CREATE TABLE administradores (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "usuario TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL" +
                    ")"
        )

        db.execSQL(
            "CREATE TABLE usuarios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT," +
                    "apellido TEXT," +
                    "dni TEXT UNIQUE NOT NULL," +
                    "fecha_nacimiento TEXT," +
                    "telefono TEXT," +
                    "email TEXT," +
                    "tipo_usuario INTEGER CHECK(tipo_usuario IN (1, 2))" +
                    ")"
        ) // 1 socio - 2 no socio

        db.execSQL(
            "CREATE TABLE socios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "usuario_id INTEGER," +
                    "fecha_alta TEXT," +
                    "apto_fisico INTEGER," +
                    "ficha_medica INTEGER," +
                    "FOREIGN KEY(usuario_id) REFERENCES usuarios(id))"
        )

        db.execSQL(
            "CREATE TABLE no_socios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "usuario_id INTEGER," +
                    "fecha_registro TEXT," +
                    "FOREIGN KEY(usuario_id) REFERENCES usuarios(id))"
        )

        // TABLA PAGOS (GENERAL)
        db.execSQL(
            "CREATE TABLE pagos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "usuario_id INTEGER," +
                    "tipo_pago TEXT," +
                    "monto_total REAL," +
                    "metodo_pago TEXT," +
                    "cuotas INTEGER," +
                    "fecha_pago TEXT," +
                    "estado_pago INTEGER CHECK(estado_pago IN (0, 1))," +
                    "FOREIGN KEY(usuario_id) REFERENCES usuarios(id))"
        ) // 0 impago - 1 pago

        // TABLA PAGOS SOCIOS (MENSUAL)
        db.execSQL(
            "CREATE TABLE pagos_socios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "pago_id INTEGER," +
                    "mes INTEGER," +
                    "anio INTEGER," +
                    "FOREIGN KEY(pago_id) REFERENCES pagos(id))"
        )

        // TABLA PAGOS NO SOCIOS (POR DÍA)
        db.execSQL(
            "CREATE TABLE pagos_no_socios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "pago_id INTEGER," +
                    "fecha_dia TEXT," +
                    "actividad_id INTEGER," +
                    "FOREIGN KEY(pago_id) REFERENCES pagos(id)," +
                    "FOREIGN KEY(actividad_id) REFERENCES actividades(id)" +
                    ")"
        )

        db.execSQL(
            "CREATE TABLE actividades (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT UNIQUE NOT NULL," +
                    "descripcion TEXT" +
                    ")"
        )

        // Insertar datos iniciales en actividades
        db.execSQL("INSERT INTO actividades (nombre, descripcion) VALUES " +
                "('Natación', 'Piscina libre'), " +
                "('Gimnasia', 'Clase de funcional'), " +
                "('Hockey', 'Entrenamiento femenino')"
        );

        // Insertar datos iniciales en administradores
        db.execSQL("INSERT INTO administradores (usuario, password) VALUES " +
                "('admin', '1234') "
        );

    }

    // ALTER TABLE para conservar la informacion
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS administradores")
        db.execSQL("DROP TABLE IF EXISTS pagos_no_socios")
        db.execSQL("DROP TABLE IF EXISTS pagos_socios")
        db.execSQL("DROP TABLE IF EXISTS pagos")
        db.execSQL("DROP TABLE IF EXISTS no_socios")
        db.execSQL("DROP TABLE IF EXISTS socios")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS actividades")
        onCreate(db)
    }

    fun validarAdministrador(usuario: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM administradores WHERE usuario = ? AND password = ?",
            arrayOf(usuario, password)
        )
        val existe = cursor.moveToFirst()
        cursor.close()
        db.close()
        return existe
    }

    fun insertarUsuario(
        nombre: String,
        apellido: String,
        dni: String,
        fechaNac: String,
        telefono: String,
        email: String,
        tipoUsuario: Int  // 1 socio - 2 no socio
    ): Long {
        val db = writableDatabase
        val values = ContentValues()

        values.put("nombre", nombre)
        values.put("apellido", apellido)
        values.put("dni", dni)
        values.put("fecha_nacimiento", fechaNac)
        values.put("telefono", telefono)
        values.put("email", email)
        values.put("tipo_usuario", tipoUsuario)

        val result = db.insert("usuarios", null, values)
        db.close()

        return result  // devuelve el id insertado
    }

    fun existeDni(dni: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id FROM usuarios WHERE dni = ?", arrayOf(dni))
        val existe = cursor.count > 0
        cursor.close()
        db.close()
        return existe
    }

    fun insertarSocio(
        usuarioId: Long,
        fechaAlta: String,
        aptoFisico: Int,
        fichaMedica: Int
    ): Long {
        val db = writableDatabase
        val values = ContentValues()

        values.put("usuario_id", usuarioId)
        values.put("fecha_alta", fechaAlta)
        values.put("apto_fisico", aptoFisico)
        values.put("ficha_medica", fichaMedica)

        val result = db.insert("socios", null, values)
        db.close()

        return result
    }

    fun insertarNoSocio(
        usuarioId: Long,
        fechaRegistro: String
    ): Long {
        val db = writableDatabase
        val values = ContentValues()

        values.put("usuario_id", usuarioId)
        values.put("fecha_registro", fechaRegistro)

        val result = db.insert("no_socios", null, values)
        db.close()

        return result
    }

    /*fun actualizarTipoUsuario(usuarioId: Long, tipoUsuario: Int): Int {
        val db = writableDatabase
        val values = ContentValues()

        values.put("tipo_usuario", tipoUsuario)

        val result = db.update("usuarios", values, "id = ?", arrayOf(usuarioId.toString()))
        db.close()

        return result
    }*/


    fun obtenerActividades(): List<String> {
        val lista = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT nombre FROM actividades", null)

        if (cursor.moveToFirst()) {
            do {
                lista.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }


}
