package com.aman.mynoteapp.feature_note.data.repository

import com.aman.mynoteapp.feature_note.data.data_source.NoteDao
import com.aman.mynoteapp.feature_note.domain.model.Note
import com.aman.mynoteapp.feature_note.domain.repository.NoteRepository

import kotlinx.coroutines.flow.Flow

class NoteRepositoryImpl(
    private val dao: NoteDao
) : NoteRepository {

    override fun getNotes(): Flow<List<Note>> {
        return dao.getNotes()
    }

    override suspend fun getNoteById(id: Int): Note? {
        println("getNoteById(): id: $id")
        return dao.getNoteById(id)
    }

    override suspend fun insertNote(note: Note) {
        println("insertNote(): note: $note")
        dao.insertNote(note)
    }

    override suspend fun deleteNote(note: Note) {
        println("deleteNote(): note: $note")
        dao.deleteNote(note)
    }
}