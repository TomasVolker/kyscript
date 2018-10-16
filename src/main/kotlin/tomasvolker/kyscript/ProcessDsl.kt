package tomasvolker.kyscript

import java.io.File
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

inline fun readProcess(
    command: String = "",
    timeoutMillis: Long = Long.MAX_VALUE,
    init: ProcessBuilder.()->Unit
): String {
    val process = startProcess(command) {
        redirectErrorToOutput = true
        init()
    }

    try {

        if(process.waitFor(timeoutMillis, TimeUnit.MILLISECONDS)) {
            return process.inputStream.bufferedReader().readText()
        } else {
            process.destroyForcibly()
            throw TimeoutException()
        }

    } finally {
        process.destroyForcibly()
    }

}

inline fun startProcess(command: String = "", init: ProcessBuilder.()->Unit) =
    ProcessBuilder().apply {
        this.command = command
        init()
    }.start()

var ProcessBuilder.command: String
    get() = command()[0]
    set(value) { command(value) }

var ProcessBuilder.arguments: List<String>
    get() = command().drop(1)
    set(value) { command(command, *value.toTypedArray()) }

var ProcessBuilder.workingDirectory: File
    get() = directory()
    set(value) { directory(value) }

var ProcessBuilder.redirectErrorToOutput: Boolean
    get() = redirectErrorStream()
    set(value) { redirectErrorStream(value) }

var ProcessBuilder.redirectInput: ProcessBuilder.Redirect
    get() = redirectInput()
    set(value) { redirectInput(value) }

var ProcessBuilder.redirectOutput: ProcessBuilder.Redirect
    get() = redirectOutput()
    set(value) { redirectOutput(value) }

var ProcessBuilder.redirectError: ProcessBuilder.Redirect
    get() = redirectError()
    set(value) { redirectError(value) }

val ProcessBuilder.PIPE get() = ProcessBuilder.Redirect.PIPE

val ProcessBuilder.INHERIT get() = ProcessBuilder.Redirect.INHERIT

fun ProcessBuilder.toFile(file: File) = ProcessBuilder.Redirect.to(file)
fun ProcessBuilder.fromFile(file: File) = ProcessBuilder.Redirect.from(file)