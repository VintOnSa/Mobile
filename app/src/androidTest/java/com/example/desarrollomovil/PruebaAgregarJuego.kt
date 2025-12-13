package com.example.desarrollomovil

import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.desarrollomovil.vistas.crud.AgregarJuegoScreen
import org.junit.Rule
import org.junit.Test

class PruebaAgregarJuego {

    @get:Rule
    val composableRule = createComposeRule()

    @Test
    fun testAgregarJuego(){
        //Pruebas Unitarias de Agregar Juego sin Imagen
        composableRule.setContent {
            AgregarJuegoScreen()
        }

        composableRule.onNodeWithText("Titulo *").performTextInput("Testing")
        composableRule.onNodeWithText("Publicador *").performTextInput("Testing")
        composableRule.onNodeWithText("Precio *").performTextInput("Testing")
        composableRule.onNodeWithText("Stock *").performTextInput("Testing")
        composableRule.onNodeWithText("Descripcion").performTextInput("Testing")
        composableRule.onNodeWithText("Plataforma *").performTextInput("Testing")
        composableRule.onNodeWithText("Genero *").performTextInput("Testing")

        composableRule.onNode(hasText("Agregar Juego") and hasClickAction())
            .assertExists().performClick()
    }
}