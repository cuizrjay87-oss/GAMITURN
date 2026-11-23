package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Immutable
data class GamiturnPalette(
    val wavePrimary: Color,
    val waveSecondary: Color,
    val watermarkTint: Color,
    val cardFrost: Color,
    val cardBorder: Color,
    val navyBrand: Color,
    val indigoAction: Color,
    val indigoSoft: Color,
    val availableBg: Color,
    val availableText: Color,
    val borrowedBg: Color,
    val borrowedText: Color,
    val pendingBg: Color,
    val pendingText: Color,
    val approvedBg: Color,
    val approvedText: Color,
    val rejectedBg: Color,
    val rejectedText: Color,
    val purpleBorrowedBg: Color,
    val purpleBorrowedText: Color,
    val textMuted: Color
)

private val LightGamiturnPalette = GamiturnPalette(
    wavePrimary = NavyDeep,
    waveSecondary = CobaltWave,
    watermarkTint = NavyDeep.copy(alpha = 0.045f),
    cardFrost = CardFrost,
    cardBorder = OutlineSubtle,
    navyBrand = NavyPrimary,
    indigoAction = IndigoAction,
    indigoSoft = IndigoSoft,
    availableBg = AvailableBg,
    availableText = AvailableText,
    borrowedBg = BorrowedBg,
    borrowedText = BorrowedText,
    pendingBg = PendingBg,
    pendingText = PendingText,
    approvedBg = ApprovedBg,
    approvedText = ApprovedText,
    rejectedBg = RejectedBg,
    rejectedText = RejectedText,
    purpleBorrowedBg = PurpleBorrowedBg,
    purpleBorrowedText = PurpleBorrowedText,
    textMuted = TextMuted
)

private val DarkGamiturnPalette = GamiturnPalette(
    wavePrimary = DarkWavePrimary,
    waveSecondary = DarkWaveSecondary,
    watermarkTint = Color.White.copy(alpha = 0.04f),
    cardFrost = DarkSurface.copy(alpha = 0.94f),
    cardBorder = Color(0xFF284166),
    navyBrand = Color(0xFF7CB2EE),
    indigoAction = Color(0xFF6366F1),
    indigoSoft = Color(0xFF1E293B),
    availableBg = Color(0xFF064E3B),
    availableText = Color(0xFF6EE7B7),
    borrowedBg = Color(0xFF1E293B),
    borrowedText = Color(0xFFCBD5E1),
    pendingBg = Color(0xFF451A03),
    pendingText = Color(0xFFFCD34D),
    approvedBg = Color(0xFF1E3A8A),
    approvedText = Color(0xFF93C5FD),
    rejectedBg = Color(0xFF7F1D1D),
    rejectedText = Color(0xFFFCA5A5),
    purpleBorrowedBg = Color(0xFF3B0764),
    purpleBorrowedText = Color(0xFFD8B4FE),
    textMuted = DarkTextMuted
)

val LocalGamiturnPalette = staticCompositionLocalOf { LightGamiturnPalette }

private val LightColorScheme = lightColorScheme(
    primary = IndigoAction,
    onPrimary = Color.White,
    primaryContainer = IndigoSoft,
    onPrimaryContainer = NavyDeep,
    secondary = NavyDeep,
    onSecondary = Color.White,
    secondaryContainer = SoftIceBlue,
    onSecondaryContainer = NavyDeep,
    tertiary = CobaltWave,
    onTertiary = Color.White,
    background = CanvasWhite,
    onBackground = TextPrimary,
    surface = SurfaceMist,
    onSurface = TextPrimary,
    surfaceVariant = SoftIceBlue,
    onSurfaceVariant = TextSecondary,
    outline = OutlineSubtle,
    error = RejectedText,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6366F1),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E293B),
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = Color(0xFF60A5FA),
    onSecondary = DarkCanvas,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = DarkOnSurface,
    tertiary = DarkWaveSecondary,
    onTertiary = Color.White,
    background = DarkCanvas,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextMuted,
    outline = Color(0xFF284166),
    error = Color(0xFFFCA5A5),
    onError = Color(0xFF450A0A)
)

val GamiturnShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

object GamiturnTheme {
    val palette: GamiturnPalette
        @Composable
        get() = LocalGamiturnPalette.current
}

@Composable
fun GamiturnTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val palette = if (darkTheme) DarkGamiturnPalette else LightGamiturnPalette

    CompositionLocalProvider(LocalGamiturnPalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = GamiturnTypography,
            shapes = GamiturnShapes,
            content = content
        )
    }
}
