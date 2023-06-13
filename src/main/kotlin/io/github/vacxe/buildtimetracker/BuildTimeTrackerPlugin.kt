package io.github.vacxe.buildtimetracker

import io.github.vacxe.buildtimetracker.Constants.PLUGIN_EXTENSION_NAME
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ReportingBasePlugin
import org.gradle.build.event.BuildEventsListenerRegistry
import javax.inject.Inject

@Suppress("UnstableApiUsage")
class BuildTimeTrackerPlugin @Inject constructor(private val registry: BuildEventsListenerRegistry) : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(ReportingBasePlugin::class.java)

        val ext = project.extensions.create(
            PLUGIN_EXTENSION_NAME, BuildTimeTrackerExtension::class.java, project
        )
        project.gradle.taskGraph.whenReady {
            val clazz = TimingRecorder::class.java
            val buildListener =
                project.gradle.sharedServices.registerIfAbsent(clazz.simpleName, clazz) { spec ->
                    with(spec.parameters) {
                        consoleConfiguration.set(ext.consoleConfiguration)
                        csvConfiguration.set(ext.csvConfiguration)
                        markdownConfiguration.set(ext.markdownConfiguration)
                        influxDBConfiguration.set(ext.influxDBConfiguration)
                    }
                }
            registry.onTaskCompletion(buildListener)
        }
    }
}
