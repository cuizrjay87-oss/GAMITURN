package com.example.ui.components

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.DesignServices
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Speaker
import androidx.compose.material.icons.rounded.Usb
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ui.theme.GamiturnTheme
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.SteelAccent

/**
 * Signature GAMITURN layered navy & cobalt curved wave background inspired directly
 * by the Gamiturn presentation slide deck, adapted for crisp edge-to-edge mobile UI.
 */
@Composable
fun GamiturnWaveBackground(
    modifier: Modifier = Modifier,
    showWatermark: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val palette = GamiturnTheme.palette
    val bgColor = MaterialTheme.colorScheme.background

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Top-left & top-right sweeping layered cobalt/navy waves (inspired by slides 2, 5, 6)
            val topCobalt = Path().apply {
                moveTo(0f, 0f)
                lineTo(w * 0.62f, 0f)
                cubicTo(
                    w * 0.42f, h * 0.08f,
                    w * 0.16f, h * 0.11f,
                    0f, h * 0.19f
                )
                close()
            }
            drawPath(path = topCobalt, color = palette.waveSecondary.copy(alpha = 0.88f))

            val topNavy = Path().apply {
                moveTo(0f, 0f)
                lineTo(w * 0.44f, 0f)
                cubicTo(
                    w * 0.28f, h * 0.06f,
                    w * 0.11f, h * 0.09f,
                    0f, h * 0.15f
                )
                close()
            }
            drawPath(path = topNavy, color = palette.wavePrimary)

            // Top-right subtle swoop accent
            val topRightSwoop = Path().apply {
                moveTo(w * 0.72f, 0f)
                lineTo(w, 0f)
                lineTo(w, h * 0.11f)
                cubicTo(
                    w * 0.88f, h * 0.08f,
                    w * 0.78f, h * 0.04f,
                    w * 0.72f, 0f
                )
                close()
            }
            drawPath(path = topRightSwoop, color = palette.wavePrimary.copy(alpha = 0.92f))

            // Bottom sweeping layered waves (inspired by slides 3, 5, 6, 11)
            val bottomCobalt = Path().apply {
                moveTo(0f, h * 0.88f)
                cubicTo(
                    w * 0.28f, h * 0.94f,
                    w * 0.66f, h * 0.89f,
                    w, h * 0.78f
                )
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(path = bottomCobalt, color = palette.waveSecondary.copy(alpha = 0.90f))

            val bottomNavy = Path().apply {
                moveTo(0f, h * 0.92f)
                cubicTo(
                    w * 0.34f, h * 0.97f,
                    w * 0.72f, h * 0.91f,
                    w, h * 0.83f
                )
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(path = bottomNavy, color = palette.wavePrimary)

            // Optional subtle centered Gamiturn emblem watermark (inspired by slide 11)
            if (showWatermark) {
                val center = Offset(w * 0.5f, h * 0.50f)
                val ringRadius = minOf(w, h) * 0.24f
                drawArc(
                    color = palette.watermarkTint,
                    startAngle = 195f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(center.x - ringRadius, center.y - ringRadius),
                    size = Size(ringRadius * 2, ringRadius * 2),
                    style = Stroke(width = ringRadius * 0.18f, cap = StrokeCap.Round)
                )
                drawArc(
                    color = palette.watermarkTint,
                    startAngle = 15f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(center.x - ringRadius, center.y - ringRadius),
                    size = Size(ringRadius * 2, ringRadius * 2),
                    style = Stroke(width = ringRadius * 0.18f, cap = StrokeCap.Round)
                )
                val boxSize = ringRadius * 0.48f
                drawRoundRect(
                    color = palette.watermarkTint,
                    topLeft = Offset(center.x - boxSize / 2f, center.y - boxSize / 2f),
                    size = Size(boxSize, boxSize),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
                )
            }
        }

        content()
    }
}

/**
 * Custom Gamiturn brand emblem matching Page 1 of the PDF:
 * Rounded navy badge with circular sharing arrows & shared package.
 */
@Composable
fun GamiturnLogoBadge(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    Surface(
        modifier = modifier.size(size),
        shape = RoundedCornerShape(size * 0.26f),
        color = NavyDeep,
        shadowElevation = 3.dp
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(size * 0.16f)) {
            val w = this.size.width
            val h = this.size.height
            val stroke = w * 0.12f

            // Top circular arrow arc
            drawArc(
                color = SteelAccent,
                startAngle = 195f,
                sweepAngle = 135f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            // Bottom circular arrow arc
            drawArc(
                color = SteelAccent,
                startAngle = 15f,
                sweepAngle = 135f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            // Center shared package box
            val boxW = w * 0.34f
            val boxH = h * 0.32f
            drawRoundRect(
                color = Color.White,
                topLeft = Offset((w - boxW) / 2f, (h - boxH) / 2f),
                size = Size(boxW, boxH),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
            )
        }
    }
}

fun itemIconForType(iconType: String): ImageVector {
    return when (iconType) {
        "calculator" -> Icons.Rounded.Calculate
        "speaker" -> Icons.Rounded.Speaker
        "projector" -> Icons.Rounded.Videocam
        "ring_light" -> Icons.Rounded.Lightbulb
        "hub" -> Icons.Rounded.Usb
        "power_bank" -> Icons.Rounded.BatteryChargingFull
        "tablet" -> Icons.Rounded.Brush
        "lab_coat" -> Icons.Rounded.Checkroom
        "compass" -> Icons.Rounded.DesignServices
        "headphones" -> Icons.Rounded.Headphones
        else -> Icons.Rounded.Calculate
    }
}

@Composable
fun ItemThumbnail(
    iconType: String,
    photoUri: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
    iconSize: Dp = 28.dp
) {
    val containerBg = MaterialTheme.colorScheme.secondaryContainer
    val tint = MaterialTheme.colorScheme.onSecondaryContainer

    Surface(
        modifier = modifier.size(size),
        shape = RoundedCornerShape(14.dp),
        color = containerBg
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (!photoUri.isNullOrBlank()) {
                AsyncImage(
                    model = Uri.parse(photoUri),
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(14.dp))
                )
            } else {
                Icon(
                    imageVector = itemIconForType(iconType),
                    contentDescription = contentDescription,
                    tint = tint,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}

@Composable
fun StatusPill(
    label: String,
    modifier: Modifier = Modifier
) {
    val palette = GamiturnTheme.palette
    val (bg, fg) = when (label.lowercase()) {
        "available" -> palette.availableBg to palette.availableText
        "borrowed" -> palette.purpleBorrowedBg to palette.purpleBorrowedText
        "pending" -> palette.pendingBg to palette.pendingText
        "approved" -> palette.approvedBg to palette.approvedText
        "returned" -> palette.availableBg to palette.availableText
        "rejected" -> palette.rejectedBg to palette.rejectedText
        else -> palette.borrowedBg to palette.borrowedText
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = bg
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = fg,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun GamiturnCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val palette = GamiturnTheme.palette
    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(18.dp),
            color = palette.cardFrost,
            border = BorderStroke(1.dp, palette.cardBorder),
            shadowElevation = 2.dp
        ) {
            content()
        }
    } else {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(18.dp),
            color = palette.cardFrost,
            border = BorderStroke(1.dp, palette.cardBorder),
            shadowElevation = 2.dp
        ) {
            content()
        }
    }
}
