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

                    // Simple string comparison for versions (assuming v1.0.0 format)
                    val currentNum = currentVersion.replace("v", "").replace(".", "")
                    val latestNum = tagName.replace("v", "").replace(".", "")

                    if (latestNum > currentNum) {
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

    private fun showUpdateDialog(context: Context, newVersion: String, releaseNotes: String, downloadUrl: String) {
        android.app.AlertDialog.Builder(context)
            .setTitle("Atualização Disponível")
            .setMessage("Uma nova versão ($newVersion) está disponível.\n\nNotas:\n$releaseNotes")
            .setPositiveButton("Baixar") { _, _ ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
                context.startActivity(intent)
            }
            .setNegativeButton("Agora não", null)
            .show()
    }
}
