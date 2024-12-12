package com.example.data.db.note

import com.example.data.model.Note
import org.litote.kmongo.combine
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class NoteDataSourceImpl(db:CoroutineDatabase) : NoteDataSource {

    private val database = db.getCollection<Note>()
    override suspend fun getNotes(): List<Note> {
     return database.find().ascendingSort(Note::timestamp).toList()
    }

    override suspend fun insertNote(note: Note) {
        database.insertOne(note)
    }

    override suspend fun updateNote(note: Note, id: String) :Boolean{
      val result =   database.updateOne(
            filter = Note::id eq id,
            update = combine( setValue(Note::title,note.title) , setValue(Note::body,note.body)))
        return result.matchedCount>0
    }
}