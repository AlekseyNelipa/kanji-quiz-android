package com.example.kanjiquiz.domain

import com.example.kanjiquiz.data.VocabEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Domain(private val repository: VocabRepository) {

    data class DomainState(
        val loading: Boolean = true,
        val allEntries: List<VocabEntry> = emptyList(),
        val selectedEntries: List<VocabEntry> = emptyList(),
        val allTags: List<String> = emptyList(),
        val selectedTags: Set<String> = emptySet(),
    )

    private val _state = MutableStateFlow<DomainState>(DomainState())
    val state: StateFlow<DomainState> = _state.asStateFlow()


    private var loaded = false
    suspend fun loadAll() {
        if (loaded)
            return
        loaded = true

        val allTags = repository.getAllTags()
        val allEntries = repository.getAll()
        _state.value = _state.value.copy(
            allEntries = allEntries,
            allTags = allTags,
            selectedEntries = allEntries.toList(),
            selectedTags = allTags.toSet(),
            loading = false )
    }

    fun getRandomEntry(): VocabEntry? = _state.value.selectedEntries.randomOrNull()
    fun updateSelectedTags(selectedTags: Set<String>) {
        _state.value = _state.value.copy(
            selectedTags = selectedTags,
            selectedEntries = state.value.allEntries
                .filter { it.vocabSet in selectedTags }
                .toList(),
        )
    }
}