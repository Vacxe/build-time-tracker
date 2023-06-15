package io.github.vacxe.buildtimetracker

import io.github.vacxe.buildtimetracker.reporters.console.ConsoleConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.internal.provider.ValueSupplier.Value.SUCCESS
import org.junit.jupiter.api.Test
import java.util.*

@Suppress("INACCESSIBLE_TYPE")
class ConsoleReporterTest : BuildTimeTrackerGradleTest() {

    private val firstTask = "first"
    private val secondTask = "second"

    @Test
    fun testOutputSingleTasksKotlin() {
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
    fun testOutputMultiTasksKotlin() {
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
    fun testOutputMultiTasksSortedKotlin() {
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
                     consoleConfiguration.set(ConsoleConfiguration(sorted = true))
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
            assertThat(this[4]).contains(":$secondTask", "s", "%")
            assertThat(this[5]).contains(":$firstTask", "s", "%")
        }
    }

    @Test
    fun testOutputMultiTasksTakeFirstKotlin() {
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
                     consoleConfiguration.set(ConsoleConfiguration(take = 1))
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
            assertThat(this[6]).contains("Some tasks been hidden by filtering configuration")
            assertThat(this[7]).contains("Count: 1, Total duration:")
            assertThat(this.size).isEqualTo(9)
        }
    }

    @Test
    fun testOutputEmptyAfterFiltrationKotlin() {
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
                    consoleConfiguration.set(ConsoleConfiguration(Duration.ofMillis(1000)))
                }
                """
        )

        val result = run(buildFile.parent, firstTask, secondTask)

        assertThat(result.task(firstTask)?.outcome == SUCCESS)

        result.output.lines().run {
            assertThat(this[0]).isEqualTo("Here first")
            assertThat(this[1]).isEqualTo("Here second")
            assertThat(this[2]).contains("Build finished: ")
            assertThat(this[5]).contains("Some tasks been hidden by filtering configuration")
            assertThat(this[6]).contains("Count: 2, Total duration:")
            assertThat(this.size).isEqualTo(8)
        }
    }
}