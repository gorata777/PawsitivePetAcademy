package com.pawsitive.petacademy

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Forest = Color(0xFF1B4D43)
val Cream = Color(0xFFF5F2E4)
val Sage = Color(0xFFBFD8B8)
val Muted = Color(0xFF4F6F66)
val Line = Color(0xFFD5D9CC)

@Composable
fun PawTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Forest, onPrimary = Color.White,
            background = Cream, surface = Color.White, onSurface = Forest
        ),
        content = content
    )
}
