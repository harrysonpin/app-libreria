package com.example.parcial_2.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.parcial_2.Model.Autores
import com.example.parcial_2.Model.Prestamos

@Dao
interface AutoresDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) //Revisión de conflictos entre registros
    suspend fun insert (autor: Autores)

    @Query("SELECT * FROM autores")
    suspend fun getAllAutores(): List<Autores>

    @Delete
    suspend fun deleteAutor(autor: Autores)

    @Update
    suspend fun updateAutor(autor: Autores)

    @Query("SELECT * FROM autores WHERE autor_id = :autorId")
    suspend fun getAutorById(autorId: Int): Autores?

}