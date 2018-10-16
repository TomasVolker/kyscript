
import tomasvolker.kyscript.*
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


fun test() {

    val p = startProcess("java") {
        arguments += "-version"
        redirectErrorToOutput = true
        inheritIO()
    }

}


/*
ProcessBuilder("command")
    .command("other command", "arg1", "arg2")
    .directory(File("current working directory"))
    .inheritIO()
    .redirectError(ProcessBuilder.Redirect.to(File("error file")))
    .redirectError(ProcessBuilder.Redirect.PIPE)
    .redirectErrorStream(false)
    .redirectInput(ProcessBuilder.Redirect.PIPE)
    .redirectOutput(ProcessBuilder.Redirect.PIPE)
    .start()
*/

data class Command(val name: String)

fun String.toCommand() = Command(this)

operator fun Command.invoke(vararg args: String) =
        readProcess(name) {
            arguments = args.toList()
        }

fun getPythonVersion() =
    "python".toCommand()("--version")
        .removePrefix("Python")
        .trim()

fun getPython3Version() =
    "python3".toCommand()("--version")
        .removePrefix("Python")
        .trim()

fun getJavaVersion() =
    "java".toCommand()("-version")
        .lineSequence()
        .first()
        .removePrefix("java version")
        .trim { it.isWhitespace() || it == '"' }

fun main() {

    //test()


    val echo = "echo".toCommand()
    val ls = "ls".toCommand()
    val pwd = "pwd".toCommand()
    val python = "python".toCommand()
    val python3 = "python3".toCommand()
    val java = "java".toCommand()

    println(java("-version"))

    println(getPython3Version())


}


data class ProcessResult(
    val code: Int,
    val output: String,
    val error: String
)

fun String.runCommand(workingDir: File? = null): String? {
    try {
        val parts = this.split("\\s".toRegex())
        val proc = ProcessBuilder(*parts.toTypedArray())
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()



        proc.waitFor(60, TimeUnit.MINUTES)
        return proc.inputStream.bufferedReader().readText()
    } catch(e: IOException) {
        e.printStackTrace()
        return null
    }
}

fun runProcess(init: ProcessBuilder.()->Unit) =
        ProcessBuilder().apply(init).start()/*.inputStream.bufferedReader().readText()*/