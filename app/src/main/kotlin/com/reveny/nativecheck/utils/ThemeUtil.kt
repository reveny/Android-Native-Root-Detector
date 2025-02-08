package com.reveny.nativecheck.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.materialkolor.palettes.CorePalette

internal fun Int.autoToDarkTone(isDarkMode: Boolean): Int {
    if (!isDarkMode) return this

    val toneMap = mapOf(
        10 to 99, 20 to 95, 25 to 90, 30 to 90, 40 to 80,
        50 to 60, 60 to 50, 70 to 40, 80 to 40, 90 to 30,
        95 to 20, 98 to 10, 99 to 10, 100 to 20
    )
    return toneMap[this] ?: this
}

fun String.applyColor(color: Int, isDarkMode: Boolean): String =
    replace("fill=\"(.+?)\"".toRegex()) { match ->
        val corePalette = CorePalette.of(color)
        val value = match.groupValues[1]
        if (value.startsWith("#")) return@replace match.value

        runCatching {
            val splitValues = value.split("(?<=\\d)(?=\\D)|(?=\\d)(?<=\\D)".toRegex())
            if (splitValues.size < 2) {
                return@runCatching match.value
            }
            val (scheme, toneStr) = splitValues
            val newTone = toneStr.toInt().autoToDarkTone(isDarkMode)
            val argb = when (scheme) {
                "p" -> corePalette.a1.tone(newTone)
                "s" -> corePalette.a2.tone(newTone)
                "t" -> corePalette.a3.tone(newTone)
                "n" -> corePalette.n1.tone(newTone)
                "nv" -> corePalette.n2.tone(newTone)
                "e" -> corePalette.error.tone(newTone)
                else -> Color.Transparent.toArgb()
            }
            "fill=\"${String.format("#%06X", argb and 0xFFFFFF)}\""
        }.getOrElse {
            it.printStackTrace()
            match.value
        }
    }