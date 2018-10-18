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

    var defaultPythonPath = "python3"

}

class KyScriptExecutor(
    var script: String = "",
    var pythonPath: String = KyScriptConfig.defaultPythonPath,
    var scriptArguments: List<String> = emptyList(),
    var timeoutMillis: Long? = null
) {

    fun execute(): String = tempFile("kyscript_generated", ".py") { file ->
        file.writeText(script)

        readProcess("python3", timeoutMillis) {
            arguments += file.absolutePath
        }

    }

}

