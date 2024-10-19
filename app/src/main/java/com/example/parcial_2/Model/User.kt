package com.example.parcial_2.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users") //se va a convertir en una tabla para la BD, llamada users o miembros
data class User(
    @PrimaryKey(autoGenerate = true)
    val miembro_id: Int = 0,
    var nombre: String,
    var apellido: String,
    var fecha_inscripcion: String
)