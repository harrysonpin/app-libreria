package com.example.parcial_2.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.parcial_2.DAO.*
import com.example.parcial_2.Model.*

@Database(entities = [User::class, Libros::class, Prestamos::class, Autores::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun librosDao(): LibrosDao
    abstract fun prestamosDao(): PrestamosDao
    abstract fun autoresDao(): AutoresDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "userdatabase"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}