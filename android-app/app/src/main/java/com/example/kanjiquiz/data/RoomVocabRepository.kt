package com.example.kanjiquiz.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomVocabRepository(private val dao: VocabEntryDao) : VocabRepository {
    override suspend fun getAll(): List<VocabEntry> =
        withContext(Dispatchers.IO) { dao.getAll() }
}