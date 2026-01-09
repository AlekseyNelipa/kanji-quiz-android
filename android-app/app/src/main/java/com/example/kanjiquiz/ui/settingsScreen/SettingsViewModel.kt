package com.example.kanjiquiz.ui.settingsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kanjiquiz.domain.VocabRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class SettingsViewModel(private val repository: VocabRepository) : ViewModel() {
    data class UIState(
        val loading: Boolean = true,
        val allTags: List<String> = emptyList(),
        val selectedTags: Set<String> = emptySet()
    )
    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val tags = repository.getAllTags()
            _uiState.value = UIState(
                loading = false,
                allTags = tags,
                selectedTags = tags.toSet()
            )
        }
    }

    fun onCheckedChange(item: String, isChecked: Boolean) {
        val current = _uiState.value
        val nextChecked =
            if (isChecked) current.selectedTags + item
            else current.selectedTags - item

        _uiState.value = current.copy(selectedTags = nextChecked)
    }
}