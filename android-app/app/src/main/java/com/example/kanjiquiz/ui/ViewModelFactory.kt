package com.example.kanjiquiz.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kanjiquiz.domain.Domain
import com.example.kanjiquiz.ui.quizScreen.QuizViewModel
import com.example.kanjiquiz.ui.settingsScreen.SettingsViewModel

class ViewModelFactory(private val domain: Domain) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(domain) as T
        }
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(domain) as T
        }
        error("Unknown ViewModel class")
    }
}