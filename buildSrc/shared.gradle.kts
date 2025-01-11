import com.android.build.gradle.api.AndroidSourceSet
import com.example.shared.buildScript.ModifyStringResources
import com.example.shared.buildScript.isObfuscateBuildType
import com.example.shared.buildScript.obfuscatedSourceSetRoot

private fun generateObfuscatedSourcesTaskFun(sourceSet: NamedDomainObjectProvider<AndroidSourceSet>) {
    tasks {
        if (isObfuscateBuildType()) {
            val obfuscateTask by register("ObfuscateTask") {
                doFirst {
                    generateObfuscatedSources(sourceSet)
                }
            }
            tasks.named("preBuild") {
                dependsOn(obfuscateTask)
            }
        }
    }
}

private fun generateObfuscatedSources(sourceSet: NamedDomainObjectProvider<AndroidSourceSet>) {
    sourceSet {
        val projectDir = project.layout.projectDirectory
        val obfuscateSourceSet = projectDir.dir(obfuscatedSourceSetRoot())
        project.delete(obfuscateSourceSet.asFile.listFiles())

        fun copy(sourceDirs: Set<File>) = sourceDirs.map { file ->
            val relativePath = file.relativeTo(file.parentFile)
            val destinationDir = obfuscateSourceSet.dir(relativePath.path)
            file.copyRecursively(destinationDir.asFile, overwrite = true)
            destinationDir.asFileTree
        }
        copy(setOf(manifest.srcFile))
        copy(java.srcDirs)
        copy(res.srcDirs).flatMap { it.files }.forEach {
            ModifyStringResources.encrypt(it)
        }
    }
}

val generateObfuscatedSourcesTask by project.extra(::generateObfuscatedSourcesTaskFun)