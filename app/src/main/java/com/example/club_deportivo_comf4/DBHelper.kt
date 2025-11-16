package com.example.club_deportivo_comf4

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.Calendar

class DBHelper(context: Context) : SQLiteOpenHelper(context, "ClubDeportivo.db", null, 16) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("DBHelper", "onCreate: Creando estructura de tablas desde cero.")

        db.execSQL("CREATE TABLE administradores (id INTEGER PRIMARY KEY, usuario TEXT UNIQUE NOT NULL, password TEXT NOT NULL)")
        db.execSQL("CREATE TABLE usuarios (id INTEGER PRIMARY KEY, nombre TEXT, apellido TEXT, dni TEXT UNIQUE NOT NULL, fecha_nacimiento TEXT, telefono TEXT, email TEXT, tipo_usuario INTEGER)")
        db.execSQL("CREATE TABLE actividades (id INTEGER PRIMARY KEY, nombre TEXT UNIQUE NOT NULL, descripcion TEXT)")
        db.execSQL("CREATE TABLE socios (id INTEGER PRIMARY KEY, usuario_id INTEGER, fecha_alta TEXT, fecha_ultimo_pago TEXT, apto_fisico INTEGER, ficha_medica INTEGER, FOREIGN KEY(usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE)")
        db.execSQL("CREATE TABLE no_socios (id INTEGER PRIMARY KEY, usuario_id INTEGER, fecha_registro TEXT, FOREIGN KEY(usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE)")
        db.execSQL("CREATE TABLE pagos (id INTEGER PRIMARY KEY, usuario_id INTEGER, tipo_pago TEXT, monto_total REAL, metodo_pago TEXT, cuotas INTEGER, fecha_pago TEXT, estado_pago INTEGER, FOREIGN KEY(usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE)")
        db.execSQL("CREATE TABLE pagos_socios (id INTEGER PRIMARY KEY, pago_id INTEGER, mes INTEGER, anio INTEGER, FOREIGN KEY(pago_id) REFERENCES pagos(id) ON DELETE CASCADE)")
        db.execSQL("CREATE TABLE pagos_no_socios (id INTEGER PRIMARY KEY, pago_id INTEGER, fecha_dia TEXT, actividad_id INTEGER, FOREIGN KEY(pago_id) REFERENCES pagos(id) ON DELETE CASCADE, FOREIGN KEY(actividad_id) REFERENCES actividades(id) ON DELETE CASCADE)")

        db.execSQL("INSERT INTO administradores (usuario, password) VALUES ('admin', '1234')")
        db.execSQL("INSERT INTO actividades (nombre, descripcion) VALUES ('Natación', 'Piscina libre')")
        db.execSQL("INSERT INTO actividades (nombre, descripcion) VALUES ('Gimnasia', 'Clase de funcional')")
        db.execSQL("INSERT INTO actividades (nombre, descripcion) VALUES ('Hockey', 'Entrenamiento femenino')")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.w("DBHelper", "onUpgrade: Borrando y recreando la base de datos.")
        db.execSQL("DROP TABLE IF EXISTS pagos_no_socios")
        db.execSQL("DROP TABLE IF EXISTS pagos_socios")
        db.execSQL("DROP TABLE IF EXISTS pagos")
        db.execSQL("DROP TABLE IF EXISTS no_socios")
        db.execSQL("DROP TABLE IF EXISTS socios")
        db.execSQL("DROP TABLE IF EXISTS actividades")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS administradores")
        onCreate(db)
    }

    // -------------------- HELPERS DE FECHAS --------------------
    fun sumarMes(fecha: String, meses: Int = 1): String {
        return try {
            // Primero normalizar la fecha al formato yyyy-MM-dd
            val fechaNormalizada = convertirFechaParaComparacion(fecha)
                ?: return "Fecha inválida: $fecha"

            val partes = fechaNormalizada.split("-")
            if (partes.size != 3) return "Fecha inválida: $fecha"

            val cal = Calendar.getInstance().apply {
                set(partes[0].toInt(), partes[1].toInt() - 1, partes[2].toInt())
                add(Calendar.MONTH, meses)
            }

            String.format("%04d-%02d-%02d",
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH))

        } catch (e: Exception) {
            Log.e("DBHelper", "Error en sumarMes: $fecha - ${e.message}")
            "Fecha inválida: $fecha"
        }
    }

    fun fechaHoyString(): String {
        val c = Calendar.getInstance()
        return String.format("%04d-%02d-%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
    }

    private fun obtenerFechaUltimoPago(usuarioId: Long): String? {
        return readableDatabase.rawQuery("SELECT fecha_ultimo_pago FROM socios WHERE usuario_id = ?", arrayOf(usuarioId.toString())).use {
            if (it.moveToFirst()) it.getString(it.getColumnIndexOrThrow("fecha_ultimo_pago")) else null
        }
    }

    // -------------------- FUNCIONES USUARIO --------------------
    fun insertarUsuario(nombre: String, apellido: String, dni: String, fechaNac: String, telefono: String, email: String, tipoUsuario: Int): Long {
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("apellido", apellido)
            put("dni", dni)
            put("fecha_nacimiento", fechaNac)
            put("telefono", telefono)
            put("email", email)
            put("tipo_usuario", tipoUsuario)
        }
        return writableDatabase.insert("usuarios", null, values)
    }

    fun existeDni(dni: String): Boolean {
        return readableDatabase.rawQuery("SELECT id FROM usuarios WHERE dni = ?", arrayOf(dni)).use { it.count > 0 }
    }

    fun obtenerUsuarioIdPorDNI(dni: String): Long? {
        return readableDatabase.rawQuery("SELECT id FROM usuarios WHERE dni = ?", arrayOf(dni)).use {
            if (it.moveToFirst()) it.getLong(it.getColumnIndexOrThrow("id")) else null
        }
    }

    // -------------------- FUNCIONES SOCIOS --------------------
    fun insertarSocio(usuarioId: Long, fechaAlta: String, aptoFisico: Int, fichaMedica: Int): Long {
        val values = ContentValues().apply {
            put("usuario_id", usuarioId)
            put("fecha_alta", fechaAlta)
            put("apto_fisico", aptoFisico)
            put("ficha_medica", fichaMedica)
        }
        return writableDatabase.insert("socios", null, values)
    }

    fun obtenerSocioPorDNI(dni: String): Socio? {
        var socio: Socio? = null
        readableDatabase.rawQuery("""
            SELECT u.id, u.nombre, u.apellido, u.dni, u.email, u.telefono, s.fecha_alta
            FROM usuarios u INNER JOIN socios s ON u.id = s.usuario_id
            WHERE u.dni = ? AND u.tipo_usuario = 1
        """, arrayOf(dni)).use { cursor ->
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
        }
        return socio
    }

    fun registrarPagoMensual(dni: String, monto: Double, metodoPago: String, cuotas: Int): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val usuarioId = obtenerUsuarioIdPorDNI(dni) ?: throw Exception("Usuario no encontrado")

            //  Contar pagos existentes
            val cursorPagos = db.rawQuery("SELECT COUNT(*) FROM pagos WHERE usuario_id = ?", arrayOf(usuarioId.toString()))
            val totalPagos = if (cursorPagos.moveToFirst()) cursorPagos.getInt(0) else 0
            cursorPagos.close()

            Log.d("DBHelper", "Registrando pago para DNI: $dni - Total pagos anteriores: $totalPagos")

            // D primer pago usa fecha_alta, otros usan fecha actual
            val fechaPagoFinal = if (totalPagos == 0) {
                val cursorSocio = db.rawQuery("SELECT fecha_alta FROM socios WHERE usuario_id = ?", arrayOf(usuarioId.toString()))
                val fechaAlta = if (cursorSocio.moveToFirst()) cursorSocio.getString(0) else fechaHoyString()
                cursorSocio.close()
                convertirFechaParaComparacion(fechaAlta) ?: fechaHoyString() // 🔹 Asegura formato yyyy-MM-dd
            } else {
                fechaHoyString()
            }



            Log.d("DBHelper", "Fecha de pago final: $fechaPagoFinal")

            val updateValues = ContentValues().apply {
                put("fecha_ultimo_pago", fechaPagoFinal)
            }
            db.update("socios", updateValues, "usuario_id = ?", arrayOf(usuarioId.toString()))

            insertarPago(db, usuarioId, "CUOTA_MENSUAL", monto, metodoPago, cuotas, fechaPagoFinal, esSocio = true)

            db.setTransactionSuccessful()
            Log.d("DBHelper", "Pago mensual registrado EXITOSAMENTE para $dni - Fecha: $fechaPagoFinal")
            return true

        } catch (e: Exception) {
            Log.e("DBHelper", "Error al registrar pago mensual: ${e.message}")
            return false
        } finally {
            db.endTransaction()
        }
    }

    // OBTENER FECHA_ALTA DEL SOCIO
    private fun obtenerFechaAltaSocio(usuarioId: Long): String? {
        return readableDatabase.rawQuery(
            "SELECT fecha_alta FROM socios WHERE usuario_id = ?",
            arrayOf(usuarioId.toString())
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getString(cursor.getColumnIndexOrThrow("fecha_alta"))
            } else {
                null
            }
        }
    }

    //  CONTAR PAGOS
    private fun obtenerTotalPagos(usuarioId: Long): Int {
        return readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM pagos WHERE usuario_id = ?",
            arrayOf(usuarioId.toString())
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getInt(0)
            } else {
                0
            }
        }
    }

    // -------------------- FUNCIONES NO SOCIOS --------------------
    fun insertarNoSocio(usuarioId: Long, fechaRegistro: String): Long {
        val values = ContentValues().apply {
            put("usuario_id", usuarioId)
            put("fecha_registro", fechaRegistro)
        }
        return writableDatabase.insert("no_socios", null, values)
    }

    fun registrarPagoNoSocio(dni: String, monto: Double, metodoPago: String, cuotas: Int, nombreActividad: String, fechaDePago: String): Long {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val usuarioId = obtenerUsuarioIdPorDNI(dni) ?: throw Exception("Usuario no encontrado")
            val actividadId = obtenerActividadIdPorNombre(db, nombreActividad) ?: throw Exception("Actividad no encontrada")
            val pagoId = insertarPago(db, usuarioId, "NO_SOCIO", monto, metodoPago, cuotas, fechaDePago, esSocio = false, actividadId = actividadId)
            db.setTransactionSuccessful()
            return pagoId
        } catch (e: Exception) {
            Log.e("DBHelper", "Error al registrar pago no socio: ${e.message}")
            return -1L
        } finally {
            db.endTransaction()
        }
    }


    fun obtenerNoSocioPorDNI(dni: String): NoSocio? {
        var noSocio: NoSocio? = null
        readableDatabase.rawQuery("""
        SELECT u.id, u.nombre, u.apellido, u.dni, u.email, u.telefono, ns.fecha_registro
        FROM usuarios u INNER JOIN no_socios ns ON u.id = ns.usuario_id
        WHERE u.dni = ? AND u.tipo_usuario = 2
    """, arrayOf(dni)).use { cursor ->
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
        }
        return noSocio
    }


    // -------------------- FUNCIONES COMUNES --------------------
    private fun insertarPago(db: SQLiteDatabase, usuarioId: Long, tipoPago: String, monto: Double, metodoPago: String, cuotas: Int, fechaPago: String, esSocio: Boolean, actividadId: Long? = null): Long {
        // Insert pago
        val pagoValues = ContentValues().apply {
            put("usuario_id", usuarioId)
            put("tipo_pago", tipoPago)
            put("monto_total", monto)
            put("metodo_pago", metodoPago)
            put("cuotas", cuotas)
            put("fecha_pago", fechaPago)
            put("estado_pago", 1)
        }
        val pagoId = db.insertOrThrow("pagos", null, pagoValues)

        if (esSocio) {
            val cal = Calendar.getInstance()
            val socioValues = ContentValues().apply {
                put("pago_id", pagoId)
                put("mes", cal.get(Calendar.MONTH) + 1)
                put("anio", cal.get(Calendar.YEAR))
            }
            db.insertOrThrow("pagos_socios", null, socioValues)
        } else {
            val noSocioValues = ContentValues().apply {
                put("pago_id", pagoId)
                put("fecha_dia", fechaPago)
                put("actividad_id", actividadId)
            }
            db.insertOrThrow("pagos_no_socios", null, noSocioValues)
        }
        return pagoId
    }



    //Esto es para filtrar todos los socios vencidos, para la lista de socios vencidos
    fun obtenerSociosVencidos(): List<SocioVencido> {
        val lista = mutableListOf<SocioVencido>()
        val hoy = fechaHoyString()

        readableDatabase.rawQuery("""
        SELECT u.nombre, u.apellido, u.dni, s.fecha_alta, s.fecha_ultimo_pago
        FROM usuarios u
        INNER JOIN socios s ON u.id = s.usuario_id
        WHERE u.tipo_usuario = 1
    """, null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                    val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido"))
                    val dni = cursor.getString(cursor.getColumnIndexOrThrow("dni"))
                    val fechaAlta = cursor.getString(cursor.getColumnIndexOrThrow("fecha_alta"))
                    val fechaUltimoPago = cursor.getString(cursor.getColumnIndexOrThrow("fecha_ultimo_pago"))

                    // Fecha base para cálculo: si tiene último pago se usa ese, si no, fecha de alta
                    val fechaBase = fechaUltimoPago.ifEmpty { fechaAlta }

                    // Calcular vencimiento (fechaBase + 1 mes)
                    val fechaVencimiento = sumarMes(convertirFechaParaComparacion(fechaBase) ?: fechaBase, 1)

                    // Si la fecha de vencimiento es menor que hoy, está vencido
                    if (fechaVencimiento < hoy) {
                        lista.add(SocioVencido(
                            nombre = "$nombre $apellido",
                            dni = dni,
                            fechaAlta = fechaAlta,
                            vencimiento = formatearFechaParaMostrar(fechaVencimiento)
                        ))
                    }
                } while (cursor.moveToNext())
            }
        }

        //cursor.close()
        return lista
    }


    //Esto es para filtrar todos los socios, para la lista de socios
    fun obtenerTodosLosSocios(hoy: String): List<Socio> {
        val lista = mutableListOf<Socio>()
        val db = readableDatabase
        val query = """
        SELECT u.id, u.nombre, u.apellido, u.dni, u.email, u.telefono, s.fecha_alta
        FROM usuarios u
        INNER JOIN socios s ON u.id = s.usuario_id
        WHERE u.tipo_usuario = 1
    """
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido"))
                val fechaAltaOriginal = cursor.getString(cursor.getColumnIndexOrThrow("fecha_alta"))

                // Convierte la fecha de "dd/MM/yyyy" a "yyyy-MM-dd" para el cálculo
                val partes = fechaAltaOriginal.split("/")
                val fechaAltaFormateada = if (partes.size == 3) "${partes[2]}-${partes[1]}-${partes[0]}" else fechaAltaOriginal

                val vencimiento = calcularFechaVencimiento(fechaAltaFormateada)

                val estado = if (vencimiento < hoy) "Vencido" else "Activo"

                lista.add(
                    Socio(
                        idUsuario = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        nombre = nombre,
                        apellido = apellido,
                        dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")),
                        email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                        fechaInscripcion = fechaAltaOriginal,

                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista


    }


    //Esto es para el boton de eliminar la card de no socios
    fun eliminarNoSocio(dni: String): Boolean {
        val db = writableDatabase
        return db.delete("NoSocios", "dni=?", arrayOf(dni)) > 0
    }

     //Esto es para filtrar todos los no socios, para la lista de no socios

    fun obtenerTodosLosNoSocios(): List<NoSocio> {
        val lista = mutableListOf<NoSocio>()
        val db = readableDatabase
        val query = """
        SELECT u.id, u.nombre, u.apellido, u.dni, u.email, u.telefono, ns.fecha_registro
        FROM usuarios u
        INNER JOIN no_socios ns ON u.id = ns.usuario_id
        WHERE u.tipo_usuario = 2
    """
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    NoSocio(
                        idUsuario = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                        dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")),
                        email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                        fechaRegistro = cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro"))
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return lista
    }



    fun eliminarSocio(dni: String): Boolean {
        val db = writableDatabase
        return try {
            db.beginTransaction()

            // 1. Obtener usuario_id
            val usuarioId = obtenerUsuarioIdPorDNI(dni) ?: return false


            db.delete("pagos_socios", "usuario_id = ?", arrayOf(usuarioId.toString()))
            db.delete("socios", "usuario_id = ?", arrayOf(usuarioId.toString()))


            val filasEliminadas = db.delete("usuarios", "id = ?", arrayOf(usuarioId.toString()))

            db.setTransactionSuccessful()
            filasEliminadas > 0
        } catch (e: Exception) {
            android.util.Log.e("DBHelper", "Error al eliminar socio: ${e.message}")
            false
        } finally {
            db.endTransaction()
            db.close()
        }
    }


    //Obtener usuario por id para el buscador de usuario
    fun obtenerUsuarioPorId(id: Long): Usuario? {
        val db = readableDatabase

        val cursor = readableDatabase.rawQuery(
            "SELECT id, tipo_usuario, nombre, apellido, dni, email, telefono, fecha_nacimiento FROM usuarios WHERE id = ?",
            arrayOf(id.toString())
        )

        return cursor.use {
            if (it.moveToFirst()) {
                Usuario(
                    id = it.getLong(0),
                    tipoUsuario = it.getInt(1),
                    nombre = it.getString(2),
                    apellido = it.getString(3),
                    dni = it.getString(4),
                    email = it.getString(5),
                    telefono = it.getString(6),
                    fechaNacimiento = it.getString(7)
                )
            } else null
        }

    }



    /*/ Verificar si un socio está vencido
    private fun estaVencido(fechaUltimoPago: String): Boolean {
        return try {
            val calPago = Calendar.getInstance().apply {
                val partes = fechaUltimoPago.split("-")
                set(partes[0].toInt(), partes[1].toInt() - 1, partes[2].toInt())
            }

            val calHoy = Calendar.getInstance()



            val mismoAnio = calPago.get(Calendar.YEAR) == calHoy.get(Calendar.YEAR)
            val mismoMes = calPago.get(Calendar.MONTH) == calHoy.get(Calendar.MONTH)
            !(mismoAnio && mismoMes)

        } catch (e: Exception) {
            Log.e("DBHelper", "Error verificando vencimiento: $fechaUltimoPago - ${e.message}")
            false
        }
    }*/

    /* Calcular próximo vencimiento para mostrar
    private fun calcularProximoVencimiento(fechaUltimoPago: String): String {
        return try {
            val fechaVencimiento = sumarMes(fechaUltimoPago, 1)
            formatearFechaParaMostrar(fechaVencimiento)
        } catch (e: Exception) {
            "Fecha inválida"
        }
    }*/

    //  Fecha conversión, esto es porque se usaron diferentes formas de fechas
    private fun convertirFechaParaComparacion(fechaOriginal: String): String? {
        return try {
            // Si ya está en formato yyyy-MM-dd, devolver tal cual
            if (fechaOriginal.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                return fechaOriginal
            }

            // Si está en formato dd/MM/yyyy, convertir a yyyy-MM-dd
            if (fechaOriginal.matches(Regex("\\d{1,2}/\\d{1,2}/\\d{4}"))) {
                val partes = fechaOriginal.split("/")
                if (partes.size == 3) {
                    val dia = partes[0].padStart(2, '0')
                    val mes = partes[1].padStart(2, '0')
                    val anio = partes[2]
                    return "$anio-$mes-$dia"
                }
            }


            // Si no coincide con ningún formato conocido
            Log.e("DBHelper", "Formato de fecha no reconocido: $fechaOriginal")
            null
        } catch (e: Exception) {
            Log.e("DBHelper", "Error convirtiendo fecha: $fechaOriginal - ${e.message}")
            null
        }
    }

    //
    private fun formatearFechaParaMostrar(fechaBD: String): String {
        return try {
            // Convierte de yyyy-MM-dd a dd/MM/yyyy para mostrar
            if (fechaBD.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                val partes = fechaBD.split("-")
                "${partes[2]}/${partes[1]}/${partes[0]}"
            } else {
                fechaBD
            }
        } catch (e: Exception) {
            fechaBD
        }
    }

    fun obtenerActividades(): List<String> {
        val lista = mutableListOf<String>()
        readableDatabase.rawQuery("SELECT nombre FROM actividades", null).use { cursor ->
            if (cursor.moveToFirst()) do { lista.add(cursor.getString(0)) } while (cursor.moveToNext())
        }
        return lista
    }

    private fun obtenerActividadIdPorNombre(db: SQLiteDatabase, nombreActividad: String): Long? {
        return db.rawQuery("SELECT id FROM actividades WHERE nombre = ?", arrayOf(nombreActividad)).use { cursor ->
            if (cursor.moveToFirst()) cursor.getLong(cursor.getColumnIndexOrThrow("id")) else null
        }
    }

    fun validarAdministrador(usuario: String, password: String): Boolean {
        return readableDatabase.rawQuery("SELECT * FROM administradores WHERE usuario = ? AND password = ?", arrayOf(usuario, password)).use { it.moveToFirst() }
    }


    // --- Añade esta función completa en tu archivo DBHelper.kt ---




    /*fun esSocio(usuarioId: Long): Boolean {
        return readableDatabase.rawQuery("SELECT id FROM socios WHERE usuario_id = ?", arrayOf(usuarioId.toString())).use { it.count > 0 }
    }*/

   /* fun tienePagosPrevios(usuarioId: Long): Boolean {
        return readableDatabase.rawQuery("SELECT id FROM pagos WHERE usuario_id = ? LIMIT 1", arrayOf(usuarioId.toString())).use { it.count > 0 }
    }*/

    fun calcularFechaVencimiento(fechaBase: String): String = sumarMes(fechaBase)

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
