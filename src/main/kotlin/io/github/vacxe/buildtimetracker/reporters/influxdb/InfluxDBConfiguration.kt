package io.github.vacxe.buildtimetracker.reporters.influxdb

import java.io.Serializable
import java.time.Duration

data class InfluxDBConfiguration(
    val url: String,
    val token: String,
    val org: String,
    val bucket: String,
    val measurementName: String = "buildTime",
    val minDuration: Duration = Duration.ZERO
) : Serializable