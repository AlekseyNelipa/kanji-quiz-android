package com.example.kanjiquiz.ui

import com.example.kanjiquiz.data.VocabEntry

data class QuizUiState(
    val currentEntry: VocabEntry? = null,
    val quizPhase: QuizPhase = QuizPhase.Loading,
    val answer: String = "",
    var validationMessage: String = ""
)