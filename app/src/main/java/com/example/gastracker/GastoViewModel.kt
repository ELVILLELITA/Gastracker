package com.example.gastracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GastoViewModel(application: Application) : AndroidViewModel(application) {
    private val gastoDao = AppDatabase.getDatabase(application).gastoDao()
    private val userPreferences = UserPreferences(application)

    // READ: Lista de todos los gastos desde la base de datos Room
    val listaGastos: StateFlow<List<Gasto>> = gastoDao.obtenerTodosLosGastos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // READ: Total gastado calculado a partir de la lista de gastos
    val totalGastado: StateFlow<Double> = listaGastos.map { lista ->
        lista.sumOf { it.monto }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    // READ: Preferencia de tema (Sistema, Claro, Oscuro)
    val themeMode: StateFlow<ThemeMode> = userPreferences.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.SYSTEM
        )

    // READ: Preferencia de paleta de color (Default, Rojo, Verde, Mostaza, Rosa)
    val appColorTheme: StateFlow<AppColorTheme> = userPreferences.appColorTheme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppColorTheme.DEFAULT
        )

    // READ: Preferencia de símbolo de divisa ($ / € / £ / S/ / Q)
    val simboloMoneda: StateFlow<String> = userPreferences.simboloMoneda
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "$"
        )

    // READ: Preferencia de idioma (SPANISH, ENGLISH)
    val appLanguage: StateFlow<AppLanguage> = userPreferences.appLanguage
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppLanguage.SPANISH
        )

    // READ: Usuario actualmente autenticado (null si no hay sesión iniciada)
    val loggedUser: StateFlow<String?> = userPreferences.loggedUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Método de autenticación con credenciales 'admin'/'123' o 'usuario'/'123'
    fun login(usuario: String, contrasenia: String): Boolean {
        val userClean = usuario.trim().lowercase()
        val passClean = contrasenia.trim()

        if ((userClean == "admin" || userClean == "usuario") && passClean == "123") {
            viewModelScope.launch {
                userPreferences.guardarLoggedUser(userClean)
            }
            return true
        }
        return false
    }

    // Método para cerrar sesión
    fun logout() {
        viewModelScope.launch {
            userPreferences.guardarLoggedUser(null)
        }
    }

    // Cambia y guarda la preferencia de tema
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            userPreferences.guardarThemeMode(mode)
        }
    }

    // Cambia y guarda la preferencia de paleta de color
    fun setAppColorTheme(theme: AppColorTheme) {
        viewModelScope.launch {
            userPreferences.guardarAppColorTheme(theme)
        }
    }

    // Cambia y guarda el símbolo de divisa
    fun setSimboloMoneda(simbolo: String) {
        viewModelScope.launch {
            userPreferences.guardarSimboloMoneda(simbolo)
        }
    }

    // Cambia y guarda la preferencia de idioma
    fun setAppLanguage(language: AppLanguage) {
        viewModelScope.launch {
            userPreferences.guardarAppLanguage(language)
        }
    }

    // CREATE: Agrega un nuevo registro de gasto guardando la fecha exacta de ingreso
    fun insertarGasto(
        monto: Double,
        tienda1: String,
        tienda2: String,
        etiqueta: String = "",
        categoria: String = "Gasolina",
        fechaTimestamp: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch {
            val nuevoGasto = Gasto(
                monto = monto,
                tienda1 = tienda1,
                tienda2 = tienda2,
                etiqueta = etiqueta,
                categoria = categoria,
                fechaTimestamp = fechaTimestamp
            )
            gastoDao.insertarGasto(nuevoGasto)
            userPreferences.guardarTotal(listaGastos.value.sumOf { it.monto } + monto)
        }
    }

    // UPDATE: Actualiza un registro de gasto existente
    fun actualizarGasto(gasto: Gasto) {
        viewModelScope.launch {
            gastoDao.actualizarGasto(gasto)
            val nuevoTotal = listaGastos.value.map { if (it.id == gasto.id) gasto else it }.sumOf { it.monto }
            userPreferences.guardarTotal(nuevoTotal)
        }
    }

    // DELETE: Elimina un gasto específico
    fun eliminarGasto(gasto: Gasto) {
        viewModelScope.launch {
            gastoDao.eliminarGasto(gasto)
            val nuevoTotal = (listaGastos.value.sumOf { it.monto } - gasto.monto).coerceAtLeast(0.0)
            userPreferences.guardarTotal(nuevoTotal)
        }
    }

    // DELETE: Elimina todos los registros
    fun eliminarTodos() {
        viewModelScope.launch {
            gastoDao.eliminarTodos()
            userPreferences.guardarTotal(0.0)
        }
    }
}
