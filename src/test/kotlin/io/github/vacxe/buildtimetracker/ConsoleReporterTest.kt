package io.github.vacxe.buildtimetracker

import io.github.vacxe.buildtimetracker.reporters.console.ConsoleConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.internal.provider.ValueSupplier.Value.SUCCESS
import org.gradle.internal.os.OperatingSystem
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.inputStream
import java.nio.file.StandardOpenOption.APPEND
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
import java.nio.file.StandardOpenOption.WRITE
import java.time.Duration
import kotlin.io.path.readText

@Suppress("INACCESSIBLE_TYPE")
class ConsoleReporterTest : BuildTimeTrackerGradleTest() {

    private val firstTask = "first"
    private val secondTask = "second"



    @Test
    fun testConsoleOutputSingleTasksKotlin() {
        val buildFile = newBuildFile(testProjectDir, "build.gradle.kts")
        buildFile.append(
            """
                import ${ConsoleConfiguration::class.qualifiedName}
                
                tasks.register("$firstTask") {
                    doLast {
                        Thread.sleep(200)
                        println("Here $firstTask")
                    }
                }              
                    
                ${Constants.PLUGIN_EXTENSION_NAME} {
                    consoleConfiguration.set(ConsoleConfiguration())
                }
                """
        )

        val result = run(buildFile.parent, firstTask)

        assertThat(result.task(firstTask)?.outcome == SUCCESS)

        result.output.lines().run {
            assertThat(this[0]).isEqualTo("Here first")
            assertThat(this[1]).contains("Build finished: ")
            assertThat(this[3]).contains(":$firstTask", "s", "100.00%")
        }
    }

    @Test
    fun testConsoleOutputMultiTasksKotlin() {
        val buildFile = newBuildFile(testProjectDir, "build.gradle.kts")
        buildFile.append(
            """                
                import ${ConsoleConfiguration::class.qualifiedName}
                
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
                     consoleConfiguration.set(ConsoleConfiguration())
                }
                """
        )

        val result = run(buildFile.parent, firstTask, secondTask)

        assertThat(result.task(firstTask)?.outcome == SUCCESS)
        assertThat(result.task(secondTask)?.outcome == SUCCESS)

        result.output.lines().run {
            assertThat(this[0]).isEqualTo("Here first")
            assertThat(this[1]).isEqualTo("Here second")
            assertThat(this[2]).contains("Build finished: ")
            assertThat(this[4]).contains(":$firstTask", "s", "%")
            assertThat(this[5]).contains(":$secondTask", "s", "%")
        }
    }

    @Test
    fun testConsoleOutputEmptyAfterFiltrationKotlin() {
        val buildFile = newBuildFile(testProjectDir, "build.gradle.kts")
        buildFile.append(
            """
                import ${ConsoleConfiguration::class.qualifiedName}
                
                tasks.register("$firstTask") {
                    doLast {
                        Thread.sleep(200)
                        println("Here $firstTask")
                    }
                }
                    
                ${Constants.PLUGIN_EXTENSION_NAME} {
                    consoleConfiguration.set(ConsoleConfiguration(Duration.ofMillis(1000)))
                }
                """
        )

        val result = run(buildFile.parent, firstTask)

        assertThat(result.task(firstTask)?.outcome == SUCCESS)

        result.output.lines().run {
            assertThat(this[0]).isEqualTo("Here first")
            assertThat(this[1]).contains("Build finished: ")
            assertThat(this.size == 3)
        }
    }
}