package com.example.kanjiquiz.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [VocabEntry::class], version = 2)
abstract class AppDb : RoomDatabase() {
    abstract fun entriesDao(): VocabEntryDao
}


