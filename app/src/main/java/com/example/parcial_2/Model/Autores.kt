package com.example.parcial_2.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "autores") //se va a convertir en una tabla para la BD, llamada autores
data class Autores(
    @PrimaryKey(autoGenerate = true)
    val autor_id: Int = 0,
    val nombre: String,
    val apellido: String,
    val nacionalidad: String,
)