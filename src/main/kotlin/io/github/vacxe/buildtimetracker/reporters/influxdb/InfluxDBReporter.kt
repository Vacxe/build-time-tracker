package io.github.vacxe.buildtimetracker.reporters.influxdb

import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import com.influxdb.client.write.Point
import com.influxdb.exceptions.InfluxException
import io.github.vacxe.buildtimetracker.reporters.Report
import io.github.vacxe.buildtimetracker.reporters.Reporter
import kotlinx.coroutines.runBlocking
import java.net.SocketTimeoutException
import java.util.UUID

class InfluxDBReporter(private val configuration: InfluxDBConfiguration): Reporter {
    private val client = InfluxDBClientKotlinFactory
        .create(configuration.url,
            configuration.token.toCharArray(),
            configuration.org,
            configuration.bucket)
    override fun report(report: Report) {
        val userName = System.getProperty("user.name")
        val osName = System.getProperty("os.name")
        val buildUUID = UUID.randomUUID()

        val filteredEventReports = report.eventReports
            .filter { it.duration > configuration.minDuration }

        val writeApi = client.getWriteKotlinApi()

        val points = filteredEventReports.map {
            Point.measurement(configuration.measurementName)
                .addTag("taskPath", it.taskPath)
                .addTag("userName", userName)
                .addTag("osName", osName)
                .addTag("buildUUID", buildUUID.toString())
                .addField("duration", it.duration.toMillis())
                .time(it.startTime.toEpochMilli(), WritePrecision.MS)
        }

        try {
            runBlocking { writeApi.writePoints(points) }
        } catch (e: InfluxException) {
            println("Unable to connect to Influx Instance ${configuration.url}. Reason: ${e.message}")
        }
        client.close()
    }
}