package com.example.notetakingapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.GridLayoutManager
import com.example.notetakingapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: NotesDatabaseHelper
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var allNotes: MutableList<Note> = mutableListOf() // Store all notes for filtering

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)

        // Check saved theme preference
        val isDarkMode = sharedPreferences.getBoolean("isDarkMode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        binding.themeSwitch.isChecked = isDarkMode

        // Initialize the database helper
        dbHelper = NotesDatabaseHelper(this)
        allNotes = dbHelper.getAllNotes().toMutableList() // Fetch all notes

        // Initialize the adapter with all notes
        notesAdapter = NotesAdapter(allNotes, dbHelper)

        // Set up RecyclerView with GridLayoutManager (2 columns)
        binding.recyclerViewNotes.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewNotes.adapter = notesAdapter

        // Set the theme switch listener
        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            toggleTheme(isChecked)
        }


        // Set up SearchView listener to filter notes
        binding.searchViewNotes.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterNotes(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterNotes(newText)
                return true
            }
        })
    }

    private fun toggleTheme(isDarkMode: Boolean) {
        sharedPreferences.edit().putBoolean("isDarkMode", isDarkMode).apply()
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    override fun onResume() {
        super.onResume()
        // Refresh the notes list when returning to the activity
        allNotes = dbHelper.getAllNotes().toMutableList()
        notesAdapter.updateNotes(allNotes)
    }

    private fun filterNotes(query: String?) {
        if (query.isNullOrEmpty()) {
            notesAdapter.updateNotes(allNotes) // Show all notes if query is empty
        } else {
            val filteredNotes = allNotes.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.content.contains(query, ignoreCase = true)
            }
            notesAdapter.updateNotes(filteredNotes) // Update with filtered list
        }
    }
}
