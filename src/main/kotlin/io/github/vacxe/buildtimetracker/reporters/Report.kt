package io.github.vacxe.buildtimetracker.reporters

import java.time.Duration
import java.time.Instant

data class Report(
    val eventReports: Collection<EventReport>
) {
    val buildStart: Instant = eventReports.map { it.startTime }.minOrNull() ?: Instant.now()
    val buildEnd: Instant = eventReports.map { it.endTime }.maxOrNull() ?: Instant.now()

    val buildDuration = Duration.between(buildStart, buildEnd)
}