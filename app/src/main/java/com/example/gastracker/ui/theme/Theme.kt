package com.example.gastracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.gastracker.AppColorTheme

// Default Purple/Blue
private val LightDefaultColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color(0xFFFBF8FF),
    surface = Color(0xFFF3EDF7),
    surfaceVariant = Color(0xFFE7E0EC),
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    onSurfaceVariant = Color(0xFF49454F)
)

private val DarkDefaultColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFF141218),
    surface = Color(0xFF211F26),
    surfaceVariant = Color(0xFF36343B),
    onBackground = Color(0xFFE6E0E9),
    onSurface = Color(0xFFE6E0E9),
    onSurfaceVariant = Color(0xFFCAC4D0)
)

// Blue (Azul)
private val LightBlueColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),
    secondary = Color(0xFF0288D1),
    tertiary = Color(0xFF03A9F4),
    primaryContainer = Color(0xFFBBDEFB),
    onPrimaryContainer = Color(0xFF001D33),
    secondaryContainer = Color(0xFFE1F5FE),
    onSecondaryContainer = Color(0xFF002A3A),
    background = Color(0xFFF4F9FF),
    surface = Color(0xFFE3F2FD),
    surfaceVariant = Color(0xFFBBDEFB),
    onBackground = Color(0xFF001D33),
    onSurface = Color(0xFF001D33),
    onSurfaceVariant = Color(0xFF003355)
)

private val DarkBlueColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    secondary = Color(0xFF81D4FA),
    tertiary = Color(0xFF4FC3F7),
    primaryContainer = Color(0xFF0D47A1),
    onPrimaryContainer = Color(0xFF90CAF9),
    secondaryContainer = Color(0xFF01579B),
    onSecondaryContainer = Color(0xFFB3E5FC),
    background = Color(0xFF0A192F),
    surface = Color(0xFF112240),
    surfaceVariant = Color(0xFF1D3557),
    onBackground = Color(0xFFE1F5FE),
    onSurface = Color(0xFFE1F5FE),
    onSurfaceVariant = Color(0xFFB3E5FC)
)

// Red (Rojo)
private val LightRedColorScheme = lightColorScheme(
    primary = Color(0xFFC62828),
    secondary = Color(0xFFD32F2F),
    tertiary = Color(0xFFEF5350),
    primaryContainer = Color(0xFFFFCDD2),
    onPrimaryContainer = Color(0xFF5F0000),
    secondaryContainer = Color(0xFFFFEBEE),
    onSecondaryContainer = Color(0xFF8C0000),
    background = Color(0xFFFFF8F8),
    surface = Color(0xFFFFECEC),
    surfaceVariant = Color(0xFFFFDADA),
    onBackground = Color(0xFF3B0808),
    onSurface = Color(0xFF3B0808),
    onSurfaceVariant = Color(0xFF531E1E)
)

private val DarkRedColorScheme = darkColorScheme(
    primary = Color(0xFFFF8A80),
    secondary = Color(0xFFFF5252),
    tertiary = Color(0xFFFF1744),
    primaryContainer = Color(0xFFB71C1C),
    onPrimaryContainer = Color(0xFFFF8A80),
    secondaryContainer = Color(0xFF8C0000),
    onSecondaryContainer = Color(0xFFFFCDD2),
    background = Color(0xFF1E0A0A),
    surface = Color(0xFF2B0F0F),
    surfaceVariant = Color(0xFF3F1717),
    onBackground = Color(0xFFFFDAD6),
    onSurface = Color(0xFFFFDAD6),
    onSurfaceVariant = Color(0xFFFFB4AB)
)

// Green (Verde)
private val LightGreenColorScheme = lightColorScheme(
    primary = Color(0xFF2E7D32),
    secondary = Color(0xFF388E3C),
    tertiary = Color(0xFF66BB6A),
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF002200),
    secondaryContainer = Color(0xFFE8F5E9),
    onSecondaryContainer = Color(0xFF003300),
    background = Color(0xFFF6FBF6),
    surface = Color(0xFFE8F5E9),
    surfaceVariant = Color(0xFFC8E6C9),
    onBackground = Color(0xFF0D280D),
    onSurface = Color(0xFF0D280D),
    onSurfaceVariant = Color(0xFF1B4D1B)
)

private val DarkGreenColorScheme = darkColorScheme(
    primary = Color(0xFFA5D6A7),
    secondary = Color(0xFF81C784),
    tertiary = Color(0xFF4CAF50),
    primaryContainer = Color(0xFF1B5E20),
    onPrimaryContainer = Color(0xFFA5D6A7),
    secondaryContainer = Color(0xFF2E7D32),
    onSecondaryContainer = Color(0xFFC8E6C9),
    background = Color(0xFF0A1E0D),
    surface = Color(0xFF122E16),
    surfaceVariant = Color(0xFF1C4221),
    onBackground = Color(0xFFE2F3E2),
    onSurface = Color(0xFFE2F3E2),
    onSurfaceVariant = Color(0xFFC8E6C9)
)

