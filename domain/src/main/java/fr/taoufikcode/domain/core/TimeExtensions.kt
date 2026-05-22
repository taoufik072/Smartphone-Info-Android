package fr.taoufikcode.domain.core

import java.time.Clock
import java.time.Duration
import java.time.Instant

fun Long.isExpired(
    minutePassed: Long,
    clock: Clock = Clock.systemDefaultZone(),
): Boolean {
    val currentTime = Instant.now(clock).toEpochMilli()
    val minutesInMillis = Duration.ofMinutes(minutePassed).toMillis()
    return currentTime - this > minutesInMillis
}
