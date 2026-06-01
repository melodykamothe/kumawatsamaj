package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.ui.theme.RajasthanCream
import com.example.ui.theme.RajasthanGold
import com.example.ui.theme.RajasthanOchre
import com.example.ui.theme.RajasthanSand

@Composable
fun RajasthaniHeritageBackground(
    modifier: Modifier = Modifier,
    subtle: Boolean = false
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        if (subtle) {
            // Elegant modern minimal light blue gradient for content pages
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF3F8FC), // Soft modern light blue mist
                        Color(0xFFFFFFFF)  // Smooth fade to pure white
                    )
                ),
                size = size
            )

            // Dynamic ambient glowing orb at top right
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF3399FF).copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.9f, height * 0.15f),
                    radius = width * 0.40f
                ),
                radius = width * 0.40f,
                center = Offset(width * 0.9f, height * 0.15f)
            )

            // Soft glowing wave at bottom to anchor screen content
            val wavesPath = Path().apply {
                moveTo(0f, height * 0.85f)
                cubicTo(
                    width * 0.35f, height * 0.82f,
                    width * 0.65f, height * 0.92f,
                    width, height * 0.88f
                )
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(
                path = wavesPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF3399FF).copy(alpha = 0.04f),
                        Color(0xFF0066CC).copy(alpha = 0.02f)
                    )
                )
            )
        } else {
            // Full rich high-fidelity modern dashboard backdrop
            // Multi-stop clean sky gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFEAF5FC), // Frosted light blue
                        Color(0xFFF4F9FE), // Soft pearl blue-white
                        Color(0xFFD6EBFC)  // Clean azure bottom backdrop
                    )
                ),
                size = size
            )

            // Neon ambient glowing aura (Light effect) at center-right
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF3399FF).copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.8f, height * 0.3f),
                    radius = width * 0.70f
                ),
                radius = width * 0.70f,
                center = Offset(width * 0.8f, height * 0.3f)
            )

            // Secondary ambient glowing aura at top-left corner
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0088FF).copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.1f, height * 0.08f),
                    radius = width * 0.50f
                ),
                radius = width * 0.50f,
                center = Offset(width * 0.1f, height * 0.08f)
            )

            // Modern Fluid Wave layer 1 (Background anchor)
            val fluidPath1 = Path().apply {
                moveTo(0f, height * 0.78f)
                cubicTo(
                    width * 0.3f, height * 0.70f,
                    width * 0.7f, height * 0.88f,
                    width, height * 0.74f
                )
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(
                path = fluidPath1,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF3399FF).copy(alpha = 0.22f),
                        Color(0xFF0066CC).copy(alpha = 0.12f)
                    ),
                    start = Offset(0f, height * 0.75f),
                    end = Offset(width, height)
                )
            )

            // Modern Fluid Wave layer 2 (Middle-ground overlay)
            val fluidPath2 = Path().apply {
                moveTo(0f, height * 0.84f)
                cubicTo(
                    width * 0.4f, height * 0.90f,
                    width * 0.6f, height * 0.78f,
                    width, height * 0.85f
                )
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(
                path = fluidPath2,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFD2E4F6).copy(alpha = 0.30f),
                        Color(0xFF0088FF).copy(alpha = 0.15f)
                    ),
                    start = Offset(0f, height * 0.8f),
                    end = Offset(width, height)
                )
            )

            // Modern Fluid Wave layer 3 (Foreground accent wave)
            val fluidPath3 = Path().apply {
                moveTo(0f, height * 0.89f)
                cubicTo(
                    width * 0.35f, height * 0.85f,
                    width * 0.65f, height * 0.93f,
                    width, height * 0.90f
                )
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(
                path = fluidPath3,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFEAFCFF).copy(alpha = 0.45f),
                        Color(0xFF3399FF).copy(alpha = 0.10f)
                    ),
                    start = Offset(width * 0.2f, height * 0.85f),
                    end = Offset(width * 0.8f, height)
                )
            )
        }
    }
}
