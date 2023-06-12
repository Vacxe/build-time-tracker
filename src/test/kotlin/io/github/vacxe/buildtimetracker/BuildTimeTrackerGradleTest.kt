package io.github.vacxe.buildtimetracker

import org.gradle.internal.os.OperatingSystem
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.Duration
import java.util.*
import kotlin.io.path.absolutePathString
import kotlin.io.path.inputStream
import kotlin.io.path.readText

open class BuildTimeTrackerGradleTest {

    @TempDir
    lateinit var testProjectDir: Path

    companion object {
        @TempDir
        @JvmStatic
        lateinit var sharedTestProjectDir: Path
    }

    val path = when (OperatingSystem.current()) {
        OperatingSystem.WINDOWS -> {
            // To fix starting path from "/"
            javaClass.protectionDomain.codeSource.location.path.drop(1)
        }

        else -> javaClass.protectionDomain.codeSource.location.path
    }

    private val props = generateSequence(Paths.get(path)) {
        val props = it.resolve("gradle.properties")
        if (Files.exists(props)) props else it.parent
    }
        .dropWhile { Files.isDirectory(it) }
        .take(1)
        .iterator()
        .next()
        .inputStream()
        .use {
            Properties().apply { load(it) }
        }

    fun newBuildFile(rootDir: Path, name: String): Path {
        Files.createDirectories(rootDir)
        val buildFile = rootDir.resolve(name)
        Files.newBufferedWriter(
            buildFile,
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE,
            StandardOpenOption.TRUNCATE_EXISTING
        ).use {
            it.write(
                """
                import ${Duration::class.qualifiedName}
                
                plugins {
                    id("${props.getProperty("pluginId")}")
                }
                """.trimIndent()
            )
            it.newLine()
        }
        return buildFile
    }

    fun run(rootDir: Path, vararg args: String): BuildResult {
        return GradleRunner.create()
            .withProjectDir(rootDir.toFile())
            .withArguments(*args, "-q", "--warning-mode=all", "--stacktrace")
            .withPluginClasspath()
            .withDebug(false)
            .forwardOutput()
            .build()
    }

    fun printHorzLine(file: Path, start: Boolean) {
        val text = file.fileName.toString() + (if (start) " start" else " end")
        val n = 80 - text.length
        val k = n / 2
        println(
            buildString {
                append("-".repeat(k))
                append(text)
                append("-".repeat(n - k))
            }
        )
    }

    fun Path.append(content: String) {
        Files.newBufferedWriter(this, StandardOpenOption.APPEND).use {
            it.write(content.trimIndent())
        }

        printHorzLine(this, true)
        println(this.readText())
        printHorzLine(this, false)
    }

    fun createTempFile(fileName: String) = when (OperatingSystem.current()) {
        OperatingSystem.WINDOWS -> "${testProjectDir.absolutePathString()}\\$fileName".let { it.replace("\\", "\\\\") }
        else -> "${testProjectDir.absolutePathString()}/$fileName"
    }
}