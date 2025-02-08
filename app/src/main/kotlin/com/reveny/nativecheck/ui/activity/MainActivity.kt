package com.reveny.nativecheck.ui.activity

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.reveny.nativecheck.R
import com.reveny.nativecheck.repository.AppSettingsRepository
import com.reveny.nativecheck.ui.providable.LocalAppSettings
import com.reveny.nativecheck.ui.theme.AppTheme
import com.reveny.nativecheck.app.UpdateChecker
import com.reveny.nativecheck.viewmodel.MainViewModel
import com.reveny.nativecheck.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var appSettingsRepository: AppSettingsRepository

    private val mainViewModel: MainViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mainViewModel.initializeData(this)

        val packageManager = packageManager

        // Use a coroutine to retrieve the value from preferences
        lifecycleScope.launch {
            val enableExperimental = appSettingsRepository.data.first().enableExperimentalDetections
            mainViewModel.performTask(this@MainActivity, packageManager, enableExperimental)
        }

        val updateChecker = UpdateChecker(this)

        setContent {
            val userPreferences by appSettingsRepository.data.collectAsStateWithLifecycle(initialValue = null, lifecycle = lifecycle)

            userPreferences ?: return@setContent

            CompositionLocalProvider(LocalAppSettings provides userPreferences!!) {
                AppTheme(
                    isDynamicColor = userPreferences!!.dynamicColor,
                    theme = userPreferences!!.darkTheme,
                    contrastMode = userPreferences!!.highContrast,
                    themeColor = userPreferences!!.themeColor.color
                ) {
                    MainScreen(mainViewModel)
                    UpdateCheck(updateChecker)
                }
            }
        }
    }

    fun showToast(text: String) {
        this.runOnUiThread {
            Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        }
    }

    @Composable
    fun UpdateCheck(updateChecker: UpdateChecker) {
        var showDialog by remember { mutableStateOf(false) }
        var latestVersion by remember { mutableStateOf("") }
        var apkUrl by remember { mutableStateOf("") }
        var apkName by remember { mutableStateOf("") }
        var downloadProgress by remember { mutableIntStateOf(0) }
        var isDownloading by remember { mutableStateOf(false) }

        if (showDialog) {
            UpdateDialog(
                latestVersion = latestVersion,
                onConfirm = {
                    isDownloading = true
                    updateChecker.downloadAndInstallApk(
                        apkUrl,
                        apkName,
                        onProgressUpdate = { progress -> downloadProgress = progress },
                        onDownloadComplete = { file ->
                            isDownloading = false
                            updateChecker.installApk(file)
                        },
                        onError = { message ->
                            isDownloading = false
                            Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                        }
                    )
                },
                onCancel = { showDialog = false },
                downloadProgress = downloadProgress,
                isDownloading = isDownloading
            )
        }

        LaunchedEffect(Unit) {
            updateChecker.checkForUpdates { version, url, name ->
                latestVersion = version
                apkUrl = url
                apkName = name
                showDialog = true
            }
        }
    }
}

@Composable
fun UpdateDialog(
    latestVersion: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    downloadProgress: Int,
    isDownloading: Boolean
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(text = stringResource(R.string.update_available)) },
        text = {
            Column {
                Text(text = String.format(stringResource(R.string.version_is_available), latestVersion))
                if (isDownloading) {
                    Spacer(modifier = Modifier.padding(top = 16.dp))
                    LinearProgressIndicator(
                        progress = { downloadProgress / 100f },
                    )
                }
            }
        },
        confirmButton = {
            if (!isDownloading) {
                TextButton(onClick = onConfirm) {
                    Text(stringResource(R.string.update_confirm))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.update_cancel))
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}