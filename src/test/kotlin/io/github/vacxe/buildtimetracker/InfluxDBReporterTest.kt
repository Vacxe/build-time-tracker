package io.github.vacxe.buildtimetracker

import io.github.vacxe.buildtimetracker.reporters.csv.CSVConfiguration
import io.github.vacxe.buildtimetracker.reporters.influxdb.InfluxDBConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.internal.provider.ValueSupplier.Value.SUCCESS
import org.junit.jupiter.api.Test
import java.io.File
import java.util.*
import kotlin.io.path.absolutePathString

@Suppress("INACCESSIBLE_TYPE")
class InfluxDBReporterTest : BuildTimeTrackerGradleTest() {

    private val firstTask = "first"
    private val secondTask = "second"
    @Test
    fun testConsoleOutputMultiTasksKotlin() {
        val buildFile = newBuildFile(testProjectDir, "build.gradle.kts")
        buildFile.append(
            """
                import ${InfluxDBConfiguration::class.qualifiedName}
                
                tasks.register("$firstTask") {
                    doLast {
                        Thread.sleep(200)
                        println("Here $firstTask")
                    }
                }    
                
                tasks.register("$secondTask") {
                    doLast {
                        Thread.sleep(400)
                        println("Here $secondTask")
                    }
                }
                    
                ${Constants.PLUGIN_EXTENSION_NAME} {
                    influxDBConfiguration.set(InfluxDBConfiguration("http://192.168.1.100:8086/", "i7WZRml93dclhYfCeYK-u4ZkGFEZ8YNe6gvK2_KD_LV6sOEKXLspT4599cUltlunqRZEGDeJSl2K-pkgOQ1--Q==", "glasshouse", "BuildTimeTracker"))
                }
                """
        )

        val result = run(buildFile.parent, firstTask, secondTask)

        assertThat(result.task(firstTask)?.outcome == SUCCESS)
        assertThat(result.task(secondTask)?.outcome == SUCCESS)
    }
}