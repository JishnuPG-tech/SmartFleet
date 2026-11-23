package com.truckconnect.core.design.component

import org.junit.Assert.assertEquals
import org.junit.Test

class InitialsTest {

    @Test
    fun testTwoEnglishWords() {
        assertEquals("AK", initials("Anil Kumar"))
    }

    @Test
    fun testSingleEnglishWord() {
        assertEquals("S", initials("Suresh"))
    }

    @Test
    fun testMultipleWords() {
        assertEquals("JR", initials("John Robert Doe"))
    }

    @Test
    fun testWhitespaceAndCasing() {
        assertEquals("AK", initials("  anil   kumar  "))
    }

    @Test
    fun testEmptyAndBlank() {
        assertEquals("?", initials(""))
        assertEquals("?", initials("   "))
        assertEquals("?", initials(null))
    }

    @Test
    fun testMalayalamGraphemes() {
        val result = initials("അനിൽ കുമാർ")
        assertEquals("അകു", result)
    }
}
