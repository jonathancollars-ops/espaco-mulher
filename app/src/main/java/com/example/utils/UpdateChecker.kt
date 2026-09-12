package com.example.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class UpdateInfo(
    val isAvailable: Boolean = false,
    val newVersion: String = "",
    val releaseNotes: String = "",
    val downloadUrl: String = ""
)

object UpdateChecker {
    
    private val _updateState = MutableStateFlow(UpdateInfo())
    val updateState: StateFlow<UpdateInfo> = _updateState.asStateFlow()
    
    suspend fun checkForUpdates(context: Context, currentVersion: String, owner: String = "RogeriaCollares", repo: String = "PilatesEspacoMulher") {
        withContext(Dispatchers.IO) {
            try {
                val url = URL("https://api.github.com/repos/$owner/$repo/releases/latest")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json")

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonObject = JSONObject(response)
                    val tagName = jsonObject.getString("tag_name")
                    val body = jsonObject.getString("body")
                    val htmlUrl = jsonObject.getString("html_url")

                    val isNewer = isNewerVersion(tagName, currentVersion)

                    if (isNewer) {
                        _updateState.value = UpdateInfo(
                            isAvailable = true,
                            newVersion = tagName,
                            releaseNotes = body,
                            downloadUrl = htmlUrl
                        )
                        withContext(Dispatchers.Main) {
                            showUpdateDialog(context, tagName, body, htmlUrl)
                        }
                    } else {
                        _updateState.value = UpdateInfo(isAvailable = false)
                    }
                }
            } catch (e: Exception) {
                Log.e("UpdateChecker", "Failed to check for updates", e)
                // Fail silently if offline or API error
            }
        }
    }

    private fun isNewerVersion(latest: String, current: String): Boolean {
        return try {
            val latestParts = latest.trimStart('v').split(".").map { it.filter { c -> c.isDigit() }.toIntOrNull() ?: 0 }
            val currentParts = current.trimStart('v').split(".").map { it.filter { c -> c.isDigit() }.toIntOrNull() ?: 0 }
            val maxLen = maxOf(latestParts.size, currentParts.size)
            for (i in 0 until maxLen) {
                val l = latestParts.getOrElse(i) { 0 }
                val c = currentParts.getOrElse(i) { 0 }
                if (l > c) return true
                if (l < c) return false
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    private fun showUpdateDialog(context: Context, newVersion: String, releaseNotes: String, downloadUrl: String) {
        try {
            val activity = context as? android.app.Activity
            if (activity != null && (activity.isFinishing || activity.isDestroyed)) {
                return
            }

            android.app.AlertDialog.Builder(context)
                .setTitle("Atualização Disponível")
                .setMessage("Uma nova versão ($newVersion) está disponível.\n\nNotas:\n$releaseNotes")
                .setPositiveButton("Baixar") { _, _ ->
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl)).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Log.e("UpdateChecker", "Failed to launch download intent", e)
                    }
                }
                .setNegativeButton("Agora não", null)
                .show()
        } catch (e: Exception) {
            Log.e("UpdateChecker", "Failed to display update dialog safely", e)
        }
    }
}
