package com.example.notetakingapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

        // Get noteId if it's for editing
        noteId = intent.getLongExtra("noteId", -1)

        // If editing an existing note, load its details
        if (noteId != -1L) {
            val note = dbHelper.getNoteById(noteId!!)
            binding.editTextTitle.setText(note?.title)
            binding.editTextContent.setText(note?.content)
        }

        // Auto-save when text changes in either field
        binding.editTextTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                saveNote()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.editTextContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                saveNote()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun saveNote() {
        val title = binding.editTextTitle.text.toString()
        val content = binding.editTextContent.text.toString()

        // Save or update the note in the database
        if (noteId == -1L) {
            // New note
            dbHelper.insertNote(title, content)
        } else {
            // Existing note, update it
            dbHelper.updateNote(noteId!!, title, content)
        }
    }
}
