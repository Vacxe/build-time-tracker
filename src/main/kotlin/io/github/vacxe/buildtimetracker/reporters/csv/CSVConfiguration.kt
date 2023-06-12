package io.github.vacxe.buildtimetracker.reporters.csv

import java.io.Serializable
import java.time.Duration

data class CSVConfiguration(
    val reportDir: String,
    val minDuration: Duration = Duration.ZERO,
    val includeSystemUserName: Boolean = false,
    val includeSystemOSName: Boolean = false
) : Serializable