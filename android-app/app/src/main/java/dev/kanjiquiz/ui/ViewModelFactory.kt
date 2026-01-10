package dev.kanjiquiz.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.kanjiquiz.domain.Domain
import dev.kanjiquiz.ui.quizScreen.QuizViewModel
import dev.kanjiquiz.ui.settingsScreen.SettingsViewModel

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