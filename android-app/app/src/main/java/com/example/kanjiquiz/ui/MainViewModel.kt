package com.example.kanjiquiz.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kanjiquiz.data.VocabEntry
import dev.esnault.wanakana.core.Wanakana
import kotlinx.coroutines.launch

enum class QuizState { Loading, Empty, Question, CorrectAnswer, IncorrectAnswer }
class MainViewModel(private val repository: VocabRepository) : ViewModel() {

    private var vocabList: List<VocabEntry> = emptyList()

    val answer = mutableStateOf("")
    val validationMessage = mutableStateOf("")

    val currentItem = mutableStateOf<VocabEntry?>(null)
    val quizState = mutableStateOf(QuizState.Loading)

    init {
        viewModelScope.launch {
            vocabList = repository.getAll()
            reset()
        }
    }


    fun submit() {
        if (quizState.value != QuizState.Question)
            return
        if (answerContainsKanji()) {
            validationMessage.value = "Answer should not contain Kanji"
            return
        }
        validationMessage.value = ""

        quizState.value =
            if (isAnswerCorrect()) QuizState.CorrectAnswer else QuizState.IncorrectAnswer
    }

    fun next() {
        if (quizState.value !in setOf(QuizState.CorrectAnswer, QuizState.IncorrectAnswer))
            return
        reset()
    }

    private fun answerContainsKanji(): Boolean =
        this.answer.value.any(Wanakana::isKanji)

    private fun isAnswerCorrect(): Boolean {
        val currentItem = this.currentItem.value
        if (currentItem == null)
            return false
        val answer = this.answer.value.trim().trim('～', '-')
        val hiraganaAnswer = Wanakana.toHiragana(answer)

        val reading = currentItem.reading.trim().trim('～', '-')
        return answer==reading || hiraganaAnswer==reading
    }

    private fun reset() {
        currentItem.value = vocabList.randomOrNull()
        quizState.value = if (currentItem.value == null)
            QuizState.Empty
        else
            QuizState.Question
        answer.value = ""
    }
}
