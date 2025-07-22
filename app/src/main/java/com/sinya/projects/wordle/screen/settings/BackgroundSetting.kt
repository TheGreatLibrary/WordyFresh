package com.sinya.projects.wordle.screen.settings

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

enum class BackgroundType {
    SYSTEM,     // ресурсы из drawable
    CUSTOM,
    GRADIENT,
    DEFAULT
}

enum class GradientBackground(val code: String) {
    GREEN("green_gradient"),
    GREEN_LIGHT("green_light_gradient"),
    LIGHT("light_gradient"),
    DARK("dark_gradient");

    companion object {
        fun fromCode(code: String): GradientBackground =
            GradientBackground.entries.find { it.code == code } ?: LIGHT
    }
}

@Serializable
data class BackgroundSetting(
    val type: BackgroundType,
    val value: String,          // resId, path, или ключ градиента
    val brushData: BrushData,    // всегда есть: и у кастомных, и у системных, и у градиентов
    val isDark: Boolean
)

@Serializable
data class BrushData(
    val colors: List<String>, // hex цвета: "#FF00FF", "#AABBCC"
    val angle: Float = 90f    // угол (по желанию)
) {
    fun toBrush(): Brush = Brush.linearGradient(
        colors = colors.map { Color(android.graphics.Color.parseColor(it)) },
//        start = Offset.Zero,
//        end = Offset(
//            x = kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat(),
//            y = kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat()
//        )
    )
}