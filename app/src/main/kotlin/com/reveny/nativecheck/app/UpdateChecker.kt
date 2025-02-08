package com.reveny.nativecheck.app
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.FileProvider
import com.reveny.nativecheck.R
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Scanner
import java.util.concurrent.Executors

class UpdateChecker(private val context: Context) {
    private val updateJsonUrl = "https://dl.reveny.me/update.json"

    @SuppressLint("StringFormatInvalid")
    fun checkForUpdates(onUpdateAvailable: (String, String, String) -> Unit) {
        Executors.newSingleThreadExecutor().execute {
            try {
                val url = URL(updateJsonUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val scanner = Scanner(connection.inputStream)
                    val jsonBuilder = StringBuilder()
                    while (scanner.hasNext()) {
                        jsonBuilder.append(scanner.nextLine())
                    }
                    scanner.close()
                    connection.disconnect()

                    val jsonObject = JSONObject(jsonBuilder.toString())
                    val updates = jsonObject.getJSONArray("updates")

                    val currentVersion = getCurrentVersion()
                    var latestVersion = currentVersion ?: "0.0.0" // Handle nullable String
                    var latestApkName = ""
                    var latestApkUrl = ""

                    for (i in 0 until updates.length()) {
                        val update = updates.getJSONObject(i)
                        val version = update.getString("version")
                        val apkName = update.getString("apkName")
                        val apkUrl = update.getString("apkUrl")

                        if (isVersionNewer(version, latestVersion)) {
                            latestVersion = version
                            latestApkName = apkName
                            latestApkUrl = apkUrl
                        }
                    }

                    if (latestVersion != currentVersion && latestApkUrl.isNotEmpty()) {
                        Handler(Looper.getMainLooper()).post {
                            onUpdateAvailable(latestVersion, latestApkUrl, latestApkName)
                        }
                    }
                } else {
                    connection.disconnect()
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context, context.getString(R.string.failed_to_get_update_info), Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, String.format(context.getString(R.string.error), e.message), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getCurrentVersion(): String? {
        return try {
            val packageInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    private fun isVersionNewer(version1: String, version2: String): Boolean {
        val v1 = version1.split(".")
        val v2 = version2.split(".")
        for (i in 0 until maxOf(v1.size, v2.size)) {
            val num1 = v1.getOrNull(i)?.toIntOrNull() ?: 0
            val num2 = v2.getOrNull(i)?.toIntOrNull() ?: 0
            if (num1 > num2) return true
            if (num1 < num2) return false
        }
        return false
    }

    @SuppressLint("StringFormatInvalid")
    fun downloadAndInstallApk(apkUrl: String, apkName: String, onProgressUpdate: (Int) -> Unit, onDownloadComplete: (File) -> Unit, onError: (String) -> Unit) {
        val apkFile = File(context.cacheDir, apkName)

        if (apkFile.exists()) {
            onDownloadComplete(apkFile)
            return
        }

        Executors.newSingleThreadExecutor().execute {
            try {
                val url = URL(apkUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()
                val handler = Handler(Looper.getMainLooper())

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val fileLength = connection.contentLength
                    val inputStream: InputStream = connection.inputStream
                    val outputStream = FileOutputStream(apkFile)
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    var totalBytesRead: Long = 0


                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead
                        val progress = (totalBytesRead * 100 / fileLength).toInt()
                        handler.post { onProgressUpdate(progress) }
                    }

                    outputStream.close()
                    inputStream.close()
                    connection.disconnect()

                    handler.post { onDownloadComplete(apkFile) }
                } else {
                    handler.post {
                        onError(String.format(context.getString(R.string.download_failed), connection.responseMessage))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    onError(String.format(context.getString(R.string.error), e.message))
                }
            }
        }
    }

    fun installApk(apkFile: File) {
        val apkUri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", apkFile)
        val canInstall = context.packageManager.canRequestPackageInstalls()

        if (!canInstall) {
            val settingsIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse("package:${context.packageName}"))
            context.startActivity(settingsIntent)
            return
        }

        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        if (installIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(installIntent)
        } else {
            Toast.makeText(context, context.getString(R.string.unable_to_install), Toast.LENGTH_LONG).show()
        }
    }
}