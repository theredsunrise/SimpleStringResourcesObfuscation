import com.android.build.api.dsl.VariantDimension

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.nativelib"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        buildConfigField("DEVELOP", false)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        externalNativeBuild {
            cmake {
                cppFlags("-std=c++17")
            }
        }
    }
    buildTypes {
        create("develop") {
            isDefault = true
            initWith(getByName("debug"))
            buildConfigField("DEVELOP", true)
        }
        create("obfuscate") {
            isDefault = false
        }
    }
    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
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

dependencies {
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}