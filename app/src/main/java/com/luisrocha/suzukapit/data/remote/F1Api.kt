package com.luisrocha.suzukapit.data.remote

import com.luisrocha.suzukapit.data.model.Driver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Consumo de la API REST pública Jolpica F1 (sucesora de Ergast).
 * Devuelve la clasificación de pilotos de la temporada actual en formato JSON.
 */
object F1Api {
    private const val ENDPOINT = "https://api.jolpi.ca/ergast/f1/current/driverStandings.json"

    /** Se ejecuta en un hilo de segundo plano (Dispatchers.IO) para no congelar la pantalla. */
    suspend fun getDriverStandings(): List<Driver> = withContext(Dispatchers.IO) {
        val connection = (URL(ENDPOINT).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 10_000
        }
        try {
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw IOException("HTTP ${connection.responseCode}")
            }
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            parse(body)
        } finally {
            connection.disconnect()
        }
    }

    private fun parse(json: String): List<Driver> {
        val lists = JSONObject(json)
            .getJSONObject("MRData")
            .getJSONObject("StandingsTable")
            .getJSONArray("StandingsLists")
        if (lists.length() == 0) return emptyList()

        val standings = lists.getJSONObject(0).getJSONArray("DriverStandings")
        return List(standings.length()) { i ->
            val item = standings.getJSONObject(i)
            val driver = item.getJSONObject("Driver")
            val constructors = item.getJSONArray("Constructors")
            val team = if (constructors.length() > 0)
                constructors.getJSONObject(0).getString("name") else "-"
            Driver(
                position = item.optString("position", "0").toIntOrNull() ?: 0,
                name = "${driver.getString("givenName")} ${driver.getString("familyName")}",
                team = team,
                nationality = driver.optString("nationality", "-"),
                points = item.optString("points", "0"),
                wins = item.optString("wins", "0")
            )
        }
    }
}
