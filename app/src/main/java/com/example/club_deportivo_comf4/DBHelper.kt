package com.example.club_deportivo_comf4

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

class DBHelper(context: Context) : SQLiteOpenHelper(context, "ClubDeportivo.db", null, 17) {

    // ==================== CONFIGURACIÓN DE LA BASE DE DATOS ====================

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("DBHelper", "onCreate: Creando estructura de tablas desde cero.")

        // Creación de tablas
        db.execSQL("CREATE TABLE administradores (id INTEGER PRIMARY KEY, usuario TEXT UNIQUE NOT NULL, password TEXT NOT NULL)")
        db.execSQL("CREATE TABLE usuarios (id INTEGER PRIMARY KEY, nombre TEXT, apellido TEXT, dni TEXT UNIQUE NOT NULL, fecha_nacimiento TEXT, telefono TEXT, email TEXT, tipo_usuario INTEGER)")
        db.execSQL("CREATE TABLE actividades (id INTEGER PRIMARY KEY, nombre TEXT UNIQUE NOT NULL, descripcion TEXT)")
        db.execSQL("CREATE TABLE socios (id INTEGER PRIMARY KEY, usuario_id INTEGER, fecha_alta TEXT, fecha_ultimo_pago TEXT, fecha_vencimiento TEXT, apto_fisico INTEGER, ficha_medica INTEGER, FOREIGN KEY(usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE)")
        db.execSQL("CREATE TABLE no_socios (id INTEGER PRIMARY KEY, usuario_id INTEGER, fecha_registro TEXT, FOREIGN KEY(usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE)")
        db.execSQL("CREATE TABLE pagos (id INTEGER PRIMARY KEY, usuario_id INTEGER, tipo_pago TEXT, monto_total REAL, metodo_pago TEXT, cuotas INTEGER, fecha_pago TEXT, estado_pago INTEGER, FOREIGN KEY(usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE)")
        db.execSQL("CREATE TABLE pagos_socios (id INTEGER PRIMARY KEY, pago_id INTEGER, mes INTEGER, anio INTEGER, FOREIGN KEY(pago_id) REFERENCES pagos(id) ON DELETE CASCADE)")
        db.execSQL("CREATE TABLE pagos_no_socios (id INTEGER PRIMARY KEY, pago_id INTEGER, fecha_dia TEXT, actividad_id INTEGER, FOREIGN KEY(pago_id) REFERENCES pagos(id) ON DELETE CASCADE, FOREIGN KEY(actividad_id) REFERENCES actividades(id) ON DELETE CASCADE)")

        // Datos iniciales
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

    // ==================== FUNCIONES DE FECHA ====================

