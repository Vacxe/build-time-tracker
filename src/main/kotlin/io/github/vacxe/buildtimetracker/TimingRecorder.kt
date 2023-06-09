package io.github.vacxe.buildtimetracker

import io.github.vacxe.buildtimetracker.reporters.csv.CSVReporter
import io.github.vacxe.buildtimetracker.reporters.console.ConsoleReporter
import io.github.vacxe.buildtimetracker.reporters.EventReport
import io.github.vacxe.buildtimetracker.reporters.Report

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
        // Global configuration
        val minTaskDuration: Property<Duration>
        val includeUserName: Property<Boolean>

        // Configuration for console output
        val consoleOutput: Property<Boolean>

        // Configuration for CSV output
        val csvOutput: Property<Boolean>
        val csvReportsDir: DirectoryProperty
    }

    private val eventReports: MutableCollection<EventReport> = ConcurrentLinkedQueue()
    private val buildStart = AtomicReference(Instant.now())

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
            buildStart.get(),
            Instant.now(),
            eventReports,
            System.getProperty("user.name"),
            System.getProperty("os.name")
        )

        if (parameters.consoleOutput.get()) {
            ConsoleReporter(parameters.minTaskDuration.get()).report(report)
        }
        if (parameters.csvOutput.get())
            CSVReporter(parameters.minTaskDuration.get()).report(report)
    }
}
