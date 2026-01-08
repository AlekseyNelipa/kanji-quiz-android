package com.example.kanjiquiz.data

interface VocabRepository {
    suspend fun getAll(): List<VocabEntry>
}