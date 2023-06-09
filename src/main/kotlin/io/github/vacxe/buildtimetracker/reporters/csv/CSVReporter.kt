package io.github.vacxe.buildtimetracker.reporters.csv

import io.github.vacxe.buildtimetracker.reporters.Report
import io.github.vacxe.buildtimetracker.reporters.Reporter
import java.time.Duration

class CSVReporter(private val minTaskDuration: Duration): Reporter {
    override fun report(report: Report) {
        val csvContent = report.eventReports.map { listOf(it.taskPath, it.duration.toMillis().toString(), it.startTime, it.endTime).joinToString() }
    }

    override fun close() {

    }
}