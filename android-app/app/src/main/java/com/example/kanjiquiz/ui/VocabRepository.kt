package com.example.kanjiquiz.ui

import com.example.kanjiquiz.data.VocabEntry

interface VocabRepository {
    suspend fun getAll(): List<VocabEntry>
}