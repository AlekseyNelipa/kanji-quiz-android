package com.example.kanjiquiz.data

class RoomVocabRepository(private val dao: VocabEntryDao) : VocabRepository {
    override suspend fun getAll(): List<VocabEntry> = dao.getAll()
}