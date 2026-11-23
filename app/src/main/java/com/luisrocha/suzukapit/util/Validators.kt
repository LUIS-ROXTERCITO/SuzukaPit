package com.luisrocha.suzukapit.util

import androidx.annotation.StringRes
import com.luisrocha.suzukapit.R
import com.luisrocha.suzukapit.data.Circuits

/**
 * Reglas de validación del formulario.
 * Cada función devuelve el id del mensaje de error (strings.xml) o null si el dato es válido.
 */
object Validators {
    private val nameRegex = Regex("^[\\p{L} .'-]{3,40}$")

    @StringRes
    fun title(value: String): Int? = when {
        value.isBlank() -> R.string.err_title_required
        value.trim().length !in 3..40 -> R.string.err_title_length
        else -> null
    }

    @StringRes
    fun circuit(value: String): Int? =
        if (value in Circuits.japanese) null else R.string.err_circuit

    @StringRes
    fun driver(value: String): Int? =
        if (nameRegex.matches(value.trim())) null else R.string.err_driver

    @StringRes
    fun notes(value: String): Int? =
        if (value.trim().length in 10..300) null else R.string.err_notes

    @StringRes
    fun rating(value: Int): Int? =
        if (value >= 1) null else R.string.err_rating
}
