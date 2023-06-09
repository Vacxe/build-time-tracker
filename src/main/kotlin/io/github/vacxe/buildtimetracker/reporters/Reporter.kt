package io.github.vacxe.buildtimetracker.reporters

import java.io.Closeable

interface Reporter : Closeable {
    fun report(report: Report)
}
