package com.example.club_deportivo_comf4

data class Usuario(
    val id: Long,
    val tipoUsuario: Int,
    val nombre: String,
    val apellido: String,
    val dni: String,
    val email: String,
    val telefono: String,
    val fechaNacimiento: String
)
