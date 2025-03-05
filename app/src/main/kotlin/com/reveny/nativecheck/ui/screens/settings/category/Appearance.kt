package com.reveny.nativecheck.ui.screens.settings.category

import android.graphics.drawable.PictureDrawable
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Scale
import coil.size.Size
import com.caverock.androidsvg.SVG
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.materialkolor.palettes.CorePalette
import com.reveny.nativecheck.R
import com.reveny.nativecheck.app.Const.PALETTE
import com.reveny.nativecheck.datastore.model.ThemeColor
import com.reveny.nativecheck.ui.component.NavBackButton
import com.reveny.nativecheck.ui.component.SettingSwitchItem
import com.reveny.nativecheck.ui.providable.LocalAppSettings
import com.reveny.nativecheck.utils.applyColor
import com.reveny.nativecheck.viewmodel.SettingsViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.Outline
import compose.icons.tablericons.outline.Check
import compose.icons.tablericons.outline.ChevronLeft
import compose.icons.tablericons.outline.Contrast
import compose.icons.tablericons.outline.Moon
import compose.icons.tablericons.outline.TestPipe

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Appearance(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            rememberTopAppBarState(),
            canScroll = { true })
    val appSettings = LocalAppSettings.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.settings_appearance)) },
                scrollBehavior = scrollBehavior,
                navigationIcon = { NavBackButton(navController) },
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            item {
                DynamicImageCard(
                    isDarkMode = appSettings.darkTheme,
                    themeColor = appSettings.themeColor.color
                )
            }
            item {
                ThemePalettes(
                    isDynamicColor = appSettings.dynamicColor,
                    themeColor = appSettings.themeColor,
                    onDynamicColorChange = viewModel::setDynamicColor,
                    onThemeColorChange = viewModel::setThemeColor,
                )
            }
            item {
                SettingSwitchItem(
                    title = stringResource(R.string.pref_dynamic_color),
                    description = stringResource(R.string.pref_dynamic_color_desc),
                    icon = TablerIcons.Outline.TestPipe,
                    isChecked = appSettings.dynamicColor,
                    onClick = { viewModel.setDynamicColor(!appSettings.dynamicColor) }
                )
            }
            item {
                SettingSwitchItem(
                    title = stringResource(R.string.pref_dark_theme),
                    icon = TablerIcons.Outline.Moon,
                    isChecked = appSettings.darkTheme,
                    onClick = { viewModel.setDarkTheme(!appSettings.darkTheme) }
                )
            }
            item {
                if (appSettings.darkTheme)
                SettingSwitchItem(
                    title = stringResource(R.string.pref_high_contrast),
                    icon = TablerIcons.Outline.Contrast,
                    isChecked = appSettings.highContrast,
                    onClick = { viewModel.setContrastMode(!appSettings.highContrast) }
                )
            }
        }
    }
}

@Composable
fun DynamicImageCard(
    isDarkMode: Boolean,
    themeColor: Color,
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val pic by remember(isDarkMode, size, themeColor) {
        mutableStateOf(
            PictureDrawable(
                SVG.getFromString(
                    PALETTE.applyColor(
                        color = themeColor.toArgb(),
                        isDarkMode = isDarkMode
                    )
                ).renderToPicture(size.width, size.height)
            )
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .aspectRatio(1.38f)
            .clip(RoundedCornerShape(24.dp))
            .background(
                colorScheme.inverseOnSurface
            )
            .clickable { }
            .padding(60.dp)
            .onGloballyPositioned {
                if (it.size != IntSize.Zero) {
                    size = it.size
                }
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Crossfade(targetState = pic, label = "svg") {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(data = it)
                        .apply {
                            crossfade(true)
                            scale(Scale.FIT)
                            precision(Precision.AUTOMATIC)
                            size(Size.ORIGINAL)
                        }.build()
                ),
                contentDescription = "",
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
fun ThemePalettes(
    themeColor: ThemeColor,
    isDynamicColor: Boolean,
    onDynamicColorChange: (Boolean) -> Unit,
    onThemeColorChange: (ThemeColor) -> Unit
) = Column {

    val groupedColorPalettes = colorPalettes.chunked(4)

    val pagerState = rememberPagerState(pageCount = { groupedColorPalettes.size })

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 12.dp),
    ) { page ->
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            colorPalettes.chunked(4)[page].forEach { color ->
                ThemeColorPalettes(
                    color = color.color.toArgb(),
                    isSelected = !isDynamicColor && themeColor == color,
                    onSelected = {
                        onDynamicColorChange(false)
                        onThemeColorChange(color)
                    }
                )
            }
        }
    }

    HorizontalPagerIndicator(
        pagerState = pagerState,
        pageCount = groupedColorPalettes.size,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(vertical = 12.dp),
        activeColor = colorScheme.primary,
        inactiveColor = colorScheme.outlineVariant,
        indicatorHeight = 6.dp,
        indicatorWidth = 6.dp
    )
}

@Composable
private fun RowScope.ThemeColorPalettes(
    color: Int,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    val corePalette = remember { CorePalette.of(color) }
    val animatedSize by animateDpAsState(if (isSelected) 28.dp else 0.dp)
    val iconSize by animateDpAsState(if (isSelected) 16.dp else 0.dp)

    Surface(
        onClick = onSelected,
        shape = RoundedCornerShape(16.dp),
        color = colorScheme.surfaceContainer,
        modifier = Modifier
            .padding(4.dp)
            .weight(1f)
            .aspectRatio(1f)
    ) {
        Box(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(Color(corePalette.a1.tone(80)))
            ) {
                Box(
                    Modifier
                        .align(Alignment.BottomStart)
                        .size(24.dp)
                        .background(Color(corePalette.a2.tone(90)))
                )
                Box(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp)
                        .background(Color(corePalette.a3.tone(60)))
                )
                Box(
                    Modifier
                        .size(animatedSize)
                        .align(Alignment.Center)
                        .background(colorScheme.primaryContainer, CircleShape)
                ) {
                    Icon(
                        imageVector = TablerIcons.Outline.Check,
                        contentDescription = null,
                        modifier = Modifier.size(iconSize).align(Alignment.Center),
                        tint = colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

private val colorPalettes = ThemeColor.entries