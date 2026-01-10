package dev.kanjiquiz.domain

import dev.kanjiquiz.data.VocabEntry

interface VocabRepository {
    suspend fun getAllVocabEntries(): List<VocabEntry>
    suspend fun getAllTags(): List<String>
}

