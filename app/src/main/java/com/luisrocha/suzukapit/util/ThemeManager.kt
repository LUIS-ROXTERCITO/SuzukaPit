package com.luisrocha.suzukapit.util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

/** Guarda y aplica la preferencia de modo oscuro usando SharedPreferences. */
object ThemeManager {
    private const val PREFS = "suzuka_prefs"
    private const val KEY_DARK = "dark_mode"

    fun isDark(context: Context): Boolean =
        prefs(context).getBoolean(KEY_DARK, false)

    fun setDark(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_DARK, enabled).apply()
        apply(enabled)
    }

    fun applySaved(context: Context) = apply(isDark(context))

    private fun apply(dark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (dark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
