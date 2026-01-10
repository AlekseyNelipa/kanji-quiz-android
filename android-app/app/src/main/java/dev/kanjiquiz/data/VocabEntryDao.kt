package dev.kanjiquiz.data

import androidx.room.Dao
import androidx.room.Query


@Dao
interface VocabEntryDao {
    @Query("SELECT * FROM vocab")
    suspend fun getAll(): List<VocabEntry>

    @Query("SELECT DISTINCT vocab_set FROM vocab")
    suspend fun getAllTags(): List<String>
}

