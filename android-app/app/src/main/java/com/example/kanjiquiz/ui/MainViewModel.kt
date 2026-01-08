package com.example.kanjiquiz.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kanjiquiz.data.VocabEntry
import dev.esnault.wanakana.core.Wanakana
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StateData(
    val currentItem: VocabEntry? = null,
    val quizState: QuizState = QuizState.Loading,
    val answer: String = "",
    var validationMessage: String = ""
)

enum class QuizState { Loading, Empty, Question, CorrectAnswer, IncorrectAnswer }
class MainViewModel(private val repository: VocabRepository) : ViewModel() {

    private var vocabList: List<VocabEntry> = emptyList()
    private val _stateData = MutableStateFlow(StateData())
    val stateData: StateFlow<StateData> = _stateData.asStateFlow()

    init {
        viewModelScope.launch {
            vocabList = repository.getAll()
            reset()
        }
    }

    fun setAnswer(answer: String) {
        _stateData.value = _stateData.value.copy(answer=answer)
    }
    fun submit() {
        if (answerContainsKanji()) {
            _stateData.value =
                _stateData.value.copy(validationMessage = "Answer should not contain Kanji")
            return
        }
        _stateData.value = _stateData.value.copy(
            validationMessage = "",
            quizState = if (isAnswerCorrect()) QuizState.CorrectAnswer else QuizState.IncorrectAnswer
        )
    }

    fun next() {
        reset()
    }

    private fun answerContainsKanji(): Boolean =
        _stateData.value.answer.any(Wanakana::isKanji)

    private fun isAnswerCorrect(): Boolean {
        val currentItem = _stateData.value.currentItem
        if (currentItem == null)
            return false
        val answer = _stateData.value.answer.trim().trim('～', '-')
        val hiraganaAnswer = Wanakana.toHiragana(answer)

        val reading = currentItem.reading.trim().trim('～', '-')
        return answer == reading || hiraganaAnswer == reading
    }

    private fun reset() {
        val currentItem = vocabList.randomOrNull()
        _stateData.value = _stateData.value.copy(
            currentItem = currentItem,
            quizState = if (currentItem == null) QuizState.Empty else QuizState.Question,
            answer = ""
        )
    }


}
