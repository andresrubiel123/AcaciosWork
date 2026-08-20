package com.acacioswork.ui.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.acacioswork.ui.theme.AcaciosWorkTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLoginScreenDisplaysTitleAndFields() {
        composeTestRule.setContent {
            AcaciosWorkTheme {
                LoginScreen(onLoginSuccess = {})
            }
        }

        // Verificar titulo y subtitulo
        composeTestRule.onNodeWithText("AcaciosWork").assertIsDisplayed()
        composeTestRule.onNodeWithText("Acceso al sistema administrativo").assertIsDisplayed()

        // Verificar boton de iniciar sesion
        composeTestRule.onNodeWithText("Iniciar Sesión").assertIsDisplayed()
    }

    @Test
    fun testInputCredentials() {
        composeTestRule.setContent {
            AcaciosWorkTheme {
                LoginScreen(onLoginSuccess = {})
            }
        }

        // Ingresar usuario y verificar que acepte el texto
        composeTestRule.onNodeWithText("Usuario").performTextInput("admin")
        composeTestRule.onNodeWithText("admin").assertIsDisplayed()
    }
}
