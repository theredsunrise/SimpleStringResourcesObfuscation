plugins {
    `kotlin-dsl`
}

repositories {
    google {
        content {
            includeGroupByRegex("com\\.android.*")
            includeGroupByRegex("com\\.google.*")
            includeGroupByRegex("androidx.*")
        }
    }
    mavenCentral()
    gradlePluginPortal()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

version = "1.0"
tasks {
    val sharedJar = register<Jar>("sharedJar") {
        dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources"))
        archiveBaseName = "sharedJar"
        version = "1.0"
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(sourceSets.main.get().output) {
            exclude("**/buildScript")
        }
    }
    jar {
        dependsOn(sharedJar)
    }
}

dependencies {
    implementation(libs.kotlin.android)
    implementation(libs.android.library)
    implementation(libs.android.application)
}