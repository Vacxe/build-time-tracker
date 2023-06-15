package io.github.vacxe.buildtimetracker.reporters.console

import io.github.vacxe.buildtimetracker.reporters.Report
import io.github.vacxe.buildtimetracker.reporters.Reporter
import java.time.Duration

class ConsoleReporter(private val configuration: ConsoleConfiguration) : Reporter {
    override fun report(report: Report) {
        println("Build finished: ${report.buildDuration.toSecondsWithMillis()}s")
        println()

        val filteredEventReports = report.eventReports
            .filter { it.duration > configuration.minDuration }
            .let {
                if (configuration.sorted) it.sortedBy { element -> element.duration }.reversed() else it
            }
            .take(configuration.take)

        filteredEventReports.map {
                StringBuilder(it.taskPath)
                    .append(" | ${it.duration.toSecondsWithMillis()}s")
                    .append(" | ${it.duration.percentOf(report.buildDuration)}%")
            }.forEach(::println)

        val excludedEvents = report.eventReports - filteredEventReports.toSet()
        if(excludedEvents.isNotEmpty()) {
            println()
            println("Some tasks been hidden by filtering configuration:")
            val duration = Duration.ofMillis(excludedEvents.sumOf { it.duration.toMillis() })
            println("Count: ${excludedEvents.size}, Total duration: ${duration.toSecondsWithMillis()} (${duration.percentOf(report.buildDuration)}%)")
        }
    }

    private fun Duration.percentOf(baseDuration: Duration) =
        String.format("%.2f", this.toMillis().toDouble() / baseDuration.toMillis().toDouble() * 100)

    private fun Duration.toSecondsWithMillis() = "$seconds.${String.format("%03d", this.toMillisPart())}"
}