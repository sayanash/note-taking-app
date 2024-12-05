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
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ADD_NOTE = 0
    private val VIEW_TYPE_NOTE = 1

    // ViewHolder for "Add New Note"
    inner class AddNoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {
            itemView.setOnClickListener {
                val context = itemView.context
                // Open an activity or dialog to add a new note
                val intent = Intent(context, EditNoteActivity::class.java)
                context.startActivity(intent)
            }
        }
    }

    // ViewHolder for Regular Notes
    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
        val timestampTextView: TextView = itemView.findViewById(R.id.textViewTimestamp)
        val deleteButton: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(note: Note) {
            titleTextView.text = note.title
            timestampTextView.text = note.timestamp

            // Edit Note on click
            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, EditNoteActivity::class.java)
                intent.putExtra("noteId", note.id)
                context.startActivity(intent)
            }

            // Delete Note on button click
            deleteButton.setOnClickListener {
                val actualPosition = adapterPosition - 1 // Adjust for the "Add New Note" item
                dbHelper.deleteNote(note.id) // Delete the note from the database
                notesList.removeAt(actualPosition) // Remove the note from the list
                notifyItemRemoved(adapterPosition) // Notify the adapter about item removal
                notifyItemRangeChanged(adapterPosition, notesList.size) // Update the affected range
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_ADD_NOTE else VIEW_TYPE_NOTE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ADD_NOTE) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_add_note, parent, false)
            AddNoteViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.note_item, parent, false)
            NoteViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AddNoteViewHolder) {
            holder.bind()
        } else if (holder is NoteViewHolder) {
            val note = notesList[position - 1] // Offset by 1 for "Add New Note"
            holder.bind(note)
        }
    }

    override fun getItemCount(): Int = notesList.size + 1 // +1 for "Add New Note" item

    fun updateNotes(newNotes: List<Note>) {
        notesList.clear()
        notesList.addAll(newNotes)
        notifyDataSetChanged()
    }
}
