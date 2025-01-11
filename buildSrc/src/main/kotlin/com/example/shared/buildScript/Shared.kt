package com.example.shared.buildScript

import com.android.build.api.dsl.VariantDimension
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import java.util.regex.Pattern

fun Project.obfuscatedSourceSetRoot(): String =
    "${property("generatedSourcesRelativePath")}"

fun Project.sharedBuildScript(): RegularFile =
    rootProject.layout.projectDirectory.file("buildSrc/shared.gradle.kts")

fun Project.isObfuscateBuildType(): Boolean {
    val taskRequestsStr = gradle.startParameter.taskRequests.toString()
    val pattern: Pattern = if (taskRequestsStr.contains("assemble")) {
        Pattern.compile("assembleObfuscate")
    } else {
        Pattern.compile("bundleObfuscate")
    }
    return pattern.matcher(taskRequestsStr).find()
}

inline fun <reified ValueT> VariantDimension.buildConfigField(name: String, value: ValueT) {
    val resolvedValue = when (value) {
        is String -> "\"$value\""
        is Int -> "$value"
        is Boolean -> "$value"
        else -> value.toString()
    }
    buildConfigField(ValueT::class.java.simpleName, name, resolvedValue)
}