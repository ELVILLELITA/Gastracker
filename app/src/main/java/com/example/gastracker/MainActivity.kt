package com.example.gastracker

import android.app.Application
import android.os.Bundle
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gastracker.ui.theme.GastrackerTheme
import kotlinx.coroutines.launch

val CATEGORIAS_GASTO = listOf("Gasolina", "Alimentacion", "Accesorios", "Parqueo")

enum class NavigationScreen {
    HISTORIAL,
    DASHBOARD,
    CONFIGURACIONES
}

enum class SettingsSubModule {
    TEMAS,
    MONEDA_DATOS,
    IDIOMA
}

fun traducirCategoria(categoria: String, language: AppLanguage): String {
    if (language == AppLanguage.SPANISH) return categoria
    return when (categoria.lowercase()) {
        "gasolina" -> "Gasoline"
        "alimentacion", "alimentación" -> "Food & Dining"
        "accesorios" -> "Accessories"
        "parqueo" -> "Parking"
        else -> categoria
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val viewModel = remember {
                GastoViewModel(context.applicationContext as Application)
            }
            val themeMode by viewModel.themeMode.collectAsState()
            val appColorTheme by viewModel.appColorTheme.collectAsState()
            val appLanguage by viewModel.appLanguage.collectAsState()
            val loggedUser by viewModel.loggedUser.collectAsState()

            val darkTheme = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            GastrackerTheme(darkTheme = darkTheme, appColorTheme = appColorTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (loggedUser == null) {
                        LoginScreen(
                            appLanguage = appLanguage,
                            onLogin = { u, p -> viewModel.login(u, p) },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        GastoScreen(
                            viewModel = viewModel,
                            loggedUser = loggedUser ?: "admin",
                            onLogout = { viewModel.logout() },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    appLanguage: AppLanguage = AppLanguage.SPANISH,
    onLogin: (String, String) -> Boolean,
    modifier: Modifier = Modifier
) {
    var usuarioText by remember { mutableStateOf("") }
    var contraseniaText by remember { mutableStateOf("") }
    var errorMensaje by remember { mutableStateOf<String?>(null) }
    val isEn = appLanguage == AppLanguage.ENGLISH

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isEn) "Sign In" else "Iniciar Sesión",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (isEn) "Gastracker Profile Management" else "Gestión de Perfiles Gastracker",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = usuarioText,
                    onValueChange = {
                        usuarioText = it
                        errorMensaje = null
                    },
                    label = { Text(if (isEn) "Username (admin / usuario)" else "Usuario (admin / usuario)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = contraseniaText,
                    onValueChange = {
                        contraseniaText = it
                        errorMensaje = null
                    },
                    label = { Text(if (isEn) "Password (123)" else "Contraseña (123)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                errorMensaje?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Button(
                    onClick = {
                        if (usuarioText.isBlank() || contraseniaText.isBlank()) {
                            errorMensaje = if (isEn) "Enter username and password" else "Ingresa usuario y contraseña"
                            return@Button
                        }
                        val exitoso = onLogin(usuarioText, contraseniaText)
                        if (!exitoso) {
                            errorMensaje = if (isEn) "Invalid credentials (User or Password)" else "Credenciales incorrectas (Usuario o Contraseña)"
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isEn) "Sign In" else "Ingresar")
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                Text(
                    text = if (isEn) "Quick Test Access:" else "Acceso Rápido de Prueba:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            usuarioText = "admin"
                            contraseniaText = "123"
                            onLogin("admin", "123")
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Admin", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            usuarioText = "usuario"
                            contraseniaText = "123"
                            onLogin("usuario", "123")
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isEn) "User" else "Usuario", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastoScreen(
    modifier: Modifier = Modifier,
    viewModel: GastoViewModel? = null,
    loggedUser: String = "admin",
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val actualViewModel = viewModel ?: if (isPreview) {
        null
    } else {
        remember {
            (context.applicationContext as? Application)?.let { GastoViewModel(it) }
        }
    }

    val listaGastos by actualViewModel?.listaGastos?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val totalGastado by actualViewModel?.totalGastado?.collectAsState() ?: remember { mutableStateOf(0.0) }
    val themeMode by actualViewModel?.themeMode?.collectAsState() ?: remember { mutableStateOf(ThemeMode.SYSTEM) }
    val appColorTheme by actualViewModel?.appColorTheme?.collectAsState() ?: remember { mutableStateOf(AppColorTheme.DEFAULT) }
    val simboloMoneda by actualViewModel?.simboloMoneda?.collectAsState() ?: remember { mutableStateOf("$") }
    val appLanguage by actualViewModel?.appLanguage?.collectAsState() ?: remember { mutableStateOf(AppLanguage.SPANISH) }
    val isEn = appLanguage == AppLanguage.ENGLISH

    val isDarkSystem = isSystemInDarkTheme()
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isDarkSystem
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    // Navigation State
    var currentScreen by remember { mutableStateOf(NavigationScreen.HISTORIAL) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Dialog states
    var mostrarCrearGastoDialog by remember { mutableStateOf(false) }
    var gastoAEditar by remember { mutableStateOf<Gasto?>(null) }
    var mostrarConfirmarBorrarTodo by remember { mutableStateOf(false) }

    GastrackerTheme(darkTheme = darkTheme, appColorTheme = appColorTheme) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = loggedUser.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                SuggestionChip(
                                    onClick = {},
                                    label = {
                                        Text(
                                            text = if (loggedUser.lowercase() == "admin") {
                                                if (isEn) "ADMINISTRATOR" else "ADMINISTRADOR"
                                            } else {
                                                if (isEn) "USER" else "USUARIO"
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                )
                            }
                        }
                    }

                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    NavigationDrawerItem(
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                        label = { Text(if (isEn) "Expense History" else "Historial de Gastos") },
                        selected = currentScreen == NavigationScreen.HISTORIAL,
                        onClick = {
                            currentScreen = NavigationScreen.HISTORIAL
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                        label = { Text(if (isEn) "Category Dashboard" else "Dashboard por Categoría") },
                        selected = currentScreen == NavigationScreen.DASHBOARD,
                        onClick = {
                            currentScreen = NavigationScreen.DASHBOARD
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        label = { Text(if (isEn) "Settings" else "Configuraciones") },
                        selected = currentScreen == NavigationScreen.CONFIGURACIONES,
                        onClick = {
                            currentScreen = NavigationScreen.CONFIGURACIONES
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    Spacer(modifier = Modifier.weight(1f))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    NavigationDrawerItem(
                        icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                        label = { Text(if (isEn) "Log Out" else "Cerrar Sesión", color = MaterialTheme.colorScheme.error) },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                onLogout()
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        ) {
            Scaffold(
                modifier = modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                floatingActionButton = {
                    if (currentScreen == NavigationScreen.HISTORIAL) {
                        FloatingActionButton(
                            onClick = { mostrarCrearGastoDialog = true },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = if (isEn) "Add new expense" else "Agregar nuevo gasto"
                            )
                        }
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Top Header Row with Menu Icon
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = if (isEn) "Open navigation menu" else "Abrir menú de navegación"
                            )
                        }
                        Text(
                            text = when (currentScreen) {
                                NavigationScreen.HISTORIAL -> if (isEn) "Expense Log" else "Registro de Gastos"
                                NavigationScreen.DASHBOARD -> if (isEn) "Expense Dashboard" else "Dashboard de Gastos"
                                NavigationScreen.CONFIGURACIONES -> if (isEn) "Settings" else "Configuraciones"
                            },
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    when (currentScreen) {
                        NavigationScreen.HISTORIAL -> {
                            HistorialView(
                                listaGastos = listaGastos,
                                totalGastado = totalGastado,
                                simboloMoneda = simboloMoneda,
                                appLanguage = appLanguage,
                                onBorrarTodoClick = { mostrarConfirmarBorrarTodo = true },
                                onEditClick = { gasto -> gastoAEditar = gasto },
                                onDeleteClick = { gasto -> actualViewModel?.eliminarGasto(gasto) }
                            )
                        }
                        NavigationScreen.DASHBOARD -> {
                            DashboardView(
                                listaGastos = listaGastos,
                                totalGastado = totalGastado,
                                simboloMoneda = simboloMoneda,
                                appLanguage = appLanguage
                            )
                        }
                        NavigationScreen.CONFIGURACIONES -> {
                            ConfiguracionesView(
                                themeMode = themeMode,
                                appColorTheme = appColorTheme,
                                simboloMoneda = simboloMoneda,
                                appLanguage = appLanguage,
                                onThemeModeChange = { actualViewModel?.setThemeMode(it) },
                                onColorThemeChange = { actualViewModel?.setAppColorTheme(it) },
                                onSimboloMonedaChange = { actualViewModel?.setSimboloMoneda(it) },
                                onLanguageChange = { actualViewModel?.setAppLanguage(it) },
                                onBorrarTodoClick = { mostrarConfirmarBorrarTodo = true },
                                listaGastos = listaGastos,
                                totalGastado = totalGastado
                            )
                        }
                    }
                }
            }
        }
    }

    // CREATE Dialog
    if (mostrarCrearGastoDialog) {
        CrearGastoDialog(
            appLanguage = appLanguage,
            onDismiss = { mostrarCrearGastoDialog = false },
            onConfirm = { monto, tienda1, tienda2, etiqueta, categoria ->
                actualViewModel?.insertarGasto(monto, tienda1, tienda2, etiqueta, categoria)
                mostrarCrearGastoDialog = false
                Toast.makeText(
                    context,
                    if (isEn) "Expense saved" else "Gasto guardado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    // UPDATE Dialog
    gastoAEditar?.let { gasto ->
        EditarGastoDialog(
            gasto = gasto,
            appLanguage = appLanguage,
            onDismiss = { gastoAEditar = null },
            onConfirm = { gastoActualizado ->
                actualViewModel?.actualizarGasto(gastoActualizado)
                gastoAEditar = null
                Toast.makeText(
                    context,
                    if (isEn) "Expense updated" else "Gasto actualizado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    // DELETE ALL Confirmation Dialog
    if (mostrarConfirmarBorrarTodo) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmarBorrarTodo = false },
            title = { Text(if (isEn) "Delete all expenses?" else "¿Eliminar todos los gastos?") },
            text = { Text(if (isEn) "This action cannot be undone." else "Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        actualViewModel?.eliminarTodos()
                        mostrarConfirmarBorrarTodo = false
                        Toast.makeText(
                            context,
                            if (isEn) "All expenses deleted" else "Todos los gastos han sido eliminados",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    Text(
                        if (isEn) "Delete All" else "Eliminar todo",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmarBorrarTodo = false }) {
                    Text(if (isEn) "Cancel" else "Cancelar")
                }
            }
        )
    }
}

@Composable
fun HistorialView(
    listaGastos: List<Gasto>,
    totalGastado: Double,
    simboloMoneda: String,
    appLanguage: AppLanguage = AppLanguage.SPANISH,
    onBorrarTodoClick: () -> Unit,
    onEditClick: (Gasto) -> Unit,
    onDeleteClick: (Gasto) -> Unit
) {
    val isEn = appLanguage == AppLanguage.ENGLISH

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isEn) "Total Spent" else "Total Gastado",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = "$simboloMoneda${String.format(Locale.getDefault(), "%.2f", totalGastado)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                if (listaGastos.isNotEmpty()) {
                    OutlinedButton(onClick = onBorrarTodoClick) {
                        Text(if (isEn) "Clear All" else "Borrar Todo")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isEn) "Expense History (${listaGastos.size})" else "Historial de Gastos (${listaGastos.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (listaGastos.isEmpty()) {
            Text(
                text = if (isEn) "No expenses recorded yet." else "No hay gastos registrados aún.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = listaGastos,
                    key = { it.id }
                ) { gasto ->
                    GastoItemCard(
                        gasto = gasto,
                        simboloMoneda = simboloMoneda,
                        appLanguage = appLanguage,
                        onEditClick = { onEditClick(gasto) },
                        onDeleteClick = { onDeleteClick(gasto) }
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardView(
    listaGastos: List<Gasto>,
    totalGastado: Double,
    simboloMoneda: String,
    appLanguage: AppLanguage = AppLanguage.SPANISH
) {
    val isEn = appLanguage == AppLanguage.ENGLISH
    val desgloses = remember(listaGastos, totalGastado) {
        CATEGORIAS_GASTO.map { cat ->
            val items = listaGastos.filter { it.categoria.equals(cat, ignoreCase = true) }
            val subtotal = items.sumOf { it.monto }
            val porcentaje = if (totalGastado > 0) (subtotal / totalGastado).toFloat() else 0f
            Triple(cat, subtotal, porcentaje to items.size)
        }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isEn) "Expenses Summary" else "Resumen de Gastos",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$simboloMoneda${String.format(Locale.getDefault(), "%.2f", totalGastado)}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = if (isEn) "${listaGastos.size} records stored" else "${listaGastos.size} registros almacenados",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        item {
            Text(
                text = if (isEn) "Breakdown by Category" else "Desglose por Categoría",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(desgloses) { (categoria, subtotal, pairData) ->
            val (porcentaje, cantidad) = pairData
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = traducirCategoria(categoria, appLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$simboloMoneda${String.format(Locale.getDefault(), "%.2f", subtotal)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { porcentaje.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isEn) "${(porcentaje * 100).toInt()}% of total" else "${(porcentaje * 100).toInt()}% del total",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (isEn) "$cantidad expenses" else "$cantidad gastos",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConfiguracionesView(
    themeMode: ThemeMode,
    appColorTheme: AppColorTheme,
    simboloMoneda: String,
    appLanguage: AppLanguage = AppLanguage.SPANISH,
    onThemeModeChange: (ThemeMode) -> Unit,
    onColorThemeChange: (AppColorTheme) -> Unit,
    onSimboloMonedaChange: (String) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onBorrarTodoClick: () -> Unit,
    listaGastos: List<Gasto>,
    totalGastado: Double
) {
    var selectedSubModule by remember { mutableStateOf<SettingsSubModule?>(null) }
    val context = LocalContext.current
    val isEn = appLanguage == AppLanguage.ENGLISH

    if (selectedSubModule == null) {
        // Menú Principal de Submódulos de Configuración
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (isEn) "System Settings" else "Ajustes del Sistema",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Submódulo 1: Apariencia y Temas
            Card(
                onClick = { selectedSubModule = SettingsSubModule.TEMAS },
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        Column {
                            Text(
                                text = if (isEn) "Appearance & Themes" else "Apariencia y Temas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isEn) "Light/dark mode and color themes." else "Modo claro/oscuro y paleta de colores de la app.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Submódulo 2: Moneda y Gestión de Datos
            Card(
                onClick = { selectedSubModule = SettingsSubModule.MONEDA_DATOS },
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AttachMoney,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                        Column {
                            Text(
                                text = if (isEn) "Currency & Data" else "Moneda y Gestión de Datos",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isEn) "Currency symbol, report export, and data reset." else "Símbolo de divisa ($ / € / £ / S/), reporte y reinicio de datos.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Submódulo 3: Idioma / Language
            Card(
                onClick = { selectedSubModule = SettingsSubModule.IDIOMA },
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                        Column {
                            Text(
                                text = if (isEn) "Language" else "Idioma / Language",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isEn) "Select application language (Spanish / English)." else "Selecciona el idioma de la aplicación (Español / English).",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    } else {
        // Vista Detallada de Submódulo Seleccionado
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header del Submódulo con botón para regresar al menú principal de ajustes
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { selectedSubModule = null }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = if (isEn) "Back to settings options" else "Volver a opciones de configuración"
                    )
                }
                Text(
                    text = when (selectedSubModule) {
                        SettingsSubModule.TEMAS -> if (isEn) "Appearance & Themes" else "Apariencia y Temas"
                        SettingsSubModule.MONEDA_DATOS -> if (isEn) "Currency & Data" else "Moneda y Datos"
                        SettingsSubModule.IDIOMA -> if (isEn) "Language" else "Idioma / Language"
                        null -> ""
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            when (selectedSubModule) {
                SettingsSubModule.TEMAS -> {
                    // Contenido del Submódulo TEMAS
                    // 1. Modo de Tema
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (isEn) "Theme Mode" else "Modo de Tema",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = if (isEn) "Choose light, dark, or system default appearance." else "Selecciona si prefieres la apariencia clara, oscura o ajustada al sistema.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FilterChip(
                                    selected = themeMode == ThemeMode.SYSTEM,
                                    onClick = { onThemeModeChange(ThemeMode.SYSTEM) },
                                    label = { Text(if (isEn) "System" else "Sistema") }
                                )

                                FilterChip(
                                    selected = themeMode == ThemeMode.LIGHT,
                                    onClick = { onThemeModeChange(ThemeMode.LIGHT) },
                                    label = { Text(if (isEn) "Light" else "Claro") }
                                )

                                FilterChip(
                                    selected = themeMode == ThemeMode.DARK,
                                    onClick = { onThemeModeChange(ThemeMode.DARK) },
                                    label = { Text(if (isEn) "Dark" else "Oscuro") }
                                )
                            }
                        }
                    }

                    // 2. Paleta de Colores
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (isEn) "App Color Palette" else "Paleta de Colores de la App",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = if (isEn) "Customize main color theme. Tones and background adapt automatically." else "Personaliza el color principal. Los tonos y el fondo se adaptarán automáticamente.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            val opcionesColor = listOf(
                                AppColorTheme.DEFAULT to Pair(if (isEn) "Original (Purple)" else "Original (Morado)", Color(0xFF6750A4)),
                                AppColorTheme.BLUE to Pair(if (isEn) "Blue" else "Azul", Color(0xFF1976D2)),
                                AppColorTheme.RED to Pair(if (isEn) "Red" else "Rojo", Color(0xFFC62828)),
                                AppColorTheme.GREEN to Pair(if (isEn) "Green" else "Verde", Color(0xFF2E7D32)),
                                AppColorTheme.MUSTARD to Pair(if (isEn) "Mustard" else "Mostaza", Color(0xFFD84315)),
                                AppColorTheme.YELLOW to Pair(if (isEn) "Yellow" else "Amarillo", Color(0xFFF57F17)),
                                AppColorTheme.PINK to Pair(if (isEn) "Pink" else "Rosa", Color(0xFFC2185B))
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                opcionesColor.forEach { (colorEnum, info) ->
                                    val (nombre, colorMuestra) = info
                                    Card(
                                        onClick = { onColorThemeChange(colorEnum) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (appColorTheme == colorEnum) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = colorMuestra,
                                                    modifier = Modifier.size(24.dp)
                                                ) {}
                                                Text(
                                                    text = nombre,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = if (appColorTheme == colorEnum) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }

                                            if (appColorTheme == colorEnum) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Seleccionado",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                SettingsSubModule.MONEDA_DATOS -> {
                    // Contenido del Submódulo MONEDA Y DATOS
                    // 1. Símbolo de Divisa
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (isEn) "Currency Symbol" else "Símbolo de Divisa",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isEn) "Choose currency symbol displayed across the app." else "Elige el símbolo con el que se mostrarán los montos en toda la aplicación.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            val simbolosDisponibles = listOf("$", "€", "£", "S/", "Q", "Bs.")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                simbolosDisponibles.forEach { simbolo ->
                                    FilterChip(
                                        selected = simboloMoneda == simbolo,
                                        onClick = { onSimboloMonedaChange(simbolo) },
                                        label = { Text(simbolo, fontWeight = FontWeight.Bold) }
                                    )
                                }
                            }
                        }
                    }

                    // 2. Exportar Resumen
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (isEn) "Expense Report" else "Informe de Gastos",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isEn) "Generate a summary report of all stored records." else "Genera un reporte resumido de tus registros almacenados.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    val reporteText = buildString {
                                        appendLine(if (isEn) "=== GASTRACTKER EXPENSE REPORT ===" else "=== INFORME DE GASTRACTKER ===")
                                        appendLine("${if (isEn) "Total Spent" else "Total Acumulado"}: $simboloMoneda${String.format(Locale.getDefault(), "%.2f", totalGastado)}")
                                        appendLine("${if (isEn) "Total Records" else "Registros Totales"}: ${listaGastos.size}")
                                        appendLine("-------------------------------")
                                        listaGastos.forEach { gasto ->
                                            appendLine("- [${traducirCategoria(gasto.categoria, appLanguage)}] $simboloMoneda${String.format(Locale.getDefault(), "%.2f", gasto.monto)} | ${gasto.tienda1} | ${gasto.fechaFormateada()}")
                                        }
                                    }

                                    val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    val clip = android.content.ClipData.newPlainText("Informe Gastracker", reporteText)
                                    clipboard.setPrimaryClip(clip)

                                    Toast.makeText(
                                        context,
                                        if (isEn) "Report copied to clipboard" else "Informe copiado al portapapeles",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(if (isEn) "Copy Report to Clipboard" else "Copiar Reporte al Portapapeles")
                            }
                        }
                    }

                    // 3. Reinicio de Datos
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (isEn) "Data Management" else "Administración de Datos",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isEn) "Permanently delete all stored expense records." else "Elimina de forma permanente todo el historial de gastos de la base de datos local.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = onBorrarTodoClick,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(if (isEn) "Clear All Expenses" else "Borrar Todos los Gastos", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }

                SettingsSubModule.IDIOMA -> {
                    // Contenido del Submódulo IDIOMA
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (isEn) "Application Language" else "Idioma de la Aplicación",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isEn) "Select language for the user interface." else "Selecciona el idioma con el que se mostrará la interfaz de usuario.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            val idiomasDisponibles = listOf(
                                AppLanguage.SPANISH to ("Español" to "Spanish"),
                                AppLanguage.ENGLISH to ("English" to "Inglés")
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                idiomasDisponibles.forEach { (langEnum, labels) ->
                                    val (labelEs, labelEn) = labels
                                    Card(
                                        onClick = { onLanguageChange(langEnum) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (appLanguage == langEnum) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(
                                                    text = if (langEnum == AppLanguage.SPANISH) "Español" else "English",
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = if (appLanguage == langEnum) FontWeight.Bold else FontWeight.Normal
                                                )
                                                Text(
                                                    text = if (isEn) labelEn else labelEs,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            if (appLanguage == langEnum) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaDropdown(
    categoriaSeleccionada: String,
    appLanguage: AppLanguage = AppLanguage.SPANISH,
    onCategoriaChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = traducirCategoria(categoriaSeleccionada, appLanguage),
            onValueChange = {},
            readOnly = true,
            label = { Text(if (appLanguage == AppLanguage.ENGLISH) "Category" else "Categoría") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            CATEGORIAS_GASTO.forEach { cat ->
                DropdownMenuItem(
                    text = { Text(traducirCategoria(cat, appLanguage)) },
                    onClick = {
                        onCategoriaChange(cat)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CrearGastoDialog(
    appLanguage: AppLanguage = AppLanguage.SPANISH,
    onDismiss: () -> Unit,
    onConfirm: (Double, String, String, String, String) -> Unit
) {
    var montoText by remember { mutableStateOf("") }
    var tienda1Text by remember { mutableStateOf("") }
    var tienda2Text by remember { mutableStateOf("") }
    var etiquetaText by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Gasolina") }
    val context = LocalContext.current
    val isEn = appLanguage == AppLanguage.ENGLISH

    val fechaActualTexto = remember {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        sdf.format(Date())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEn) "New Expense" else "Nuevo Gasto") },
        text = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "${if (isEn) "Entry date" else "Fecha de ingreso"}: $fechaActualTexto",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                OutlinedTextField(
                    value = montoText,
                    onValueChange = { montoText = it },
                    label = { Text(if (isEn) "Amount ($)" else "Monto ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                CategoriaDropdown(
                    categoriaSeleccionada = categoriaSeleccionada,
                    appLanguage = appLanguage,
                    onCategoriaChange = { categoriaSeleccionada = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = tienda1Text,
                    onValueChange = { tienda1Text = it },
                    label = { Text(if (isEn) "First store / stop" else "Primera tienda / parada") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = tienda2Text,
                    onValueChange = { tienda2Text = it },
                    label = { Text(if (isEn) "Second store / stop (optional)" else "Segunda tienda / parada (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = etiquetaText,
                    onValueChange = { etiquetaText = it },
                    label = { Text(if (isEn) "Label / Description (optional)" else "Etiqueta / Descripción (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val monto = montoText.toDoubleOrNull()
                    if (monto == null || monto <= 0) {
                        Toast.makeText(context, if (isEn) "Enter a valid amount" else "Ingresa un monto válido", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (tienda1Text.isBlank()) {
                        Toast.makeText(context, if (isEn) "Enter first store / stop" else "Ingresa la primera tienda/parada", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    onConfirm(monto, tienda1Text.trim(), tienda2Text.trim(), etiquetaText.trim(), categoriaSeleccionada)
                }
            ) {
                Text(if (isEn) "Save" else "Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isEn) "Cancel" else "Cancelar")
            }
        }
    )
}

@Composable
fun EditarGastoDialog(
    gasto: Gasto,
    appLanguage: AppLanguage = AppLanguage.SPANISH,
    onDismiss: () -> Unit,
    onConfirm: (Gasto) -> Unit
) {
    var montoText by remember { mutableStateOf(gasto.monto.toString()) }
    var tienda1Text by remember { mutableStateOf(gasto.tienda1) }
    var tienda2Text by remember { mutableStateOf(gasto.tienda2) }
    var etiquetaText by remember { mutableStateOf(gasto.etiqueta) }
    var categoriaSeleccionada by remember { mutableStateOf(gasto.categoria.ifBlank { "Gasolina" }) }
    val isEn = appLanguage == AppLanguage.ENGLISH

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEn) "Edit Expense" else "Editar Gasto") },
        text = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "${if (isEn) "Entry date" else "Fecha de ingreso"}: ${gasto.fechaFormateada()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OutlinedTextField(
                    value = montoText,
                    onValueChange = { montoText = it },
                    label = { Text(if (isEn) "Amount ($)" else "Monto ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                CategoriaDropdown(
                    categoriaSeleccionada = categoriaSeleccionada,
                    appLanguage = appLanguage,
                    onCategoriaChange = { categoriaSeleccionada = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = tienda1Text,
                    onValueChange = { tienda1Text = it },
                    label = { Text(if (isEn) "First store / stop" else "Primera tienda / parada") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = tienda2Text,
                    onValueChange = { tienda2Text = it },
                    label = { Text(if (isEn) "Second store / stop (optional)" else "Segunda tienda / parada (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = etiquetaText,
                    onValueChange = { etiquetaText = it },
                    label = { Text(if (isEn) "Label / Description (optional)" else "Etiqueta / Descripción (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val nuevoMonto = montoText.toDoubleOrNull() ?: gasto.monto
                    if (tienda1Text.isNotBlank()) {
                        onConfirm(
                            gasto.copy(
                                monto = nuevoMonto,
                                tienda1 = tienda1Text.trim(),
                                tienda2 = tienda2Text.trim(),
                                etiqueta = etiquetaText.trim(),
                                categoria = categoriaSeleccionada
                            )
                        )
                    }
                }
            ) {
                Text(if (isEn) "Save" else "Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isEn) "Cancel" else "Cancelar")
            }
        }
    )
}

@Composable
fun GastoItemCard(
    gasto: Gasto,
    simboloMoneda: String = "$",
    appLanguage: AppLanguage = AppLanguage.SPANISH,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isEn = appLanguage == AppLanguage.ENGLISH

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$simboloMoneda${String.format(Locale.getDefault(), "%.2f", gasto.monto)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        SuggestionChip(
                            onClick = { },
                            label = {
                                Text(
                                    text = traducirCategoria(gasto.categoria, appLanguage),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        )

                        if (gasto.etiqueta.isNotBlank()) {
                            SuggestionChip(
                                onClick = { },
                                label = {
                                    Text(
                                        text = gasto.etiqueta,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${if (isEn) "Stop 1" else "Parada 1"}: ${gasto.tienda1}",
                    style = MaterialTheme.typography.bodyMedium
                )

                if (gasto.tienda2.isNotBlank()) {
                    Text(
                        text = "${if (isEn) "Stop 2" else "Parada 2"}: ${gasto.tienda2}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Fecha de ingreso",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${if (isEn) "Entered" else "Ingresado"}: ${gasto.fechaFormateada()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar gasto",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar gasto",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GastoScreenPreview() {
    GastrackerTheme {
        GastoScreen()
    }
}
