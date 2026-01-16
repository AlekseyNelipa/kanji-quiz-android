package dev.kanjiquiz

import dev.kanjiquiz.app.SettingsRepository
import dev.kanjiquiz.data.VocabEntry
import dev.kanjiquiz.domain.Domain
import dev.kanjiquiz.domain.EntrySelector
import dev.kanjiquiz.domain.VocabRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DomainTest {
    val singleEntryVocabRepository = object : VocabRepository {
        override suspend fun getAllVocabEntries(): List<VocabEntry> =
            listOf(VocabEntry(1, "犬", "いぬ", "dog", "jlpt-n5"))

        override suspend fun getAllTags(): List<String> = listOf("jlpt-n5")
    }

    class FakeSettingsRepository(var saved: Set<String>? = null) : SettingsRepository {

        override fun saveSettings(tags: Set<String>) {
            saved = tags
        }

        override fun loadSettings(): Set<String>? = saved
    }

    class NthEntrySelector(var n: Int) : EntrySelector {
        override fun selectEntry(entries: List<VocabEntry>): VocabEntry? =
            entries.getOrNull(n)
    }

    @Test
    fun loadAll_populatesState0() = runTest {
        val domain = Domain(
            singleEntryVocabRepository,
            FakeSettingsRepository(),
            NthEntrySelector(0))

        assertTrue(domain.state.value.loading)
        domain.loadAll()

        assertFalse(domain.state.value.loading)
        assertEquals(listOf("jlpt-n5"), domain.state.value.allTags)
        assertEquals(1, domain.state.value.allEntries.size)
        assertEquals(1, domain.state.value.selectedEntries.size)
        assertTrue(domain.state.value.selectedTags.contains("jlpt-n5"))
    }

    @Test
    fun updateSelectedTags_updatesSelectedEntriesAndSaves() = runTest {
        val settings = FakeSettingsRepository()
        val domain = Domain(
            singleEntryVocabRepository,
            settings,
            NthEntrySelector(0))

        domain.loadAll()

        val newTags = setOf("jlpt-n5")
        domain.updateSelectedTags(newTags)

        assertEquals(newTags, domain.state.value.selectedTags)
        assertEquals(1, domain.state.value.selectedEntries.size)
        assertEquals(newTags, settings.saved)
    }
}