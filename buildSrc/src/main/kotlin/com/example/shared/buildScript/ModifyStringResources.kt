package com.example.shared.buildScript

import java.io.File

object ModifyStringResources {
    fun encrypt(file: File) {
        var text = file.readText(charset = Charsets.UTF_8)
        val regex = Regex("""<string(?:\s+skip="(true|false)")?\s+name="([^"]+)">(.*?)</string>""")
        val result = regex.findAll(text).toList().reversed()
        if (result.isEmpty()) {
            return
        }
        result.filter {
            it.groups[1]?.value?.lowercase()?.let { value ->
                value == "false"
            } ?: true
        }.map { it.groups }.forEach {
            val id = it[2] ?: return
            val content = it[3] ?: return
            println("****** ${id.value}  ${content.value}")
            text = text.replaceRange(content.range, Obfuscation.encrypt(id.value, content.value))
        }
        file.writeText(text, charset = Charsets.UTF_8)
    }
}