// Mustard (Mostaza)
private val LightMustardColorScheme = lightColorScheme(
    primary = Color(0xFFD84315),
    secondary = Color(0xFFE65100),
    tertiary = Color(0xFFFF8F00),
    primaryContainer = Color(0xFFFFD54F),
    onPrimaryContainer = Color(0xFF3E1D00),
    secondaryContainer = Color(0xFFFFECB3),
    onSecondaryContainer = Color(0xFF4E2600),
    background = Color(0xFFFFFDF5),
    surface = Color(0xFFFFF8E1),
    surfaceVariant = Color(0xFFFFECB3),
    onBackground = Color(0xFF2C1A00),
    onSurface = Color(0xFF2C1A00),
    onSurfaceVariant = Color(0xFF5D3E00)
)

private val DarkMustardColorScheme = darkColorScheme(
    primary = Color(0xFFFFD54F),
    secondary = Color(0xFFFFC107),
    tertiary = Color(0xFFFFB300),
    primaryContainer = Color(0xFF7F4000),
    onPrimaryContainer = Color(0xFFFFECB3),
    secondaryContainer = Color(0xFF522900),
    onSecondaryContainer = Color(0xFFFFE082),
    background = Color(0xFF221706),
    surface = Color(0xFF33230B),
    surfaceVariant = Color(0xFF473211),
    onBackground = Color(0xFFFFF1D0),
    onSurface = Color(0xFFFFF1D0),
    onSurfaceVariant = Color(0xFFFFECB3)
)

// Yellow (Amarillo)
private val LightYellowColorScheme = lightColorScheme(
    primary = Color(0xFFF57F17),
    secondary = Color(0xFFFBC02D),
    tertiary = Color(0xFFFFF176),
    primaryContainer = Color(0xFFFFF59D),
    onPrimaryContainer = Color(0xFF332000),
    secondaryContainer = Color(0xFFFFFDE7),
    onSecondaryContainer = Color(0xFF3E2C00),
    background = Color(0xFFFFFFF2),
    surface = Color(0xFFFFFDE7),
    surfaceVariant = Color(0xFFFFF9C4),
    onBackground = Color(0xFF2C2200),
    onSurface = Color(0xFF2C2200),
    onSurfaceVariant = Color(0xFF4A3B00)
)

private val DarkYellowColorScheme = darkColorScheme(
    primary = Color(0xFFFFEE58),
    secondary = Color(0xFFFFF176),
    tertiary = Color(0xFFFFD54F),
    primaryContainer = Color(0xFFF57F17),
    onPrimaryContainer = Color(0xFFFFFF8D),
    secondaryContainer = Color(0xFF827717),
    onSecondaryContainer = Color(0xFFFFF59D),
    background = Color(0xFF1E1C03),
    surface = Color(0xFF2B2807),
    surfaceVariant = Color(0xFF3B380C),
    onBackground = Color(0xFFFFFFE0),
    onSurface = Color(0xFFFFFFE0),
    onSurfaceVariant = Color(0xFFFFF59D)
)

// Pink (Rosa)
private val LightPinkColorScheme = lightColorScheme(
    primary = Color(0xFFC2185B),
    secondary = Color(0xFFD81B60),
    tertiary = Color(0xFFEC407A),
    primaryContainer = Color(0xFFF48FB1),
    onPrimaryContainer = Color(0xFF330013),
    secondaryContainer = Color(0xFFF8BBD0),
    onSecondaryContainer = Color(0xFF4A001F),
    background = Color(0xFFFFF5F8),
    surface = Color(0xFFFCE4EC),
    surfaceVariant = Color(0xFFF8BBD0),
    onBackground = Color(0xFF38081A),
    onSurface = Color(0xFF38081A),
    onSurfaceVariant = Color(0xFF5C1B33)
)

private val DarkPinkColorScheme = darkColorScheme(
    primary = Color(0xFFF48FB1),
    secondary = Color(0xFFF06292),
    tertiary = Color(0xFFE91E63),
    primaryContainer = Color(0xFF880E4F),
    onPrimaryContainer = Color(0xFFF8BBD0),
    secondaryContainer = Color(0xFF5C0030),
    onSecondaryContainer = Color(0xFFFFD9E4),
    background = Color(0xFF210812),
    surface = Color(0xFF310E1C),
    surfaceVariant = Color(0xFF46172B),
    onBackground = Color(0xFFFFD9E4),
    onSurface = Color(0xFFFFD9E4),
    onSurfaceVariant = Color(0xFFF8BBD0)
)

@Composable
fun GastrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    appColorTheme: AppColorTheme = AppColorTheme.DEFAULT,
    content: @Composable () -> Unit
) {
    val colorScheme = when (appColorTheme) {
        AppColorTheme.BLUE -> if (darkTheme) DarkBlueColorScheme else LightBlueColorScheme
        AppColorTheme.RED -> if (darkTheme) DarkRedColorScheme else LightRedColorScheme
        AppColorTheme.GREEN -> if (darkTheme) DarkGreenColorScheme else LightGreenColorScheme
        AppColorTheme.MUSTARD -> if (darkTheme) DarkMustardColorScheme else LightMustardColorScheme
        AppColorTheme.YELLOW -> if (darkTheme) DarkYellowColorScheme else LightYellowColorScheme
        AppColorTheme.PINK -> if (darkTheme) DarkPinkColorScheme else LightPinkColorScheme
        AppColorTheme.DEFAULT -> if (darkTheme) DarkDefaultColorScheme else LightDefaultColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
