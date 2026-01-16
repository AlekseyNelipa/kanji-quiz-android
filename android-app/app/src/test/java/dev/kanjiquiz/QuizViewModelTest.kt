package dev.kanjiquiz.ui.quizScreen

import dev.kanjiquiz.app.SettingsRepository
import dev.kanjiquiz.data.VocabEntry
import dev.kanjiquiz.domain.VocabRepository
import dev.kanjiquiz.domain.Domain
import dev.kanjiquiz.domain.EntrySelector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {

    private fun makeFakeDomain() : Domain {
        val repo = object : VocabRepository {
            override suspend fun getAllVocabEntries(): List<VocabEntry> =
                listOf(VocabEntry(1, "犬", "いぬ", "dog", "jlpt-n5"))

            override suspend fun getAllTags(): List<String> = listOf("jlpt-n5")
        }

        val settings = object : SettingsRepository {
            override fun loadSettings(): Set<String>? = null
            override fun saveSettings(tags: Set<String>) {}
        }

        val entrySelector = object : EntrySelector {
            override fun selectEntry(entries: List<VocabEntry>): VocabEntry? =
                entries.firstOrNull()

        }

        return Domain(repo, settings, entrySelector)
    }

    @Test
    fun submit_correct_hiragana() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val domain = makeFakeDomain()
            domain.loadAll()

            val vm = QuizViewModel(domain)
            testScheduler.advanceUntilIdle()

            vm.setAnswer("いぬ")
            vm.submit()

            assertEquals(QuizPhase.CorrectAnswer, vm.uiState.value.quizPhase)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun submit_correct_romaji() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val domain = makeFakeDomain()
            domain.loadAll()

            val vm = QuizViewModel(domain)
            testScheduler.advanceUntilIdle()

            vm.setAnswer("inu") // romaji should convert to いぬ
            vm.submit()

            assertEquals(QuizPhase.CorrectAnswer, vm.uiState.value.quizPhase)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun submit_incorrect_answer() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val domain = makeFakeDomain()
            domain.loadAll()

            val vm = QuizViewModel(domain)
            testScheduler.advanceUntilIdle()

            vm.setAnswer("うま") // wrong reading for 犬
            vm.submit()

            assertEquals(QuizPhase.IncorrectAnswer, vm.uiState.value.quizPhase)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun submit_answer_with_kanji_showsValidationError() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val domain = makeFakeDomain()
            domain.loadAll()

            val vm = QuizViewModel(domain)
            testScheduler.advanceUntilIdle()

            vm.setAnswer("犬") // contains Kanji
            vm.submit()

            assertEquals("Answer should not contain Kanji", vm.uiState.value.validationMessage)
        } finally {
            Dispatchers.resetMain()
        }
    }
}