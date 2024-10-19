package com.example.parcial_2.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.parcial_2.Model.User

@Dao //Van a estar todas las accciones sobre la tabla
interface UserDao {

    // @Query("INSERT INTO users (nombre, apellido, edad) VALUES (:nombre, apellido, edad)")
    // Suspend evitar que la aplicacion falle cuando se realizan las peticiones al realizar operaciones de forma asicronas

    @Insert(onConflict = OnConflictStrategy.REPLACE) //Revisión de conflictos entre registros
    suspend fun insert (user: User)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Delete
    suspend fun deleteUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE miembro_id = :userId")
    suspend fun getUserById(userId: Int): User?

}