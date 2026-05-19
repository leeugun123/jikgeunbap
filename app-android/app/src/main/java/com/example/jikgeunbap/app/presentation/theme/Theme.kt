package com.example.jikgeunbap.app.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val WarmColorScheme = lightColorScheme(
    primary             = WarmOrange,
    onPrimary           = Color.White,
    primaryContainer    = WarmContainer,
    onPrimaryContainer  = WarmBrown,
    secondary           = WarmAmber,
    onSecondary         = WarmBrown,
    secondaryContainer  = Color(0xFFFFF3CD),
    onSecondaryContainer = WarmBrown,
    background          = WarmCream,
    onBackground        = WarmBrown,
    surface             = WarmSurface,
    onSurface           = WarmBrown,
    surfaceVariant      = Color(0xFFF5E6D5),
    onSurfaceVariant    = WarmBrownMid,
    error               = WarmError,
    outline             = WarmDivider
)

@Composable
fun JikGeunBapTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = WarmColorScheme,
        typography  = Typography,
        content     = content
    )
}
