import tomasvolker.kyscript.arguments
import tomasvolker.kyscript.kyScript
import tomasvolker.kyscript.readProcess
import tomasvolker.kyscript.tempFile

fun main() {

    val script = kyScript {

        importAs("numpy", alias = "np")

        nl(2)

        comment(
            """
        |Auto generated script
        |Python is so shit we
        |have to generate it
        """.trimMargin()
        )

        nl()

        val x = id("x")
        val print = id("print")
        val range = id("range")

        x assign 8

        nl()

        ifThen(inject("x > 5")) {
            +print(x)
        }

        nl()

        val i = id("i")

        forEach(i, range(0, x)) {
            +print(i)
        }

    }

    println(script)

    val result = tempFile("kyscript_generated", ".py") { file ->

        file.writeText(script)

        readProcess("python3") {
            arguments += file.absolutePath
        }

    }

    println(result)

}