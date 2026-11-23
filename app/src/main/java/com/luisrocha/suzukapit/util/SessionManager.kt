package com.luisrocha.suzukapit.util

/**
 * Guarda en MEMORIA si el usuario ya se autenticó con huella.
 * Al cerrar la app (o si el sistema mata el proceso) el valor vuelve a false,
 * así que siempre se vuelve a pedir la huella.
 */
object SessionManager {
    @Volatile
    var isAuthenticated: Boolean = false
        private set

    fun login() {
        isAuthenticated = true
    }

    fun logout() {
        isAuthenticated = false
    }
}
