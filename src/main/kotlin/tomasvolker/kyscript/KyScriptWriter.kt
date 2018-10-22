package tomasvolker.kyscript

import java.lang.reflect.Array

open class KyScriptWriter {

    private val script = StringBuilder()
    private var indentation: Int = 0

    val True = true
    val False = false

    val None = null

    fun newLine(lines: Int = 1) = repeat(lines) { script.appendln() }

    fun writeLine(line: String) {
        script.append("\t".repeat(indentation))
        script.appendln(line)
    }

    fun write(text: String) {
        script.append(text)
    }

    operator fun KyExpression.unaryPlus() {
        writeLine(this.code)
    }

    fun comment(comment: String) {
        writeLine("# " + comment.replace("\n", "\n# "))
    }

    fun import(identifier: String) {
        writeLine("import $identifier")
    }

    fun importAs(identifier: String, alias: String) {
        writeLine("import $identifier as $alias")
    }

    fun fromImport(from: String, identifier: String) {
        writeLine("from $from import $identifier")
    }

    fun fromImportAs(from: String, identifier: String, alias: String) {
        writeLine("from $from import $identifier as $alias")
    }

    fun ifThen(expression: KyExpression, block: KyScriptWriter.() -> Unit) {
        writeLine("if $expression:")
        indentation++
        block()
        indentation--
    }

    fun whileThen(expression: KyExpression, block: KyScriptWriter.() -> Unit) {
        writeLine("while $expression:")
        indentation++
        block()
        indentation--
    }

    fun forEach(index: KyIdentifier, range: KyExpression, block: KyScriptWriter.() -> Unit) {
        writeLine("for $index in $range:")
        indentation++
        block()
        indentation--
    }

    fun id(name: String) = KyIdentifier(name)

    fun inject(code: String) = KyInject(code)

    operator fun KyIdentifier.invoke(vararg expression: Any?) =
        KyInject("$name(${expression.joinToString { it.toPythonExpression() }})")

    infix fun KyIdentifier.assign(expression: Any?) {
        writeLine("$this = ${expression.toPythonExpression()}")
    }

    infix fun String.setTo(value: Any?) =
            KyNamedArgument(
                name = this,
                value = value
            )

    fun Any?.toPythonExpression(): String = when(this) {

        null -> "None"

        is KyExpression -> code

        is KyNamedArgument -> "$name = ${value.toPythonExpression()}"

        is String -> '\'' + this.escapeChars() + '\''

        is Number -> if(this is Double && isNaN() || this is Float && isNaN())
                "float('nan')"
            else
                when(this) {
                    Double.POSITIVE_INFINITY, Float.POSITIVE_INFINITY -> "float('+inf')"
                    Double.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY -> "float('-inf')"
                    else -> toString()
                }

        is List<*> -> this.joinToString(
            separator = ", ",
            prefix = "[",
            postfix = "]"
        ) { it.toPythonExpression() }

        is Set<*> -> this.joinToString(
            separator = ", ",
            prefix = "{",
            postfix = "}"
        ) { it.toPythonExpression() }

        is Map<*, *> -> this.entries.joinToString(
            separator = ", ",
            prefix = "{",
            postfix = "}"
        ) { "${it.key.toPythonExpression()}:${it.value.toPythonExpression()}" }


        else -> {

            val obj = this

            if (this::class.java.isArray) {
                buildString {

                    append('[')

                    var first = true

                    for (i in 0 until Array.getLength(obj)) {
                        if (!first)
                            append(", ")
                        append(Array.get(obj, i).toPythonExpression())
                        first = false
                    }

                    append(']')
                }
            } else {
                TODO("Class is not supported: ${this::class.java}")
            }

        }
    }


    fun build(): KyScript = kyScript(script.toString())

}

fun String.escapeChars(): String =
    this.asSequence()
        .flatMap {
            when(it) {
                '\\' -> "\\\\"
                '\n' -> "\\n"
                '\r' -> "\\r"
                '\t' -> "\\t"
                '"' -> "\\\""
                '\'' -> "\\\'"
                '\b' -> "\\\b"
                else -> it.toString()
            }.asSequence()
        }.joinToString(separator = "")

sealed class KyExpression(val code: String) {
    override fun toString(): String = code
}

class KyIdentifier(name: String): KyExpression(name) {
    val name get() = code
}

class KyInject(code: String): KyExpression(code)

data class KyNamedArgument(val name: String, val value: Any?)

inline fun kyScript(block: KyScriptWriter.()->Unit): KyScript =
        KyScriptWriter().apply(block).build()

