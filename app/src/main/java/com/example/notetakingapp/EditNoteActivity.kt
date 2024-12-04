package com.example.notetakingapp

import android.content.Intent
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
    private var isSharing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = NotesDatabaseHelper(this)
        noteId = intent.getLongExtra("noteId", -1L).takeIf { it != -1L }

        noteId?.let {
            val note = dbHelper.getNoteById(it)
            if (note != null) {
                binding.editTextTitle.setText(note.title)
                binding.editTextContent.setText(note.content)
            }
        }

        // Optional: Set up text watchers if auto-save is needed later
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.editTextTitle.addTextChangedListener(textWatcher)
        binding.editTextContent.addTextChangedListener(textWatcher)

        binding.buttonShare.setOnClickListener {
            shareNote()
        }
    }

    private fun saveNote() {
        val title = binding.editTextTitle.text.toString().trim()
        val content = binding.editTextContent.text.toString().trim()

        if (title.isBlank()) {
            Toast.makeText(this, "Title can't be empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (noteId != null) {
            dbHelper.updateNote(noteId!!, title, content)
        } else {
            noteId = dbHelper.insertNote(title, content)
        }

        if (!isSharing) {
            Toast.makeText(this, "Note saved successfully", Toast.LENGTH_SHORT).show()
            finish() // Finish only if not sharing
        }
    }

    override fun onPause() {
        super.onPause()
        if (!isSharing) {
            saveNote()
        }
    }

    private fun shareNote() {
        isSharing = true
        saveNote() // Ensure the note is saved before sharing

        val title = binding.editTextTitle.text.toString().trim()
        val content = binding.editTextContent.text.toString().trim()

        val shareContent = when {
            title.isBlank() && content.isBlank() -> "Oops! This note is empty. Capture and share your notes effortlessly with our app!!"
            title.isBlank() -> "Content: $content\n\nNo title? No problem! Capture and share your notes effortlessly with our app!!"
            content.isBlank() -> "Title: $title\n\nReady to add some content? Try our Note-Taking App and get organized!!"
            else -> "Title: $title\nContent: $content\n\nCapture and share your notes effortlessly with our app!!"
        }

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareContent)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share note via"))
        // Reset sharing state after sharing
        isSharing = false
    }
}
