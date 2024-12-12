package com.example.controller

import com.example.data.db.note.NoteDataSource
import com.example.data.model.Note

class NoteController (private val dataSource: NoteDataSource){
    suspend fun fetchNotes():List<Note>{

       return dataSource.getNotes()
    }
    suspend fun insertNote(note: Note){

        dataSource.insertNote(note)
    }
    suspend fun update(note: Note, id:String):Boolean{
       return dataSource.updateNote(note,id)
    }
}