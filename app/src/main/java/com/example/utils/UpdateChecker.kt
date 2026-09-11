package com.example.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.content.DialogInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object UpdateChecker {
    
    suspend fun checkForUpdates(context: Context, currentVersion: String, owner: String = "RogeriaCollares", repo: String = "PilatesEspacoMulher") {
        withContext(Dispatchers.IO) {
            try {
                val url = URL("https://api.github.com/repos/\$owner/\$repo/releases/latest")
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
                    if (tagName.replace("v", "") > currentVersion.replace("v", "")) {
                        withContext(Dispatchers.Main) {
                            showUpdateDialog(context, tagName, body, htmlUrl)
                        }
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
            .setPositiveButton("Baixar") { dialog, which ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
                context.startActivity(intent)
            }
            .setNegativeButton("Agora não", null)
            .show()
    }
}
