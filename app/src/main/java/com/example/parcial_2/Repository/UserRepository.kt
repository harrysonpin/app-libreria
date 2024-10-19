package com.example.parcial_2.Repository

import com.example.parcial_2.DAO.UserDao
import com.example.parcial_2.Model.User

class UserRepository(private val userDao: UserDao){
    suspend fun insertar(user: User){
        userDao.insert(user)
    }

    suspend fun getAllUsers(): List<User>{
        return userDao.getAllUsers()
    }
    suspend fun eliminar(user: User) {
        userDao.deleteUser(user)
    }
    suspend fun actualizar(user: User) {
        userDao.updateUser(user)
    }
    suspend fun getUserById(userId: Int): User? {
        return userDao.getUserById(userId)
    }
}