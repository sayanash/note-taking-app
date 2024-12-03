package com.example.notetakingapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notetakingapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: NotesDatabaseHelper
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the database helper
        dbHelper = NotesDatabaseHelper(this)

        // Initialize the adapter with a mutable list of notes and dbHelper
        notesAdapter = NotesAdapter(dbHelper.getAllNotes().toMutableList(), dbHelper)

        // Set up RecyclerView with a LinearLayoutManager
        binding.recyclerViewNotes.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewNotes.adapter = notesAdapter

        // Set onClickListener for the FAB to add a new note
        binding.fabAddNote.setOnClickListener {
            startActivity(Intent(this, EditNoteActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        // Update the notes when returning to this activity
        notesAdapter.updateNotes(dbHelper.getAllNotes().toMutableList())
    }
}
