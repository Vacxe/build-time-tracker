package io.github.vacxe.buildtimetracker.reporters

import java.time.Duration
import java.time.Instant

data class Report(
    val buildStart: Instant,
    val buildEnd: Instant,
    val eventReports: Collection<EventReport>,
    val userName: String,
    val osName: String
) {
    val buildDuration = Duration.between(buildStart, buildEnd)
}