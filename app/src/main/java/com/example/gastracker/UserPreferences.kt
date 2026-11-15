package com.example.gastracker

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "gas_tracker_prefs")

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class AppColorTheme(val displayName: String) {
    DEFAULT("Original"),
    BLUE("Azul"),
    RED("Rojo"),
    GREEN("Verde"),
    MUSTARD("Mostaza"),
    YELLOW("Amarillo"),
    PINK("Rosa")
}

enum class AppLanguage(val code: String, val displayName: String) {
    SPANISH("es", "Español"),
    ENGLISH("en", "English")
}

enum class UserRole(val label: String) {
    ADMIN("Administrador"),
    USER("Usuario")
}

data class UserSession(
    val username: String,
    val role: UserRole
)

class UserPreferences(private val context: Context) {

    companion object {
        val TOTAL_GASTADO_KEY = doublePreferencesKey("total_gastado")
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        val COLOR_THEME_KEY = stringPreferencesKey("color_theme")
        val SIMBOLO_MONEDA_KEY = stringPreferencesKey("simbolo_moneda")
        val LOGGED_USER_KEY = stringPreferencesKey("logged_user")
        val APP_LANGUAGE_KEY = stringPreferencesKey("app_language")
    }

    // Lee el usuario actualmente autenticado (null si no hay sesión)
    val loggedUser: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[LOGGED_USER_KEY]
    }

    // Lee el total acumulado
    val totalGastado: Flow<Double> = context.dataStore.data.map { preferences ->
        preferences[TOTAL_GASTADO_KEY] ?: 0.0
    }

    // Lee la preferencia de tema (por defecto SISTEMA)
    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        val name = preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
        try {
            ThemeMode.valueOf(name)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
    }

    // Lee la preferencia de paleta de colores (por defecto DEFAULT)
    val appColorTheme: Flow<AppColorTheme> = context.dataStore.data.map { preferences ->
        val name = preferences[COLOR_THEME_KEY] ?: AppColorTheme.DEFAULT.name
        try {
            AppColorTheme.valueOf(name)
        } catch (e: Exception) {
            AppColorTheme.DEFAULT
        }
    }

    // Lee la preferencia de símbolo de divisa/moneda (por defecto "$")
    val simboloMoneda: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SIMBOLO_MONEDA_KEY] ?: "$"
    }

    // Lee el idioma configurado (por defecto SPANISH)
    val appLanguage: Flow<AppLanguage> = context.dataStore.data.map { preferences ->
        val name = preferences[APP_LANGUAGE_KEY] ?: AppLanguage.SPANISH.name
        try {
            AppLanguage.valueOf(name)
        } catch (e: Exception) {
            AppLanguage.SPANISH
        }
    }

    // Guarda o remueve el usuario autenticado
    suspend fun guardarLoggedUser(username: String?) {
        context.dataStore.edit { preferences ->
            if (username == null) {
                preferences.remove(LOGGED_USER_KEY)
            } else {
                preferences[LOGGED_USER_KEY] = username
            }
        }
    }

    // Guarda el nuevo total
    suspend fun guardarTotal(nuevoTotal: Double) {
        context.dataStore.edit { preferences ->
            preferences[TOTAL_GASTADO_KEY] = nuevoTotal
        }
    }

    // Guarda la opción de tema elegida
    suspend fun guardarThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.name
        }
    }

    // Guarda la opción de paleta de color elegida
    suspend fun guardarAppColorTheme(theme: AppColorTheme) {
        context.dataStore.edit { preferences ->
            preferences[COLOR_THEME_KEY] = theme.name
        }
    }

    // Guarda el símbolo de divisa/moneda
    suspend fun guardarSimboloMoneda(simbolo: String) {
        context.dataStore.edit { preferences ->
            preferences[SIMBOLO_MONEDA_KEY] = simbolo
        }
    }

    // Guarda el idioma configurado
    suspend fun guardarAppLanguage(language: AppLanguage) {
        context.dataStore.edit { preferences ->
            preferences[APP_LANGUAGE_KEY] = language.name
        }
    }
}
