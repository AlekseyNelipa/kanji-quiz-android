package com.example.kanjiquiz.app

import android.app.Application
import androidx.room.Room
import com.example.kanjiquiz.data.AppDb
import com.example.kanjiquiz.data.VocabEntry
import com.example.kanjiquiz.ui.VocabRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class App : Application() {
    lateinit var db: AppDb
    lateinit var vocabRepository: VocabRepository

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(applicationContext, AppDb::class.java, "vocab.db")
            .createFromAsset("vocab.db")
            .fallbackToDestructiveMigration(false)
            .build()

        vocabRepository = object : VocabRepository {
            override suspend fun getAll(): List<VocabEntry> =
                withContext(Dispatchers.IO) { db.entriesDao().getAll() }
        }
    }
}