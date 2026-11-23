package com.luisrocha.suzukapit.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Base de datos SQLite local. Aquí viven las 4 operaciones del CRUD:
 * insert (Create), getAll/getById (Read), update (Update) y delete (Delete).
 */
class LogDbHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE TEXT NOT NULL,
                $COL_CIRCUIT TEXT NOT NULL,
                $COL_DRIVER TEXT NOT NULL,
                $COL_NOTES TEXT NOT NULL,
                $COL_RATING INTEGER NOT NULL,
                $COL_DATE TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE")
        onCreate(db)
    }

    // ---------- CREATE ----------
    fun insert(entry: LogEntry): Long =
        writableDatabase.insert(TABLE, null, toValues(entry))

    // ---------- READ ----------
    fun getAll(): List<LogEntry> {
        val list = mutableListOf<LogEntry>()
        readableDatabase.query(TABLE, null, null, null, null, null, "$COL_ID DESC").use { c ->
            while (c.moveToNext()) list.add(fromCursor(c))
        }
        return list
    }

    fun getById(id: Long): LogEntry? =
        readableDatabase.query(TABLE, null, "$COL_ID = ?", arrayOf(id.toString()), null, null, null)
            .use { c -> if (c.moveToFirst()) fromCursor(c) else null }

    // ---------- UPDATE ----------
    fun update(entry: LogEntry): Int =
        writableDatabase.update(TABLE, toValues(entry), "$COL_ID = ?", arrayOf(entry.id.toString()))

    // ---------- DELETE ----------
    fun delete(id: Long): Int =
        writableDatabase.delete(TABLE, "$COL_ID = ?", arrayOf(id.toString()))

    private fun toValues(e: LogEntry) = ContentValues().apply {
        put(COL_TITLE, e.title)
        put(COL_CIRCUIT, e.circuit)
        put(COL_DRIVER, e.driver)
        put(COL_NOTES, e.notes)
        put(COL_RATING, e.rating)
        put(COL_DATE, e.date)
    }

    private fun fromCursor(c: Cursor) = LogEntry(
        id = c.getLong(c.getColumnIndexOrThrow(COL_ID)),
        title = c.getString(c.getColumnIndexOrThrow(COL_TITLE)),
        circuit = c.getString(c.getColumnIndexOrThrow(COL_CIRCUIT)),
        driver = c.getString(c.getColumnIndexOrThrow(COL_DRIVER)),
        notes = c.getString(c.getColumnIndexOrThrow(COL_NOTES)),
        rating = c.getInt(c.getColumnIndexOrThrow(COL_RATING)),
        date = c.getString(c.getColumnIndexOrThrow(COL_DATE))
    )

    companion object {
        private const val DB_NAME = "suzuka.db"
        private const val DB_VERSION = 1
        private const val TABLE = "entries"
        private const val COL_ID = "id"
        private const val COL_TITLE = "title"
        private const val COL_CIRCUIT = "circuit"
        private const val COL_DRIVER = "driver"
        private const val COL_NOTES = "notes"
        private const val COL_RATING = "rating"
        private const val COL_DATE = "date"
    }
}
