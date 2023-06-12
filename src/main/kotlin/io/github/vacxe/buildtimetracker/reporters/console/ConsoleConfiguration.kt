package io.github.vacxe.buildtimetracker.reporters.console

import java.io.Serializable
import java.time.Duration

data class ConsoleConfiguration(val minDuration: Duration = Duration.ZERO) : Serializable