package dev.kanjiquiz.domain

import dev.kanjiquiz.data.VocabEntry

interface VocabRepository {
    suspend fun getAll(): List<VocabEntry>
    suspend fun getAllTags(): List<String>
}

