package ec.edu.monster.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF9BBBE3),
    onPrimary = Color(0xFF071426),
    secondary = Color(0xFFB8C8DC),
    background = Color(0xFF101923),
    surface = Color(0xFF172432),
    onSurface = Color(0xFFE7EDF5)
)

private val LightColorScheme = lightColorScheme(
    primary = EurekaBankSOAPJava.primary,
    onPrimary = EurekaBankSOAPJava.onPrimary,
    secondary = EurekaBankSOAPJava.secondary,
    background = EurekaBankSOAPJava.background,
    surface = EurekaBankSOAPJava.surface,
    onSurface = Color(0xFF182230)
)

@Composable
fun CLIMOV_EurekaBank_SOAPJAVA_GR09Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
