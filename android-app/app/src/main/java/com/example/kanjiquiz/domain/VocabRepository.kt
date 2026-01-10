package com.example.kanjiquiz.domain

import com.example.kanjiquiz.data.VocabEntry

interface VocabRepository {
    suspend fun getAll(): List<VocabEntry>
    suspend fun getAllTags(): List<String>
}

