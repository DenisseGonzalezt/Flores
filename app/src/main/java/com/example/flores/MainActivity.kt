package com.example.flores

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.example.flores.data.ThemeAndLocaleManager
import com.example.flores.ui.navigation.AppNavigation
import com.example.flores.ui.theme.FloresTheme
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val isDarkMode by ThemeAndLocaleManager.isDarkMode.collectAsState()
            val languageCode by ThemeAndLocaleManager.currentLanguage.collectAsState()

            // Crear la configuración del locale seleccionado
            val locale = Locale(languageCode)
            Locale.setDefault(locale)

            val configuration = Configuration(LocalConfiguration.current).apply {
                setLocale(locale)
            }

            // Inyectar la nueva configuración para que Jetpack Compose actualice los stringResource en vivo
            CompositionLocalProvider(
                LocalConfiguration provides configuration,
                LocalContext provides LocalContext.current.createConfigurationContext(configuration)
            ) {
                FloresTheme(darkTheme = isDarkMode) {
                    AppNavigation()
                }
            }
        }
    }
}