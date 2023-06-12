package io.github.vacxe.buildtimetracker.reporters.markdown

import java.io.Serializable
import java.time.Duration

data class MarkdownConfiguration(
    val reportFile: String,
    val minDuration: Duration = Duration.ZERO,
    val withTableLabels: Boolean = true
) : Serializable