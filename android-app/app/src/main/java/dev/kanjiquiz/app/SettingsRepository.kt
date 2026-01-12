package dev.kanjiquiz.app

interface SettingsRepository {
    fun saveSettings(tags: Set<String>)

    fun loadSettings(): Set<String>?
}