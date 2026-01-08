package com.example.kanjiquiz.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocab")
data class VocabEntry (
    @PrimaryKey
    val id: Int,
    val expression: String,
    val reading: String,
    val meaning: String,
    @ColumnInfo(name = "vocab_set") val vocabSet: String
)