package io.github.vacxe.buildtimetracker.reporters.console

import io.github.vacxe.buildtimetracker.reporters.Report
import io.github.vacxe.buildtimetracker.reporters.Reporter
import org.gradle.internal.impldep.org.apache.commons.lang.time.DurationFormatUtils
import java.time.Duration
import kotlin.text.StringBuilder

class ConsoleReporter(private val minTaskDuration: Duration) : Reporter {
    override fun report(report: Report) {
        println("Build finished: ${ if(report.buildDuration.toMillis() > 0) DurationFormatUtils.formatDuration(report.buildDuration.toMillis(), "H'H' mm'm' ss's'") else "0s"}")
        println()

        val filteredEventReports = report.eventReports
            .filter { it.duration > minTaskDuration }
        val maxTaskNameLength = filteredEventReports.maxOf { it.taskPath.length }
        filteredEventReports.map {
            StringBuilder(it.taskPath)
                .append(" ".repeat(maxTaskNameLength - it.taskPath.length))
                .append("  | ${it.duration.toSecondsPart()}.${it.duration.toMillisPart()}s")
                .append(" (${it.duration.percentFrom(report.buildDuration)}%)")
        }.forEach(::println)
    }

    override fun close() {
        // Nothing to close
    }

    private fun Duration.percentFrom(baseDuration: Duration) = String.format("%.2f", this.seconds.toDouble() / baseDuration.seconds.toDouble() * 100)
}