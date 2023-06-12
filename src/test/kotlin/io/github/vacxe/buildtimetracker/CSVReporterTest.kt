package io.github.vacxe.buildtimetracker

import io.github.vacxe.buildtimetracker.reporters.csv.CSVConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.internal.provider.ValueSupplier.Value.SUCCESS
import org.junit.jupiter.api.Test
import java.io.File
import java.util.*
import kotlin.io.path.absolutePathString

@Suppress("INACCESSIBLE_TYPE")
class CSVReporterTest : BuildTimeTrackerGradleTest() {

    private val firstTask = "first"
    private val secondTask = "second"

    @Test
    fun testConsoleOutputSingleTasksKotlin() {
        val csvReportFile = this.createTempFile("report.csv")
        val buildFile = newBuildFile(testProjectDir, "build.gradle.kts")
        buildFile.append(
            """
                import ${CSVConfiguration::class.qualifiedName}
                
                tasks.register("$firstTask") {
                    doLast {
                        Thread.sleep(200)
                        println("Here $firstTask")
                    }
                }              
                    
                ${Constants.PLUGIN_EXTENSION_NAME} {
                    csvConfiguration.set(CSVConfiguration("$csvReportFile"))
                }
                """
        )

        val result = run(buildFile.parent, firstTask)

        assertThat(result.task(firstTask)?.outcome == SUCCESS)

        assertThat(File(csvReportFile).exists()).isTrue
        val lines = File(csvReportFile).readLines()
        assertThat(lines[0].contains(":first"))
    }

    @Test
    fun testConsoleOutputMultiTasksKotlin() {
        val csvReportFile = this.createTempFile("report.csv")
        val buildFile = newBuildFile(testProjectDir, "build.gradle.kts")
        buildFile.append(
            """
                import ${CSVConfiguration::class.qualifiedName}
                
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
                    csvConfiguration.set(CSVConfiguration("$csvReportFile"))
                }
                """
        )

        val result = run(buildFile.parent, firstTask, secondTask)

        assertThat(result.task(firstTask)?.outcome == SUCCESS)
        assertThat(result.task(secondTask)?.outcome == SUCCESS)

        assertThat(File(csvReportFile).exists()).isTrue
        val lines = File(csvReportFile).readLines()
        assertThat(lines[0].contains(":first"))
        assertThat(lines[0].contains(":second"))
    }

    @Test
    fun testConsoleOutputMultiTasksIncludedUserAndOSKotlin() {
        val csvReportFile = this.createTempFile("report.csv")
        val buildFile = newBuildFile(testProjectDir, "build.gradle.kts")
        buildFile.append(
            """
                import ${CSVConfiguration::class.qualifiedName}
                
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
                    csvConfiguration.set(CSVConfiguration("$csvReportFile", Duration.ofMillis(0), true, true))
                }
                """
        )

        val result = run(buildFile.parent, firstTask, secondTask)
        val userName = System.getProperty("user.name")
        val osName = System.getProperty("os.name")

        assertThat(result.task(firstTask)?.outcome == SUCCESS)
        assertThat(result.task(secondTask)?.outcome == SUCCESS)

        assertThat(File(csvReportFile).exists()).isTrue
        val lines = File(csvReportFile).readLines()

        assertThat(lines[0]).contains(":$firstTask", userName, osName)
        assertThat(lines[1]).contains(":$secondTask", userName, osName)
    }

    @Test
    fun testConsoleOutputEmptyAfterFiltrationKotlin() {
        val csvReportFile = "${testProjectDir.absolutePathString()}\\report.csv".let {
            it.replace("\\", "\\\\");
        }
        val buildFile = newBuildFile(testProjectDir, "build.gradle.kts")
        buildFile.append(
            """
                import ${CSVConfiguration::class.qualifiedName}
                
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
                    csvConfiguration.set(CSVConfiguration("$csvReportFile", Duration.ofMillis(1000)))
                }
                """
        )

        val result = run(buildFile.parent, firstTask, secondTask)

        assertThat(result.task(firstTask)?.outcome == SUCCESS)
        assertThat(result.task(secondTask)?.outcome == SUCCESS)

        assertThat(File(csvReportFile).exists()).isTrue
        val lines = File(csvReportFile).readLines()

        assertThat(lines).isEmpty()
    }
}