    fun sumarMes(fecha: String, meses: Int = 1): String {
        return try {
            val fechaNormalizada = convertirFechaParaComparacion(fecha) ?: return "Fecha inválida: $fecha"
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

    fun fechaActual(): String {
        val c = Calendar.getInstance()
        return String.format("%04d-%02d-%02d",
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH) + 1,
            c.get(Calendar.DAY_OF_MONTH))
    }

    fun calcularFechaVencimiento(fechaBase: String): String = sumarMes(fechaBase)

    private fun obtenerFechaUltimoPago(usuarioId: Long): String? {
        return readableDatabase.rawQuery("SELECT fecha_ultimo_pago FROM socios WHERE usuario_id = ?", arrayOf(usuarioId.toString())).use {
            if (it.moveToFirst()) it.getString(it.getColumnIndexOrThrow("fecha_ultimo_pago")) else null
        }
    }

    fun obtenerUltimaFechaPagoReal(usuarioId: Long): String? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT fecha_pago FROM pagos WHERE usuario_id = ? ORDER BY id DESC LIMIT 1",
            arrayOf(usuarioId.toString())
        )
        cursor.use {
            if (it.moveToFirst()) {
                return it.getString(0)
            }
        }
        return null
    }


    // ==================== FUNCIONES DE USUARIO ====================

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

    fun obtenerUsuarioPorId(id: Long): Usuario? {
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

    // ==================== FUNCIONES DE SOCIOS ====================

    fun insertarSocio(usuarioId: Long, fechaAlta: String, aptoFisico: Int, fichaMedica: Int): Long {
        // Calcular primer vencimiento (fechaAlta + 1 mes)
        val fechaVencimiento = try {
            // Parseamos fechaAlta en formato dd/MM/yyyy
            val sdfAlta = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dateAlta = sdfAlta.parse(fechaAlta)!!

            // Sumamos 1 mes
            val cal = Calendar.getInstance()
            cal.time = dateAlta
            cal.add(Calendar.MONTH, 1)

            // Guardamos vencimiento también en formato dd/MM/yyyy
            sdfAlta.format(cal.time)
        } catch (e: Exception) {
            fechaAlta
        }

        val values = ContentValues().apply {
            put("usuario_id", usuarioId)
            put("fecha_alta", fechaAlta)
            put("fecha_ultimo_pago", fechaAlta)
            put("fecha_vencimiento", fechaVencimiento)
            put("apto_fisico", aptoFisico)
            put("ficha_medica", fichaMedica)
        }
        return writableDatabase.insert("socios", null, values)
    }

    fun obtenerSocioPorDNI(dni: String): Socio? {
        var socio: Socio? = null
        readableDatabase.rawQuery("""
            SELECT u.id, u.nombre, u.apellido, u.dni, u.email, u.telefono, s.fecha_alta, s.fecha_ultimo_pago, s.fecha_vencimiento
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
                    fechaInscripcion = cursor.getString(cursor.getColumnIndexOrThrow("fecha_alta")),
                    fechaUltimoPago = cursor.getString(cursor.getColumnIndexOrThrow("fecha_ultimo_pago")),
                    fechaVencimiento = cursor.getString(cursor.getColumnIndexOrThrow("fecha_vencimiento"))

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

            // Contar pagos existentes
            val cursorPagos = db.rawQuery("SELECT COUNT(*) FROM pagos WHERE usuario_id = ?", arrayOf(usuarioId.toString()))
            val totalPagos = if (cursorPagos.moveToFirst()) cursorPagos.getInt(0) else 0
            cursorPagos.close()

            Log.d("DBHelper", "Registrando pago para DNI: $dni - Total pagos anteriores: $totalPagos")

            // Determinar fecha de pago
            val fechaPagoFinal = if (totalPagos == 0) {
                val cursorSocio = db.rawQuery("SELECT fecha_alta FROM socios WHERE usuario_id = ?", arrayOf(usuarioId.toString()))
                val fechaAlta = if (cursorSocio.moveToFirst()) cursorSocio.getString(0) else fechaHoyString()
                cursorSocio.close()
                convertirFechaParaComparacion(fechaAlta) ?: fechaHoyString()
            } else {
                fechaHoyString()
            }

            Log.d("DBHelper", "Fecha de pago final: $fechaPagoFinal")

            // Obtener el último vencimiento actual del socio
            val cursorVenc = db.rawQuery(
                "SELECT fecha_vencimiento FROM socios WHERE usuario_id = ?",
                arrayOf(usuarioId.toString())
            )
            val ultimoVenc = if (cursorVenc.moveToFirst()) cursorVenc.getString(0) else fechaPagoFinal
            cursorVenc.close()

            // Si no existe último vencimiento (primer pago), usamos fechaPagoFinal como base
            val baseParaCalcular = ultimoVenc ?: fechaPagoFinal

            // Calcular el próximo vencimiento sumando 1 mes
            val nuevoVencimiento = calcularFechaVencimiento(baseParaCalcular)

            // Actualizar fecha de último pago
            val updateValues = ContentValues().apply {
                put("fecha_ultimo_pago", fechaPagoFinal)
                put("fecha_vencimiento", nuevoVencimiento)
            }
            db.update("socios", updateValues, "usuario_id = ?", arrayOf(usuarioId.toString()))

            // Insertar pago
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

                    val fechaBase = fechaUltimoPago.ifEmpty { fechaAlta }
                    val fechaVencimiento = sumarMes(convertirFechaParaComparacion(fechaBase) ?: fechaBase, 1)

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
        return lista
    }

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

    fun eliminarSocio(dni: String): Boolean {
        val db = this.writableDatabase
        var exito = false

        val usuarioId = obtenerUsuarioIdPorDNI(dni)
        if (usuarioId == null) {
            Log.e("DBHelper", "Error al eliminar: No se encontró un usuario con DNI $dni")
            return false
        }

        db.beginTransaction()
        try {
            val filasEliminadas = db.delete("usuarios", "id = ?", arrayOf(usuarioId.toString()))

            if (filasEliminadas > 0) {
                db.setTransactionSuccessful()
                exito = true
                Log.d("DBHelper", "Usuario (y su rol de socio) con DNI $dni eliminado exitosamente.")
            } else {
                Log.w("DBHelper", "No se eliminó ninguna fila para el usuario con DNI $dni (ID: $usuarioId).")
            }
        } catch (e: Exception) {
            Log.e("DBHelper", "Error durante la transacción de eliminación para DNI $dni: ${e.message}")
        } finally {
            db.endTransaction()
        }
        return exito
    }

    // ==================== FUNCIONES DE NO SOCIOS ====================

    fun insertarNoSocio(usuarioId: Long, fechaRegistro: String): Long {
        val values = ContentValues().apply {
            put("usuario_id", usuarioId)
            put("fecha_registro", fechaRegistro)
        }
        return writableDatabase.insert("no_socios", null, values)
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

    fun eliminarNoSocio(dni: String): Boolean {
        val db = this.writableDatabase
        var exito = false

        // Obtener el ID del usuario por DNI
        val usuarioId = obtenerUsuarioIdPorDNI(dni)
        if (usuarioId == null) {
            android.util.Log.e("DBHelper", "Error al eliminar: No se encontró un No Socio con DNI $dni")
            return false
        }

        db.beginTransaction()
        try {
            // Eliminar de la tabla 'usuarios'
            val filasEliminadas = db.delete("usuarios", "id = ?", arrayOf(usuarioId.toString()))

            if (filasEliminadas > 0) {
                db.setTransactionSuccessful()
                exito = true
                android.util.Log.d("DBHelper", "No Socio con DNI $dni eliminado exitosamente.")
            } else {
                android.util.Log.w("DBHelper", "No se eliminó ninguna fila para el No Socio con DNI $dni (ID: $usuarioId).")
            }
        } catch (e: Exception) {
            android.util.Log.e("DBHelper", "Error durante la transacción de eliminación para DNI $dni: ${e.message}")
        } finally {
            db.endTransaction()
            db.close()
        }

        return exito
    }


    // ==================== FUNCIONES DE ACTIVIDADES ====================

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

    // ==================== FUNCIONES DE ADMINISTRADOR ====================

    fun validarAdministrador(usuario: String, password: String): Boolean {
        return readableDatabase.rawQuery("SELECT * FROM administradores WHERE usuario = ? AND password = ?", arrayOf(usuario, password)).use { it.moveToFirst() }
    }

    // ==================== FUNCIONES PRIVADAS AUXILIARES ====================

    private fun insertarPago(db: SQLiteDatabase, usuarioId: Long, tipoPago: String, monto: Double, metodoPago: String, cuotas: Int, fechaPago: String, esSocio: Boolean, actividadId: Long? = null): Long {
        // Insertar pago principal
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

        // Insertar en tabla específica según tipo
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

    private fun convertirFechaParaComparacion(fechaOriginal: String): String? {
        return try {
            if (fechaOriginal.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                return fechaOriginal
            }

            if (fechaOriginal.matches(Regex("\\d{1,2}/\\d{1,2}/\\d{4}"))) {
                val partes = fechaOriginal.split("/")
                if (partes.size == 3) {
                    val dia = partes[0].padStart(2, '0')
                    val mes = partes[1].padStart(2, '0')
                    val anio = partes[2]
                    return "$anio-$mes-$dia"
                }
            }

            Log.e("DBHelper", "Formato de fecha no reconocido: $fechaOriginal")
            null
        } catch (e: Exception) {
            Log.e("DBHelper", "Error convirtiendo fecha: $fechaOriginal - ${e.message}")
            null
        }
    }

    private fun formatearFechaParaMostrar(fechaBD: String): String {
        return try {
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
}