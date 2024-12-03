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
        noteId = intent.getLongExtra("noteId", -1L).takeIf { it != -1L }

        // Load the existing note if available
        noteId?.let {
            val note = dbHelper.getNoteById(it)
            if (note != null) {
                binding.editTextTitle.setText(note.title)
                binding.editTextContent.setText(note.content)
            }
        }

        // Automatically save the note when the content changes
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                saveNote()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.editTextTitle.addTextChangedListener(textWatcher)
        binding.editTextContent.addTextChangedListener(textWatcher)
    }

    private fun saveNote() {
        val title = binding.editTextTitle.text.toString()
        val content = binding.editTextContent.text.toString()

        if (noteId != null) {
            // Update existing note
            dbHelper.updateNote(noteId!!, title, content)
        } else if (title.isNotBlank() || content.isNotBlank()) {
            // Insert a new note only if one doesn't exist and fields are not empty
            noteId = dbHelper.insertNote(title, content)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        saveNote() // Ensure note is saved when exiting
    }
}
