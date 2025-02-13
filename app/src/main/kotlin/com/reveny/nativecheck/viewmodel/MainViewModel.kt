package com.reveny.nativecheck.viewmodel

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reveny.nativecheck.R
import com.reveny.nativecheck.app.DetectionData
import com.reveny.nativecheck.app.Native
import com.reveny.nativecheck.repository.AppSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Locale

class MainViewModel() : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _deviceInfo = MutableStateFlow("")
    val deviceInfo: StateFlow<String> = _deviceInfo

    private val _androidVersion = MutableStateFlow("")
    val androidVersion: StateFlow<String> = _androidVersion

    private val _kernelVersion = MutableStateFlow("")
    val kernelVersion: StateFlow<String> = _kernelVersion

    private val _appVersion = MutableStateFlow("")
    val appVersion: StateFlow<String> = _appVersion

    private val _signature = MutableStateFlow("")
    val signature: StateFlow<String> = _signature

    private val _signatureValid = MutableStateFlow("")
    val signatureValid: StateFlow<String> = _signatureValid

    val experimentalEnabled = MutableStateFlow("")

    private val _detections = MutableStateFlow<List<DetectionData>>(emptyList())
    val detections: StateFlow<List<DetectionData>> = _detections

    fun initializeData(context: Context) {
        viewModelScope.launch {
            _deviceInfo.value = String.format(context.getString(R.string.sysinfo_device), getDevice())
            _androidVersion.value = String.format(context.getString(R.string.sysinfo_android_version), Build.VERSION.RELEASE)
            _kernelVersion.value = String.format(context.getString(R.string.sysinfo_kernel_version), getKernelVersion())
            _appVersion.value = String.format(context.getString(R.string.appinfo_version), getAppVersion(context))
            _signature.value = String.format(context.getString(R.string.appinfo_signature), getSignature(context))
            _signatureValid.value = String.format(context.getString(R.string.appinfo_is_signature_valid), context.getString(R.string.true_b))
        }
    }

    fun performTask(context: Context, packageManager: PackageManager, enableExperimental: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true

            experimentalEnabled.value = String.format(context.getString(R.string.experimental_detections), if (enableExperimental) context.getString(R.string.true_a) else context.getString(R.string.false_a))

            if (_detections.value.isNotEmpty()) {
                _isLoading.value = false
                return@launch
            }

            val detections = getDetections(context, packageManager, enableExperimental)
            _detections.value = detections
            _isLoading.value = false
        }
    }

    private suspend fun getDetections(context: Context, packageManager: PackageManager, enableExperimental: Boolean): List<DetectionData> {
        // return withContext(Dispatchers.IO) {
        //     val native = Native()
        //     native.getDetections(context, packageManager, enableExperimental).toList()
        // }
        return emptyList()
    }

    private fun getDevice(): String {
        var manufacturer = Build.MANUFACTURER.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.ROOT
            ) else it.toString()
        }
        if (Build.BRAND != Build.MANUFACTURER) {
            manufacturer += " " + Build.BRAND.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.ROOT
                ) else it.toString()
            }
        }
        manufacturer += " " + Build.MODEL + " "
        return manufacturer
    }

    private fun getKernelVersion(): String {
        val kernelVersion = StringBuilder()
        try {
            val process = Runtime.getRuntime().exec("uname -r")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                kernelVersion.append(line)
            }
            reader.close()
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return kernelVersion.toString().split("-")[0]
    }

    private fun getSignature(context: Context): String {
        return try {
            val pm = context.packageManager
            val packageInfo = pm.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            val digest = MessageDigest.getInstance("SHA-256")
            for (signature in packageInfo.signatures!!) {
                val signatureBytes = signature.toByteArray()
                digest.update(signatureBytes)
            }
            val hashBytes = digest.digest()
            val hexString = StringBuilder()
            for (hashByte in hashBytes) {
                val hex = Integer.toHexString(0xff and hashByte.toInt())
                if (hex.length == 1) hexString.append('0')
                hexString.append(hex)
            }
            hexString.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "Package name not found."
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            "SHA-256 algorithm not found."
        }
    }

    private fun getAppVersion(context: Context): String {
        return try {
            val pm = context.packageManager
            val packageInfo = pm.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "Unknown"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "Unknown"
        }
    }
}
