package com.example.parcial_2.Repository

import com.example.parcial_2.DAO.AutoresDao
import com.example.parcial_2.Model.Autores

class AutoresRepository(private val autoresDao: AutoresDao) {
    suspend fun insertar(autor: Autores) {
        autoresDao.insert(autor)
    }

    suspend fun getAllAutores(): List<Autores> {
        return autoresDao.getAllAutores()
    }

    suspend fun eliminar(autor: Autores) {
        autoresDao.deleteAutor(autor)
    }

    suspend fun actualizar(autor: Autores) {
        autoresDao.updateAutor(autor)
    }

}
