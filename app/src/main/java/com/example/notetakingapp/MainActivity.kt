package com.example.notetakingapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notetakingapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: NotesDatabaseHelper
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var sharedPreferences: SharedPreferences

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

        // Initialize the adapter
        notesAdapter = NotesAdapter(dbHelper.getAllNotes().toMutableList(), dbHelper)

        // Set up RecyclerView
        binding.recyclerViewNotes.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewNotes.adapter = notesAdapter

        // Set the theme switch listener
        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            toggleTheme(isChecked)
        }

        // Add note with FAB
        binding.fabAddNote.setOnClickListener {
            startActivity(Intent(this, EditNoteActivity::class.java))
        }
    }

    private fun toggleTheme(isDarkMode: Boolean) {
        sharedPreferences.edit().putBoolean("isDarkMode", isDarkMode).apply()
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    override fun onResume() {
        super.onResume()
        // Update the notes when returning
        notesAdapter.updateNotes(dbHelper.getAllNotes().toMutableList())
    }
}
