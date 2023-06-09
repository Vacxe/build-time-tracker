package io.github.vacxe.buildtimetracker

import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.reporting.ReportingExtension
import java.time.Duration

open class BuildTimeTrackerExtension(private val project: Project) {
    val minTaskDuration: Property<Duration> = project.objects.property(Duration::class.java)
        .convention(Duration.ofMillis(Constants.DEFAULT_MIN_TASK_DURATION))
    val includeUserName: Property<Boolean> = project.objects.property(Boolean::class.java)
        .convention(false)

    val consoleOutput: Property<Boolean> = project.objects.property(Boolean::class.java)
        .convention(false)

    val csvOutput: Property<Boolean> = project.objects.property(Boolean::class.java)
        .convention(false)
    val csvReportsDir: DirectoryProperty = project.objects.directoryProperty()
        .convention(baseReportsDir.map { it.dir(Constants.PLUGIN_EXTENSION_NAME) })

    private val baseReportsDir: DirectoryProperty
        get() = project.extensions.getByType(ReportingExtension::class.java)
            .baseDirectory
}
