package io.github.vacxe.buildtimetracker.reporters.csv

import io.github.vacxe.buildtimetracker.reporters.Report
import io.github.vacxe.buildtimetracker.reporters.Reporter

class CSVReporter(private val csvConfiguration: CSVConfiguration): Reporter {
    override fun report(report: Report) {
        val userName = System.getProperty("user.name")
        val osName = System.getProperty("os.name")
        val csvContent = report.eventReports.map { listOf(it.taskPath, it.duration.toMillis().toString(), it.startTime, it.endTime).joinToString() }
    }

    override fun close() {

    }
}