package com.example.club_deportivo_comf4

data class Socio(
    val idUsuario: Long,
    val nombre: String,
    val apellido: String,
    val dni: String,
    val email: String,
    val telefono: String,
    val fechaInscripcion: String? = null
)
