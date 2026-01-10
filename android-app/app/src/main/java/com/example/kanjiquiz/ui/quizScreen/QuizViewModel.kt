package com.example.kanjiquiz.ui.quizScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kanjiquiz.data.VocabEntry
import com.example.kanjiquiz.domain.Domain
import dev.esnault.wanakana.core.Wanakana
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

enum class QuizPhase { Loading, Empty, Question, CorrectAnswer, IncorrectAnswer }

class QuizViewModel(private val domain: Domain) : ViewModel() {
    data class UIState(
        val currentEntry: VocabEntry? = null,
        val quizPhase: QuizPhase = QuizPhase.Loading,
        val answer: String = "",
        val validationMessage: String = ""
    )

    private val _stateData = MutableStateFlow(UIState())
    val stateData: StateFlow<UIState> = _stateData.asStateFlow()

    init {
        viewModelScope.launch {
            domain.state.map { it.selectedTags }.distinctUntilChanged().collect { tags ->
                if (_stateData.value.currentEntry?.vocabSet !in tags) {
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
        _stateData.value = _stateData.value.copy(answer = answer)
    }

    fun submit() {
        if (answerContainsKanji()) {
            _stateData.value =
                _stateData.value.copy(validationMessage = "Answer should not contain Kanji")
            return
        }
        _stateData.value = _stateData.value.copy(
            validationMessage = "",
            quizPhase = if (isAnswerCorrect()) QuizPhase.CorrectAnswer else QuizPhase.IncorrectAnswer
        )
    }

    fun next() {
        reset()
    }

    private fun answerContainsKanji(): Boolean =
        _stateData.value.answer.any(Wanakana::isKanji)

    private fun isAnswerCorrect(): Boolean {
        val currentEntry = _stateData.value.currentEntry ?: return false

        val answer = _stateData.value.answer.trim().trim('～', '-')
        val hiraganaAnswer = Wanakana.toHiragana(answer)

        val reading = currentEntry.reading.trim().trim('～', '-')
        return answer == reading || hiraganaAnswer == reading
    }

    private fun reset() {
        val currentEntry = domain.getRandomEntry()
        _stateData.value = _stateData.value.copy(
            currentEntry = currentEntry,
            quizPhase = if (currentEntry == null) QuizPhase.Empty else QuizPhase.Question,
            answer = ""
        )
    }


}
