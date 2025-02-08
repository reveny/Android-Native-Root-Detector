package com.reveny.nativecheck.ui.theme

import android.R
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextDirection
import com.materialkolor.rememberDynamicColorScheme

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AppTheme(
    themeColor: Color,
    isDynamicColor: Boolean,
    theme: Boolean,
    contrastMode: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = rememberDynamicColorScheme(
        seedColor = if (isDynamicColor) colorResource(id = R.color.system_accent1_500) else themeColor,
        isDark = theme,
        isAmoled = contrastMode,
    )
    ProvideTextStyle(
        value = LocalTextStyle.current.copy(
            lineBreak = LineBreak.Paragraph,
            textDirection = TextDirection.Content
        )
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}