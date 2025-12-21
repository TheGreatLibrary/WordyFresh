package com.sinya.projects.wordle.domain.enums

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.sinya.projects.wordle.R
import kotlinx.serialization.Serializable

enum class BackgroundSettings(
    val type: TypeBackground,
    @DrawableRes val res: Int?,
    val brushData: BrushData,
    val theme: TypeThemes
) {
    LIGHT_IMAGE(
        res = R.drawable.bg1,
        brushData = BrushData(listOf("#FFFFFF", "#8C8C8C")),
        type = TypeBackground.SYSTEM,
        theme = TypeThemes.LIGHT
    ),
    DARK_IMAGE(
        res = R.drawable.bg2,
        brushData = BrushData(listOf("#272727", "#060606")),
        type = TypeBackground.SYSTEM,
        theme = TypeThemes.DARK
    ),
    GREEN_LIGHT_IMAGE(
        res = R.drawable.bg3,
        brushData = BrushData(listOf("#FFFFFF", "#104644")),
        type = TypeBackground.SYSTEM,
        theme = TypeThemes.LIGHT
    ),
    GREEN_DARK_IMAGE(
        res = R.drawable.bg4,
        brushData = BrushData(listOf("#6DD4D0", "#104644")),
        type = TypeBackground.SYSTEM,
        theme = TypeThemes.DARK
    ),

    LIGHT_GRADIENT(
        res = null,
        brushData = BrushData(listOf("#FFFFFF", "#8C8C8C")),
        type = TypeBackground.GRADIENT,
        theme = TypeThemes.LIGHT
    ),
    DARK_GRADIENT(
        res = null,
        brushData = BrushData(listOf("#272727", "#060606")),
        type = TypeBackground.GRADIENT,
        theme = TypeThemes.DARK
    ),

    DEFAULT(
        res = null,
        brushData = BrushData(listOf("#FFFFFF", "#8C8C8C")),
        type = TypeBackground.DEFAULT,
        theme = TypeThemes.LIGHT
    );

    companion object {
        fun fromName(name: String): BackgroundSettings =
            BackgroundSettings.entries.find { it.name == name } ?: DEFAULT
    }
}

@Serializable
data class BrushData(
    val colors: List<String>, // hex цвета: "#FF00FF", "#AABBCC"
    val angle: Float = 90f    // угол
) {
    fun toBrush(): Brush = Brush.linearGradient(
        colors = colors.map { Color(android.graphics.Color.parseColor(it)) },
    )
}