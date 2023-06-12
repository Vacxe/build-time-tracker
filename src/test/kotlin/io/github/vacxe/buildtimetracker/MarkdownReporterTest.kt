package io.github.vacxe.buildtimetracker

import io.github.vacxe.buildtimetracker.reporters.markdown.MarkdownConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.internal.provider.ValueSupplier.Value.SUCCESS
import org.junit.jupiter.api.Test
import java.io.File
import java.util.*

@Suppress("INACCESSIBLE_TYPE")
class MarkdownReporterTest : BuildTimeTrackerGradleTest() {

    private val firstTask = "first"
    private val secondTask = "second"

    @Test
    fun testConsoleOutputSingleTasksKotlin() {
        val markdownReportFile = this.createTempFile("report.md")
        val buildFile = newBuildFile(testProjectDir, "build.gradle.kts")
        buildFile.append(
            """
                import ${MarkdownConfiguration::class.qualifiedName}
                
                tasks.register("$firstTask") {
                    doLast {
                        Thread.sleep(200)
                        println("Here $firstTask")
                    }
                }              
                    
                ${Constants.PLUGIN_EXTENSION_NAME} {
                    markdownConfiguration.set(MarkdownConfiguration("$markdownReportFile"))
                }
                """
        )

        val result = run(buildFile.parent, firstTask)

        assertThat(result.task(firstTask)?.outcome == SUCCESS)

        assertThat(File(markdownReportFile).exists()).isTrue
        val lines = File(markdownReportFile).readLines()
        assertThat(lines[0].contains("#### Build finished"))
        assertThat(lines[1].contains("|Task|Duration|Proportion|"))
        assertThat(lines[2].contains("|---|---|---|"))
        assertThat(lines[3].contains(":first"))
    }

    @Test
    fun testConsoleOutputMultiTasksKotlin() {
        val markdownReportFile = this.createTempFile("report.md")
        val buildFile = newBuildFile(testProjectDir, "build.gradle.kts")
        buildFile.append(
            """
                import ${MarkdownConfiguration::class.qualifiedName}
                
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
                    markdownConfiguration.set(MarkdownConfiguration("$markdownReportFile"))
                }
                """
        )

        val result = run(buildFile.parent, firstTask, secondTask)

        assertThat(result.task(firstTask)?.outcome == SUCCESS)
        assertThat(result.task(secondTask)?.outcome == SUCCESS)

        assertThat(File(markdownReportFile).exists()).isTrue
        val lines = File(markdownReportFile).readLines()

        assertThat(lines[0].contains("#### Build finished"))
        assertThat(lines[1].contains("|Task|Duration|Proportion|"))
        assertThat(lines[2].contains("|---|---|---|"))
        assertThat(lines[3].contains(":first"))
        assertThat(lines[4].contains(":second"))
    }
    @Test
    fun testConsoleOutputEmptyAfterFiltrationKotlin() {
        val markdownReportFile = this.createTempFile("report.md")
        val buildFile = newBuildFile(testProjectDir, "build.gradle.kts")
        buildFile.append(
            """
                import ${MarkdownConfiguration::class.qualifiedName}
                
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
                    markdownConfiguration.set(MarkdownConfiguration("$markdownReportFile", Duration.ofMillis(1000)))
                }
                """
        )

        val result = run(buildFile.parent, firstTask, secondTask)

        assertThat(result.task(firstTask)?.outcome == SUCCESS)
        assertThat(result.task(secondTask)?.outcome == SUCCESS)

        assertThat(File(markdownReportFile).exists()).isTrue
        val lines = File(markdownReportFile).readLines()

        assertThat(lines[0].contains("#### Build finished"))
        assertThat(lines[1].contains("|Task|Duration|Proportion|"))
        assertThat(lines[2].contains("|---|---|---|"))
        assertThat(lines.size).isEqualTo(3)
    }
}