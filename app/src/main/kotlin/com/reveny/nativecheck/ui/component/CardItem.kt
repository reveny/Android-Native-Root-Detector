package com.reveny.nativecheck.ui.component

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.reveny.nativecheck.R
import com.reveny.nativecheck.app.DetectionData
import com.reveny.nativecheck.viewmodel.MainViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.Filled
import compose.icons.tablericons.Outline
import compose.icons.tablericons.filled.BrandGithub
import compose.icons.tablericons.filled.BrandPatreon
import compose.icons.tablericons.outline.BrandTelegram
import compose.icons.tablericons.outline.Virus

@Composable
fun CheckCard(
    modifier: Modifier = Modifier,
    isDetected: Boolean,
) = Surface(
    modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .then(modifier),
    color = MaterialTheme.colorScheme.primary,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = "Info",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(24.dp))
        Column {
            Text(
                text = if (isDetected) stringResource(R.string.the_environment_is_abnormal) else stringResource(
                    R.string.the_environment_is_normal
                ),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Medium
            )
            // Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isDetected) stringResource(R.string.description_abnormal) else stringResource(
                    R.string.description_normal
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun DetectionCard(
    detection: DetectionData,
) {
    var expanded by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { expanded = !expanded },
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            Modifier
                .padding(all = 16.dp)
                .animateContentSize(spring(stiffness = Spring.StiffnessLow))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = detection.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                val rotateAngle by animateFloatAsState(
                    if (expanded) 180f else 0f,
                    label = "rotateAngle"
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier
                        .rotate(rotateAngle)
                        .alpha(1f)
                        .clickable { expanded = !expanded }
                )
            }

            if (expanded) {
                Column(
                    Modifier
                        .padding(top = 16.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    clipboardManager.setText(AnnotatedString(detection.description))
                                    Toast
                                        .makeText(
                                            context,
                                            "Details copied to clipboard",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }
                            )
                        }
                ) {
                    Text("Details:", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(detection.description, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun AboutCard() {
    val uriHandler = LocalUriHandler.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.support_us),
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                IconButton(onClick = { uriHandler.openUri("https://github.com/reveny") }) {
                    Icon(
                        imageVector = TablerIcons.Filled.BrandGithub,
                        contentDescription = null,
                    )
                }
                IconButton(onClick = { uriHandler.openUri("https://patreon.com/Reveny") }) {
                    Icon(
                        imageVector = TablerIcons.Filled.BrandPatreon,
                        contentDescription = null,
                    )
                }
                IconButton(onClick = { uriHandler.openUri("https://t.me/reveny1") }) {
                    Icon(
                        imageVector = TablerIcons.Outline.BrandTelegram,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Composable
fun SystemInfoCard(deviceInfo: String, androidVersion: String, kernelVersion: String) = Surface(
    modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp)),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 1.dp
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "System :",
            style = MaterialTheme.typography.titleSmall,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = deviceInfo,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = androidVersion,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = kernelVersion,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun AppInfoCard(
    appVersion: String,
    signature: String,
    signatureValid: String,
    experimentalEnabled: String,
) = Surface(
    modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp)),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 1.dp
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "App :",
            style = MaterialTheme.typography.titleSmall,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = appVersion,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = signature,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = signatureValid,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = experimentalEnabled,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun InfoCards(viewModel: MainViewModel) {
    val deviceInfo by viewModel.deviceInfo.collectAsState()
    val androidVersion by viewModel.androidVersion.collectAsState()
    val kernelVersion by viewModel.kernelVersion.collectAsState()
    val appVersion by viewModel.appVersion.collectAsState()
    val signature by viewModel.signature.collectAsState()
    val signatureValid by viewModel.signatureValid.collectAsState()
    val experimentalEnabled by viewModel.experimentalEnabled.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SystemInfoCard(
            deviceInfo = deviceInfo,
            androidVersion = androidVersion,
            kernelVersion = kernelVersion
        )
        AppInfoCard(
            appVersion = appVersion,
            signature = signature,
            signatureValid = signatureValid,
            experimentalEnabled = experimentalEnabled
        )
    }
}

@Preview
@Composable
fun PreviewInfoCards() {
    MaterialTheme {
        InfoCards(viewModel = MainViewModel())
    }
}