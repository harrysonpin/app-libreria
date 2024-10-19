package com.example.parcial_2.Model

import android.provider.ContactsContract.Data
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "prestamos",
    foreignKeys = [
        ForeignKey(
            entity = Libros::class,
            parentColumns = ["libro_id"],
            childColumns = ["libro_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["miembro_id"],
            childColumns = ["miembro_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Prestamos(
    @PrimaryKey(autoGenerate = true)
    val Prestamo_id: Int = 0,
    val fecha_prestamo: String,
    val fecha_devolucion: String,
    val libro_id: Int, // referencia a la tabla Libros
    val miembro_id: Int // referencia a la tabla User
)