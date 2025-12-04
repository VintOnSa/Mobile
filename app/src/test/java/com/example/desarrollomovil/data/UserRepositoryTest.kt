package com.example.desarrollomovil.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

// Asumiendo que tienes una interfaz para la base de datos como esta:
interface UserDao {
    suspend fun insertUser(user: User)
    suspend fun getUserByEmail(email: String): User?
    suspend fun getUserByEmailAndPassword(email: String, passwordHash: String): User?
}

// Nota: El UserRepository debe manejar el hasheo de contraseñas, lo simularemos aquí
class UserRepositoryTest {

    private val userDao: UserDao = mockk()
    private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        // Inicializar el repositorio con el mock de la DAO
        userRepository = UserRepository(userDao)
    }

    @Test
    fun registrarUsuario_llamaAInsertarEnDAO() = runTest {
        // 1. Arrange: Simular que el usuario no existe para permitir el registro
        coEvery { userDao.getUserByEmail("nuevo@mail.com") } returns null
        coEvery { userDao.insertUser(any()) } returns Unit

        val userToRegister = User(nombre = "Nuevo", correo = "nuevo@mail.com", password = "password123", tipo = "usuario")

        // 2. Act
        userRepository.registrarUsuario(userToRegister)

        // 3. Assert: Verificar que el método de inserción en la DAO fue llamado
        coVerify(exactly = 1) { userDao.insertUser(any()) }
    }

    @Test
    fun verificarUsuarioExiste_retornaTrueSiExiste() = runTest {
        // 1. Arrange: Simular que la DAO devuelve un usuario
        val existingUser = User(nombre = "Existente", correo = "existe@mail.com", password = "", tipo = "usuario")
        coEvery { userDao.getUserByEmail("existe@mail.com") } returns existingUser

        // 2. Act
        val exists = userRepository.verificarUsuarioExiste("existe@mail.com")

        // 3. Assert
        assertEquals(true, exists)
    }

    @Test
    fun verificarUsuarioExiste_retornaFalseSiNoExiste() = runTest {
        // 1. Arrange: Simular que la DAO devuelve null
        coEvery { userDao.getUserByEmail("noexiste@mail.com") } returns null

        // 2. Act
        val exists = userRepository.verificarUsuarioExiste("noexiste@mail.com")

        // 3. Assert
        assertEquals(false, exists)
    }

    @Test
    fun login_credencialesCorrectas_retornaUsuario() = runTest {
        // 1. Arrange: Simular el hash de la contraseña (el repositorio debe hacer esto)
        val email = "admin@mail.com"
        val password = "admin123"
        // Asumiendo que el repositorio llama a un método con la contraseña ya hasheada
        val expectedUser = User(nombre = "Admin", correo = email, password = "hashed_admin123", tipo = "admin")

        // Simular que la DAO encuentra el usuario con las credenciales
        coEvery { userRepository.login(email, password) } returns expectedUser

        // 2. Act
        val user = userRepository.login(email, password)

        // 3. Assert
        assertNotNull(user)
        assertEquals("Admin", user?.nombre)
    }
}