package com.example.desarrollomovil.repository

import com.example.desarrollomovil.data.Juego
import com.example.desarrollomovil.data.JuegoAgregar
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface JuegoService {
    companion object {
        val instance =
            Retrofit.Builder().baseUrl("https://api-production-71da.up.railway.app/")
                .addConverterFactory(MoshiConverterFactory.create())
                .client(OkHttpClient.Builder().build())
                .build().create(JuegoService::class.java)
    }

    @GET("juegos")
    suspend fun obtenerJuegos(): List<Juego>

    @GET("juegos/{id}")
    suspend fun buscarJuego(@Path("id") id: Int): Juego

    @POST("juegos")
    suspend fun agregarJuego(@Body juego: JuegoAgregar)

    @PUT("juegos")
    suspend fun actualizarJuego(@Body juego: Juego)

    @DELETE("juegos/{id}")
    suspend fun eliminarJuego(@Path("id") id: Int)

    @Multipart
    @POST("subir-imagen")
    suspend fun subirImagen(@Part imagen: MultipartBody.Part): ImageUploadResponse
    data class ImageUploadResponse(val imageUrl: String)
}