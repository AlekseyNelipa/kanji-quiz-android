package dev.kanjiquiz.ui.settingsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.kanjiquiz.domain.Domain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class SettingsViewModel(val domain: Domain) : ViewModel() {
    data class UIState(
        val loading: Boolean = true,
        val allTags: List<String> = emptyList(),
        val selectedTags: Set<String> = emptySet()
    )

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()


    init {

        viewModelScope.launch {
            domain.state.map { it.loading }
                .distinctUntilChanged()
                .collect { loading ->
                    _uiState.value = _uiState.value.copy(loading = loading)
                }
        }

        viewModelScope.launch {
            domain.state.map { it.allTags }
                .distinctUntilChanged()
                .collect { tags ->
                    _uiState.value = _uiState.value.copy(allTags = tags.sorted())
                }
        }

        viewModelScope.launch {
            domain.state.map { it.selectedTags }
                .distinctUntilChanged()
                .collect { tags ->
                    _uiState.value = _uiState.value.copy(selectedTags = tags)
                }
        }

    }

    fun onCheckedChange(item: String, isChecked: Boolean) {
        val nextChecked =
            if (isChecked) domain.state.value.selectedTags + item
            else domain.state.value.selectedTags - item

        domain.updateSelectedTags(nextChecked)
    }
}