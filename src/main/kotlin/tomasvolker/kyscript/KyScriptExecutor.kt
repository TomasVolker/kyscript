package tomasvolker.kyscript

import java.io.File

inline fun <T> tempFile(prefix: String, suffix: String, block: (file: File)->T): T {
    var file: File? = null
    try {
        file = File.createTempFile(prefix, suffix)
        return block(file)
    } finally {
        file?.delete()
    }
}

object KyScriptConfig {

    var defaultPythonPath: String = "python3"
    var defaultTempFilePrefix: String = "kyscript_generated"

}

class KyScriptExecutor(
    var script: KyScript = kyScript(""),
    var pythonPath: String = KyScriptConfig.defaultPythonPath,
    var scriptArguments: MutableList<String> = mutableListOf(),
    var workingDirectory: File? = null,
    var timeoutMillis: Long? = null
) {

    fun execute(): String = when(val script = script) {

        is KyScript.FromString -> tempFile(
            prefix = KyScriptConfig.defaultTempFilePrefix,
            suffix = ".py"
        ) { file ->
            file.writeText(script.text)
            execute(file)
        }

        is KyScript.FromFile -> execute(script.file)

    }

    private fun execute(file: File) = readProcess(pythonPath, timeoutMillis) {
        arguments += file.absolutePath
        arguments += scriptArguments

        val scriptWorkingDirectory = this@KyScriptExecutor.workingDirectory

        if (scriptWorkingDirectory != null)
            workingDirectory = scriptWorkingDirectory

    }

}

inline fun runKyScript(script: File, config: KyScriptExecutor.()->Unit = {}): String =
    runKyScript(kyScript(script), config)

inline fun runKyScript(script: String, config: KyScriptExecutor.()->Unit = {}): String =
    runKyScript(kyScript(script), config)

inline fun runKyScript(script: KyScript = kyScript(""), config: KyScriptExecutor.()->Unit = {}): String =
    KyScriptExecutor(script = script)
        .apply(config)
        .execute()
