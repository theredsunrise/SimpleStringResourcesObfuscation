import com.android.build.gradle.api.AndroidSourceSet
import com.example.shared.buildScript.buildConfigField
import com.example.shared.buildScript.obfuscatedSourceSetRoot
import com.example.shared.buildScript.sharedBuildScript
import gradle.kotlin.dsl.accessors._966cb67a518d20d06781757b968bfa74.implementation
import java.util.Properties

plugins {
    id("com.android.library")
    kotlin("android")
}

apply(from = sharedBuildScript())

android {
    defaultConfig {
        buildConfigField("DEVELOP", false)
    }

    signingConfigs {
        create("obfuscate") {
            val keystoreProperties: Properties by rootProject.extra
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
        }
    }

    buildTypes {
        create("develop") {
            isDefault = true
            initWith(getByName("debug"))
            signingConfig = signingConfigs.getByName("debug")
            buildConfigField("DEVELOP", true)
        }
        create("obfuscate") {
            isDefault = false
            signingConfig = signingConfigs.getByName("obfuscate")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    androidComponents {
        beforeVariants { variantBuilder ->
            if(variantBuilder.buildType == "debug" || variantBuilder.buildType == "release") {
                variantBuilder.enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    sourceSets {
        named("obfuscate") {
            java.srcDirs(mutableSetOf("${obfuscatedSourceSetRoot()}/java"))
            res.setSrcDirs(mutableSetOf("${obfuscatedSourceSetRoot()}/res"))
            manifest.srcFile("${obfuscatedSourceSetRoot()}/AndroidManifest.xml")
        }
        named("main") {
            java.setSrcDirs(emptyList<File>())
            res.setSrcDirs(emptyList<File>())
            manifest.srcFile(named("develop").get().manifest.toString())
        }
    }
}

val generateObfuscatedSourcesTask: (NamedDomainObjectProvider<AndroidSourceSet>) -> Unit by project.extra
generateObfuscatedSourcesTask(project.android.sourceSets.named("develop"))

dependencies {
    implementation(files("$rootDir/buildSrc/build/libs/sharedJar-1.0.jar"))
}