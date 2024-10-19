package com.example.parcial_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.parcial_2.DAO.UserDao
import com.example.parcial_2.Database.UserDatabase
import com.example.parcial_2.Repository.*
import com.example.parcial_2.Screen.navegador

class MainActivity : ComponentActivity() {
    private lateinit var userRepository: UserRepository
    private lateinit var prestamosRepository: PrestamosRepository
    private lateinit var autoresRepository: AutoresRepository
    private lateinit var librosRepository: LibrosRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize DAOs
        val userDao: UserDao = UserDatabase.getDatabase(application).userDao()
        val prestamosDao = UserDatabase.getDatabase(application).prestamosDao()
        val autoresDao = UserDatabase.getDatabase(application).autoresDao()
        val librosDao = UserDatabase.getDatabase(application).librosDao()

        // Initialize repositories
        userRepository = UserRepository(userDao)
        prestamosRepository = PrestamosRepository(prestamosDao)
        autoresRepository = AutoresRepository(autoresDao)
        librosRepository = LibrosRepository(librosDao)

        setContent {
            navegador(userRepository, prestamosRepository, autoresRepository, librosRepository)
        }
    }
}