package io.github.vacxe.buildtimetracker.reporters.csv

import io.github.vacxe.buildtimetracker.reporters.Report
import io.github.vacxe.buildtimetracker.reporters.Reporter
import java.io.File

class CSVReporter(private val csvConfiguration: CSVConfiguration) : Reporter {
    override fun report(report: Report) {
        val userName = System.getProperty("user.name")
        val osName = System.getProperty("os.name")

        val filteredEventReports = report.eventReports
            .filter { it.duration > csvConfiguration.minDuration }

        val csvContent = filteredEventReports.map {
            arrayListOf(it.taskPath, it.duration.toMillis().toString(), it.startTime, it.endTime)
                .apply {
                    if (csvConfiguration.includeSystemUserName) {
                        add(userName)
                    }
                    if (csvConfiguration.includeSystemOSName) {
                        add(osName)
                    }
                }
                .joinToString()
        }

        File(csvConfiguration.reportFile).run {
            if (exists()) delete()
            parentFile.mkdirs()
            createNewFile()
            printWriter().use { out -> csvContent.forEach(out::println) }
        }
    }
}