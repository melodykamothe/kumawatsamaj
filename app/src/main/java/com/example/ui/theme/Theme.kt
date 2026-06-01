package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = RajasthanGold,
    secondary = RajasthanSand,
    tertiary = RajasthanAccent,
    background = RajasthanDarkText,
    surface = RajasthanDarkText,
    onPrimary = RajasthanDarkText,
    onSecondary = RajasthanDarkText,
    onTertiary = RajasthanDarkText,
    onBackground = RajasthanCream,
    onSurface = RajasthanCream
  )

private val LightColorScheme =
  lightColorScheme(
    primary = RajasthanOchre,
    secondary = RajasthanGold,
    tertiary = RajasthanSepia,
    background = RajasthanCream,
    surface = RajasthanSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = RajasthanDarkText,
    onSurface = RajasthanDarkText
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disabling dynamic colors by default so our traditional Rajasthani theme is preserved
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
