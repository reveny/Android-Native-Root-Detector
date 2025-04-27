package com.reveny.nativecheck.datastore.model

import androidx.compose.ui.graphics.Color

/**
 * Enum representing different theme colors, each associated with a specific [Color] value.
 */
enum class ThemeColor(val hex: String) {
    Marsala("6D2E46"),
    DigitalLavender("E4BAD4"),
    TranquilBlue("88C1E6"),
    SageGreen("7A9D7E"),
    CyberLime("CFFF04"),
    Goldenrod("DAA520"),
    CoralBurst("FF6F61"),
    WarmTaupe("8B7D6B");

    val color: Color get() = Color(android.graphics.Color.parseColor("#$hex"))
}

/**
 * Enum representing different language settings.
 */
enum class Language {
    SYSTEM_DEFAULT,
    ENGLISH,
    ARABIC,
    GERMAN,
    GREEK,
    SPANISH,
    INDONESIAN,
    JAPANESE,
    POLISH,
    PORTUGUESE_BRAZIL,
    RUSSIAN,
    SLOVENIAN,
    TURKISH_TURKEY,
    VIETNAMESE,
    CHINESE_SIMPLIFIED,
    CHINESE_TRADITIONAL,
    BULGARIAN
}
