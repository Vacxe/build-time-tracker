# build-time-tracker

Gradle plugin that prints the time taken by the tasks in a build.

You can customize the plugin as follows:

## Reporters
### `ConsoleConfiguration`

Parameters:
* `minDuration: Duration` - `optional` minimum task duration for report, default `0` *(all tasks included)*

`build.gradle.kts`: 
```
buildTimeTracker {
    consoleConfiguration.set(ConsoleConfiguration(Duration.ofMillis(200))
}
```

Console output:
```
Build finished: 0.643s

:first | 0.229s | 35.61%
:second | 0.412s | 64.07%
```

### `CSVConfiguration`

Parameters:
* `reportFile: String` - `required` path for report file
* `minDuration: Duration` - `optional` minimum task duration for report, default `0` *(all tasks included)*
* `includeSystemUserName: Boolean` - `optional` add username to report, default `false`
* `includeSystemOSName: Boolean` - `optional` add os name to report, default `false`

`build.gradle.kts`:
```
buildTimeTracker {
    csvConfiguration.set(CSVConfiguration("${project.buildDir}/report/buildStats.csv", Duration.ofMillis(200), true, true)) 
}
```

`buildStats.csv` output:
```
:first, 229, 2023-06-12T04:59:05.621Z, 2023-06-12T04:59:05.850Z, myusername, Windows 11
:second, 411, 2023-06-12T04:59:05.852Z, 2023-06-12T04:59:06.263Z, myusername, Windows 11
```

### `MarkdownConfiguration`

Parameters:
* `reportFile: String` - `required` path for report file
* `minDuration: Duration` - `optional` minimum task duration for report, default `0` *(all tasks included)*
* `withTableLabels: Boolean` - `optional` add table labels, default `true`

`build.gradle.kts`:
```
buildTimeTracker {
    markdownConfiguration.set(MarkdownConfiguration("${project.buildDir}/report/buildStats.md", Duration.ofMillis(0))) 
}
```

`buildStats.md` output:
```
#### Build finished: 0.638s
|Task|Duration|Proportion|
|---|---|---|
|:first|0.223s|34.95%|
|:second|0.413s|64.73%|
```

## Minimum Requirements
- Java 11
- Gradle 6.1

## License
Released under [Apache License v2.0](LICENSE).
