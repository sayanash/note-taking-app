package com.example.notetakingapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NotesDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "notes.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "Notes"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_CONTENT TEXT NOT NULL,
                $COLUMN_TIMESTAMP TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertNote(title: String, content: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_CONTENT, content)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getNotesSorted(sortType: String): List<Note> {
        val db = this.readableDatabase
        val query = when (sortType) {
            "date_asc" -> "SELECT * FROM Notes ORDER BY timestamp ASC"
            "date_desc" -> "SELECT * FROM Notes ORDER BY timestamp DESC"
            "title_asc" -> "SELECT * FROM Notes ORDER BY title COLLATE NOCASE ASC"
            "title_desc" -> "SELECT * FROM Notes ORDER BY title COLLATE NOCASE DESC"
            else -> "SELECT * FROM Notes" // Default: no sorting
        }

        val cursor = db.rawQuery(query, null)
        val notes = mutableListOf<Note>()

        if (cursor.moveToFirst()) {
            do {
                // Extract and map fields to match your Note class
                val id = cursor.getLong(cursor.getColumnIndexOrThrow("id")) // Long for `id`
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title")) // String for `title`
                val content = cursor.getString(cursor.getColumnIndexOrThrow("content")) // String for `content`
                val timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp")) // String for `timestamp`

                // Create a Note object
                notes.add(Note(id, title, content, timestamp))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return notes
    }


    fun getAllNotes(): List<Note> {
        val notesList = mutableListOf<Note>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME, null, null, null,
            null, null, "$COLUMN_TIMESTAMP DESC"
        )
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(COLUMN_ID))
                val title = getString(getColumnIndexOrThrow(COLUMN_TITLE))
                val content = getString(getColumnIndexOrThrow(COLUMN_CONTENT))
                val timestamp = getString(getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                notesList.add(Note(id, title, content, timestamp))
            }
            close()
        }
        return notesList
    }
    // Get a single note by ID
    fun getNoteById(id: Long): Note? {
        val db = readableDatabase
        val cursor = db.query(
            "Notes", // The table name
            arrayOf("id", "title", "content", "timestamp"), // Columns to fetch
            "id = ?", // WHERE clause
            arrayOf(id.toString()), // Selection arguments
            null, // Group by
            null, // Having
            null // Order by
        )

        var note: Note? = null
        if (cursor != null && cursor.moveToFirst()) {
            val noteIdColumnIndex = cursor.getColumnIndex("id")
            val titleColumnIndex = cursor.getColumnIndex("title")
            val contentColumnIndex = cursor.getColumnIndex("content")
            val timestampColumnIndex = cursor.getColumnIndex("timestamp")

            // Check if all columns exist and are valid (>= 0)
            if (noteIdColumnIndex >= 0 && titleColumnIndex >= 0 && contentColumnIndex >= 0 && timestampColumnIndex >= 0) {
                val noteId = cursor.getLong(noteIdColumnIndex)
                val title = cursor.getString(titleColumnIndex)
                val content = cursor.getString(contentColumnIndex)
                val timestamp = cursor.getString(timestampColumnIndex)

                note = Note(noteId, title, content, timestamp)
            }
        }
        cursor?.close()
        db.close()
        return note
    }

    fun updateNote(id: Long, title: String, content: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_CONTENT, content)
        }
        return db.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(id.toString()))
    }


    fun deleteNote(id: Long): Boolean {
        val db = writableDatabase
        val result = db.delete("Notes", "id=?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }
}
