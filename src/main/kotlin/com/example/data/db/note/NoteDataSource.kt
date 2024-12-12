package com.example.data.db.note

import com.example.data.model.Note

interface NoteDataSource {
    suspend fun getNotes():List<Note>
    suspend fun insertNote(note: Note)
    suspend fun updateNote(note: Note, id:String):Boolean
}