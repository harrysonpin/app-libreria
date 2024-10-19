package com.example.parcial_2.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.parcial_2.Model.Libros
import com.example.parcial_2.Model.Prestamos

@Dao
interface PrestamosDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) //Revisión de conflictos entre registros
    suspend fun insert (prestamo: Prestamos)

    @Query("SELECT * FROM prestamos")
    suspend fun getAllPrestamos(): List<Prestamos>

    @Query("SELECT * FROM prestamos WHERE libro_id = :libroId")
    suspend fun getPrestamosByLibro(libroId: Int): List<Prestamos>


    @Query("SELECT * FROM prestamos WHERE miembro_id = :userId")
    suspend fun getPrestamosByUser(userId: Int): List<Prestamos>

    @Delete
    suspend fun deletePrestamo(prestamo: Prestamos)

    @Update
    suspend fun updatePrestamo(prestamo: Prestamos)

    @Query("SELECT * FROM prestamos WHERE libro_id = :libroId AND miembro_id = :userId")
    suspend fun getPrestamoByLibroAndUser(libroId: Int, userId: Int): Prestamos?
}