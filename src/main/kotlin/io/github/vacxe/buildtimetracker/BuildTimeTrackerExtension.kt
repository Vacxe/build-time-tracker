package io.github.vacxe.buildtimetracker

import io.github.vacxe.buildtimetracker.reporters.console.ConsoleConfiguration
import io.github.vacxe.buildtimetracker.reporters.csv.CSVConfiguration
import org.gradle.api.Project
import org.gradle.api.provider.Property

open class BuildTimeTrackerExtension(project: Project) {

    val consoleConfiguration: Property<ConsoleConfiguration> = project.objects.property(ConsoleConfiguration::class.java)
        .convention(null)

    val csvConfiguration: Property<CSVConfiguration> = project.objects.property(CSVConfiguration::class.java)
        .convention(null)
}
