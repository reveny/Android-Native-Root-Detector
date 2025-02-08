package com.reveny.nativecheck.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingItem(title: String, description: String, icon: ImageVector?, onClick: () -> Unit) {
    Surface(modifier = Modifier.clickable { onClick() }) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 16.dp).size(24.dp),
                    tint = colorScheme.primary,
                )
            }
            Column(
                modifier = Modifier.weight(1f).padding(start = if (icon == null) 12.dp else 0.dp)
            ) {
                Text(
                    text = title,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge,
                    color = colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)
@Composable
fun SettingNormalItem(
    title: String,
    description: String? = null,
    icon: Any? = null,
    enabled: Boolean = true,
    onLongClickLabel: String? = null,
    onLongClick: (() -> Unit)? = null,
    onClickLabel: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = Modifier.combinedClickable(
            onClick = onClick,
            onClickLabel = onClickLabel,
            enabled = enabled,
            onLongClickLabel = onLongClickLabel,
            onLongClick = onLongClick
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leadingIcon?.invoke()

            when (icon) {
                is ImageVector -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 8.dp, end = 16.dp)
                            .size(24.dp),
                        tint = colorScheme.onSurfaceVariant
                    )
                }

                is Painter -> {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 8.dp, end = 16.dp)
                            .size(24.dp),
                        tint = colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = if (icon == null && leadingIcon == null) 12.dp else 0.dp)
                    .padding(end = 8.dp)
            ) {
                Text(
                    modifier = Modifier,
                    text = title,
                    maxLines = 2,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                    overflow = TextOverflow.Ellipsis
                )
                if (!description.isNullOrEmpty())
                    Text(
                        modifier = Modifier.padding(top = 2.dp),
                        text = description,
                        maxLines = Int.MAX_VALUE,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant,
                        overflow = TextOverflow.Ellipsis
                    )
            }
            trailingIcon?.let {
                VerticalDivider(
                    modifier = Modifier
                        .height(32.dp)
                        .padding(horizontal = 8.dp)
                        .align(Alignment.CenterVertically),
                    color = colorScheme.onSurface.copy(alpha = 0.3f),
                    thickness = 1.dp
                )
                trailingIcon.invoke()
            }
        }
    }

}

@Composable
fun SettingSwitchItem(
    title: String,
    description: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isChecked: Boolean = true,
    onClick: (() -> Unit) = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        modifier = Modifier.toggleable(
            value = isChecked,
            enabled = enabled,
            onValueChange = { onClick() },
            indication = LocalIndication.current,
            interactionSource = interactionSource
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, 16.dp)
                .padding(start = 0.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 16.dp)
                        .size(24.dp),
                    tint = colorScheme.onSurfaceVariant
                )
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    modifier = Modifier,
                    text = title,
                    maxLines = 2,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                    overflow = TextOverflow.Ellipsis
                )
                if (!description.isNullOrEmpty())
                    Text(
                        modifier = Modifier.padding(top = 2.dp),
                        text = description,
                        maxLines = Int.MAX_VALUE,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant,
                        overflow = TextOverflow.Ellipsis
                    )
            }
            Switch(
                checked = isChecked,
                onCheckedChange = null,
                interactionSource = interactionSource,
                modifier = Modifier.padding(start = 20.dp, end = 6.dp),
                enabled = enabled,
            )
        }
    }
}


@Composable
fun SettingSubtitleItem(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(start = 20.dp, top = 24.dp, bottom = 12.dp),
    text: String,
    color: Color = colorScheme.primary,
) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding),
        color = color,
        style = MaterialTheme.typography.labelLarge
    )
}