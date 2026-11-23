package com.luisrocha.suzukapit

import android.app.Application
import com.luisrocha.suzukapit.util.ThemeManager

/**
 * Clase Application: se ejecuta una sola vez al abrir el proceso de la app.
 * Aquí aplicamos el tema (claro/oscuro) que el usuario guardó la última vez.
 */
class SuzukaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ThemeManager.applySaved(this)
    }
}
