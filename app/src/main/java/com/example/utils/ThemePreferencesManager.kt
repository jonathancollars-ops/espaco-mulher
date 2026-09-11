package com.example.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.ui.theme.AppThemeColor

class ThemePreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    var themeColor: AppThemeColor
        get() {
            val themeName = prefs.getString("theme_color", AppThemeColor.PURPLE.name)
            return try {
                AppThemeColor.valueOf(themeName ?: AppThemeColor.PURPLE.name)
            } catch (e: Exception) {
                AppThemeColor.PURPLE
            }
        }
        set(value) {
            prefs.edit().putString("theme_color", value.name).apply()
        }
}
