package com.example.notetakingapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notetakingapp.databinding.ActivityEditNoteBinding

class EditNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditNoteBinding
    private lateinit var dbHelper: NotesDatabaseHelper
    private var noteId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = NotesDatabaseHelper(this)

        noteId = intent.getLongExtra("noteId", -1L).takeIf { it != -1L }
        if (noteId != null) {
            loadNote()
        }

        binding.buttonSave.setOnClickListener {
            saveNote()
        }
    }

    private fun loadNote() {
        val notes = dbHelper.getAllNotes()
        val note = notes.find { it.id == noteId }
        note?.let {
            binding.editTextTitle.setText(it.title)
            binding.editTextContent.setText(it.content)
        }
    }

    private fun saveNote() {
        val title = binding.editTextTitle.text.toString()
        val content = binding.editTextContent.text.toString()

        if (title.isBlank() || content.isBlank()) {
            Toast.makeText(this, "Please enter a title and content", Toast.LENGTH_SHORT).show()
            return
        }

        if (noteId == null) {
            dbHelper.insertNote(title, content)
        } else {
            dbHelper.updateNote(noteId!!, title, content)
        }
        finish()
    }
}
