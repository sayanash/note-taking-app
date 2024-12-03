package com.example.notetakingapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotesAdapter(
    private var notesList: MutableList<Note>,
    private val dbHelper: NotesDatabaseHelper
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
        val timestampTextView: TextView = itemView.findViewById(R.id.textViewTimestamp)
        val deleteButton: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notesList[position]
        holder.titleTextView.text = note.title
        holder.timestampTextView.text = note.timestamp

        // Edit Note on click
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EditNoteActivity::class.java)
            intent.putExtra("noteId", note.id)
            context.startActivity(intent)
        }

        // Delete Note on button click
        holder.deleteButton.setOnClickListener {
            dbHelper.deleteNote(note.id)  // Make sure note.id is of type Long
            notesList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, notesList.size)
        }
    }

    override fun getItemCount(): Int = notesList.size

    fun updateNotes(newNotes: List<Note>) {
        notesList.clear()
        notesList.addAll(newNotes)
        notifyDataSetChanged()
    }
}
