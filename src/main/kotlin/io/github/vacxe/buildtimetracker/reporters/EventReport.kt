package io.github.vacxe.buildtimetracker.reporters

import java.time.Duration
import java.time.Instant

data class EventReport(
    val taskPath: String,
    val startTime: Instant,
    val endTime: Instant
) {
    val duration = Duration.between(startTime, endTime)
}