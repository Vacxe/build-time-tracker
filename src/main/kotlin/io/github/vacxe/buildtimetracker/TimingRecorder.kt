package io.github.vacxe.buildtimetracker

import io.github.vacxe.buildtimetracker.reporters.csv.CSVReporter
import io.github.vacxe.buildtimetracker.reporters.console.ConsoleReporter
import io.github.vacxe.buildtimetracker.reporters.EventReport
import io.github.vacxe.buildtimetracker.reporters.Report
import io.github.vacxe.buildtimetracker.reporters.console.ConsoleConfiguration
import io.github.vacxe.buildtimetracker.reporters.csv.CSVConfiguration

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.OperationCompletionListener
import org.gradle.tooling.events.task.TaskFinishEvent
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicReference

@Suppress("UnstableApiUsage")
abstract class TimingRecorder : BuildService<TimingRecorder.Params>, OperationCompletionListener, AutoCloseable {
    interface Params : BuildServiceParameters {
        val consoleConfiguration: Property<ConsoleConfiguration>
        val csvConfiguration: Property<CSVConfiguration>
    }

    private val eventReports: MutableCollection<EventReport> = ConcurrentLinkedQueue()

    override fun onFinish(event: FinishEvent) {
        if (event is TaskFinishEvent) {
            eventReports.add(
                EventReport(
                    event.descriptor.taskPath,
                    Instant.ofEpochMilli(event.result.startTime),
                    Instant.ofEpochMilli(event.result.endTime)
                )
            )
        }
    }

    override fun close() {
        val report = Report(
            eventReports
        )

        if (parameters.consoleConfiguration.isPresent) {
            ConsoleReporter(parameters.consoleConfiguration.get()).report(report)
        }
        if (parameters.csvConfiguration.isPresent) {
            CSVReporter(parameters.csvConfiguration.get()).report(report)
        }
    }
}
