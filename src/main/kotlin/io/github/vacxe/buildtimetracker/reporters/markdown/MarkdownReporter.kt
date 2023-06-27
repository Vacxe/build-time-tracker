package io.github.vacxe.buildtimetracker.reporters.markdown

import io.github.vacxe.buildtimetracker.reporters.Report
import io.github.vacxe.buildtimetracker.reporters.Reporter
import java.io.File
import java.time.Duration

class MarkdownReporter(private val configuration: MarkdownConfiguration) : Reporter {
    override fun report(report: Report) {
        val builder = StringBuilder()
        builder.appendLine("#### Build finished: ${report.buildDuration.toSecondsWithMillis()}s")

        val filteredEventReports = report.eventReports
            .filter { it.duration > configuration.minDuration }
            .let {
                if (configuration.sorted) it.sortedBy { element -> element.duration }.reversed() else it
            }
            .take(configuration.take)

        if(configuration.withTableLabels) {
            builder.appendLine("|Task|Duration|Proportion|")
        }
        builder.appendLine("|---|---|---|")
        filteredEventReports.map {
            "|${it.taskPath}|${it.duration.toSecondsWithMillis()}s|${it.duration.percentOf(report.buildDuration)}%|"
        }.forEach(builder::appendLine)


        val excludedEvents = report.eventReports - filteredEventReports.toSet()
        if(excludedEvents.isNotEmpty()) {
            builder.appendLine()
            builder.appendLine("${excludedEvents.size} tasks been hidden by filtering configuration")
        }

        File(configuration.reportFile).run {
            if (exists()) delete()
            parentFile.mkdirs()
            createNewFile()
            printWriter().use { out -> out.write(builder.toString()) }
        }
    }

    private fun Duration.percentOf(baseDuration: Duration) =
        String.format("%.2f", this.toMillis().toDouble() / baseDuration.toMillis().toDouble() * 100)

    private fun Duration.toSecondsWithMillis() = "$seconds.${String.format("%03d", this.toMillisPart())}"
}