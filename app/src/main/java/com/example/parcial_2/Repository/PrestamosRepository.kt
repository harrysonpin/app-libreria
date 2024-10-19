package com.example.parcial_2.Repository

import com.example.parcial_2.DAO.PrestamosDao
import com.example.parcial_2.Model.Prestamos

class PrestamosRepository(private val prestamosDao: PrestamosDao) {
    suspend fun insertar(prestamo: Prestamos) {
        prestamosDao.insert(prestamo)
    }

    suspend fun eliminar(prestamo: Prestamos) {
        prestamosDao.deletePrestamo(prestamo)
    }

    suspend fun actualizar(prestamo: Prestamos) {
        prestamosDao.updatePrestamo(prestamo)
    }
    suspend fun getPrestamosByLibro(libroId: Int): List<Prestamos> {
        return prestamosDao.getPrestamosByLibro(libroId)
    }
    suspend fun getPrestamosByUser(userId: Int): List<Prestamos> {
        return prestamosDao.getPrestamosByUser(userId)
    }
    suspend fun getPrestamoByLibroAndUser(libroId: Int, userId: Int): Prestamos? {
        return prestamosDao.getPrestamoByLibroAndUser(libroId, userId)
    }
}