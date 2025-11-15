package com.example.club_deportivo_comf4

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.Calendar

class DBHelper(private val context: Context) :
    SQLiteOpenHelper(context, "ClubDeportivo.db", null, 7) { // Versión incrementada a 7

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
                    "password TEXT NOT NULL)"
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
                    "tipo_usuario INTEGER CHECK(tipo_usuario IN (1, 2)))"
        )

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
        )

        db.execSQL(
            "CREATE TABLE pagos_socios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "pago_id INTEGER," +
                    "usuario_id INTEGER," +
                    "mes INTEGER," +
                    "anio INTEGER," +
                    "FOREIGN KEY(pago_id) REFERENCES pagos(id))"
        )

        db.execSQL(
            "CREATE TABLE pagos_no_socios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "pago_id INTEGER," +
                    "fecha_dia TEXT," +
                    "actividad_id INTEGER," +
                    "FOREIGN KEY(pago_id) REFERENCES pagos(id)," +
                    "FOREIGN KEY(actividad_id) REFERENCES actividades(id))"
        )

        db.execSQL(
            "CREATE TABLE actividades (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT UNIQUE NOT NULL," +
                    "descripcion TEXT)"
        )

        db.execSQL(
            "INSERT INTO actividades (nombre, descripcion) VALUES " +
                    "('Natación', 'Piscina libre'), " +
                    "('Gimnasia', 'Clase de funcional'), " +
                    "('Hockey', 'Entrenamiento femenino')"
        )

        db.execSQL("INSERT INTO administradores (usuario, password) VALUES ('admin', '1234')")
    }

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
        return existe
    }

    fun insertarUsuario(
        nombre: String,
        apellido: String,
        dni: String,
        fechaNac: String,
        telefono: String,
        email: String,
        tipoUsuario: Int
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("apellido", apellido)
            put("dni", dni)
            put("fecha_nacimiento", fechaNac)
            put("telefono", telefono)
            put("email", email)
            put("tipo_usuario", tipoUsuario)
        }
        val result = db.insert("usuarios", null, values)
        db.close()
        return result
    }

    fun existeDni(dni: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id FROM usuarios WHERE dni = ?", arrayOf(dni))
        val existe = cursor.count > 0
        cursor.close()
        return existe
    }

    fun insertarSocio(usuarioId: Long, fechaAlta: String, aptoFisico: Int, fichaMedica: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("usuario_id", usuarioId)
            put("fecha_alta", fechaAlta)
            put("apto_fisico", aptoFisico)
            put("ficha_medica", fichaMedica)
        }
        val result = db.insert("socios", null, values)
        db.close()
        return result
    }

    fun insertarNoSocio(usuarioId: Long, fechaRegistro: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("usuario_id", usuarioId)
            put("fecha_registro", fechaRegistro)
        }
        val result = db.insert("no_socios", null, values)
        db.close()
        return result
    }

    fun obtenerUsuarioIdPorDNI(dni: String): Long? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id FROM usuarios WHERE dni = ?", arrayOf(dni))
        val id = if (cursor.moveToFirst()) cursor.getLong(cursor.getColumnIndexOrThrow("id")) else null
        cursor.close()
        return id
    }

    fun obtenerActividades(): List<String> {
        val lista = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT nombre FROM actividades", null)
        if (cursor.moveToFirst()) {
            do { lista.add(cursor.getString(0)) } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun obtenerActividadIdPorNombre(db: SQLiteDatabase, nombreActividad: String): Long? {
        val cursor = db.rawQuery("SELECT id FROM actividades WHERE nombre = ?", arrayOf(nombreActividad))
        var id: Long? = null
        if (cursor.moveToFirst()) {
            id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
        }
        cursor.close()
        return id
    }

    fun obtenerSocioPorDNI(dni: String): Socio? {
        val db = readableDatabase
        var socio: Socio? = null
        val query = """
            SELECT u.id, u.nombre, u.apellido, u.dni, u.email, u.telefono, s.fecha_alta
            FROM usuarios u
            INNER JOIN socios s ON u.id = s.usuario_id
            WHERE u.dni = ? AND u.tipo_usuario = 1
        """
        val cursor = db.rawQuery(query, arrayOf(dni))
        if (cursor.moveToFirst()) {
            socio = Socio(
                idUsuario = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                fechaInscripcion = cursor.getString(cursor.getColumnIndexOrThrow("fecha_alta"))
            )
        }
        cursor.close()
        return socio
    }

    fun obtenerNoSocioPorDNI(dni: String): NoSocio? {
        val db = readableDatabase
        var noSocio: NoSocio? = null
        val query = """
            SELECT u.id, u.nombre, u.apellido, u.dni, u.email, u.telefono, ns.fecha_registro
            FROM usuarios u
            INNER JOIN no_socios ns ON u.id = ns.usuario_id
            WHERE u.dni = ? AND u.tipo_usuario = 2
        """
        val cursor = db.rawQuery(query, arrayOf(dni))
        if (cursor.moveToFirst()) {
            noSocio = NoSocio(
                idUsuario = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                fechaRegistro = cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro"))
            )
        }
        cursor.close()
        return noSocio
    }

    // Obtener fecha de alta del socio
    fun obtenerFechaAltaSocio(usuarioId: Long): String? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT fecha_alta FROM socios WHERE usuario_id = ?",
            arrayOf(usuarioId.toString())
        )
        val fechaAlta = if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndexOrThrow("fecha_alta"))
        } else null
        cursor.close()
        return fechaAlta
    }

    // Obtener fecha de registro del no socio
    fun obtenerFechaRegistroNoSocio(usuarioId: Long): String? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT fecha_registro FROM no_socios WHERE usuario_id = ?",
            arrayOf(usuarioId.toString())
        )
        val fechaRegistro = if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro"))
        } else null
        cursor.close()
        return fechaRegistro
    }

    fun registrarPagoMensual(
        dni: String,
        monto: Double,
        metodoPago: String,
        cuotas: String,
        fechaAlta: String
    ): Boolean {
        val db = writableDatabase

        // 1. Buscar usuario_id por DNI
        val usuarioId = obtenerUsuarioIdPorDNI(dni)
        if (usuarioId == null) return false

        // 2. Registrar en pagos
        val pagoValues = ContentValues().apply {
            put("usuario_id", usuarioId)
            put("tipo_pago", "CUOTA_MENSUAL")
            put("monto_total", monto)
            put("metodo_pago", metodoPago)
            put("cuotas", cuotas.toInt())
            put("fecha_pago", fechaAlta)   // o Calendar.getInstance().time.toString()
            put("estado_pago", 1)          // 1 = pagado
        }

        val pagoId = db.insert("pagos", null, pagoValues)
        if (pagoId == -1L) {
            return false
        }

        // 3. Registrar en pagos_socios
        val calendario = Calendar.getInstance()
        val mes = calendario.get(Calendar.MONTH) + 1
        val anio = calendario.get(Calendar.YEAR)

        val socioValues = ContentValues().apply {
            put("pago_id", pagoId)
            put("usuario_id", usuarioId)
            put("mes", mes)
            put("anio", anio)
        }

        val result = db.insert("pagos_socios", null, socioValues)
        return result != -1L
    }

    // Registrar pago de no socio usando fecha de registro
    fun registrarPagoNoSocio(
        dni: String,
        monto: Double,
        metodoPago: String,
        cuotas: Int,
        nombreActividad: String,
        fechaDePago: String  //
    ): Long {
        val db = writableDatabase
        return try {
            db.beginTransaction()
            val usuarioId = obtenerUsuarioIdPorDNI(dni) ?: throw Exception("Usuario no encontrado con DNI: $dni")
            val actividadId = obtenerActividadIdPorNombre(db, nombreActividad) ?: throw Exception("Actividad no encontrada: $nombreActividad")

            // Usar fechaDePago en lugar de fechaRegistro
            if (fechaDePago.isNullOrEmpty()) {
                throw Exception("Fecha de pago no disponible para el no socio")
            }

            val pagoValues = ContentValues().apply {
                put("usuario_id", usuarioId)
                put("tipo_pago", "NO_SOCIO")
                put("monto_total", monto)
                put("metodo_pago", metodoPago)
                put("cuotas", cuotas)
                put("fecha_pago", fechaDePago)
                put("estado_pago", 1)
            }
            val pagoId = db.insert("pagos", null, pagoValues)
            if (pagoId == -1L) throw Exception("Error al registrar en la tabla 'pagos'")

            val noSocioPagoValues = ContentValues().apply {
                put("pago_id", pagoId)
                put("fecha_dia", fechaDePago)
                put("actividad_id", actividadId)
            }
            val pagoNoSocioId = db.insert("pagos_no_socios", null, noSocioPagoValues)
            if (pagoNoSocioId == -1L) throw Exception("Error al registrar en la tabla 'pagos_no_socios'")

            db.setTransactionSuccessful()
            pagoId
        } catch (e: Exception) {
            e.printStackTrace()
            android.util.Log.e("DBHelper", "Error al registrar pago no socio: ${e.message}")
            -1L
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    // Verificar si es socio
    fun esSocio(usuarioId: Long): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id FROM socios WHERE usuario_id = ?",
            arrayOf(usuarioId.toString())
        )
        val esSocio = cursor.count > 0
        cursor.close()
        return esSocio
    }

    // Ver si es no socio
    fun esNoSocio(usuarioId: Long): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id FROM no_socios WHERE usuario_id = ?",
            arrayOf(usuarioId.toString())
        )
        val esNoSocio = cursor.count > 0
        cursor.close()
        return esNoSocio
    }

    // CALCULAR FECHA DE VENCIMIENTO SUMANDO 30 DÍAS
    fun calcularFechaVencimiento(fechaAlta: String): String {
        // fechaAlta debe venir en formato yyyy-MM-dd
        val partes = fechaAlta.split("-")
        val anio = partes[0].toInt()
        val mes = partes[1].toInt() - 1   // Calendar usa meses 0-11
        val dia = partes[2].toInt()

        val calendar = Calendar.getInstance()
        calendar.set(anio, mes, dia)

        // Sumamos 30 días, 1 mes aproximado
        calendar.add(Calendar.DAY_OF_MONTH, 30)

        val nuevoAnio = calendar.get(Calendar.YEAR)
        val nuevoMes = calendar.get(Calendar.MONTH) + 1
        val nuevoDia = calendar.get(Calendar.DAY_OF_MONTH)

        return String.format("%04d-%02d-%02d", nuevoAnio, nuevoMes, nuevoDia)
    }



    // OBTENER SOCIOS VENCIDOS
    fun obtenerSociosVencidos(): List<SocioVencido> {
        val lista = mutableListOf<SocioVencido>()
        val db = readableDatabase

        val cursor = db.rawQuery("""
        SELECT u.nombre, u.apellido, u.dni, s.fecha_alta
        FROM usuarios u
        INNER JOIN socios s ON u.id = s.usuario_id
        WHERE u.tipo_usuario = 1
    """, null)

        val hoy = fechaActual() // yyyy-MM-dd

        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido"))
                val dni = cursor.getString(cursor.getColumnIndexOrThrow("dni"))
                val fechaAltaOriginal = cursor.getString(cursor.getColumnIndexOrThrow("fecha_alta"))

                val nombreCompleto = "$nombre $apellido"

                // --- CONVERSIÓN CORRECTA DE dd/MM/yyyy → yyyy-MM-dd ---
                val partes = fechaAltaOriginal.split("/")
                if (partes.size != 3) continue

                val dia = partes[0]
                val mes = partes[1]
                val anio = partes[2]

                val fechaAltaFormateada = "$anio-$mes-$dia"
                // ------------------------------------------------------

                val vencimiento = calcularFechaVencimiento(fechaAltaFormateada)

                if (vencimiento < hoy) {
                    lista.add(
                        SocioVencido(
                            nombre = nombreCompleto,
                            dni = dni,
                            fechaAlta = fechaAltaOriginal,
                            vencimiento = vencimiento
                        )
                    )
                }

            } while (cursor.moveToNext())
        }

        cursor.close()
        return lista
    }




    // FECHA ACTUAL yyyy-MM-dd
    fun fechaActual(): String {
        val c = Calendar.getInstance()
        return String.format(
            "%04d-%02d-%02d",
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH) + 1,
            c.get(Calendar.DAY_OF_MONTH)
        )
    }

}





