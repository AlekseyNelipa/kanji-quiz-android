package dev.kanjiquiz.app

import android.app.Application
import android.content.Context
import androidx.room.Room
import dev.kanjiquiz.data.AppDb
import dev.kanjiquiz.data.VocabEntry
import dev.kanjiquiz.domain.Domain
import dev.kanjiquiz.domain.VocabRepository
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

        if (isFirstTimeSinceUpdate()) {
            applicationContext.deleteDatabase("vocab.db")
        }
        val db = Room.databaseBuilder(applicationContext, AppDb::class.java, "vocab.db")
            .createFromAsset("vocab.db")
            .fallbackToDestructiveMigration(false)
            .build()

        appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

        vocabRepository = object : VocabRepository {
            override suspend fun getAllVocabEntries(): List<VocabEntry> =
                withContext(Dispatchers.IO) { db.entriesDao().getAll() }

            override suspend fun getAllTags(): List<String> =
                withContext(Dispatchers.IO) { db.entriesDao().getAllTags() }
        }

        domain = Domain(vocabRepository)
        appScope.launch {
            domain.loadAll()
        }
    }

    private fun isFirstTimeSinceUpdate(): Boolean {
        val prefs = applicationContext.getSharedPreferences("version_prefs", MODE_PRIVATE)
        val currentVersion = applicationContext.packageManager
            .getPackageInfo(applicationContext.packageName, 0)
            .longVersionCode
        val last = prefs.getLong("version", 0L)
        val prefEditor = prefs.edit()
        prefEditor.putLong("version", currentVersion)
        prefEditor.commit()
        return currentVersion != last
    }
}