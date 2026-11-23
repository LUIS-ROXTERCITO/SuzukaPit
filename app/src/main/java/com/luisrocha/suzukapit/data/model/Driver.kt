package com.luisrocha.suzukapit.data.model

/** Piloto en la clasificación del campeonato (viene de la API REST). */
data class Driver(
    val position: Int,
    val name: String,
    val team: String,
    val nationality: String,
    val points: String,
    val wins: String
)
