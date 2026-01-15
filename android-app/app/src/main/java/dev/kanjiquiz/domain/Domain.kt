package dev.kanjiquiz.domain

import dev.kanjiquiz.app.SettingsRepository
import dev.kanjiquiz.data.VocabEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Domain(
    private val repository: VocabRepository,
    private val settingsRepository: SettingsRepository,
    private val entrySelector: EntrySelector) {

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

        val savedSelectedTags = settingsRepository.loadSettings()
        val allTags = repository.getAllTags()
        val allEntries = repository.getAllVocabEntries()
        val selectedTags = savedSelectedTags?.intersect(allTags.toSet()) ?: allTags.toSet()
        val selectedEntries = allEntries
            .filter { it.vocabSet in selectedTags }
            .toList()
        _state.value = _state.value.copy(
            allEntries = allEntries,
            allTags = allTags,
            selectedEntries = selectedEntries,
            selectedTags = selectedTags,
            loading = false )
    }

    fun getRandomEntry(): VocabEntry? =
        entrySelector.selectEntry(_state.value.selectedEntries)

    fun updateSelectedTags(selectedTags: Set<String>) {
        _state.value = _state.value.copy(
            selectedTags = selectedTags,
            selectedEntries = state.value.allEntries
                .filter { it.vocabSet in selectedTags }
                .toList(),
        )
        settingsRepository.saveSettings(selectedTags)
    }
}