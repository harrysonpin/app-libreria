package com.example.parcial_2.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.parcial_2.Model.Libros
import com.example.parcial_2.Model.User

@Dao
interface LibrosDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) //Revisión de conflictos entre registros
    suspend fun insert (libro: Libros)

    @Query("SELECT * FROM libros")
    suspend fun getAllLibros(): List<Libros>

    @Query("SELECT * FROM libros WHERE autor_id = :autorId")
    suspend fun getLibrosByAutor(autorId: Int): List<Libros>

    @Delete
    suspend fun deleteLibro(libro: Libros)

    @Update
    suspend fun updateLibro(libro: Libros)

    @Query("SELECT * FROM libros WHERE libro_id = :libroId")
    suspend fun getLibroById(libroId: Int): Libros?
}