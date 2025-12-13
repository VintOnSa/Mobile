package com.example.desarrollomovil.data

import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    suspend fun registro(usuario: User): Long {
        return userDao.registro(usuario)
    }

    suspend fun login(correo: String, password: String): User? {
        return userDao.login(correo, password)
    }

    suspend fun verificarUsuarioExiste(correo: String): Boolean {
        return userDao.getUserByEmail(correo) != null
    }


    suspend fun initializeAdmin() {
        if (userDao.adminExists() == 0) {
            val adminUser = User(
                id = 0,
                nombre = "Administrador",
                correo = "admin@mail.com",
                password = "admin123",
                tipo = "admin"
            )
            userDao.registro(adminUser)
        }
    }
}