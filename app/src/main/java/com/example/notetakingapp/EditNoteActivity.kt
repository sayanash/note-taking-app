package com.example.notetakingapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

        // Load the existing note if available
        noteId?.let {
            val note = dbHelper.getNoteById(it)
            if (note != null) {
                binding.editTextTitle.setText(note.title)
                binding.editTextContent.setText(note.content)
            }
        }

        // Automatically save the note when the content changes (Optional)
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Optional: You can remove auto-save behavior by using a separate save button
            }
            override fun afterTextChanged(s: Editable?) {
                // Optional: You can call a save mechanism here if desired, but for now it's removed
            }
        }

        binding.editTextTitle.addTextChangedListener(textWatcher)
        binding.editTextContent.addTextChangedListener(textWatcher)
    }

    private fun saveNote() {
        val title = binding.editTextTitle.text.toString().trim()
        val content = binding.editTextContent.text.toString().trim()

        // Check title and content validation before saving
        when {
            title.isBlank() && content.isNotBlank() -> {
                // If title is empty and content is not empty
                Toast.makeText(this, "Title can't be empty", Toast.LENGTH_SHORT).show()
                return
            }
            title.isNotBlank() && content.isBlank() -> {
                // If only title is provided, content is empty
                Toast.makeText(this, "Write some content", Toast.LENGTH_SHORT).show()
                return
            }
            title.isNotBlank() && content.isNotBlank() -> {
                // If both title and content are provided
                if (noteId != null) {
                    // Update existing note
                    dbHelper.updateNote(noteId!!, title, content)
                } else {
                    // Insert a new note
                    noteId = dbHelper.insertNote(title, content)
                }
                Toast.makeText(this, "Note saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            else -> {
                // If title is empty and content is empty
            }
        }
    }

    // Add a method to trigger saving on button press or when exiting the activity
    override fun onPause() {
        super.onPause()
        saveNote() // Ensure note is saved when leaving the activity
    }
}
