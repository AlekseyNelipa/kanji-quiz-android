package com.example.kanjiquiz.app

import android.app.Application
import androidx.room.Room
import com.example.kanjiquiz.data.AppDb
import com.example.kanjiquiz.data.RoomVocabRepository

class App : Application() {
    lateinit var db: AppDb
    lateinit var vocabRepository: RoomVocabRepository

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(applicationContext, AppDb::class.java, "vocab.db")
            .createFromAsset("vocab.db")
            .fallbackToDestructiveMigration(false)
            .build()

        vocabRepository = RoomVocabRepository(db.entriesDao())
    }
}