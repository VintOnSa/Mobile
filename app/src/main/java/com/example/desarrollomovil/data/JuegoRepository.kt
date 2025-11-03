package com.example.desarrollomovil.data

import android.content.Context
import android.net.Uri
import com.example.desarrollomovil.R
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.FileOutputStream
import kotlin.Long
import kotlin.String

class JuegoRepository(private val juegoDao: JuegoDao) {
    suspend fun insertarJuego(juego: Juego): Long {
        return juegoDao.insertarJuego(juego)
    }

    fun obtenerTodosLosJuegos(): Flow<List<Juego>> {
        return juegoDao.obtenerTodosLosJuegos()
    }

    suspend fun obtenerJuegoPorId(id: Long): Juego? {
        return juegoDao.obtenerJuegoPorId(id)
    }

    suspend fun actualizarJuego(juego: Juego) {
        juegoDao.actualizarJuego(juego)
    }

    suspend fun eliminarJuego(id: Long) {
        juegoDao.eliminarJuego(id)
    }


    suspend fun juegosInicio(context: Context) {
        if (juegoDao.contarJuegos() > 0) {
            return
        }
        val imagenUri1 = copiarImagenDesdeDrawable(R.drawable.zelda, "zelda.jpg", context)
        val imagenUri2 = copiarImagenDesdeDrawable(R.drawable.mario, "mario.jpg", context)
        val imagenUri3 = copiarImagenDesdeDrawable(R.drawable.minecraft, "minecraft.jpg", context)

        val juegosIniciales = listOf(
            Juego(
                id = 1,
                titulo = "The Legend of Zelda: Breath of the Wild",
                publicador = "Nintendo",
                precio = 59990.0,
                stock = 8,
                descripcion = "Explora el vasto reino de Hyrule en esta aventura épica donde la libertad y el descubrimiento son clave. Resuelve puzzles, combate criaturas y descubre los secretos de un reino en ruinas.",
                plataforma = "Nintendo Switch",
                genero = "Aventura",
                imagenUri = imagenUri1.toString()
            ),
            Juego(
                id = 2,
                titulo = "Super Mario Odyssey",
                publicador = "Nintendo",
                precio = 49990.0,
                stock = 12,
                descripcion = "Acompaña a Mario y su nuevo aliado Cappy en un viaje alrededor del mundo para rescatar a la Princesa Peach de Bowser. Captura enemigos y objetos con tu sombrero para usar sus habilidades.",
                plataforma = "Nintendo Switch",
                genero = "Aventura",
                imagenUri = imagenUri2.toString()
            ),
            Juego(
                id = 3,
                titulo = "Minecraft",
                publicador = "Mojang Studios",
                precio = 29990.0,
                stock = 25,
                descripcion = "El juego de construcción con bloques más popular del mundo. Construye, explora y sobrevive en mundos infinitos. Crea desde simples casas hasta impresionantes castillos y mecanismos complejos.",
                plataforma = "Multiplataforma",
                genero = "Sandbox",
                imagenUri = imagenUri3.toString()
            )
        )

        juegosIniciales.forEach { juego ->
            juegoDao.insertarJuego(juego)
        }
    }

    private fun copiarImagenDesdeDrawable(drawableId: Int, fileName: String, context: Context): Uri {
        val file = File(context.filesDir, fileName)

        context.resources.openRawResource(drawableId).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return Uri.fromFile(file)
    }
}