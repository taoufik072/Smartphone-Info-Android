package fr.taoufikcode.domain.usecase.core

import assertk.assertThat
import assertk.assertions.isTrue
import fr.taoufikcode.domain.core.isExpired
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import org.hamcrest.MatcherAssert.assertThat

class TimeExtensionsTest {
    private val fixedTime = Instant.parse("2026-01-01T12:00:00Z").toEpochMilli()
    private val fixedClock = Clock.fixed(Instant.ofEpochMilli(fixedTime), ZoneId.of("UTC"))

    @Test
    fun `given timestamp 60 minutes ago when isExpired with 61 minutes then return true`() {
        // Given
        val timestamp = fixedTime - (61 * 60000) // 60 minutes

        // When
        val result = timestamp.isExpired(minutePassed = 60, clock = fixedClock)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `given timestamp 6 minutes ago when isExpired with 5 minutes then return true`() {
        // Given
        val timestamp = fixedTime - (6 * 60000)

        // When
        val result = timestamp.isExpired(minutePassed = 5, clock = fixedClock)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `given zero timestamp when isExpired then return true`() {
        // Given
        val timestamp = 0L

        // When
        val result = timestamp.isExpired(minutePassed = 1, clock = fixedClock)

        // Then
        assertThat(result).isTrue()
    }
}
