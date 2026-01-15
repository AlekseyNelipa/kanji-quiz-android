package dev.kanjiquiz.domain

import dev.kanjiquiz.data.VocabEntry

interface EntrySelector {
    fun selectEntry(entries: List<VocabEntry>): VocabEntry?
}
