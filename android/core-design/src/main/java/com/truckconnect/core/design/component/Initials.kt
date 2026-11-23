package com.truckconnect.core.design.component

import java.text.BreakIterator
import java.util.Locale

/**
 * Extracts initials from a user's name (fixes BUG-07).
 *
 * Rules:
 * - First letter/grapheme of the first two words, uppercased.
 * - Unicode and Malayalam safe using [BreakIterator].
 * - Single word -> single initial (e.g. "Suresh" -> "S").
 * - Blank or empty -> fallback "?".
 */
fun initials(name: String?): String {
    if (name.isNullOrBlank()) return "?"
    val words = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    if (words.isEmpty()) return "?"

    fun firstGrapheme(word: String): String {
        val iterator = BreakIterator.getCharacterInstance()
        iterator.setText(word)
        val start = iterator.first()
        val end = iterator.next()
        return if (end != BreakIterator.DONE) {
            word.substring(start, end).uppercase(Locale.getDefault())
        } else {
            word.take(1).uppercase(Locale.getDefault())
        }
    }

    return when {
        words.size == 1 -> firstGrapheme(words[0])
        else -> firstGrapheme(words[0]) + firstGrapheme(words[1])
    }
}
