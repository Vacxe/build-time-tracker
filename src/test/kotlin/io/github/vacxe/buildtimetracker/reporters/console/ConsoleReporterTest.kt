package io.github.vacxe.buildtimetracker.reporters.console

import io.github.vacxe.buildtimetracker.reporters.EventReport
import io.github.vacxe.buildtimetracker.reporters.Report
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant

class ConsoleReporterTest {

    @Test
    fun reportTest() {
        val consoleReporter = ConsoleReporter(Duration.ofSeconds(1))
        val report = Report(buildStart = Instant.ofEpochSecond(0),
            buildEnd =  Instant.ofEpochSecond(60),
            eventReports = listOf(
                EventReport(taskPath = ":tast:a1",
                startTime = Instant.ofEpochMilli(5345),
                endTime = Instant.ofEpochSecond(10)),
                    EventReport(taskPath = ":tast:a222",
                        startTime = Instant.ofEpochSecond(15),
                        endTime = Instant.ofEpochSecond(25))
            ),
            userName = System.getProperty("user.name"),
            osName = System.getProperty("os.name")
        )
        val result = consoleReporter.report(report)
        assert(true)
    }
}