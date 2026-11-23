package com.luisrocha.suzukapit.data.local

/** Una entrada de la bitácora personal (fila de la tabla "entries"). */
data class LogEntry(
    val id: Long = 0,
    val title: String,
    val circuit: String,
    val driver: String,
    val notes: String,
    val rating: Int,
    val date: String
)
