package dev.kanjiquiz.ui.quizScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.kanjiquiz.data.VocabEntry
import dev.kanjiquiz.domain.Domain
import dev.esnault.wanakana.core.Wanakana
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class QuizViewModel(private val domain: Domain) : ViewModel() {
    data class UIState(
        val currentEntry: VocabEntry? = null,
        val quizPhase: QuizPhase = QuizPhase.Loading,
        val answer: String = "",
        val validationMessage: String = ""
    )

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            domain.state.map { it.selectedTags }.distinctUntilChanged().collect { tags ->
                if (_uiState.value.currentEntry?.vocabSet !in tags) {
                    reset()
                }
            }
        }
        viewModelScope.launch {
            domain.state.map { it.loading }.distinctUntilChanged().filter { !it }.first()
            reset()
        }
    }

    fun setAnswer(answer: String) {
        _uiState.value = _uiState.value.copy(answer = answer)
    }

    fun submit() {
        if (answerContainsKanji()) {
            _uiState.value =
                _uiState.value.copy(validationMessage = "Answer should not contain Kanji")
            return
        }
        _uiState.value = _uiState.value.copy(
            validationMessage = "",
            quizPhase = if (isAnswerCorrect()) QuizPhase.CorrectAnswer else QuizPhase.IncorrectAnswer
        )
    }

    fun next() {
        reset()
    }

    private fun answerContainsKanji(): Boolean =
        _uiState.value.answer.any(Wanakana::isKanji)

    private fun isAnswerCorrect(): Boolean {
        val currentEntry = _uiState.value.currentEntry ?: return false

        val answer = _uiState.value.answer.trim().trim('～', '-')
        val hiraganaAnswer = Wanakana.toHiragana(answer)

        val reading = currentEntry.reading.trim().trim('～', '-')
        return answer == reading || hiraganaAnswer == reading
    }

    private fun reset() {
        val currentEntry = domain.getRandomEntry()
        _uiState.value = _uiState.value.copy(
            currentEntry = currentEntry,
            quizPhase = if (currentEntry == null) QuizPhase.Empty else QuizPhase.Question,
            answer = ""
        )
    }


}
