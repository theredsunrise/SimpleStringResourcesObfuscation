package com.example.shared.buildScript

import com.example.shared.Obfuscation
import java.io.File

object ModifyStringResources {
    fun encrypt(file: File) {
        var text = file.readText(charset = Charsets.UTF_8)
        val escapedPattern = "(<string\\s+(skip=\"(.+)\"\\s+)?name=.+>)(.+?)(</string>)"
        val regex = escapedPattern.toRegex()
        val result = regex.findAll(text).toList().reversed()
        if (result.isEmpty()) {
            return
        }
        result.filter {
            it.groupValues[3].lowercase().let { value ->
                value.isEmpty() || value == "false"
            }
        }.mapNotNull { it.groups[4] }.forEach {
            text = text.replaceRange(it.range, Obfuscation.encrypt(it.value))
        }
        file.writeText(text, charset = Charsets.UTF_8)
    }
}