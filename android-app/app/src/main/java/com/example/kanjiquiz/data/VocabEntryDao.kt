package com.example.kanjiquiz.data

import androidx.room.Dao
import androidx.room.Query


@Dao
interface VocabEntryDao {
    @Query("SELECT * FROM vocab")
    suspend fun getAll(): List<VocabEntry>
}

