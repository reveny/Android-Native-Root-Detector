package com.reveny.nativecheck.ui.screens.settings.category

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Box
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import com.reveny.nativecheck.R
import com.reveny.nativecheck.datastore.model.Language
import com.reveny.nativecheck.ui.component.SettingNormalItem
import compose.icons.TablerIcons
import compose.icons.tablericons.Outline
import compose.icons.tablericons.outline.Language
import java.util.Locale

private val Languages = LanguageItem.entries.toTypedArray()

@Composable
internal fun Language(
    language: Language,
    onLanguageChange: (Language) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box {
        SettingNormalItem(
            title = stringResource(R.string.pref_language),
            icon = TablerIcons.Outline.Language,
            description = Languages.find { it.value == language }?.text?.let { stringResource(it) },
            onClick = { expanded = true }
        )

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Languages.forEach { item ->
                DropdownMenuItem(onClick = {
                    onLanguageChange(item.value)
                    val localeList = LocaleListCompat.create(item.locale)
                    AppCompatDelegate.setApplicationLocales(localeList)
                    expanded = false

                    // Restart the activity to apply the new language
                    val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                    intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                }) {
                    Text(stringResource(item.text))
                }
            }
        }
    }
}

private enum class LanguageItem(
    val value: Language,
    val locale: Locale?,
    val text: Int,
) {
    @SuppressLint("ConstantLocale")
    ENGLISH(Language.ENGLISH, Locale("en"), R.string.en),
    ARABIC(Language.ARABIC, Locale("ar"), R.string.ar),
    GERMAN(Language.GERMAN, Locale("de"), R.string.de),
    SPANISH(Language.SPANISH, Locale("es"), R.string.es),
    INDONESIAN(Language.INDONESIAN, Locale("id"), R.string.id),
    JAPANESE(Language.JAPANESE, Locale("ja"), R.string.ja),
    POLISH(Language.POLISH, Locale("pl"), R.string.pl),
    PORTUGUESE_BRAZIL(Language.PORTUGUESE_BRAZIL, Locale("pt", "BR"), R.string.pt_br),
    RUSSIAN(Language.RUSSIAN, Locale("ru"), R.string.ru),
    SLOVENIAN(Language.SLOVENIAN, Locale("sl"), R.string.sl),
    TURKISH_TURKEY(Language.TURKISH_TURKEY, Locale("tr", "TR"), R.string.tr_tr),
    VIETNAMESE(Language.VIETNAMESE, Locale("vi"), R.string.vi),
    CHINESE_SIMPLIFIED(Language.CHINESE_SIMPLIFIED, Locale("zh", "CN"), R.string.zh_cn),
    CHINESE_TRADITIONAL(Language.CHINESE_TRADITIONAL, Locale("zh", "TW"), R.string.zh_tw),
}
