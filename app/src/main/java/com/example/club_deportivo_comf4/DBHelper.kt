package com.example.club_deportivo_comf4

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, "ClubDeportivo.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        android.util.Log.d("DBHelper", "BASE DE DATOS CREADA")
        db.execSQL(
            "CREATE TABLE usuarios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT," +
                    "apellido TEXT," +
                    "dni TEXT," +
                    "telefono TEXT," +
                    "email TEXT," +
                    "tipo_usuario INTEGER)"
        )

        db.execSQL(
            "CREATE TABLE socios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "usuario_id INTEGER," +
                    "numero_socio TEXT," +
                    "fecha_alta TEXT," +
                    "apto_fisico INTEGER," +
                    "ficha_medica INTEGER," +
                    "FOREIGN KEY(usuario_id) REFERENCES usuarios(id))"
        )

        db.execSQL(
            "CREATE TABLE no_socios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "usuario_id INTEGER," +
                    "tipo_registro TEXT," +
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
                    "FOREIGN KEY(usuario_id) REFERENCES usuarios(id))"
        )

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
                    "actividad TEXT," +
                    "FOREIGN KEY(pago_id) REFERENCES pagos(id))"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS pagos_no_socios")
        db.execSQL("DROP TABLE IF EXISTS pagos_socios")
        db.execSQL("DROP TABLE IF EXISTS pagos")
        db.execSQL("DROP TABLE IF EXISTS no_socios")
        db.execSQL("DROP TABLE IF EXISTS socios")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }


    fun insertarUsuario(
        nombre: String,
        apellido: String,
        dni: String,
        telefono: String,
        email: String,
        tipoUsuario: Int
    ): Long {

        val db = writableDatabase
        val values = ContentValues()

        values.put("nombre", nombre)
        values.put("apellido", apellido)
        values.put("dni", dni)
        values.put("telefono", telefono)
        values.put("email", email)
        values.put("tipo_usuario", tipoUsuario)

        val result = db.insert("usuarios", null, values)
        db.close()

        return result  // devuelve el id insertado
    }
}
