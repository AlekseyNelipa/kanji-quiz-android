package com.example.kanjiquiz.app

import android.app.Application
import androidx.room.Room
import com.example.kanjiquiz.data.AppDb
import com.example.kanjiquiz.data.VocabEntry
import com.example.kanjiquiz.domain.Domain
import com.example.kanjiquiz.domain.VocabRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class App : Application() {

    lateinit var vocabRepository: VocabRepository
        private set

    lateinit var appScope: CoroutineScope
        private set

    lateinit var domain: Domain
        private set

    override fun onCreate() {
        super.onCreate()
        val db = Room.databaseBuilder(applicationContext, AppDb::class.java, "vocab.db")
            .createFromAsset("vocab.db")
            .fallbackToDestructiveMigration(false)
            .build()

        appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

        vocabRepository = object : VocabRepository {
            override suspend fun getAll(): List<VocabEntry> =
                withContext(Dispatchers.IO) { db.entriesDao().getAll() }

            override suspend fun getAllTags(): List<String> =
                withContext(Dispatchers.IO) { db.entriesDao().getAllTags() }
        }

        domain = Domain(vocabRepository)
        appScope.launch {
            domain.loadAll()
        }
    }
}