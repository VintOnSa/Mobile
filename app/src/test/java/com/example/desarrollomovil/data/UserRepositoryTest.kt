package com.example.desarrollomovil.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

// ---------------------------------------------------------------------------------------
// Nota: Definición simulada de la DAO basada en tu repositorio
interface UserDao {
    suspend fun registro(user: User): Long
    suspend fun login(correo: String, password: String): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun adminExists(): Int
}
// ---------------------------------------------------------------------------------------

class UserRepositoryTest {

    private val userDao: UserDao = mockk()
    private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        userRepository = UserRepository(userDao)
    }

    // =========================================================================
    // PRUEBAS PARA registro() - CORREGIDO
    // =========================================================================

    @Test
    fun registro_llamaAregistroEnDAO_yRetornaId() = runTest {
        // 1. Arrange
        val userToRegister = User(
            nombre = "Nuevo",
            correo = "nuevo@mail.com",
            password = "password123",
            tipo = "usuario"
        )
        val expectedId = 5L // ID simulado que retorna la base de datos

        // Simular que el método 'registro' de la DAO retorna el ID
        coEvery { userDao.registro(userToRegister) } returns expectedId

        // 2. Act
        // ✅ CORRECCIÓN: Llama a 'registro' y pasa el objeto User, coincidiendo con tu UserRepository
        val actualId = userRepository.registro(userToRegister)

        // 3. Assert
        coVerify(exactly = 1) { userDao.registro(userToRegister) }
        assertEquals(expectedId, actualId)
    }


    // =========================================================================
    // PRUEBAS PARA login()
    // =========================================================================

    @Test
    fun login_credencialesCorrectas_retornaUsuario() = runTest {
        val email = "admin@mail.com"
        val password = "admin123"
        val expectedUser = User(nombre = "Admin", correo = email, password = password, tipo = "admin")

        coEvery { userDao.login(email, password) } returns expectedUser

        val user = userRepository.login(email, password)

        assertNotNull(user)
        assertEquals("Admin", user?.nombre)
        coVerify(exactly = 1) { userDao.login(email, password) }
    }

    @Test
    fun login_credencialesIncorrectas_retornaNull() = runTest {
        val email = "admin@mail.com"
        val password = "wrongpassword"

        coEvery { userDao.login(email, password) } returns null

        val user = userRepository.login(email, password)

        assertNull(user)
        coVerify(exactly = 1) { userDao.login(email, password) }
    }

    // =========================================================================
    // PRUEBAS PARA verificarUsuarioExiste()
    // =========================================================================

    @Test
    fun verificarUsuarioExiste_retornaTrueSiExiste() = runTest {
        val email = "existe@mail.com"
        val existingUser = User(nombre = "Existente", correo = email, password = "", tipo = "usuario")
        coEvery { userDao.getUserByEmail(email) } returns existingUser

        val exists = userRepository.verificarUsuarioExiste(email)

        assertEquals(true, exists)
        coVerify(exactly = 1) { userDao.getUserByEmail(email) }
    }

    @Test
    fun verificarUsuarioExiste_retornaFalseSiNoExiste() = runTest {
        val email = "noexiste@mail.com"
        coEvery { userDao.getUserByEmail(email) } returns null

        val exists = userRepository.verificarUsuarioExiste(email)

        assertEquals(false, exists)
        coVerify(exactly = 1) { userDao.getUserByEmail(email) }
    }

    // =========================================================================
    // PRUEBAS PARA initializeAdmin()
    // =========================================================================

    @Test
    fun initializeAdmin_noExisteAdmin_creaAdmin() = runTest {
        coEvery { userDao.adminExists() } returns 0
        coEvery { userDao.registro(any()) } returns 1L

        userRepository.initializeAdmin()

        coVerify(exactly = 1) { userDao.adminExists() }
        coVerify(exactly = 1) { userDao.registro(match { it.correo == "admin@mail.com" }) }
    }

    @Test
    fun initializeAdmin_existeAdmin_noHaceNada() = runTest {
        coEvery { userDao.adminExists() } returns 1

        userRepository.initializeAdmin()

        coVerify(exactly = 1) { userDao.adminExists() }
        coVerify(exactly = 0) { userDao.registro(any()) }
    }
}