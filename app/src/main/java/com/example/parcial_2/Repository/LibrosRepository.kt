package com.example.parcial_2.Repository

import com.example.parcial_2.DAO.LibrosDao
import com.example.parcial_2.Model.Libros

class LibrosRepository(private val librosDao: LibrosDao) {
    suspend fun insertar(libro: Libros) {
        librosDao.insert(libro)
    }

    suspend fun getAllLibros(): List<Libros> {
        return librosDao.getAllLibros()
    }

    suspend fun eliminar(libro: Libros) {
        librosDao.deleteLibro(libro)
    }

    suspend fun getLibrosByAutor(autorId: Int): List<Libros> {
        return librosDao.getLibrosByAutor(autorId)
    }
    suspend fun getLibroById(libroId: Int): Libros? {
        return librosDao.getLibroById(libroId)
    }
    suspend fun updateLibro(libro: Libros) {
        librosDao.updateLibro(libro)
    }
}